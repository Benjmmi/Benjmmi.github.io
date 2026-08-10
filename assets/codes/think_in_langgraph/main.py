"""FastAPI 应用入口。

开发环境直接运行 ``uv run python main.py``；生产环境通常由独立的 ASGI
进程管理器导入本模块中的 ``app``。
"""

import uvicorn
from fastapi import FastAPI
from langchain_core.globals import set_debug

from src.http_route import R

# 使用 LangChain 官方调试开关输出 Runnable、模型调用与节点执行信息。
# 学习时保持开启便于观察最终 prompt；生产环境应通过配置关闭，避免日志中
# 出现邮件正文、客户资料或其他敏感内容。
set_debug(True)

app = FastAPI(
    title="Thinking in LangGraph - 客服邮件智能体",
    description="支持持久化人工审核的客服邮件处理流程。",
)
app.include_router(R)


@app.get("/health")
def health() -> dict[str, str]:
    """供本地检查或容器健康探针确认 HTTP 服务仍可响应。"""

    return {"status": "ok"}


if __name__ == "__main__":
    # 使用导入字符串启动，才能在代码变化时自动重载进程。
    # debug 日志会输出请求、异常以及服务重载信息，方便本地观察流程。
    uvicorn.run(
        "main:app",
        host="127.0.0.1",
        port=8000,
        reload=True,
        log_level="debug",
    )
