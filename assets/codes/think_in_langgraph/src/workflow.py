from langgraph.checkpoint.memory import MemorySaver
from langgraph.constants import START, END
from langgraph.graph import StateGraph
from langgraph.types import RetryPolicy

from src.model import EmailAgentState
from src.work_nodes import read_email, classify_intent, search_documentation, bug_tracking, draft_response, \
    human_review, send_reply

workflow = StateGraph(EmailAgentState)

# 注册邮件处理流程中的各个节点。
workflow.add_node("read_email", read_email)
workflow.add_node("classify_intent", classify_intent)

# 知识库检索可能出现临时故障，因此配置自动重试策略。
workflow.add_node(
    "search_documentation",
    search_documentation,
    retry_policy=RetryPolicy(max_attempts=3)
)
workflow.add_node("bug_tracking", bug_tracking)
workflow.add_node("draft_response", draft_response)
workflow.add_node("human_review", human_review)
workflow.add_node("send_reply", send_reply)

# 这里只声明固定边；其余动态路由由各节点返回的 Command 决定。
workflow.add_edge(START, "read_email")
workflow.add_edge("read_email", "classify_intent")
workflow.add_edge("send_reply", END)

# 使用内存检查点保存流程状态，使人工审核节点可以暂停和恢复。
# 生产环境应替换成数据库等持久化检查点实现。
memory = MemorySaver()
workflow_app = workflow.compile(checkpointer=memory)
