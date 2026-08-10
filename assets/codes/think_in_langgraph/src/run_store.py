"""面向业务查询的流程状态与节点流转记录。

``MySQLSaver`` 保存的是 LangGraph 内部恢复数据，适合程序读取，但不适合直接
展示给运营或监控系统。本模块额外维护两张易读的业务表：

* ``agent_runs``：一个 thread 当前处于 running、needs_review、completed
  还是 failed；
* ``agent_run_steps``：每次节点尝试的名称、状态、开始时间和结束时间。

这两套存储用途不同：删除业务记录不会替代 checkpoint，反之亦然。
"""

from __future__ import annotations

import threading
from contextlib import contextmanager
from typing import Any
from urllib.parse import unquote, urlparse
from uuid import UUID

import pymysql
from langchain_core.callbacks import BaseCallbackHandler
from langgraph.errors import GraphInterrupt


class MySQLRunStore:
    """保存便于业务查询的流程状态和节点流转记录。"""

    def __init__(
        self,
        *,
        host: str,
        port: int,
        user: str,
        password: str,
        database: str,
    ) -> None:
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
    def from_conn_string(cls, conn_string: str) -> MySQLRunStore:
        """从 MySQL URI 创建流程记录存储。"""

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
        )

    @contextmanager
    def _cursor(self, *, dictionary: bool = False):
        """创建短事务；查询接口可选择返回字典形式的记录。"""

        cursor_class = pymysql.cursors.DictCursor if dictionary else pymysql.cursors.Cursor
        connection = pymysql.connect(
            **self.connection_kwargs,
            cursorclass=cursor_class,
        )
        try:
            with connection.cursor() as cursor:
                yield cursor
            connection.commit()
        except Exception:
            connection.rollback()
            raise
        finally:
            connection.close()

    def setup(self) -> None:
        """创建流程状态表和节点流转表。"""

        # agent_runs 是当前状态快照，agent_run_steps 是只追加的执行历史。
        # 外键级联保证删除测试 run 时不会留下孤立的 step。
        statements = (
            """
            CREATE TABLE IF NOT EXISTS agent_runs (
                thread_id VARCHAR(191) NOT NULL,
                email_id VARCHAR(191) NULL,
                status VARCHAR(32) NOT NULL,
                current_node VARCHAR(191) NULL,
                error_message TEXT NULL,
                created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                    ON UPDATE CURRENT_TIMESTAMP(6),
                finished_at DATETIME(6) NULL,
                PRIMARY KEY (thread_id),
                INDEX idx_agent_runs_status (status, updated_at)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
            """,
            """
            CREATE TABLE IF NOT EXISTS agent_run_steps (
                id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                thread_id VARCHAR(191) NOT NULL,
                callback_run_id VARCHAR(64) NOT NULL,
                step_number INT NULL,
                node_name VARCHAR(191) NOT NULL,
                status VARCHAR(32) NOT NULL,
                error_message TEXT NULL,
                started_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                finished_at DATETIME(6) NULL,
                PRIMARY KEY (id),
                UNIQUE KEY uq_agent_step_callback (callback_run_id),
                INDEX idx_agent_steps_thread (thread_id, id),
                CONSTRAINT fk_agent_steps_run
                    FOREIGN KEY (thread_id) REFERENCES agent_runs(thread_id)
                    ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
            """,
        )
        with self._cursor() as cursor:
            for statement in statements:
                cursor.execute(statement)

    def start_run(self, thread_id: str, email_id: str | None = None) -> None:
        """创建流程记录；恢复已有流程时将其重新标记为运行中。"""

        # 同一个 thread 在人工审核恢复时已经存在，因此使用 upsert，而不是
        # 每次生成新的业务流程记录。
        with self._cursor() as cursor:
            cursor.execute(
                """
                INSERT INTO agent_runs (
                    thread_id, email_id, status, current_node,
                    error_message, finished_at
                ) VALUES (%s, %s, 'running', NULL, NULL, NULL)
                ON DUPLICATE KEY UPDATE
                    email_id = COALESCE(VALUES(email_id), email_id),
                    status = 'running',
                    current_node = NULL,
                    error_message = NULL,
                    finished_at = NULL
                """,
                (thread_id, email_id),
            )

    def set_run_status(
        self,
        thread_id: str,
        status: str,
        *,
        current_node: str | None = None,
        error_message: str | None = None,
    ) -> None:
        """更新流程的当前业务状态。"""

        is_finished = status in {"completed", "failed"}
        with self._cursor() as cursor:
            cursor.execute(
                """
                UPDATE agent_runs
                SET status = %s,
                    current_node = COALESCE(%s, current_node),
                    error_message = %s,
                    finished_at = CASE WHEN %s THEN CURRENT_TIMESTAMP(6) ELSE NULL END
                WHERE thread_id = %s
                """,
                (status, current_node, error_message, is_finished, thread_id),
            )

    def start_step(
        self,
        *,
        thread_id: str,
        callback_run_id: str,
        node_name: str,
        step_number: int | None,
    ) -> None:
        """记录一次节点执行尝试。"""

        # callback_run_id 是 LangChain 为这次节点调用生成的唯一 ID。同一个
        # human_review 节点在中断前后会执行两次，因此会留下两条 step。
        with self._cursor() as cursor:
            cursor.execute(
                """
                INSERT INTO agent_run_steps (
                    thread_id, callback_run_id, step_number, node_name, status
                ) VALUES (%s, %s, %s, %s, 'running')
                """,
                (thread_id, callback_run_id, step_number, node_name),
            )
            cursor.execute(
                """
                UPDATE agent_runs
                SET status = 'running', current_node = %s, error_message = NULL
                WHERE thread_id = %s
                """,
                (node_name, thread_id),
            )

    def finish_step(
        self,
        callback_run_id: str,
        status: str,
        *,
        error_message: str | None = None,
    ) -> None:
        """将节点执行尝试标记为完成、等待输入或失败。"""

        with self._cursor() as cursor:
            cursor.execute(
                """
                UPDATE agent_run_steps
                SET status = %s,
                    error_message = %s,
                    finished_at = CURRENT_TIMESTAMP(6)
                WHERE callback_run_id = %s
                """,
                (status, error_message, callback_run_id),
            )

    def get_run(self, thread_id: str) -> dict[str, Any] | None:
        """读取流程当前业务状态。"""

        with self._cursor(dictionary=True) as cursor:
            cursor.execute(
                "SELECT * FROM agent_runs WHERE thread_id = %s",
                (thread_id,),
            )
            return cursor.fetchone()

    def list_steps(self, thread_id: str) -> list[dict[str, Any]]:
        """按发生顺序读取流程的节点流转历史。"""

        with self._cursor(dictionary=True) as cursor:
            cursor.execute(
                """
                SELECT id, thread_id, callback_run_id, step_number, node_name,
                       status, error_message, started_at, finished_at
                FROM agent_run_steps
                WHERE thread_id = %s
                ORDER BY id
                """,
                (thread_id,),
            )
            return list(cursor.fetchall())

    def delete_run(self, thread_id: str) -> None:
        """删除测试或过期流程的业务记录，节点记录由外键级联删除。"""

        with self._cursor() as cursor:
            cursor.execute("DELETE FROM agent_runs WHERE thread_id = %s", (thread_id,))


