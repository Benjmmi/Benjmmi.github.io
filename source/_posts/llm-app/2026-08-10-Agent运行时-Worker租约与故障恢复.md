---
layout: post
title: "Agent 运行时：Worker、租约与故障恢复"
keywords: "AI Agent, LangGraph, Worker, Lease, Heartbeat, Checkpoint, Aegra, Durable Execution"
description: "从 LangGraph 流程运行在 HTTP 进程中的问题出发，理解 Worker、心跳、执行租约、崩溃恢复和幂等，并比较可参考的开源 Agent Runtime。"
tagline: ""
date: '2026-08-10 10:00:00'
category: llm-app
tags:
  - AI Agent
  - LangGraph
  - Worker
  - Durable Execution
  - Aegra
---
> {{ page.description }}

# 从一个没有及时更新的 running 状态说起

在上一篇 [Thinking in LangGraph 工程化实践](/2026/08/10/Thinking-in-LangGraph-工程化实践/) 中，我实现了一个支持人工审核和 MySQL checkpoint 的客服邮件 Agent。图中每个节点的执行状态会写入：

```text
agent_runs
agent_run_steps
```

正常执行时，节点生命周期很容易记录：

```text
进入节点     -> running
正常返回     -> completed
interrupt    -> waiting_input
抛出异常     -> failed
```

真正的问题出现在进程突然消失时。假设邮件发送节点刚刚开始：

```text
send_reply = running
    ↓
容器被强制重启
```

进程没有机会执行 `on_chain_end`，也没有机会进入 HTTP 接口的 `except`。数据库会一直保留：

```text
status = running
finished_at = NULL
```

系统无法仅凭这两个字段判断节点还在运行，还是执行它的进程已经死亡。

这不是 LangGraph 独有的问题。任何需要运行数分钟、数小时甚至等待人工输入数天的 Agent，只要进入真实部署环境，最终都会遇到同一个分布式系统问题：

> 执行者是临时的，任务状态必须是持久的。

# Agent Framework 和 Agent Runtime 不是一层

讨论这个问题前，首先要区分 Agent Framework 和 Agent Runtime。

```text
Agent Framework
    模型调用
    Tool Calling
    State
    节点与路由
    Prompt
    interrupt

Agent Runtime
    API Server
    持久化任务队列
    Worker
    心跳与租约
    故障恢复
    取消与超时
    流式消息
    水平扩容
```

LangGraph OSS 属于前者。它提供 State、Command、checkpoint、pending writes 和恢复接口，但不会自动部署一个后台任务队列，也不会替应用注册 Worker。

LangGraph Agent Server、Aegra、Temporal 等属于后者。它们负责决定哪个执行进程拥有任务、执行者消失后谁来接管，以及怎样将实时事件传回客户端。

所以，大多数 Agent 框架并不是“忘记解决 Worker 崩溃”，而是把这个问题留给部署运行时。

# 当前 HTTP 内执行模型的边界

当前学习项目的执行链路是：

```text
客户端
    ↓
FastAPI /emails/process
    ↓
HTTP 处理线程
    ↓
workflow_app.invoke()
    ↓
完成或 interrupt
    ↓
HTTP 返回
```

需要注意，`interrupt()` 不会让 HTTP 请求一直等待人工输入。图执行到 `interrupt()` 后会保存 checkpoint，本次 `invoke()` 返回，HTTP 可以立即响应 `needs_review`。几小时后，审核接口使用同一个 `thread_id` 和 `Command(resume=...)` 恢复流程。

HTTP Worker 被占用的是节点实际执行的时间，例如等待一次 LLM 调用、知识库查询或邮件接口，而不是整个等待人工审核的时间。

对于演示和短任务，这种模型足够简单。真实环境中的长任务更适合解耦成：

```text
客户端
    ↓
API Server
    ↓ 持久化任务
Task Queue
    ↓ 原子认领
Execution Worker
    ↓
LangGraph + Checkpointer
```

API 接口只创建任务并返回：

```http
HTTP/1.1 202 Accepted
```

```json
{
  "thread_id": "thread-001",
  "status": "queued"
}
```

客户端再通过状态接口或 SSE 获取进度。

# Worker 到底是什么

这里的 Worker 指长期运行的后台进程或容器。它不断从队列中领取任务，并调用 LangGraph：

```python
def run_worker() -> None:
    while True:
        job = claim_next_job(worker_id)

        if job is None:
            wait_for_new_job()
            continue

        execute_graph(job)
```

它不是 LangGraph 节点，也不是 Uvicorn 接收请求的 Web Worker。三个概念需要明确区分：

```text
Uvicorn Worker
    处理 HTTP 请求的 Web 进程

Queue Worker
    从任务队列领取 Run 并执行图的后台进程

LangGraph Node
    图中的一个业务步骤
```

