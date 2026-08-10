---
layout: post
title: "Thinking in LangGraph 工程化实践：从流程编排到可恢复执行"
keywords: "LangGraph, LangChain, DeepSeek, Agent, Checkpoint, Human in the Loop, MySQL, FastAPI"
description: "以客服邮件 Agent 为例，记录如何把 Thinking in LangGraph 教程扩展成支持结构化输出、人工审核、MySQL 持久化和节点审计的可运行项目。"
tagline: ""
date: '2026-08-10 00:00:00'
category: llm-app
tags:
  - LangGraph
  - LangChain
  - AI Agent
  - DeepSeek
  - MySQL
---
> {{ page.description }}

# 为什么要做这个项目

第一次阅读 LangGraph 官方的 [Thinking in LangGraph](https://docs.langchain.com/oss/python/langgraph/thinking-in-langgraph) 时，很容易产生一个疑问：它是不是只展示了如何把几个函数编排成一张流程图？

如果只看 `add_node` 和 `add_edge`，确实很像普通工作流。但真正值得学习的并不是画图，而是下面这套思考方式：

1. 怎样把业务拆成可独立失败、重试和恢复的节点；
2. 哪些数据应该进入 State，哪些上下文应该在节点内临时构造；
3. LLM、数据库、外部动作和人工输入分别应该怎样处理失败；
4. 如何让一次执行暂停几小时甚至几天后，仍能从原来的位置继续；
5. 如何区分“机器恢复所需的状态”和“人类查询所需的运行记录”。

为了把这些概念真正串起来，我实现了一个客服邮件 Agent，并逐步加入 FastAPI、DeepSeek、人工审核、MySQL checkpoint 和节点流转记录。完整代码位于 [think_in_langgraph](https://github.com/benjamin-yanh/benjamin-yanh.github.io/tree/main/assets/codes/think_in_langgraph)。

# 先从业务流程开始，而不是从 Agent 开始

这个项目处理一封客户邮件，流程如下：

```text
START -> read_email -> classify_intent
                         | question/feature -> search_documentation
                         | bug              -> bug_tracking
                         | billing/critical -> human_review
                         | complex          -> draft_response

search_documentation -> draft_response
bug_tracking         -> draft_response
draft_response       -> send_reply 或 human_review
human_review         -> send_reply 或 END
send_reply           -> END
```

节点可以分为四种类型：

- LLM 节点：分类意图、生成回复；
- 数据节点：查询知识库；
- 动作节点：创建工单、发送邮件；
- 人工节点：审核高风险回复。

这种分类很重要，因为不同节点的失败处理方式不同。网络超时适合自动重试，LLM 输出错误可以把错误反馈给模型，缺少客户信息需要等待用户补充，未知异常则应该直接暴露给开发者。

节点拆分粒度也不只是代码风格问题。LangGraph 在节点边界形成 checkpoint。节点越独立，失败后需要重新执行的范围越小，也越容易看出到底是分类、检索还是发送出了问题。

# State 是流程数据，不等于模型上下文

项目使用 `TypedDict` 定义共享状态：

```python
class EmailAgentState(TypedDict):
    email_content: str
    sender_email: str
    email_id: str
    classification: NotRequired[EmailClassification | None]
    search_results: NotRequired[list[str] | None]
    customer_history: NotRequired[dict | None]
    draft_response: NotRequired[str | None]
    messages: NotRequired[list[str]]
    current_step: NotRequired[str]
```

这里最容易混淆的是三个概念：

```text
State
    图中所有节点共享、可以持久化的数据

messages
    State 中的一个普通字段，是否作为聊天历史取决于节点如何使用它

Prompt
    某个节点调用 invoke() 时真正组装并发送给模型的内容
```

把数据写进 State，并不意味着它会自动发送给模型。例如项目中的 `messages` 只是用于观察流程的文本列表。分类节点只把邮件正文和发件人拼进分类提示词，因此客户历史、检索结果和其他 State 字段不会自动增加这次模型调用的上下文。

这也解释了为什么当前项目不具备自然的多轮对话连贯性。`thread_id` 能让 LangGraph 找回同一条流程的 State，但不会自动把历史内容变成聊天消息。要实现连续对话，需要显式使用消息 State、消息 reducer，并在每次模型调用时决定携带哪些历史消息。

官方教程强调“State 保存原始数据，Prompt 按需生成”。例如知识库节点保存原始文档片段，而回复节点才把它格式化为项目符号。这样做有几个好处：

- 修改提示词格式不需要迁移历史 State；
- 同一份数据可以被不同节点以不同方式使用；
- checkpoint 中不会反复保存冗余的格式化文本；
- 调试时能区分原始数据和最终发送给模型的内容。

# 结构化输出为什么表现为 tool_call

分类节点希望模型稳定返回下面四个字段：

```python
class EmailClassification(TypedDict):
    intent: Literal["question", "bug", "billing", "feature", "complex"]
    urgency: Literal["low", "medium", "high", "critical"]
    topic: str
    summary: str
```

代码通过 `with_structured_output(EmailClassification)` 创建结构化模型：

```python
struct_llm = llm.with_structured_output(EmailClassification)
classification = struct_llm.invoke(classification_prompt)
```

在 DeepSeek 的调试日志里，最终响应可能是：

```text
content: ""
finish_reason: "tool_calls"
tool_calls:
  - name: EmailClassification
    args:
      intent: question
      urgency: medium
      topic: 密码重置
      summary: 客户询问如何重置密码
```

这不表示程序真的执行了一个外部工具。LangChain 把 `EmailClassification` 转换成 JSON Schema，并以工具定义的形式约束模型；模型把结构化字段放进 tool call 参数，LangChain 再将参数解析成 Python 字典。

为什么不只在提示词里写“请输出 JSON”？因为提示词约束是软约束，模型仍可能输出 Markdown、解释文字、缺少字段或使用错误枚举值。工具调用或供应商原生的结构化输出可以提供更稳定的解析边界。

代价是请求会增加一段 Schema 上下文。调试日志中看到的 Human Prompt 并不是完整 HTTP 请求，工具定义通常在独立的 `tools` 字段中，所以 `prompt_tokens` 会明显高于肉眼看到的提示词。Schema 越复杂，固定 token 开销越大。对于只有几个字段的分类任务，这个开销通常值得；如果有几十个字段，就应该拆分 Schema、缩短描述，或者确认模型是否支持更轻量的原生 JSON Schema。

# `interrupt()` 不是让 HTTP 一直等待

人工审核节点调用：

```python
human_decision = interrupt({
    "email_id": state["email_id"],
    "draft_response": state.get("draft_response", ""),
    "action": "请审核这封回复",
})
```

它的业务语义是“暂停并等待外部输入”，但不是让当前 Python 调用一直阻塞到用户点击批准。实际过程是：

```text
POST /emails/process
        |
        v
图执行到 human_review
        |
        v
interrupt 写入 checkpoint
        |
        v
本次 invoke 返回，HTTP 返回 needs_review

......几小时后......

POST /emails/{thread_id}/review
        |
        v
Command(resume=审核结果)
        |
        v
从 checkpoint 恢复并继续执行
```

所以等待人工输入期间，不会长期占用 HTTP Worker。HTTP Worker 只在本次 `invoke()` 正在执行节点时被占用，遇到 interrupt 后就能返回响应。

不过，当前实现仍然是在 HTTP 进程内同步执行图。一次 LLM 请求、知识库查询或邮件发送如果耗时几十秒，就会占用对应的 FastAPI 工作线程。演示和短流程可以这样做；真实环境中的长任务通常应该由 HTTP 层创建任务，再交给独立的任务 Worker 执行，HTTP 返回 `202 Accepted + thread_id`，客户端通过状态接口或事件流查询进度。

还有一个容易踩坑的点：恢复 interrupt 时，节点会从开头重新执行，而不是从 Python 函数的中间继续。因此 `interrupt()` 应尽量放在节点开头，其前面的外部副作用必须保证幂等。

# MemorySaver 能演示，但不能跨进程恢复

官方示例使用 `InMemorySaver`。它适合单元测试，因为配置简单，也不依赖外部数据库。但它有明确边界：

- 服务重启后 checkpoint 消失；
- 多个 Web 或 Worker 进程无法共享状态；
- 无法跨实例恢复人工审核流程。

因此项目实现了一个教学用的 `MySQLSaver`，对接 `BaseCheckpointSaver` 接口。它将数据拆为三类：

```text
langgraph_checkpoints
    checkpoint 结构、元数据、父 checkpoint

langgraph_checkpoint_blobs
    State 中每个 channel 对应版本的序列化值

langgraph_checkpoint_writes
    已产生但还没有合并到下一 checkpoint 的中间写入
```

`thread_id` 是恢复流程的主线索。同一个 `thread_id` 下可以存在多个 checkpoint，最新 checkpoint 表示当前执行状态，父 checkpoint 则形成历史链。新进程只要使用同一张图、同一个数据库和同一个 `thread_id`，就可以重新加载 State 和下一执行位置。

这里保存的不只是“当前节点名称”，而是恢复图所需的完整状态，包括 channel 版本、序列化 State、pending writes、执行元数据和中断信息。如果只在业务表中记录 `current_node=human_review`，程序仍然不知道之前的邮件、分类结果和草稿是什么，也无法正确恢复。

当前 MySQL Saver 是为了理解 checkpointer 接口和表结构而写的同步实现，并通过 `asyncio.to_thread` 暴露异步方法。生产环境还需要进一步评估连接池、数据库容量、过期清理、加密、版本升级兼容和高并发事务，优先考虑 LangGraph 官方维护的生产级 checkpointer。

# Checkpoint 和节点审计是两套数据

有了 checkpoint 后，为什么还要增加 `agent_runs` 和 `agent_run_steps`？

因为 checkpoint 是给 LangGraph 运行时读取的，里面包含二进制序列化数据和内部版本结构，不适合直接给运营页面或监控系统查询。

项目因此增加了两张业务表：

```text
agent_runs
    一条流程的当前状态：running、needs_review、completed、failed

agent_run_steps
    每次节点尝试：节点名、开始时间、结束时间、执行结果和异常
```

节点记录通过 LangChain Callback 完成：

```text
on_chain_start                  -> running
on_chain_end                    -> completed
on_chain_error(GraphInterrupt)  -> waiting_input
on_chain_error(其他异常)        -> failed
```

这里需要特别处理嵌套 Runnable。一个 LangGraph 节点内部可能还有 `RunnableSequence` 和 LLM 调用，它们会继承节点 metadata。如果不做筛选，同一个业务节点会被记录多次。当前实现只记录 `name == metadata["langgraph_node"]` 的最外层节点事件。

`GraphInterrupt` 在 Callback 看来属于错误事件，但在业务上不是失败，而是正常的 `waiting_input`。Callback 只观察并记录它，不能吞掉异常，否则会破坏 LangGraph 自己的暂停机制。

两套存储的关系可以概括为：

```text
checkpoint：程序能不能恢复
run/steps：人能不能看懂流程发生了什么
```

# HTTP 接口如何映射流程生命周期

项目暴露三个主要接口：

```text
POST /emails/process
    创建并执行新流程

POST /emails/{thread_id}/review
    提交人工审核结果并恢复流程

GET /emails/{thread_id}/status
    查询流程状态和节点执行历史
```

启动测试邮件：

```bash
curl -X POST http://127.0.0.1:8000/emails/process \
  -H 'Content-Type: application/json' \
  -d '{
    "email_content": "我的订阅被重复扣费了，请尽快处理！",
    "sender_email": "customer@example.com",
    "email_id": "email-001"
  }'
```

如果流程需要人工审核，响应会包含 `thread_id`：

```json
{
  "thread_id": "...",
  "status": "needs_review",
  "review": {
    "email_id": "email-001",
    "action": "请审核这封回复，并选择批准、编辑或交由人工处理"
  }
}
```

随后使用同一个 `thread_id` 恢复：

```bash
curl -X POST http://127.0.0.1:8000/emails/THREAD_ID/review \
  -H 'Content-Type: application/json' \
  -d '{
    "approved": true,
    "edited_response": "很抱歉给您带来困扰，我们正在核对这笔重复扣费。"
  }'
```

这两个 HTTP 请求可能落到不同进程甚至不同机器。只要它们访问同一个 checkpoint 数据库，就不依赖第一次执行时的进程内存。

# 如何观察真正发给模型的内容

学习阶段可以使用 LangChain 官方调试开关：

```python
from langchain_core.globals import set_debug

set_debug(True)
```

它能输出 Runnable、节点和 LLM 的输入输出，适合观察动态 Prompt、token 使用量以及结构化输出结果。项目不再维护额外的 `invoke_with_debug` 包装函数，所有节点直接调用官方 `invoke()`。

但 debug 日志展示的是 LangChain 抽象后的事件，不一定等价于供应商收到的完整 HTTP JSON。特别是结构化输出使用的 `tools`、`tool_choice` 等参数，可能不会出现在 `[llm/start]` 的 Human Prompt 文本里。需要逐字确认请求体时，应在开发环境使用 HTTP 客户端日志或受控代理进行观察。

生产环境不应该无条件开启 debug。邮件正文、客户资料、提示词和模型输出都可能进入日志，必须考虑脱敏、访问权限和保留周期。

# 当前实现解决了什么，尚未解决什么

目前已经验证：

- 不需要人工审核的流程可以完整执行；
- 高风险邮件能 interrupt，并通过后续 HTTP 请求恢复；
- 拒绝审核后可以进入人工接管状态；
- 新建图实例后仍能从 MySQL 恢复中断；
- 每次业务节点尝试都会写入运行历史；
- 单元测试使用假模型，不依赖真实 DeepSeek API Key。

但它仍然是学习项目，而不是完整生产系统。真实环境还需要补充：

- 将长耗时图执行迁移到独立 Worker；
- 邮件发送、工单创建等副作用的幂等键；
- 连接池、限流、超时、熔断和死信任务；
- checkpoint 与业务记录的归档和清理策略；
- Prompt、模型、State Schema 变更后的兼容策略；
- 多租户权限控制和敏感数据脱敏；
- 更完整的指标、Tracing 和告警。

# 最后的理解

完成这个项目后，我对 LangGraph 的理解不再是“用图编排几个 LLM 调用”。它更像一个面向有状态任务的运行时：节点负责局部工作，State 负责传递事实，Command 负责路由，checkpoint 负责恢复，interrupt 负责把人的决定纳入流程。

真正落地时还要再加一层业务运行模型。LangGraph checkpoint 回答“机器如何继续执行”，`agent_runs` 和 `agent_run_steps` 回答“系统当前发生了什么”。把这两个问题分开，才能同时获得可靠恢复和可观测性。

因此，Thinking in LangGraph 展示的不只是流程编排，而是一种把 Agent 当成长期、有状态、会失败、需要人工协作的软件系统来设计的方法。

# 参考资料

- [Thinking in LangGraph](https://docs.langchain.com/oss/python/langgraph/thinking-in-langgraph)
- [LangGraph Interrupts](https://docs.langchain.com/oss/python/langgraph/interrupts)
- [LangGraph Persistence](https://docs.langchain.com/oss/python/langgraph/persistence)
- [本项目完整代码](https://github.com/benjamin-yanh/benjamin-yanh.github.io/tree/main/assets/codes/think_in_langgraph)
