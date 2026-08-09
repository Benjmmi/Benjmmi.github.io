from typing import Literal, NotRequired, TypedDict


class EmailClassification(TypedDict):
    """客户邮件的意图、紧急程度、主题和摘要。"""

    intent: Literal["question", "bug", "billing", "feature", "complex"]
    urgency: Literal["low", "medium", "high", "critical"]
    topic: str
    summary: str


class EmailAgentState(TypedDict):
    """邮件智能体在所有节点之间共享的原始状态。"""

    # 原始邮件数据
    email_content: str
    sender_email: str
    email_id: str

    # 结构化分类结果
    classification: NotRequired[EmailClassification | None]

    # 检索和外部接口返回的原始数据
    search_results: NotRequired[list[str] | None]  # 知识库返回的原始文档片段
    customer_history: NotRequired[dict | None]  # 客户关系管理系统返回的原始资料

    # 模型生成的内容及流程消息
    draft_response: NotRequired[str | None]
    messages: NotRequired[list[str]]

    # 执行元数据和可观察的外部操作结果
    current_step: NotRequired[str]
    delivery_status: NotRequired[Literal["sent", "human_handled"]]