Worker 本身不应该保存不可丢失的任务数据。进程随时可以因为发布、OOM、机器断电或网络分区而消失。新 Worker 必须能够只依赖数据库重新构造执行上下文。

# 三种状态必须分开

可靠恢复至少需要三套信息。

## Worker 状态

回答“某个执行进程还活着吗”：

```text
worker_id
status
heartbeat_at
lease_until
hostname
process_id
version
started_at
```

Worker 状态可以是：

```text
starting
ready
draining
stopped
```

但不能只看 `status=ready`。进程崩溃时无法主动把自己改成 `stopped`，最终判断必须依赖 `lease_until` 是否过期。

## Job 或 Run 状态

回答“哪一个 Worker 目前有权执行这条流程”：

```text
thread_id
status
lease_owner
lease_version
lease_until
attempt_count
next_retry_at
```

建议状态包括：

```text
queued
running
waiting_input
retry_wait
recovering
completed
failed
cancelled
```

## LangGraph checkpoint

回答“流程应该从哪里继续”：

```text
State
下一执行节点
channel version
pending writes
interrupt 信息
checkpoint 历史
```

三者的关系是：

```text
Worker heartbeat
    判断进程是否还活着

Run lease
    判断任务执行权属于谁

Checkpoint
    判断接管后从哪里继续
```

业务表中的 `current_node=send_reply` 不能替代 checkpoint，因为恢复还需要邮件正文、分类结果、草稿、channel version 和 pending writes。

# Callback 是节点生命周期的 AOP

LangGraph 没有直接使用 AOP 这个术语，但 LangChain Callback 可以承担横切逻辑：

```text
on_chain_start   -> Before Advice
on_chain_end     -> After Returning
on_chain_error   -> After Throwing
```

当前项目的 `NodeTransitionRecorder` 已经用这种方式记录节点：

```python
class NodeTransitionRecorder(BaseCallbackHandler):
    def on_chain_start(...):
        start_step(...)

    def on_chain_end(...):
        finish_step(..., "completed")

    def on_chain_error(...):
        finish_step(..., "failed")
```

其中还要特别识别 `GraphInterrupt`：它在 Callback 层表现为错误事件，但业务含义不是失败，而是 `waiting_input`。

Callback 适合记录节点开始、结束、异常和耗时，但不应该成为恢复位置的唯一依据。可能发生：

```text
节点函数已经返回
    ↓
Callback 写 completed
    ↓
checkpoint 尚未提交
    ↓
进程崩溃
```

此时业务表认为节点完成，LangGraph 却必须重新执行它。恢复时仍应以 `get_state()` 返回的 checkpoint 为准。

# `runtime.heartbeat()` 不是 Worker 心跳

LangGraph 的 `runtime.heartbeat()` 容易和分布式 Worker 心跳混淆。

它的作用是刷新当前节点的 `idle_timeout`：

```text
runtime.heartbeat()
    ↓
LangGraph Timeout Controller
    ↓
说明当前节点仍有进展
```

它不会自动：

- 更新 Worker 注册表；
- 延长数据库任务租约；
- 写入 checkpoint；
- 上报 Prometheus；
- 调用 HTTP 健康检查；
- 触发故障恢复。

两种心跳的语义不同：

| 心跳 | 回答的问题 |
|---|---|
| `runtime.heartbeat()` | 节点是否仍在产生进展 |
| Worker heartbeat | 执行进程是否还活着 |
| Run lease heartbeat | 这个进程是否仍拥有任务执行权 |

不建议每个节点分别创建一个心跳线程。并行节点会产生大量线程、数据库连接和相互覆盖的流程状态。更合理的方式是：

```text
Callback
    注册当前活跃 Run 和 Step

每个 Worker 一个 Heartbeat Scheduler
    每 10 秒批量续租所有活跃 Run
```

对于长时间没有自然进度事件的节点，再显式调用 `runtime.heartbeat()` 处理 idle timeout。

# 租约如何判断 Worker 已经死亡

Worker 领取任务时写入：

```text
lease_owner = worker-03
lease_version = 7
lease_until = 2026-08-10 15:01:00
```

执行期间定期续租：

```sql
UPDATE agent_jobs
SET heartbeat_at = CURRENT_TIMESTAMP(6),
    lease_until = DATE_ADD(
        CURRENT_TIMESTAMP(6),
        INTERVAL 30 SECOND
    )
WHERE id = ?
  AND lease_owner = ?
  AND lease_version = ?
  AND status = 'running';
```

如果 Worker 崩溃，心跳停止，租约自然过期。Recovery Scanner 查询：

```sql
SELECT id, thread_id
FROM agent_jobs
WHERE status = 'running'
  AND lease_until < CURRENT_TIMESTAMP(6);
```

