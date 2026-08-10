"""邮件处理图中的节点函数。

节点接收当前 State，并返回普通字典或 ``Command``：

* 普通字典只更新 State，下一步由 workflow 中声明的固定边决定；
* ``Command(update=..., goto=...)`` 同时更新 State 并动态选择下一节点。

节点尽量保持单一职责，既便于观察流程，也能缩小失败重试的范围。
"""

from functools import lru_cache
from typing import Literal

from langchain.chat_models import init_chat_model
from langgraph.constants import END
from langgraph.types import interrupt, Command, Send

from src.model import (
    EmailAgentState,
    EmailClassification,
    TravelResearchPlan,
    TravelWorkerState,
)


@lru_cache(maxsize=1)
def get_llm():
    """延迟创建并复用模型，避免导入 API 模块时立即检查凭据。"""

    # lru_cache 使一个 Python 进程只创建一个模型客户端；它不会缓存模型回复。
    return init_chat_model("deepseek-chat", model_provider="deepseek")


def read_email(state: EmailAgentState) -> dict:
    """读取邮件内容并记录当前处理消息。"""

    # 返回值是 State 增量，而不是一份完整的新 State。
    return {
        "messages": [f"正在处理邮件：{state['email_content']}"],
        "current_step": "email_read",
    }


def classify_intent(state: EmailAgentState) -> Command[
    Literal[
        "search_documentation",
        "human_review",
        "draft_response",
        "bug_tracking",
        "plan_travel",
    ]]:
    """使用大模型分类邮件，并根据意图和紧急程度选择后续节点。"""
    llm = get_llm()
    # with_structured_output 会根据 EmailClassification 生成 JSON Schema。
    # DeepSeek 的兼容接口通常通过一次 tool_call 返回参数，LangChain 再把参数
    # 解析成 EmailClassification；这里并没有真的执行一个外部工具。
    struct_llm = llm.with_structured_output(EmailClassification)
    classification_prompt = f"""
    请分析下面的客户邮件，并对邮件进行分类。

    邮件内容：{state['email_content']}
    发件人：{state['sender_email']}

    请返回以下分类信息：
    - intent：客户意图
    - urgency：紧急程度
    - topic：邮件主题
    - summary：邮件内容摘要
    """

    # 只有 classification_prompt 和结构化输出 Schema 会发送给模型；State 中
    # 其他字段不会因为调用 invoke 而自动进入上下文。
    classification = struct_llm.invoke(classification_prompt)

    if classification['intent'] == 'billing' or classification['urgency'] == 'critical':
        goto = "human_review"
    elif classification['intent'] == 'travel':
        goto = "plan_travel"
    elif classification['intent'] in ['question', 'feature']:
        goto = "search_documentation"
    elif classification['intent'] == 'bug':
        goto = "bug_tracking"
    else:
        goto = "draft_response"

    # update 会合并进 State，goto 则覆盖静态边并决定下一节点。
    return Command(
        update={"classification": classification, "current_step": "classified"},
        goto=goto
    )


def plan_travel(state: EmailAgentState) -> dict:
    """Orchestrator 根据旅游邮件动态规划本次需要的调研任务。"""

    planner = get_llm().with_structured_output(TravelResearchPlan)
    plan = planner.invoke(
        f"""
        你是旅行规划协调者。请根据邮件动态拆分必要且可独立执行的调研任务。

        邮件：{state['email_content']}

        category 只能是 transport、accommodation、activities、budget、visa。
        任务数量由邮件实际需求决定，每个 task_id 必须唯一；不要为了凑数创建任务。
        """
    )
    if not plan["tasks"]:
        raise ValueError("没有生成旅游调研任务")
    return {
        "travel_research_tasks": plan["tasks"],
        "current_step": "travel_planned",
    }


def assign_travel_workers(state: EmailAgentState) -> list[Send]:
    """使用 Send 为 Orchestrator 运行时生成的每项任务创建 Worker。"""

    tasks = state["travel_research_tasks"]
    print(f"即将执行 {len(tasks)} 个旅游调研任务：")
    for task in tasks:
        print(
            f"- [{task['task_id']}] "
            f"category={task['category']} query={task['query']}"
        )

    return [
        Send("travel_worker", {"task": task})
        for task in tasks
    ]


def travel_worker(state: TravelWorkerState) -> dict:
    """独立完成一项旅游调研任务。"""

    task = state["task"]
    response = get_llm().invoke(
        f"""
        你是旅游调研 Worker，只处理下面这一项任务：
        类别：{task['category']}
        查询：{task['query']}

        返回简洁、可操作的结论。价格、班次、开放时间和签证政策等时效信息，
        必须提醒用户在预订或出发前通过官方渠道确认。
        """
    )
    return {
        "travel_research_results": [
            {
                "task_id": task["task_id"],
                "category": task["category"],
                "content": response.content,
            }
        ]
    }


def synthesize_itinerary(state: EmailAgentState) -> Command[Literal["draft_response"]]:
    """汇总所有 Worker 输出，形成一份一致的旅行方案。"""

    response = get_llm().invoke(
        f"""
        请把下面各旅游调研 Worker 的结果合成为一份中文旅行方案。
        需要解决建议之间的冲突，不要只是逐项拼接，也不要声称已经完成预订。

        原始邮件：{state['email_content']}
        调研结果：{state['travel_research_results']}
        """
    )
    return Command(
        update={
            "travel_itinerary": response.content,
            "current_step": "travel_synthesized",
        },
        goto="draft_response",
    )


