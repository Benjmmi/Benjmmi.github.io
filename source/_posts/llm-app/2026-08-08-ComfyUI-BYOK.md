---
layout: post
title: "ComfyUI 接入 Grok 与 Nano Banana：BYOK 方案调研"
keywords: "ComfyUI, BYOK, Grok, Nano Banana, Gemini, API"
description: "记录 ComfyUI 使用自有 Google Gemini、xAI Grok API Key 的可选方案、限制与安全注意事项。"
tagline: ""
date: '2026-08-08 00:00:00'
category: llm-app
tags:
  - ComfyUI
  - BYOK
  - Gemini
  - Grok
---
> {{ page.description }}

# BYOK 是什么

BYOK 是 **Bring Your Own Key**，即用户提供自己的 API Key。ComfyUI 直接调用 Google、xAI 等服务，费用由对应的开发者 API 账户结算。

需要特别区分：

- Grok、Gemini 网页或 App 会员，只适用于消费端产品。
- `XAI_API_KEY`、`GEMINI_API_KEY` 属于开发者 API，通常需要独立开通计费。
- ComfyUI 内置 Partner Nodes 默认使用 Comfy 账户和 credits，不会消耗用户自己的供应商 API 额度。

# 为什么 ComfyUI 没有内置 BYOK 页面

当前 Partner Nodes 的调用链路是：

```text
ComfyUI → Comfy 账户/credits → api.comfy.org 代理 → 模型供应商
```

官方选择统一代理，主要降低多供应商密钥、鉴权、计费和接口差异带来的配置成本。但对于已经拥有开发者账户的用户，这会增加一层账户和 credits 管理。

一个可靠的 BYOK 页面还需要解决：

- API Key 应保存在配置文件、系统钥匙串还是外部密码库。
- 工作流与 PNG 元数据导出时不能包含密钥。
- ComfyUI 开放到局域网或公网时，不能让其他用户读取或滥用密钥。
- 桌面版、源码版、便携版和云端需要保持一致。
- Google、xAI、OpenAI 等供应商使用不同的认证与接口协议。

社区已有 [Partner Nodes 支持 BYOK 的功能讨论](https://github.com/Comfy-Org/ComfyUI_frontend/discussions/7850)，但目前尚未成为官方内置功能。

# 可选开源方案

## Better-Gemini

[Better-Gemini](https://github.com/ttulttul/Better-Gemini) 是独立的 ComfyUI 自定义节点包，提供：

- `Better Gemini`：Gemini/Nano Banana 生图、图片编辑和文本生成。
- `Better Grok`：Grok 生图、图片编辑和文本生成。
- `Better OpenRouter`：通过 OpenRouter 调用图片模型。

它从环境变量读取 `GEMINI_API_KEY` 和 `XAI_API_KEY`，调用供应商官方 API。优点是实现隔离，不修改 ComfyUI 核心；不足是供应商 URL 当前写在扩展代码中，没有通用的 `base_url` 配置。

## ComfyUI_Nano_Banana

[ComfyUI_Nano_Banana](https://github.com/ru4ls/ComfyUI_Nano_Banana) 专注于 Nano Banana，支持 Gemini API 与 Vertex AI，并提供文生图、图片编辑、多图融合和多轮编辑。只使用 Google 模型时，它比多供应商节点更聚焦。

## Google GenMedia Nodes

[GoogleCloudPlatform/comfyui-google-genmedia-custom-nodes](https://github.com/GoogleCloudPlatform/comfyui-google-genmedia-custom-nodes) 位于 GoogleCloudPlatform 组织下，覆盖 Gemini、Nano Banana、Imagen、Veo 等模型。它使用 Vertex AI，需要 Google Cloud Project、区域和 Application Default Credentials，更适合已经使用 GCP 的环境。

## ComfyUI API Liberation

[ComfyUI API Liberation](https://github.com/holo-q/comfy-api-liberation) 是最接近“在 ComfyUI 里直接增加 API Keys 页面”的项目。它不替换现有 Partner Nodes，而是拦截 `/proxy/*` 请求，将调用改写到 Google、xAI 等供应商的官方地址。

优点：

- 原有 Partner Nodes 和工作流可以继续使用。
- 工具栏提供 API Keys 配置页面。
- 请求不再经过 Comfy 代理。

风险：

- 项目仍处于早期阶段，供应商覆盖度与稳定性需要自行验证。
- 它依赖 ComfyUI 内部代理路径，ComfyUI 更新后可能需要同步修改。
- 本地模式会把 Key 写进 `api_keys.json`。当前代码创建文件时没有显式设置严格权限，共享机器上不建议保存明文 Key。
- 项目支持 `pass` 密码库路径，安全要求较高时应优先使用该模式。

# 方案选择

```text
只用网页会员
└── 在 Grok/Gemini 网页生成，再导入 ComfyUI

追求简单
└── 使用官方 Partner Nodes 和 Comfy credits

同时直连 Gemini 与 Grok
└── Better-Gemini

只使用 Nano Banana
└── ComfyUI_Nano_Banana

希望保留官方 Partner Nodes
└── 试验性使用 ComfyUI API Liberation

需要任意 base_url 或自建中转
└── 开发一个小型 custom_nodes 扩展
```

# 结论

不建议为了 BYOK 长期维护完整的 ComfyUI fork。更合理的边界是 `custom_nodes`：在扩展中处理供应商认证、URL、请求转换和输出规范化，保持 ComfyUI 核心可正常升级。

理想的自有扩展只需要暴露：

```text
provider / base_url / model / prompt / image
```

API Key 应从环境变量、系统钥匙串或外部密码库读取，不应写入节点、工作流 JSON 或图片元数据。

# 参考资料

- [ComfyUI Partner Nodes](https://docs.comfy.org/tutorials/partner-nodes/overview)
- [Google Gemini API Key](https://ai.google.dev/gemini-api/docs/api-key)
- [xAI API 账户与计费说明](https://docs.x.ai/console/faq/accounts)
- [ComfyUI Frontend BYOK 讨论](https://github.com/Comfy-Org/ComfyUI_frontend/discussions/7850)
