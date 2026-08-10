import os
import unittest
from typing import TypedDict
from uuid import uuid4

from langgraph.constants import END, START
from langgraph.graph import StateGraph
from langgraph.types import Command, interrupt

from src.mysql_saver import MySQLSaver
from src.run_store import MySQLRunStore, NodeTransitionRecorder


class ReviewState(TypedDict):
    draft: str
    delivery_status: str


def review(state: ReviewState):
    decision = interrupt({"draft": state["draft"], "action": "请审核"})
    if decision["approved"]:
        return Command(
            update={"draft": decision["edited_response"]},
            goto="send",
        )
    return Command(goto=END)


def send(_state: ReviewState):
    return {"delivery_status": "sent"}


def build_graph(saver: MySQLSaver):
    graph = StateGraph(ReviewState)
    graph.add_node("review", review)
    graph.add_node("send", send)
    graph.add_edge(START, "review")
    graph.add_edge("send", END)
    return graph.compile(checkpointer=saver)


@unittest.skipUnless(
    os.getenv("MYSQL_INTEGRATION_TEST") == "1",
    "设置 MYSQL_INTEGRATION_TEST=1 后运行 MySQL 集成测试",
)
class MySQLSaverIntegrationTests(unittest.TestCase):
    def test_interrupt_can_resume_with_a_new_graph_instance(self):
        uri = os.getenv(
            "MYSQL_URI",
            "mysql://langgraph:langgraph@127.0.0.1:3307/langgraph",
        )
        thread_id = f"mysql-test-{uuid4()}"

        saver_1 = MySQLSaver.from_conn_string(uri)
        saver_1.setup()
        self.addCleanup(saver_1.delete_thread, thread_id)
        run_store = MySQLRunStore.from_conn_string(uri)
        run_store.setup()
        run_store.start_run(thread_id, "test-email")
        self.addCleanup(run_store.delete_run, thread_id)
        config = {
            "configurable": {"thread_id": thread_id},
            "metadata": {"thread_id": thread_id},
            "callbacks": [NodeTransitionRecorder(run_store)],
        }
        first_graph = build_graph(saver_1)
        paused = first_graph.invoke(
            {"draft": "初始草稿", "delivery_status": ""},
            config,
        )
        self.assertEqual(paused["__interrupt__"][0].value["draft"], "初始草稿")
        paused_steps = run_store.list_steps(thread_id)
        self.assertEqual(
            [(step["node_name"], step["status"]) for step in paused_steps],
            [("review", "waiting_input")],
        )

        # 使用全新的保存器和图实例，确认状态确实来自 MySQL。
        saver_2 = MySQLSaver.from_conn_string(uri)
        second_graph = build_graph(saver_2)
        self.assertEqual(second_graph.get_state(config).next, ("review",))
        self.assertEqual(list(saver_2.list(config, limit=0)), [])
        self.assertGreaterEqual(len(list(saver_2.list(config, limit=2))), 1)

        resumed = second_graph.invoke(
            Command(
                resume={
                    "approved": True,
                    "edited_response": "审核后的草稿",
                }
            ),
            {
                "configurable": {"thread_id": thread_id},
                "metadata": {"thread_id": thread_id},
                "callbacks": [NodeTransitionRecorder(run_store)],
            },
        )
        self.assertEqual(resumed["draft"], "审核后的草稿")
        self.assertEqual(resumed["delivery_status"], "sent")
        finished_steps = run_store.list_steps(thread_id)
        self.assertEqual(
            [(step["node_name"], step["status"]) for step in finished_steps],
            [
                ("review", "waiting_input"),
                ("review", "completed"),
                ("send", "completed"),
            ],
        )