def search_documentation(state: EmailAgentState) -> Command[Literal["draft_response"]]:
    """根据分类信息检索相关知识库内容。"""

    # 使用结构化分类结果构造检索条件。
    classification = state.get('classification', {})
    query = f"{classification.get('intent', '')} {classification.get('topic', '')}".strip()

    try:
        # 此处为演示数据，接入真实知识库时在这里替换检索逻辑。
        # 状态中保存原始检索结果，供下游节点按需格式化。
        search_results = [
            f"知识库匹配条件：{query}",
            "可以通过“设置 > 安全 > 修改密码”重置密码",
            "密码长度必须至少为 12 个字符",
            "密码应包含大写字母、小写字母、数字和符号"
        ]
    except IOError as e:
        # 可恢复的检索错误写入状态，让流程继续生成回复。
        search_results = [f"知识库暂时不可用：{str(e)}"]

    return Command(
        update={"search_results": search_results, "current_step": "documentation_searched"},
        goto="draft_response"
    )


def bug_tracking(state: EmailAgentState) -> Command[Literal["draft_response"]]:
    """创建或更新缺陷跟踪工单。"""

    # 此处为演示工单号，接入真实系统后由工单 API 返回。
    ticket_id = "BUG-12345"

    return Command(
        update={
            "search_results": [f"已创建缺陷工单 {ticket_id}"],
            "current_step": "bug_tracked"
        },
        goto="draft_response"
    )


def draft_response(state: EmailAgentState) -> Command[Literal["human_review", "send_reply"]]:
    """结合已有上下文生成回复，并判断是否需要人工审核。"""

    classification = state.get('classification', {})

    # 使用状态中的原始数据按需组装提示上下文。
    context_sections = []

    if state.get('search_results'):
        # 将原始检索结果格式化为提示词中的资料列表。
        formatted_docs = "\n".join([f"- {doc}" for doc in state['search_results']])
        context_sections.append(f"相关资料：\n{formatted_docs}")

    if state.get('customer_history'):
        # 将需要的客户资料加入提示词，不在状态中保存格式化文本。
        context_sections.append(f"客户等级：{state['customer_history'].get('tier', '标准')}")

    if state.get('travel_itinerary'):
        context_sections.append(f"旅行方案：\n{state['travel_itinerary']}")

    # 在节点内部动态构造回复提示词。
    draft_prompt = f"""
    请为下面的客户邮件起草一封中文回复：
    {state['email_content']}

    客户意图：{classification.get('intent', '未知')}
    紧急程度：{classification.get('urgency', '中等')}

    {chr(10).join(context_sections)}

    回复要求：
    - 使用专业、友善的中文
    - 直接回应客户的具体问题
    - 在适用时使用上面提供的资料
    - 不要编造资料中不存在的操作结果或承诺
    """

    response = get_llm().invoke(draft_prompt)

    # 高紧急程度或复杂问题必须进入人工审核。
    needs_review = (
            classification.get('urgency') in ['high', 'critical'] or
            classification.get('intent') == 'complex' or
            any(
                task.get('category') == 'visa'
                for task in state.get('travel_research_tasks', [])
            )
    )

    # 根据审核要求选择下一个节点。
    goto = "human_review" if needs_review else "send_reply"

    return Command(
        update={"draft_response": response.content, "current_step": "response_drafted"},
        goto=goto
    )


def human_review(state: EmailAgentState) -> Command[Literal["send_reply", END]]:
    """暂停流程等待人工审核，再根据审核决定继续或结束。"""

    classification = state.get('classification', {})

    # interrupt() 会把参数写入 checkpoint 并让本次 invoke 返回；它不是阻塞
    # 当前 HTTP 请求等待用户输入。后续审核请求用同一个 thread_id 恢复执行。
    # interrupt 应尽量放在节点开头，因为恢复时该节点会从头重新执行。
    human_decision = interrupt({
        "email_id": state.get('email_id', ''),
        "original_email": state.get('email_content', ''),
        "draft_response": state.get('draft_response', ''),
        "urgency": classification.get('urgency'),
        "intent": classification.get('intent'),
        "action": "请审核这封回复，并选择批准、编辑或交由人工处理"
    })

    # 恢复执行后，根据审核结果选择发送回复或交由人工处理。
    if human_decision.get("approved"):
        return Command(
            update={
                "draft_response": human_decision.get("edited_response") or state.get('draft_response', ''),
                "current_step": "human_approved",
            },
            goto="send_reply"
        )
    else:
        # 审核拒绝表示后续工作由人工直接处理。
        return Command(
            update={"current_step": "human_handled", "delivery_status": "human_handled"},
            goto=END,
        )


def send_reply(state: EmailAgentState) -> dict:
    """发送最终邮件回复。"""

    # 此处仅输出日志，接入真实邮件服务时替换为发送接口。
    draft = state.get("draft_response") or ""
    print(f"正在发送回复：{draft[:100]}...")
    return {"current_step": "reply_sent", "delivery_status": "sent"}