class NodeTransitionRecorder(BaseCallbackHandler):
    """监听 LangGraph 业务节点事件并写入 MySQL，不记录内部 Runnable。

    生命周期映射如下：

    ``on_chain_start`` -> running
    ``on_chain_end`` -> completed
    ``on_chain_error(GraphInterrupt)`` -> waiting_input
    ``on_chain_error(其他异常)`` -> failed

    Callback 只观察事件，不会捕获或吞掉 ``GraphInterrupt``，因此不会破坏
    LangGraph 原本的暂停机制。
    """

    # 如果节点流转记录写入失败，就让当前执行失败，避免业务流程继续运行但
    # 审计记录静默丢失。学习项目中可以直观看到这一取舍。
    raise_error = True

    def __init__(self, store: MySQLRunStore) -> None:
        self.store = store
        # on_chain_end/on_chain_error 不再携带节点 metadata，所以开始事件中
        # 先按 run_id 记住它是不是一个需要跟踪的业务节点。
        self._active_nodes: dict[UUID, str] = {}
        self._lock = threading.Lock()

    def on_chain_start(
        self,
        serialized: dict[str, Any] | None,
        inputs: dict[str, Any],
        *,
        run_id: UUID,
        name: str | None = None,
        metadata: dict[str, Any] | None = None,
        **kwargs: Any,
    ) -> None:
        metadata = metadata or {}
        node_name = metadata.get("langgraph_node")
        thread_id = metadata.get("thread_id")
        # RunnableSequence 会继承节点 metadata，只记录名称等于节点名的那一层。
        if not node_name or name != node_name or not thread_id:
            return
        callback_run_id = str(run_id)
        self.store.start_step(
            thread_id=str(thread_id),
            callback_run_id=callback_run_id,
            node_name=str(node_name),
            step_number=metadata.get("langgraph_step"),
        )
        with self._lock:
            self._active_nodes[run_id] = callback_run_id

    def on_chain_end(
        self,
        outputs: dict[str, Any],
        *,
        run_id: UUID,
        **kwargs: Any,
    ) -> None:
        callback_run_id = self._pop_active(run_id)
        if callback_run_id:
            self.store.finish_step(callback_run_id, "completed")

    def on_chain_error(
        self,
        error: BaseException,
        *,
        run_id: UUID,
        **kwargs: Any,
    ) -> None:
        callback_run_id = self._pop_active(run_id)
        if not callback_run_id:
            return
        if isinstance(error, GraphInterrupt):
            # interrupt 在内部表现为特殊异常，但业务语义是等待输入而非失败。
            self.store.finish_step(callback_run_id, "waiting_input")
        else:
            self.store.finish_step(
                callback_run_id,
                "failed",
                error_message=str(error),
            )

    def _pop_active(self, run_id: UUID) -> str | None:
        with self._lock:
            return self._active_nodes.pop(run_id, None)
