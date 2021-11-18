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
`G端`：中间件，可以理解为 `Nginx`

`VirtualService`: 定义了对特定目标服务的一组规则，一种抽象出来的虚拟服务，可以理解为 `G` 端
`DestinationRule`: 定义了对特定目标服务的细分规则，负载均衡策略、连接池大小、异常实例驱逐规则划分不同的子集

`VirtualService` 定义了服务请求满足什么条件应该转发到哪里，而`DestinationRule`则定义了这些请求
具体的路由规则,比如请求目标服务的什么版本, 负载均衡策略，连接限制等.
`VirtualService` 像是代替做了服务发现的操作，只做服务发现和发起调用， `DestinationRule` 来控制调用流量
的具体指向，调和断路器、限流器、分流器等中间件。

**Example:**

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: service-b
spec:
  hosts:
  - service-b  #客户端访问服务的地址，扩展为 service-b.default.svc.cluster.local
  http:
  - route:
    - destination:         # 目标服务，这里不一定是service-b服务，也就是说, 虽然客户端访问service-b，但不一定就需要转到service-b，转发到其它服务也是支持的.
        host: service-b      #扩展为 service-b.default.svc.cluster.local
        subset: v1           #subset 会在destinationRule中使用
---
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: service-b
spec:
  host: service-b  #这里的名字需要跟virtualservice中定义的一致
  trafficPolicy:
    loadBalancer:
      simple: RANDOM
  subsets:
  - name: v1 #这里的名字需要跟virtualservice中定义的一致
    labels:
      version: v1
  - name: v2
    labels:
      version: v2        
```



`ServiceEntry`: 可以看作是一个网格外部的 `VirtualService`。
描述服务的属性: DNS 名称、VIP、端口、协议以及端点

**EXAMPLE**
```yaml
apiVersion: networking.istio.io/v1alpha3
kind: ServiceEntry
metadata:
  name: svc-entry
spec:
  hosts:
  - ext-svc.example.com   #外部服务，与匹配的VirtualServices和DestinationRules中的hosts字段相同。将https://ext-svc.example.com:443认为是外部服务
  ports:                  #外部服务对应的端口
  - number: 443
    name: https
    protocol: HTTPS
  location: MESH_EXTERNAL #flag，表示该服务是网格外的服务
  resolution: DNS         #主机服务的发现模型
```

`WorkloadEntry`: 可以看做是一个网格外部的 `Pod` 实例。网格外部服务使用 `ServiceEntry` 已经可以提供服务
了，为什么还需要 `WorkloadEntry`。因为 `ServiceEntry` 只能是作为 `Service` 的存在，无法对后端服务的抽象
就像 `Pod` 的属性集：名称、标签、安全属性、生命周期状态事件等，这些可以通过 `WorkloadEntry` 来描述。

官方解释：服务迁移

**EXAMPLE**

设想 `ServiceEntry` 下有几十个实例在提供服务：

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: ServiceEntry
metadata:
  name: svc1
spec:
  hosts:
  - svc1.internal.com
  ports:
  - number: 80
    name: http
    protocol: HTTP
  resolution: STATIC
  endpoints:
  - address: 1.1.1.1
  - address: 2.2.2.2
  ...
```

这种方式的问题:

- 当工作负载非常多的情况下，ServiceEntry变得很大，更新极易出错，同时很大的资源对象更新必将带来额外的开销。
- 同时，不支持一个服务的容器和虚拟机混合部署。比如说有个服务同时部署在Kubernetes集群以及虚拟机上，集群内的
  服务由K8s Service、Pod表示，虚拟机上的只能通过ServiceEntry表示，这种部署模型在老版本中很难无缝集成。

如果您想以主动的方式将此服务迁移到`Kubernetes`，即启动一组`POD`，通过 `Istio mTLS` 将一部分流量
发送到POD，并将其余流量发送到没有 `Sidecar` 的`VMs`，您将如何做？

您可能需要使用`Kubernetes Service`、`VirtualService` 和 `DestinationRule` 的组合来实现该行为。
现在，假设您决定将 `Sidecar` 一个接一个地添加到这些 `VM` 中，这样您只希望带 `Sidecar` 的 `VM` 的流
量使用`Istio mTLS`。如果任何其他`Service Entry`恰好在其地址中包含相同的 `VM` ，事情就会变得非常复
杂和容易出错。`Service Entry` 与 `VirtualService` 同时包含目标  `VM` .

这些复杂性的主要来源是`Istio`缺乏非容器化一级工作负载的定义，这些定义可以独立于其所属的服务进行描述的属性。

这种部署模型在老版本中很难无缝集成，问题点来自于网格内使用 `Sidecar` 相互调用,网格外没有 `Sidecar` 流量
如果包含相同的 `VM` 那么可能会导致调用方误认为服务方也存在 `Sidecar` 而发起 `Istio mTLS` 流量，导致不
可预知的错误。















Pilot 是 Istio 里的一个组件，它控制 Envoy 代理，负责服务发现、负载均衡和路由分发。











