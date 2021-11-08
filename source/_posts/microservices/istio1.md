---
title: Kubernetes - ISTIO
date: 2021-10-28 19:17:35
categories: 
	- [gRPC]
tags:
  - microservices
  - Kubernetes
  - k8s
author: Jony
---


# 理解

服务网格采用 sidecar 的运行模式，将 client-server 连接起来，形成格子的效果。
而这个 `sidecar` 系统就是 `envoy`。至于 `envoy` 需要如何运行就需要 `istio`
之类的系统。
所以服务网格大致分为两个部分组成执行和命令下发，即：数据面（Envoy）和控制面板（Istio）
个人总结：istio 应该数据面向 API 编程，严格按照数据面API 的方式编程，包括启动、
更新、命令下发。

istio-pliot：监督资源的变化、VirtualService 修改、DestinationRule修改，翻译为对外协议。
资源包括：service、endpoint、pod、node

Pilot支持从Kubernetes、Consul等多种平台获取服务发现功能
用户通过VirtualService、DestinationRule等API制定服务间的流量治理规则

Pilot将发现的服务以及用户定义的服务间的调用规则进行融合并与底层Proxy的API进行适配后将规则下发

istio 分为三层
上层 istio 调用 kuberntes 来发现服务之间的关系，名称：Platform Adapter 层
中层 抽象模型，将获取的数据转换为istio 自己的数据模型。名称：Abstrace Model 层
下层 istio 被 envoy 调用，数据通过 envoy API 暴露出去等待envoy去拉取这些规则。名称：Envoy API 层
还有对外的接口 Rules API 用于,给管理员的接口，管理员通过这个接口设定一些规则

总结：自动发现的这些Clusters和Endpoints，外加管理员设置的规则，形成了Pilot的数据模型，其实就是 istio 
定义的一系列数据结构，然后通过envoy API暴露出去，等待envoy去拉取这些规则

`PushContext`：跟踪推送状态
`aggregate`:ADS 控制器
`DiscoveryServer`:从Pilot的内部网格数据结构中获取数据。
`ConfigGenerator`:数据平面配置生成器实例。
`Environment`:为Pilot提供了一个综合环境API
`NetworksWatcher`:l3 层拓扑结构感知
`MeshHandlers`:网格和网络处理程序
`MeshConfig`:定义了Istio服务网格的设置

















