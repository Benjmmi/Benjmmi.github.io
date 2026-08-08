from fastapi import APIRouter

from src.workflow import workflow_app

R = APIRouter()


@R.get("/")
async def read_message():
    initial_state = {
        "email_content": "I was charged twice for my subscription! This is urgent!",
        "response_text": "Draft response",
    }

    config = {"configurable": {"thread_id": "customer_123"}}

    resumed = workflow_app.stream_events(initial_state, config, version="v3")

    return {"message": f"Hello World {resumed.output}"}
