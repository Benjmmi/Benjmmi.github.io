---
layout: post
title: "LangGraph Orchestrator–Worker：动态拆解任务、分派 Worker 与汇总结果"
keywords: "LangGraph, Orchestrator Worker, Send API, Reducer, Parallelization, AI Agent"
description: "以客服邮件中的旅游规划分支为例，理解 LangGraph 如何使用 Orchestrator 动态生成任务，通过 Send 分派多个 Worker，并利用 reducer 和 Synthesizer 汇总结果。"
tagline: ""
date: '2026-08-10 18:00:00'
category: llm-app
tags:
  - LangGraph
  - LangChain
  - AI Agent
  - Orchestrator Worker
---
> {{ page.description }}

# 为什么固定并行不足以说明 Orchestrator–Worker

LangGraph 官方的 [Workflows and agents](https://docs.langchain.com/oss/python/langgraph/workflows-agents) 文档介绍了两种容易混淆的模式：Parallelization 和 Orchestrator–Worker。

假设一个邮件处理系统总是查询三个固定数据源：

```text
本地文档
搜索引擎
企业知识库
```

这更适合普通并行化。任务在写代码时已经确定，无论使用线程池、异步 IO，还是三个固定图节点，本质都是让已知任务并发执行。

Orchestrator–Worker 要解决的是另一个问题：执行前并不知道子任务的数量和内容，需要先理解输入，再制定本次执行计划。

例如，用户发来一封旅游咨询邮件：

```text
请规划十月从上海出发的杭州两日游，
两人预算 3000 元，喜欢自然和美食。
```

模型可能把它拆成：

```text
交通：比较上海到杭州的往返方式
活动：根据自然和美食偏好安排两天行程
预算：检查两人 3000 元是否可行
```

如果邮件改成跨国多城市旅行，任务可能变成航班、住宿、城市间交通、活动、预算和签证。子任务列表只有在运行时读完邮件后才能确定，这才是 Orchestrator–Worker 的典型场景。

# 在原客服流程中增加一个旅游分支

没有必要为了演示一种新模式而推翻整个客服邮件流程。原图只需在分类节点增加一个 `travel` 分支：

```text
START -> read_email -> classify_intent
                         | question/feature -> search_documentation
                         | bug              -> bug_tracking
                         | billing/critical -> human_review
                         | travel           -> plan_travel
                                                ↓
                                           Worker × N
                                                ↓
                                       synthesize_itinerary
                                                ↓
                                          draft_response
```

分类节点仍然负责选择业务流程：

```python
if classification["intent"] == "billing":
    goto = "human_review"
elif classification["intent"] == "travel":
    goto = "plan_travel"
elif classification["intent"] in ["question", "feature"]:
    goto = "search_documentation"
```

旅游分支最终重新进入原有 `draft_response`，因此邮件草拟、人工审核、发送、checkpoint 和 HTTP 接口都可以复用。

# Orchestrator 输出的是任务数据

首先定义单项任务及计划的结构：

```python
class TravelResearchTask(TypedDict):
    task_id: str
    category: Literal[
        "transport",
        "accommodation",
        "activities",
        "budget",
        "visa",
    ]
    query: str


class TravelResearchPlan(TypedDict):
    tasks: list[TravelResearchTask]
```

`plan_travel` 是 Orchestrator。它不是直接写完整行程，而是生成一组可独立执行的任务：

```python
def plan_travel(state: EmailAgentState) -> dict:
    planner = get_llm().with_structured_output(TravelResearchPlan)
    plan = planner.invoke(
        f"""
        你是旅行规划协调者。请根据邮件动态拆分必要且可独立执行的调研任务。

        邮件：{state['email_content']}

        category 只能是 transport、accommodation、activities、budget、visa。
        任务数量由邮件实际需求决定，每个 task_id 必须唯一。
        """
    )
    return {"travel_research_tasks": plan["tasks"]}
```

这里的关键不是让模型输出越多任务越好，而是得到一组边界明确、可以独立处理、最终能够汇总的任务。

# `Send` 如何动态创建 Worker

普通条件边通常返回下一个节点名。Orchestrator–Worker 使用 `Send`，为同一个 Worker 节点发送多份不同输入：

```python
def assign_travel_workers(state: EmailAgentState) -> list[Send]:
    tasks = state["travel_research_tasks"]

    return [
        Send("travel_worker", {"task": task})
        for task in tasks
    ]
```

图中将它注册为动态条件边：

```python
workflow.add_conditional_edges(
    "plan_travel",
    assign_travel_workers,
    ["travel_worker"],
)
workflow.add_edge("travel_worker", "synthesize_itinerary")
```

假设计划包含三个任务，返回值在概念上等价于：

```python
[
    Send("travel_worker", {"task": transport_task}),
    Send("travel_worker", {"task": activities_task}),
    Send("travel_worker", {"task": budget_task}),
]
```

需要注意：图结构中仍然只有一个 `travel_worker` 节点。它是一份 Worker 模板，不会因为本次创建三个任务就在流程图上复制成三个节点。运行时会使用三份独立输入执行同一个节点三次，因此应该从运行日志和 State 结果判断实际执行次数。

为了便于观察，可以在分派前打印任务：

```python
print(f"即将执行 {len(tasks)} 个旅游调研任务：")
for task in tasks:
    print(
        f"- [{task['task_id']}] "
        f"category={task['category']} query={task['query']}"
    )
```

如果日志只显示一个任务，通常不是 `Send` 失效，而是 Orchestrator 的 `tasks` 本来就只有一项。可以调整规划提示词，要求每项任务只处理一个 category，避免模型把交通、活动和预算合并成一个综合任务。

# 每个 Worker 使用独立的局部状态

Worker 不需要拿到整封邮件流程的全部 State，只接收自己的任务：

```python
class TravelWorkerState(TypedDict):
    task: TravelResearchTask
    travel_research_results: Annotated[
        list[TravelResearchResult],
        operator.add,
    ]
```

节点根据任务生成单项调研结果：

```python
def travel_worker(state: TravelWorkerState) -> dict:
    task = state["task"]
    response = get_llm().invoke(
        f"""
        你是旅游调研 Worker，只处理下面这一项任务：
        类别：{task['category']}
        查询：{task['query']}
        """
    )
    return {
        "travel_research_results": [
            {
                "task_id": task["task_id"],
                "category": task["category"],
                "content": response.content,
            }
        ]
    }
```

缩小 Worker State 有两个好处：一是减少不必要的上下文耦合，二是让任务输入在日志和 checkpoint 中更容易理解。

当前练习中的 Worker 使用 LLM 生成演示性结果。真实旅游系统还需要根据 category 调用交通、地图、酒店、天气或使领馆等可靠数据源，不能把模型自身知识当作实时价格、班次或签证政策。

# reducer 负责数据汇总

多个 Worker 都会更新 `travel_research_results`。如果 State 没有定义合并规则，并行写入同一个 key 会发生冲突。这里使用 `operator.add` 作为 reducer：

```python
import operator
from typing import Annotated


class EmailAgentState(TypedDict):
    travel_research_results: Annotated[
        list[TravelResearchResult],
        operator.add,
    ]
```

三个 Worker 分别返回：

```text
Worker 1 -> [交通结果]
Worker 2 -> [活动结果]
Worker 3 -> [预算结果]
```

LangGraph 使用 reducer 合并为：

```text
travel_research_results = [
    交通结果,
    活动结果,
    预算结果,
]
```

这是数据层面的汇总，只保证结果都被保留下来，并不负责理解结果之间的关系。

# Synthesizer 负责内容汇总

所有动态 Worker 完成后，图进入 `synthesize_itinerary`：

```python
def synthesize_itinerary(state: EmailAgentState) -> Command:
    response = get_llm().invoke(
        f"""
        请把下面各旅游调研 Worker 的结果合成为一份中文旅行方案。
        需要解决建议之间的冲突，不要只是逐项拼接。

        原始邮件：{state['email_content']}
        调研结果：{state['travel_research_results']}
        """
    )
    return Command(
        update={"travel_itinerary": response.content},
        goto="draft_response",
    )
```

因此需要区分两层汇总：

```text
Reducer
    合并数据结构，确保每个 Worker 的结果都进入 State

Synthesizer
    理解所有结果、解决冲突、生成连贯的最终内容
```

只有 reducer 而没有 Synthesizer，最终得到的只是调研结果列表；只有 Synthesizer 而没有 reducer，部分并行结果可能无法进入完整输入。

# 与线程池、普通并行和 Agent 的区别

这几个概念处于不同层次：

```text
线程池或异步 IO
    解决任务如何并发执行

普通 Parallelization
    预先定义固定的并行任务

Orchestrator–Worker
    运行时决定任务内容和数量，再动态分派和汇总

Agent
    在反馈循环中继续决定下一步动作、工具和停止条件
```

Orchestrator–Worker 也可以并行执行 Worker，因此它不是多线程的替代品。它增加的是“先规划本次要做什么”这一层动态决策。

当前旅游分支的整体控制路径仍由代码规定：

```text
规划 -> 分派 -> 执行 -> 汇总 -> 回复
```

模型能决定任务列表，但不能任意改变整个流程，所以它仍然是工作流，而不是完全自主的 Agent。

# 什么时候应该使用这个模式

一个任务同时满足下面三个条件时，通常适合 Orchestrator–Worker：

1. 执行前不知道需要多少个子任务；
2. 必须理解输入后才能决定如何拆分；
3. 子任务可以相对独立地执行，最终能够汇总。

除旅游规划外，常见场景还包括：

- 分析未知数量的合同并汇总风险；
- 根据代码仓库实际用法生成框架升级任务；
- 动态规划报告章节并分别撰写；
- 根据事故现象选择需要调查的服务、日志和指标；
- 更新运行时发现的多份文档或配置文件。

如果任务数量和职责始终固定，普通并行通常更简单。如果 Worker 需要根据第一轮结果持续选择新工具、生成新任务并循环执行，则应该考虑 Agent 或带循环的 Orchestrator–Worker。

# 小结

LangGraph 中的 Orchestrator–Worker 可以归纳为四步：

```text
Orchestrator 生成结构化任务列表
    ↓
Send 为每项任务动态执行 Worker
    ↓
Reducer 将并行结果合并进 State
    ↓
Synthesizer 生成最终结果
```

这个模式真正解决的不是“怎样同时调用几次模型”，而是“怎样根据当前输入动态确定本次需要完成哪些工作”。当任务拆分本身也需要推理时，Orchestrator–Worker 才能体现出相对于固定并行的价值。

本文完整示例代码位于 [think_in_langgraph](https://github.com/benjamin-yanh/benjamin-yanh.github.io/tree/main/assets/codes/think_in_langgraph)。
