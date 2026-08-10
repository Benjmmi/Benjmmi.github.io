# Thinking in LangGraph：客服邮件 Agent

本项目按照 LangGraph 官方的 “Thinking in LangGraph” 教程实现了一个可运行的客服邮件流程：

```text
START -> read_email -> classify_intent
                         | question/feature -> search_documentation -> draft_response
                         | bug              -> bug_tracking         -> draft_response
                         | travel           -> plan_travel -> workers -> synthesize_itinerary -> draft_response
                         | complex          -> draft_response
                         | billing/critical -> human_review

draft_response -> send_reply 或 human_review
human_review   -> send_reply（批准）或 END（人工接管）
send_reply     -> END
```

## 建议阅读顺序

这是一个学习型项目，建议按下面的顺序阅读源码：

```text
src/model.py          先理解图中流转的 State
    ↓
src/work_nodes.py     理解节点、Command 路由和 interrupt
    ↓
src/workflow.py       理解节点注册、固定边和 compile
    ↓
src/mysql_saver.py    理解 checkpoint 如何持久化和恢复
    ↓
src/run_store.py      理解 checkpoint 与业务审计记录的区别
    ↓
src/http_route.py     理解首次执行、人工恢复和状态查询
```

数据库中存在两套用途不同的数据：

```text
LangGraph checkpoint 表
    用于程序恢复 State 和执行位置

agent_runs / agent_run_steps
    用于人类查询流程状态和节点执行历史
```

流程状态只保存原始邮件、结构化分类、检索结果和回复草稿；提示词在节点执行时动态生成。文档检索节点配置了重试策略，人工审核使用 `interrupt()` 和 MySQL checkpointer 实现可跨进程的暂停/恢复。

## Orchestrator–Worker 旅游分支

当 `classify_intent` 把邮件识别为 `travel` 时，原客服流程会进入新增的旅游分支：

```text
plan_travel                 Orchestrator 根据邮件生成 N 个任务
    ↓
assign_travel_workers       使用 Send 动态创建 N 个 Worker
    ├── travel_worker       交通
    ├── travel_worker       活动
    ├── travel_worker       预算
    └── ...                 数量和类别由本次邮件决定
    ↓
synthesize_itinerary       汇总全部 Worker 结果
    ↓
draft_response             回到原有邮件草拟和审核流程
```

多个 Worker 都写入 `travel_research_results`，该字段使用
`Annotated[list, operator.add]` reducer 合并并行输出。含 `visa` 任务的旅游方案
会复用原有 `human_review` 节点。其他客服邮件路径与原练习保持不变。

## 启动

要求 Python 3.11+、[uv](https://docs.astral.sh/uv/) 和 Docker。当前模型配置为 DeepSeek。

先启动本地 MySQL 8.4：

```bash
docker compose up -d mysql
docker compose ps
```

默认数据库连接为：

```text
mysql://langgraph:langgraph@127.0.0.1:3307/langgraph
```

然后启动服务：

```bash
export DEEPSEEK_API_KEY=你的密钥
uv sync
uv run python main.py
```

如需使用其他 MySQL，可通过环境变量覆盖：

```bash
export MYSQL_URI='mysql://用户名:密码@主机:3306/数据库名'
```

`main.py` 默认启用自动重载和 LangChain debug 日志。只在测试或临时调试时使用内存检查点：

```bash
CHECKPOINT_BACKEND=memory uv run python main.py
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

当前的文档检索、缺陷单创建和邮件发送是便于演示的本地占位实现；对应节点已经独立，后续可直接替换成真实知识库、工单和邮件 API。MySQL checkpointer 会在启动时自动创建以下表：

```text
langgraph_checkpoints          # checkpoint 结构
langgraph_checkpoint_blobs     # State channel 数据
langgraph_checkpoint_writes    # 中间写入
agent_runs                     # 流程当前业务状态
agent_run_steps                # 每次节点流转记录
```

查询流程当前状态和节点历史：

```bash
curl http://127.0.0.1:8000/emails/THREAD_ID/status
```

节点记录包含 `running`、`completed`、`waiting_input` 和 `failed` 四种状态。

停止容器但保留数据：

```bash
docker compose down
```

MySQL 数据存放在命名卷 `langgraph_mysql_data` 中。仅在确认不再需要历史 checkpoint 时才删除该卷。

## 测试

测试会替换掉真实 LLM，不需要 API Key：

```bash
uv run python -m unittest discover -s tests -v
```

MySQL 集成测试会创建临时 thread，使用全新的图实例恢复中断，完成后删除测试数据：

```bash
MYSQL_INTEGRATION_TEST=1 uv run python -m unittest discover -s tests -v
```
