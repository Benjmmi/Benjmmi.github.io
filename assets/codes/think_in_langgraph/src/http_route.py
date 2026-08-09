from typing import Any
from uuid import uuid4

from fastapi import APIRouter, HTTPException
from langgraph.types import Command
from pydantic import BaseModel, Field

from src.workflow import workflow_app

# 邮件 Agent 的 HTTP 接口统一放在 /emails 路径下。
R = APIRouter(prefix="/emails", tags=["email-agent"])


class EmailRequest(BaseModel):
    """启动邮件处理流程时需要的原始数据。"""

    email_content: str = Field(min_length=1)
    sender_email: str = Field(min_length=1)
    email_id: str = Field(min_length=1)
    # 可由调用方指定，未提供时由服务生成；它也是 LangGraph checkpoint 的会话标识。
    thread_id: str | None = None
    # 可选的原始客户资料，后续节点会在生成回复时按需格式化。
    customer_history: dict[str, Any] | None = None


class ReviewRequest(BaseModel):
    """人工审核节点恢复执行时提交的决定。"""

    approved: bool
    # 审核人可以保留原草稿，也可以提交修改后的最终回复。
    edited_response: str | None = None


def _config(thread_id: str) -> dict:
    """构造 LangGraph checkpointer 要求的运行配置。"""

    return {"configurable": {"thread_id": thread_id}}


def _result(thread_id: str, state: dict) -> dict:
    """将 LangGraph 的运行状态转换成稳定的 HTTP 响应结构。"""

    # interrupt() 会将待处理的人工审核信息写入 __interrupt__。
    interrupts = state.get("__interrupt__", ())
    if interrupts:
        interrupt_value = interrupts[0].value
        return {
            "thread_id": thread_id,
            "status": "needs_review",
            "review": interrupt_value,
        }

    # 流程执行结束后返回业务状态，隐藏 LangGraph 的内部中断字段。
    return {
        "thread_id": thread_id,
        "status": "completed",
        "state": {key: value for key, value in state.items() if key != "__interrupt__"},
    }


@R.post("/process")
def process_email(request: EmailRequest) -> dict:
    """启动一个新的、可暂停恢复的邮件处理流程。"""

    # 每个流程必须有独立 thread_id，人工审核时依靠它恢复对应 checkpoint。
    thread_id = request.thread_id or str(uuid4())

    # thread_id 属于运行配置，不属于 EmailAgentState，因此不写入图状态。
    initial_state = request.model_dump(exclude={"thread_id"}, exclude_none=True)
    result = workflow_app.invoke(initial_state, _config(thread_id))
    return _result(thread_id, result)


@R.post("/{thread_id}/review")
def review_email(thread_id: str, request: ReviewRequest) -> dict:
    """提交人工审核结果，并从 human_review 节点恢复流程。"""

    # 只有确实暂停在 human_review 的流程才能恢复，避免重复审核或错误 thread_id。
    snapshot = workflow_app.get_state(_config(thread_id))
    if not snapshot.next or "human_review" not in snapshot.next:
        raise HTTPException(status_code=409, detail="当前流程未处于等待人工审核状态")

    # Command(resume=...) 会让 interrupt() 返回审核数据，并继续执行后续路由。
    result = workflow_app.invoke(
        Command(resume=request.model_dump(exclude_none=True)),
        _config(thread_id),
    )
    return _result(thread_id, result)
