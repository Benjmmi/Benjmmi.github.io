---
title: Kubernetes - ISTIO2
date: 2021-11-17 16:49:53
categories: 
	- [gRPC]
tags:
  - microservices
  - Kubernetes
  - k8s
author: Jony
---


# 梅开二度

***主要搞清上下文边界***

Pilot 的代码主要分为两部分:
- pilot-agent 负责数据面 Sidecar 实例的生命周期管理
- pilot-discovery 负责控制面流量管理配置及路由规则的生成和下发

pilot-discovery 的主服务，包含了三个比较重要的组件：
- Config Controller： 管理配置信息，比如路由规则、流量权重控制、故障注入。主在下发各种动作
- Service Controller：管理各种服务及其服务下具体实例，比如：k8s 的 Service 和 Pod，来
  自其他注册中心的服务和实例信息
- XdsServer：将上述信息下发至各个实例，被动发送和主动发送。因为开源热衷于各种标准化，所以有了 xDS


基础概念：

`C端`：调用方
`S端`：服务方

`VirtualService`:在 Istio 服务网格中定义路由规则，控制 `C端` 流量到 `S端` 服务上的各种路由方式






















