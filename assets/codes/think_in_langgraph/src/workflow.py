"""定义邮件工作流，并组装 checkpoint 与业务状态存储。"""

import os

from langgraph.checkpoint.memory import InMemorySaver
from langgraph.constants import START, END
from langgraph.graph import StateGraph
from langgraph.types import RetryPolicy

from src.model import EmailAgentState
from src.mysql_saver import MySQLSaver
from src.run_store import MySQLRunStore
from src.work_nodes import (
    assign_travel_workers,
    bug_tracking,
    classify_intent,
    draft_response,
    human_review,
    read_email,
    plan_travel,
    search_documentation,
    send_reply,
    synthesize_itinerary,
    travel_worker,
)

# StateGraph 只描述图的结构；compile 后才得到可以 invoke/stream 的运行时对象。
workflow = StateGraph(EmailAgentState)

# 每个节点只完成一种职责。节点越小，checkpoint 越细，失败恢复时需要重做的
# 工作越少。
workflow.add_node("read_email", read_email)
workflow.add_node("classify_intent", classify_intent)

# 知识库检索可能出现临时故障，因此配置自动重试策略。
workflow.add_node(
    "search_documentation",
    search_documentation,
    retry_policy=RetryPolicy(max_attempts=3)
)
workflow.add_node("bug_tracking", bug_tracking)
workflow.add_node("plan_travel", plan_travel)
workflow.add_node(
    "travel_worker",
    travel_worker,
    retry_policy=RetryPolicy(max_attempts=3),
)
workflow.add_node("synthesize_itinerary", synthesize_itinerary)
workflow.add_node("draft_response", draft_response)
workflow.add_node("human_review", human_review)
workflow.add_node("send_reply", send_reply)

# START/read_email/send_reply 的去向固定，所以使用 edge；分类、草拟和人工审核
# 的去向由运行结果决定，所以它们在节点内部返回 Command(goto=...)。
workflow.add_edge(START, "read_email")
workflow.add_edge("read_email", "classify_intent")
# plan_travel 运行时决定任务数量；Send 动态创建 Worker。所有 Worker 的输出
# 通过 State reducer 合并后，再统一进入 synthesizer。
workflow.add_conditional_edges(
    "plan_travel",
    assign_travel_workers,
    ["travel_worker"],
)
workflow.add_edge("travel_worker", "synthesize_itinerary")
workflow.add_edge("send_reply", END)

def create_checkpointer():
    """根据环境变量创建检查点；默认使用本地 MySQL 持久化。"""

    backend = os.getenv("CHECKPOINT_BACKEND", "mysql").lower()
    if backend == "memory":
        # 单元测试不依赖 Docker，使用进程内存即可；服务重启后数据会丢失。
        return InMemorySaver()
    if backend != "mysql":
        raise ValueError(f"不支持的 CHECKPOINT_BACKEND：{backend}")

    mysql_uri = os.getenv(
        "MYSQL_URI",
        "mysql://langgraph:langgraph@127.0.0.1:3307/langgraph",
    )
    # setup 使用 CREATE TABLE IF NOT EXISTS，可以在每次应用启动时安全调用。
    saver = MySQLSaver.from_conn_string(mysql_uri)
    saver.setup()
    return saver


def create_run_store() -> MySQLRunStore | None:
    """创建业务流程记录存储；内存测试模式不写业务记录。"""

    if os.getenv("CHECKPOINT_BACKEND", "mysql").lower() == "memory":
        # 内存模式只测试图行为，不额外维护业务审计表。
        return None
    mysql_uri = os.getenv(
        "MYSQL_URI",
        "mysql://langgraph:langgraph@127.0.0.1:3307/langgraph",
    )
    store = MySQLRunStore.from_conn_string(mysql_uri)
    store.setup()
    return store


# 两种存储同时存在：checkpointer 用于恢复图，run_store 用于查询业务状态。
checkpointer = create_checkpointer()
run_store = create_run_store()
# compile 会把 checkpointer 注入 LangGraph 运行时；之后每个 super-step 都会
# 通过 BaseCheckpointSaver 接口持久化。
workflow_app = workflow.compile(checkpointer=checkpointer)
