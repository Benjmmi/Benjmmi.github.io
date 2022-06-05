---
title: Kubernetes 调研
date: 2021-09-08 11:09:56
categories: 
	- [eBPF]
	- [Kubernetes]
	- [kube-proxy]
	- [iptable]
	- [envoy]
tags:
  - ebpf
author: Zhongya Yan
---


# kubernetes 的服务发现

k8s 默认情况下是使用 kube-proxy 来负责节点与节点之前的通信，使用 `service` 绑定到 Pod IP。
这里面有几个问题需要打通：

1. 就是关于 `kube-proxy` 之间的通信方式
2. Kuberntes `service` 绑定到 `Pod IP` 是如何进行动态发现的


用户创建 service 的时候，endpointController 会根据 service 的 selector 找到对应
的 Pod，然后将生成的 Endpoint 对象保存到 Etcd 中。kube-proxy 的主要工作就是监听 
etcd（通过 apiserver 的接口，而不是直接读取 etcd），来实时更新节点上的 iptables。

endpointController 会根据 service 的 selector 找到对应的 pod，然后生成 endpoints 对象保存到 etcd 中






