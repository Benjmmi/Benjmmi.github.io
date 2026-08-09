import uvicorn
from fastapi import FastAPI
from langchain_core.globals import set_debug

from src.http_route import R

# 使用 LangChain 官方调试开关输出 Runnable、模型调用与节点执行信息。
set_debug(True)

app = FastAPI(
    title="Thinking in LangGraph - 客服邮件智能体",
    description="支持持久化人工审核的客服邮件处理流程。",
)
app.include_router(R)


@app.get("/health")
def health() -> dict[str, str]:
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
