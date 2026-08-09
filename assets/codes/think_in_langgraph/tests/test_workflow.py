import unittest
from unittest.mock import patch

from fastapi.testclient import TestClient
from langchain_core.messages import AIMessage

from main import app


class FakeStructuredModel:
    def invoke(self, prompt: str, config=None) -> dict:
        lower = prompt.lower()
        if "charged twice" in lower:
            return {
                "intent": "billing",
                "urgency": "critical",
                "topic": "duplicate charge",
                "summary": "Customer reports a duplicate charge",
            }
        return {
            "intent": "question",
            "urgency": "low",
            "topic": "password reset",
            "summary": "Customer asks how to reset a password",
        }


class FakeModel:
    def with_structured_output(self, _schema):
        return FakeStructuredModel()

    def invoke(self, _prompt: str, config=None) -> AIMessage:
        return AIMessage(content="Open Settings > Security to reset your password.")


class EmailWorkflowTests(unittest.TestCase):
    def setUp(self):
        self.client = TestClient(app)
        self.model_patch = patch("src.work_nodes.get_llm", return_value=FakeModel())
        self.model_patch.start()

    def tearDown(self):
        self.model_patch.stop()

    def test_question_is_searched_drafted_and_sent(self):
        response = self.client.post(
            "/emails/process",
            json={
                "email_content": "How do I reset my password?",
                "sender_email": "customer@example.com",
                "email_id": "question-1",
            },
        )

        self.assertEqual(response.status_code, 200)
        body = response.json()
        self.assertEqual(body["status"], "completed")
        self.assertEqual(body["state"]["delivery_status"], "sent")
        self.assertEqual(body["state"]["classification"]["intent"], "question")
        self.assertTrue(body["state"]["search_results"])

    def test_billing_pauses_and_can_be_approved(self):
        started = self.client.post(
            "/emails/process",
            json={
                "email_content": "I was charged twice for my subscription!",
                "sender_email": "customer@example.com",
                "email_id": "billing-1",
            },
        ).json()

        self.assertEqual(started["status"], "needs_review")
        self.assertEqual(started["review"]["intent"], "billing")

        resumed = self.client.post(
            f"/emails/{started['thread_id']}/review",
            json={"approved": True, "edited_response": "A refund has been initiated."},
        )

        self.assertEqual(resumed.status_code, 200)
        body = resumed.json()
        self.assertEqual(body["status"], "completed")
        self.assertEqual(body["state"]["draft_response"], "A refund has been initiated.")
        self.assertEqual(body["state"]["delivery_status"], "sent")

    def test_rejected_review_is_marked_for_human_handling(self):
        started = self.client.post(
            "/emails/process",
            json={
                "email_content": "I was charged twice for my subscription!",
                "sender_email": "customer@example.com",
                "email_id": "billing-2",
            },
        ).json()

        resumed = self.client.post(
            f"/emails/{started['thread_id']}/review",
            json={"approved": False},
        )

        self.assertEqual(resumed.status_code, 200)
        self.assertEqual(resumed.json()["state"]["delivery_status"], "human_handled")


if __name__ == "__main__":
    unittest.main()