注意应使用数据库时间，而不是不同机器上的本地时间，避免时钟偏差造成误判。

# 为什么还需要 fencing token

只使用 `lease_owner` 仍然不能完全阻止重复执行。考虑网络分区：

```text
worker-A 暂时失联
    ↓
租约过期
    ↓
worker-B 接管
    ↓
worker-A 网络恢复
```

如果旧 Worker 不知道自己的租约已经失效，A 和 B 可能同时更新状态。

解决方法是每次认领任务都递增 `lease_version`：

```text
worker-A 持有 version=6
worker-B 接管后 version=7
```

后续所有续租和状态更新都要求版本匹配：

```sql
WHERE lease_owner = ?
  AND lease_version = ?
```

旧 Worker 使用 version 6 更新时，影响行数为 0，必须立即终止本次执行。这种单调递增的版本号就是 fencing token。

# Worker 崩溃后如何恢复图

新 Worker 原子接管过期任务后，使用同一个 `thread_id` 读取 checkpoint：

```python
config = {
    "configurable": {"thread_id": job.thread_id},
    "metadata": {"thread_id": job.thread_id},
}

snapshot = workflow_app.get_state(config)
```

随后区分四种情况：

```python
if snapshot.interrupts:
    # 正在等待人工输入，不能自动继续
    mark_waiting_input(job)

elif snapshot.next:
    # 从最后成功 checkpoint 继续
    workflow_app.invoke(
        None,
        config,
        durability="sync",
    )

elif snapshot.created_at is None:
    # 第一个 checkpoint 还没建立，使用持久化输入重新开始
    workflow_app.invoke(
        job.payload,
        config,
        durability="sync",
    )

else:
    # 图已经完成，只是业务状态没及时更新
    mark_completed(job)
```

人工暂停必须等待真实审核结果：

```python
workflow_app.invoke(
    Command(resume=review_payload),
    config,
)
```

不能把 `waiting_input` 当作 Worker 崩溃后自动执行，否则没有数据可以成为 `interrupt()` 的返回值。

# 不同崩溃位置会发生什么

| 崩溃位置 | 恢复方式 |
|---|---|
| 任务认领前 | 任务仍是 queued，其他 Worker 正常领取 |
| 认领后、首个 checkpoint 前 | 租约过期后使用持久化 payload 重启 |
| 节点执行中 | 从上一个 checkpoint 重跑当前节点 |
| 节点写入已持久化 | 复用 pending writes 或进入下一节点 |
| interrupt 后 | 等待人工 `Command(resume=...)` |
| 流程完成但状态未更新 | 根据 checkpoint 校正为 completed |

这也是为什么任务的初始 payload 必须先写入持久化队列。如果它只存在 API Server 内存里，Worker 在第一个 checkpoint 前崩溃，系统连重新开始所需的输入都找不到。

# 心跳和 checkpoint 仍然解决不了重复副作用

最危险的情况是：

```text
邮件服务已经发送成功
    ↓
Worker 在保存节点结果前崩溃
    ↓
新 Worker 从 checkpoint 重跑 send_reply
    ↓
客户收到第二封邮件
```

Checkpoint 只能证明本地记录到了哪里，无法知道远端邮件服务是否已经完成请求。可靠系统通常只能做到任务至少执行一次，因此外部动作必须设计为幂等。

可以为动作生成稳定的幂等键：

```python
action_key = f"{thread_id}:send_reply"
```

或者增加 Outbox：

```text
LangGraph 节点
    ↓ 数据库事务
写入 agent_actions / outbox
    ↓
节点 checkpoint
    ↓
独立 Dispatcher
    ↓
按 action_key 幂等发送邮件
```

所以完整可靠性由四层组成：

```text
Task Queue
    保证任务不丢

Heartbeat + Lease
    判断执行者是否失效

Checkpoint
    决定从哪里恢复

Idempotency + Outbox
    防止外部动作重复
```

# 只看开源实现

官方 LangGraph Agent Server 核心运行时不是开源项目。如果希望阅读完整实现，可以关注下面这些开源项目。

## Aegra

