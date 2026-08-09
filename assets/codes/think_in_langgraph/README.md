# Thinking in LangGraph：客服邮件 Agent

本项目按照 LangGraph 官方的 “Thinking in LangGraph” 教程实现了一个可运行的客服邮件流程：

```text
START -> read_email -> classify_intent
                         | question/feature -> search_documentation -> draft_response
                         | bug              -> bug_tracking         -> draft_response
                         | complex          -> draft_response
                         | billing/critical -> human_review

draft_response -> send_reply 或 human_review
human_review   -> send_reply（批准）或 END（人工接管）
send_reply     -> END
```

流程状态只保存原始邮件、结构化分类、检索结果和回复草稿；提示词在节点执行时动态生成。文档检索节点配置了重试策略，人工审核使用 `interrupt()` 和内存 checkpointer 实现暂停/恢复。

## 启动

要求 Python 3.11+ 和 [uv](https://docs.astral.sh/uv/)。当前模型配置为 DeepSeek：

```bash
export DEEPSEEK_API_KEY=你的密钥
uv sync
uv run fastapi dev
```

也可以直接运行 `main.py` 进入本地调试模式（自动重载并输出 debug 日志）：

```bash
uv run python main.py
```

接口文档打开 <http://127.0.0.1:8000/docs>，健康检查为 `GET /health`。

## 处理邮件

```bash
curl -X POST http://127.0.0.1:8000/emails/process \
  -H 'Content-Type: application/json' \
  -d '{
    "email_content": "I was charged twice for my subscription! This is urgent!",
    "sender_email": "customer@example.com",
    "email_id": "email-001"
  }'
```

普通问题会直接返回 `status: completed`。需要人工判断时返回 `status: needs_review` 和 `thread_id`。使用该 `thread_id` 恢复流程：

```bash
curl -X POST http://127.0.0.1:8000/emails/THREAD_ID/review \
  -H 'Content-Type: application/json' \
  -d '{
    "approved": true,
    "edited_response": "We apologize for the duplicate charge and will process a refund."
  }'
```

拒绝时提交 `{"approved": false}`，流程将结束并标记为人工接管。

当前的文档检索、缺陷单创建和邮件发送是便于演示的本地占位实现；对应节点已经独立，后续可直接替换成真实知识库、工单和邮件 API。`MemorySaver` 也只适合本地开发，服务重启后不会保留审核断点，生产环境应改用持久化 checkpointer。

## 测试

测试会替换掉真实 LLM，不需要 API Key：

```bash
uv run python -m unittest discover -s tests -v
```
