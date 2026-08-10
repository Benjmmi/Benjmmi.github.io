"""LangGraph Checkpointer 的 MySQL 教学实现。

理解这个文件时，可以把持久化数据分成三层：

1. checkpoint：图在某个 super-step 的结构、版本和父节点关系；
2. channel blob：State 中每个字段在某个版本下的实际值；
3. pending write：节点已经产生、但还没有合并进下一个 checkpoint 的写入。

LangGraph 只依赖 ``BaseCheckpointSaver`` 接口，不关心底层使用内存、MySQL
还是其他数据库。这个类实现接口后，就可以被 ``workflow.compile`` 使用。
"""

from __future__ import annotations

import asyncio
from collections.abc import AsyncIterator, Iterator, Sequence
from contextlib import contextmanager
from typing import Any
from urllib.parse import unquote, urlparse

import pymysql
from langchain_core.runnables import RunnableConfig
from langgraph.checkpoint.base import (
    WRITES_IDX_MAP,
    BaseCheckpointSaver,
    ChannelVersions,
    Checkpoint,
    CheckpointMetadata,
    CheckpointTuple,
    SerializerProtocol,
    get_checkpoint_id,
    get_checkpoint_metadata,
)


class MySQLSaver(BaseCheckpointSaver[int]):
    """使用 MySQL 持久化 checkpoint、channel blob 和 pending write。

    ``int`` 表示 channel version 使用基类提供的递增整数实现。版本号让
    LangGraph 只保存发生变化的 State channel，而不必复制全部状态。
    """

    def __init__(
        self,
        *,
        host: str,
        port: int,
        user: str,
        password: str,
        database: str,
        serde: SerializerProtocol | None = None,
    ) -> None:
        super().__init__(serde=serde)
        # 保存连接参数而不是长期持有一个连接，避免多个 FastAPI 线程共享
        # PyMySQL connection。每个接口调用会创建自己的短事务。
        self.connection_kwargs = {
            "host": host,
            "port": port,
            "user": user,
            "password": password,
            "database": database,
            "charset": "utf8mb4",
            "autocommit": False,
            "connect_timeout": 5,
            "read_timeout": 30,
            "write_timeout": 30,
        }

    @classmethod
    def from_conn_string(
        cls,
        conn_string: str,
        *,
        serde: SerializerProtocol | None = None,
    ) -> MySQLSaver:
        """从 mysql://user:password@host:port/database 创建保存器。"""

        parsed = urlparse(conn_string)
        if parsed.scheme not in {"mysql", "mysql+pymysql"}:
            raise ValueError("MYSQL_URI 必须使用 mysql:// 或 mysql+pymysql://")
        if not parsed.hostname or not parsed.username or not parsed.path.strip("/"):
            raise ValueError("MYSQL_URI 缺少主机、用户名或数据库名")

        return cls(
            host=parsed.hostname,
            port=parsed.port or 3306,
            user=unquote(parsed.username),
            password=unquote(parsed.password or ""),
            database=unquote(parsed.path.strip("/")),
            serde=serde,
        )

    @contextmanager
    def _cursor(self):
        """为一次保存器操作创建独立事务，便于多线程请求安全使用。"""

        connection = pymysql.connect(**self.connection_kwargs)
        try:
            with connection.cursor() as cursor:
                yield cursor
            # checkpoint 和对应 blob 必须一起成功，否则恢复时可能得到
            # 不完整的状态。
            connection.commit()
        except Exception:
            connection.rollback()
            raise
        finally:
            connection.close()

    def setup(self) -> None:
        """创建 LangGraph 持久化所需的数据表。"""

        # checkpoints 保存图结构，blobs 保存 State 值，writes 保存中间写入。
        # 表结构分开是为了复用未发生变化的 channel blob，减少重复数据。
        statements = (
            """
            CREATE TABLE IF NOT EXISTS langgraph_checkpoints (
                thread_id VARCHAR(191) NOT NULL,
                checkpoint_ns VARCHAR(191) NOT NULL DEFAULT '',
                checkpoint_id VARCHAR(191) NOT NULL,
                parent_checkpoint_id VARCHAR(191) NULL,
                checkpoint_type VARCHAR(64) NOT NULL,
                checkpoint_blob LONGBLOB NOT NULL,
                metadata_type VARCHAR(64) NOT NULL,
                metadata_blob LONGBLOB NOT NULL,
                PRIMARY KEY (thread_id, checkpoint_ns, checkpoint_id),
                INDEX idx_checkpoint_latest (thread_id, checkpoint_ns, checkpoint_id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
            """,
            """
            CREATE TABLE IF NOT EXISTS langgraph_checkpoint_blobs (
                thread_id VARCHAR(191) NOT NULL,
                checkpoint_ns VARCHAR(191) NOT NULL DEFAULT '',
                channel_name VARCHAR(191) NOT NULL,
                channel_version VARCHAR(191) NOT NULL,
                value_type VARCHAR(64) NOT NULL,
                value_blob LONGBLOB NOT NULL,
                PRIMARY KEY (
                    thread_id, checkpoint_ns, channel_name, channel_version
                )
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
            """,
            """
            CREATE TABLE IF NOT EXISTS langgraph_checkpoint_writes (
                thread_id VARCHAR(191) NOT NULL,
                checkpoint_ns VARCHAR(191) NOT NULL DEFAULT '',
                checkpoint_id VARCHAR(191) NOT NULL,
                task_id VARCHAR(191) NOT NULL,
                write_index INT NOT NULL,
                channel_name VARCHAR(191) NOT NULL,
                value_type VARCHAR(64) NOT NULL,
                value_blob LONGBLOB NOT NULL,
                task_path TEXT NOT NULL,
                PRIMARY KEY (
                    thread_id, checkpoint_ns, checkpoint_id, task_id, write_index
                ),
                INDEX idx_writes_checkpoint (
                    thread_id, checkpoint_ns, checkpoint_id
                )
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
            """,
        )
        with self._cursor() as cursor:
            for statement in statements:
                cursor.execute(statement)

    def _load_blobs(
        self,
        cursor,
        thread_id: str,
        checkpoint_ns: str,
        versions: ChannelVersions,
    ) -> dict[str, Any]:
        """根据 checkpoint 中记录的 channel version 还原 State 字段。"""

        values: dict[str, Any] = {}
        for channel, version in versions.items():
            cursor.execute(
                """
                SELECT value_type, value_blob
                FROM langgraph_checkpoint_blobs
                WHERE thread_id = %s AND checkpoint_ns = %s
                  AND channel_name = %s AND channel_version = %s
                """,
                (thread_id, checkpoint_ns, channel, str(version)),
            )
            row = cursor.fetchone()
            if row and row[0] != "empty":
                # dumps_typed 会同时保存类型标签和二进制内容，因此反序列化时
                # 必须把这两个字段作为 tuple 一起交还给 serde。
                values[channel] = self.serde.loads_typed((row[0], bytes(row[1])))
        return values

    def _load_tuple(self, cursor, row) -> CheckpointTuple:
        """把三张表中的记录重新组装成 LangGraph 需要的 CheckpointTuple。"""

        (
            thread_id,
            checkpoint_ns,
            checkpoint_id,
            parent_checkpoint_id,
            checkpoint_type,
            checkpoint_blob,
            metadata_type,
            metadata_blob,
        ) = row
        checkpoint: Checkpoint = self.serde.loads_typed(
            (checkpoint_type, bytes(checkpoint_blob))
        )
        # checkpoint 主记录不直接包含 State 值，需要根据 channel_versions
        # 从 blob 表加载后再放回 channel_values。
        checkpoint = {
            **checkpoint,
            "channel_values": self._load_blobs(
                cursor,
                thread_id,
                checkpoint_ns,
                checkpoint["channel_versions"],
            ),
        }
        cursor.execute(
            """
            SELECT task_id, channel_name, value_type, value_blob
            FROM langgraph_checkpoint_writes
            WHERE thread_id = %s AND checkpoint_ns = %s AND checkpoint_id = %s
            ORDER BY task_id, write_index
            """,
            (thread_id, checkpoint_ns, checkpoint_id),
        )
        pending_writes = [
            (task_id, channel, self.serde.loads_typed((value_type, bytes(value_blob))))
            for task_id, channel, value_type, value_blob in cursor.fetchall()
        ]
        config: RunnableConfig = {
            "configurable": {
                "thread_id": thread_id,
                "checkpoint_ns": checkpoint_ns,
                "checkpoint_id": checkpoint_id,
            }
        }
        parent_config: RunnableConfig | None = None
        if parent_checkpoint_id:
            # parent_config 让 LangGraph 可以沿 checkpoint 链回看历史状态。
            parent_config = {
                "configurable": {
                    "thread_id": thread_id,
                    "checkpoint_ns": checkpoint_ns,
                    "checkpoint_id": parent_checkpoint_id,
                }
            }
        return CheckpointTuple(
            config=config,
            checkpoint=checkpoint,
            metadata=self.serde.loads_typed((metadata_type, bytes(metadata_blob))),
            parent_config=parent_config,
            pending_writes=pending_writes,
        )

    def get_tuple(self, config: RunnableConfig) -> CheckpointTuple | None:
        """读取指定 checkpoint；未指定 checkpoint_id 时读取线程最新记录。"""

        thread_id = config["configurable"]["thread_id"]
        checkpoint_ns = config["configurable"].get("checkpoint_ns", "")
        checkpoint_id = get_checkpoint_id(config)
        sql = """
            SELECT thread_id, checkpoint_ns, checkpoint_id, parent_checkpoint_id,
                   checkpoint_type, checkpoint_blob, metadata_type, metadata_blob
            FROM langgraph_checkpoints
            WHERE thread_id = %s AND checkpoint_ns = %s
        """
        params: list[Any] = [thread_id, checkpoint_ns]
        if checkpoint_id:
            # time travel 等场景会明确指定历史 checkpoint_id。
            sql += " AND checkpoint_id = %s"
            params.append(checkpoint_id)
        else:
            # 普通恢复只需要当前 thread 最新的 checkpoint。
            sql += " ORDER BY checkpoint_id DESC LIMIT 1"

        with self._cursor() as cursor:
            cursor.execute(sql, params)
            row = cursor.fetchone()
            return self._load_tuple(cursor, row) if row else None

    def list(
        self,
        config: RunnableConfig | None,
        *,
        filter: dict[str, Any] | None = None,
        before: RunnableConfig | None = None,
        limit: int | None = None,
    ) -> Iterator[CheckpointTuple]:
        """按时间倒序列出满足线程、命名空间和元数据条件的 checkpoint。"""

        if limit is not None and limit <= 0:
            return

        conditions: list[str] = []
        params: list[Any] = []
        if config:
            conditions.append("thread_id = %s")
            params.append(config["configurable"]["thread_id"])
            if "checkpoint_ns" in config["configurable"]:
                conditions.append("checkpoint_ns = %s")
                params.append(config["configurable"]["checkpoint_ns"])
            if checkpoint_id := get_checkpoint_id(config):
                conditions.append("checkpoint_id = %s")
                params.append(checkpoint_id)
        if before and (before_id := get_checkpoint_id(before)):
            conditions.append("checkpoint_id < %s")
            params.append(before_id)

        sql = """
            SELECT thread_id, checkpoint_ns, checkpoint_id, parent_checkpoint_id,
                   checkpoint_type, checkpoint_blob, metadata_type, metadata_blob
            FROM langgraph_checkpoints
        """
        if conditions:
            sql += " WHERE " + " AND ".join(conditions)
        sql += " ORDER BY checkpoint_id DESC"

        results: list[CheckpointTuple] = []
        with self._cursor() as cursor:
            cursor.execute(sql, params)
            for row in cursor.fetchall():
                item = self._load_tuple(cursor, row)
                if filter and not all(
                    item.metadata.get(key) == value for key, value in filter.items()
                ):
                    continue
                results.append(item)
                if limit is not None and len(results) >= limit:
                    break
        yield from results

    def put(
        self,
        config: RunnableConfig,
        checkpoint: Checkpoint,
        metadata: CheckpointMetadata,
        new_versions: ChannelVersions,
    ) -> RunnableConfig:
        """在同一个事务中保存 channel blob 和 checkpoint。"""

        checkpoint_copy = checkpoint.copy()
        thread_id = config["configurable"]["thread_id"]
        checkpoint_ns = config["configurable"].get("checkpoint_ns", "")
        parent_checkpoint_id = config["configurable"].get("checkpoint_id")
        # channel_values 单独写入 blob 表；checkpoint 主体只保留版本映射。
        channel_values: dict[str, Any] = checkpoint_copy.pop(  # type: ignore[misc]
            "channel_values"
        )

        with self._cursor() as cursor:
            for channel, version in new_versions.items():
                # new_versions 只包含本次发生变化的 channel，因此这里天然是
                # 增量写入。
                typed_value = (
                    self.serde.dumps_typed(channel_values[channel])
                    if channel in channel_values
                    else ("empty", b"")
                )
                cursor.execute(
                    """
                    INSERT INTO langgraph_checkpoint_blobs (
                        thread_id, checkpoint_ns, channel_name, channel_version,
                        value_type, value_blob
                    ) VALUES (%s, %s, %s, %s, %s, %s)
                    ON DUPLICATE KEY UPDATE
                        value_type = VALUES(value_type), value_blob = VALUES(value_blob)
                    """,
                    (
                        thread_id,
                        checkpoint_ns,
                        channel,
                        str(version),
                        typed_value[0],
                        typed_value[1],
                    ),
                )

            checkpoint_value = self.serde.dumps_typed(checkpoint_copy)
            # metadata 包含 step、source、run_id 等执行信息。辅助函数会合并
            # RunnableConfig 中适合持久化的简单元数据。
            metadata_value = self.serde.dumps_typed(
                get_checkpoint_metadata(config, metadata)
            )
            cursor.execute(
                """
                INSERT INTO langgraph_checkpoints (
                    thread_id, checkpoint_ns, checkpoint_id, parent_checkpoint_id,
                    checkpoint_type, checkpoint_blob, metadata_type, metadata_blob
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE
                    parent_checkpoint_id = VALUES(parent_checkpoint_id),
                    checkpoint_type = VALUES(checkpoint_type),
                    checkpoint_blob = VALUES(checkpoint_blob),
                    metadata_type = VALUES(metadata_type),
                    metadata_blob = VALUES(metadata_blob)
                """,
                (
                    thread_id,
                    checkpoint_ns,
                    checkpoint["id"],
                    parent_checkpoint_id,
                    checkpoint_value[0],
                    checkpoint_value[1],
                    metadata_value[0],
                    metadata_value[1],
                ),
            )
        return {
            "configurable": {
                "thread_id": thread_id,
                "checkpoint_ns": checkpoint_ns,
                "checkpoint_id": checkpoint["id"],
            }
        }

    def put_writes(
        self,
        config: RunnableConfig,
        writes: Sequence[tuple[str, Any]],
        task_id: str,
        task_path: str = "",
    ) -> None:
        """保存中间写入；普通写入保持首次值，特殊写入允许覆盖。"""

        thread_id = config["configurable"]["thread_id"]
        checkpoint_ns = config["configurable"].get("checkpoint_ns", "")
        checkpoint_id = config["configurable"]["checkpoint_id"]
        with self._cursor() as cursor:
            for index, (channel, value) in enumerate(writes):
                # ERROR、INTERRUPT、RESUME 等系统 channel 有固定负索引；普通
                # channel 使用本批次顺序作为索引。
                write_index = WRITES_IDX_MAP.get(channel, index)
                typed_value = self.serde.dumps_typed(value)
                # 普通写入采用 INSERT IGNORE，保证任务重试时不会覆盖首次结果；
                # 特殊系统写入允许更新，使 interrupt/resume 状态保持最新。
                insert_mode = "" if write_index < 0 else "IGNORE"
                sql = f"""
                    INSERT {insert_mode} INTO langgraph_checkpoint_writes (
                        thread_id, checkpoint_ns, checkpoint_id, task_id,
                        write_index, channel_name, value_type, value_blob, task_path
                    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                """
                if write_index < 0:
                    sql += """
                        ON DUPLICATE KEY UPDATE
                            channel_name = VALUES(channel_name),
                            value_type = VALUES(value_type),
                            value_blob = VALUES(value_blob),
                            task_path = VALUES(task_path)
                    """
                cursor.execute(
                    sql,
                    (
                        thread_id,
                        checkpoint_ns,
                        checkpoint_id,
                        task_id,
                        write_index,
                        channel,
                        typed_value[0],
                        typed_value[1],
                        task_path,
                    ),
                )

    def delete_thread(self, thread_id: str) -> None:
        """删除一个线程的全部 checkpoint、blob 和 pending write。"""

        with self._cursor() as cursor:
            for table in (
                "langgraph_checkpoint_writes",
                "langgraph_checkpoint_blobs",
                "langgraph_checkpoints",
            ):
                cursor.execute(f"DELETE FROM {table} WHERE thread_id = %s", (thread_id,))

    async def aget_tuple(self, config: RunnableConfig) -> CheckpointTuple | None:
        """异步接口通过工作线程调用同步 PyMySQL，避免阻塞事件循环。"""

        return await asyncio.to_thread(self.get_tuple, config)

    async def alist(
        self,
        config: RunnableConfig | None,
        *,
        filter: dict[str, Any] | None = None,
        before: RunnableConfig | None = None,
        limit: int | None = None,
    ) -> AsyncIterator[CheckpointTuple]:
        """异步列出 checkpoint 历史。"""

        items = await asyncio.to_thread(
            lambda: list(self.list(config, filter=filter, before=before, limit=limit))
        )
        for item in items:
            yield item

    async def aput(
        self,
        config: RunnableConfig,
        checkpoint: Checkpoint,
        metadata: CheckpointMetadata,
        new_versions: ChannelVersions,
    ) -> RunnableConfig:
        """异步保存 checkpoint。"""

        return await asyncio.to_thread(
            self.put, config, checkpoint, metadata, new_versions
        )

    async def aput_writes(
        self,
        config: RunnableConfig,
        writes: Sequence[tuple[str, Any]],
        task_id: str,
        task_path: str = "",
    ) -> None:
        """异步保存 pending write。"""

        await asyncio.to_thread(self.put_writes, config, writes, task_id, task_path)

    async def adelete_thread(self, thread_id: str) -> None:
        """异步删除 thread 的全部 checkpoint 数据。"""

        await asyncio.to_thread(self.delete_thread, thread_id)
