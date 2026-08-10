"""定义 LangGraph 节点之间共享的数据结构。

LangGraph 的 State 可以理解为一份由所有节点共同读写的字典。节点不需要
原地修改它，而是返回本次产生的增量；运行时负责把增量合并到最新 State。
"""

import operator
from typing import Annotated, Literal, NotRequired, TypedDict


class EmailClassification(TypedDict):
    """限制大模型结构化输出的字段和可选值。"""

    # Literal 不只是类型提示，也会进入模型看到的 JSON Schema。
    intent: Literal["question", "bug", "billing", "feature", "complex", "travel"]
    urgency: Literal["low", "medium", "high", "critical"]
    topic: str
    summary: str


class TravelResearchTask(TypedDict):
    """Orchestrator 根据本次旅游邮件动态生成的一项调研任务。"""

    task_id: str
    category: Literal["transport", "accommodation", "activities", "budget", "visa"]
    query: str


class TravelResearchPlan(TypedDict):
    """包装动态任务列表，供大模型输出结构化规划。"""

    tasks: list[TravelResearchTask]


class TravelResearchResult(TypedDict):
    """单个旅游调研 Worker 的输出。"""

    task_id: str
    category: str
    content: str


class EmailAgentState(TypedDict):
    """邮件智能体在所有节点之间共享的原始状态。"""

    # 请求进入图时必须提供的字段。未使用 NotRequired，类型检查器会把它们
    # 视为必填字段。
    email_content: str
    sender_email: str
    email_id: str

    # 后续节点才会生成的字段使用 NotRequired；流程刚启动时可以不存在。
    classification: NotRequired[EmailClassification | None]

    # 检索和外部接口返回的原始数据
    search_results: NotRequired[list[str] | None]  # 知识库返回的原始文档片段
    customer_history: NotRequired[dict | None]  # 客户关系管理系统返回的原始资料

    # 旅游邮件才会产生这些字段。多个动态 Worker 会并行写入 results，因此用
    # reducer 将列表追加合并，避免并行更新互相覆盖。
    travel_research_tasks: NotRequired[list[TravelResearchTask]]
    travel_research_results: Annotated[list[TravelResearchResult], operator.add]
    travel_itinerary: NotRequired[str | None]

    # messages 在这里是学习用的流程消息，并不是自动传给大模型的聊天历史。
    # 只有节点显式放进 invoke(prompt) 的内容，才会发送给模型。
    draft_response: NotRequired[str | None]
    messages: NotRequired[list[str]]

    # 执行元数据和可观察的外部操作结果
    current_step: NotRequired[str]
    delivery_status: NotRequired[Literal["sent", "human_handled"]]


class TravelWorkerState(TypedDict):
    """动态 Worker 的局部状态，只包含分配给它的任务。"""

    task: TravelResearchTask
    travel_research_results: Annotated[list[TravelResearchResult], operator.add]