[Aegra](https://github.com/aegra/aegra) 使用 Apache 2.0 许可证，是目前最接近 LangGraph Agent Server 的开源实现。

它采用：

```text
FastAPI
PostgreSQL
Redis Job Queue
Asyncio Worker
Lease + Heartbeat
Lease Reaper
LangGraph Checkpointer
SSE Pub/Sub
```

生产模式中，API 先把执行参数保存到 PostgreSQL，再把 Run ID 推入 Redis。Worker 使用 `BLPOP` 获取任务，原子获取 PostgreSQL 租约，每 10 秒续租。Lease Reaper 定期查找过期租约，将崩溃任务重新入队，新 Worker 从最后 checkpoint 恢复。[Aegra Worker Architecture](https://docs.aegra.dev/guides/worker-architecture)

## DuraGraph

[DuraGraph](https://github.com/Duragraph/duragraph) 使用 Apache 2.0 许可证，采用 Go、PostgreSQL、NATS、Event Sourcing 和 CQRS。它拥有自己的 Python/Go Worker SDK，不是 LangGraph Agent Server 的直接兼容实现，适合学习事件溯源和可回放运行时。

## Temporal

[Temporal](https://github.com/temporalio/temporal) 使用 MIT 许可证，是成熟的通用 Durable Execution 平台。它提供 Worker、任务队列、心跳、重试和 Workflow History，但需要自行设计 LangGraph 的集成边界。

## DBOS

[DBOS Transact Python](https://github.com/dbos-inc/dbos-transact-py) 使用 MIT 许可证，基于 PostgreSQL 提供 Durable Workflow 和 Queue。它比 Temporal 轻量，不需要单独的消息代理，适合 Python 应用逐步引入持久化任务执行。

## Hatchet

[Hatchet](https://github.com/hatchet-dev/hatchet) 使用 MIT 许可证，面向后台任务、AI Agent 和 Durable Workflow，提供队列、Worker、重试、调度、监控及自托管能力。

## Celery

[Celery](https://github.com/celery/celery) 使用 BSD 许可证，是成熟的 Python 分布式任务队列。它能解决任务分发和 Worker 重试，但不了解 LangGraph State 和 checkpoint，恢复逻辑仍需应用实现。

# Aegra 与当前项目是否兼容

Aegra 可以加载 LangGraph 的静态编译图或 Graph Factory，并兼容 `langgraph_sdk`、Agent Protocol、Studio、interrupt、streaming 和 PostgreSQL checkpoint。它还会回退读取现有 `langgraph.json`。[Aegra Configuration](https://docs.aegra.dev/reference/configuration)

当前项目的大部分图代码可以保留：

```text
EmailAgentState
节点函数
Command(update/goto)
interrupt()
RetryPolicy
DeepSeek 调用
中文 Prompt
```

但运行层需要替换：

```text
当前项目                   Aegra
-------------------------------------------------
自定义 FastAPI             Aegra Agent API
MySQLSaver                 PostgreSQL Checkpointer
MySQLRunStore              Aegra Thread / Run
直接 workflow.invoke       Redis Queue + Worker
自定义节点 Callback         Run 状态与 OpenTelemetry
```

最大差异是当前项目使用 MySQL，而 Aegra 使用 PostgreSQL + Redis。如果必须保留 MySQL，更适合参考 Aegra 的 Worker、租约和 Lease Reaper 设计，在当前项目中实现一个简化版本；如果可以接受 PostgreSQL + Redis，则可以直接尝试迁移图代码。

Aegra 是独立社区实现，不保证与官方 Agent Server 的每一个扩展功能同步。更准确的兼容描述是：

```text
LangGraph 图代码                 高度兼容
Agent Protocol 核心 API          兼容
官方 Agent Server 所有扩展        不保证完全一致
MySQL 数据和表结构                不兼容
```

# 最后的理解

Agent 进入生产环境后，本质上不再只是一次模型调用，而是一条可能持续很久的有状态任务。模型可能超时，节点可能失败，Worker 可能消失，部署可能发生在执行中，人工输入可能几天后才到来。

LangGraph checkpoint 解决了“图怎样继续”，但还需要 Agent Runtime 解决“谁来继续、什么时候继续”。Worker 不是可靠性的来源，它只是随时可以被替换的执行者。真正可靠的是持久化任务、执行租约、checkpoint 和幂等动作共同构成的协议。

可以把整个系统浓缩为一句话：

```text
不要尝试恢复 Worker 进程，
要让新的 Worker 恢复它尚未完成的任务。
```

# 参考资料

- [LangGraph OSS](https://github.com/langchain-ai/langgraph)
- [LangGraph Persistence](https://docs.langchain.com/oss/python/langgraph/persistence)
- [LangGraph Fault Tolerance](https://docs.langchain.com/oss/python/langgraph/fault-tolerance)
- [LangGraph Agent Server](https://docs.langchain.com/langsmith/agent-server)
- [Agent Protocol](https://github.com/langchain-ai/agent-protocol)
- [Aegra](https://github.com/aegra/aegra)
- [Aegra Worker Architecture](https://docs.aegra.dev/guides/worker-architecture)
- [DuraGraph](https://github.com/Duragraph/duragraph)
- [Temporal](https://github.com/temporalio/temporal)
- [DBOS Transact Python](https://github.com/dbos-inc/dbos-transact-py)
- [Hatchet](https://github.com/hatchet-dev/hatchet)
- [Celery](https://github.com/celery/celery)
