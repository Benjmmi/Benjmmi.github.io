---
title: LVS  学习： 学习和思考
date: 2021-08-05 14:55:53
categories: 
	- [eBPF]
tags:
  - ebpf
  - lvs
author: Jony
---

# 简单认识

LVS 是 Linux 平台开发的负载均衡器全称 `Linux Virtural Server` 。负载均衡器的收益主要体现在：
1. 提高系统性能
2. 提高系统可扩展性
3. 提高系统的可用性

传统上均在均衡器分为三种：
1. DNS 实现负载均衡
2. 硬件实现负载均衡
3. 软件实现负载均衡

## DNS 负载均衡
DNS 是最典型的一种方式，基本上大多数的互联网都在用。通过域名解析出 IP，每个 IP 对应不同的服务器实力，这样就完成了琉璃的调度。
缺点也很明显：
1. 延迟大。主要体现在 TTL 规则与本地缓存时间
2. 调度不均衡。主要体现在 IP 解析策略
3. 调度策略不易扩展。只支持 RR 的方式
4. 后端节点有限制。根据每个公司定制 DNS 策略不一样

## 硬件负载均衡：
专门的硬件类似于交换机、路由器。例如 F5 和 A10
优点：
1. 功能强大。支持各层级负载均衡，更全面的负载均衡算法
2. 新能强大
3. 稳定性高
4. 安全性高
缺点：
1. 价格昂贵
2. 扩展性差，无法定制
3. 维护成本高

## 软件负载均衡
运行在普通服务器上的负载均衡。常见的有 Nginx、HAProxy、LVS。其中的区别：
1. Nginx、HAproxy：七层、四层都支持，需要经过 TCP 协议栈进行判断
2. LVS：纯四层负载均衡，运行在内核态，通用性高、性能最高
优点：
1. 便宜
2. 可扩展
3. 灵活

## 认识 LVS 

LVS 是基于 Linux 内核中的 netfilter 框架实现的负载均衡系统。neifilter 在内核态运行，通过 iptables 工具从用户态控制。

![lb003netfilter.png](/jony.github.io/images/ebpf/lb003netfilter.png)

netfilter 共有 5 个 Hook 点：PREROUTING、INPUT、FORWARD、OUTPUT、POSTOUTING

- PREROUTING：刚进入网络层，还未进行路由查找的包
- INPUT：通过路由查找，确定发往本机的包
- FORWARD：通过路由查找，要转发的包
- OUTPUT：从本机刚进发出的包
- POSTOUTING：进入网络层经过路由查找，确定要转发，将要离开本设备的包

工作流程：
	当一个数据包进入网卡，经过链路层之后进入网络层就会到达 PREROUTING，接着根据目标
	IP 地址进行路由查找，如果目标 IP 是本机，数据包继续传递到 INPUT 上，经过协议栈后根据
	端口将数据送到相应的应用程序；应用程序处理请求后将响应数据包发送到 OUTPUT 上，最终
	通过 POSTROUTING 后发送出网卡。如果目标 IP 不是本机，而且服务器开启了 forward 参
	数，就会将数据包递送给 FORWARD 上，最后通过 POSTROUTING 后发送出网卡。

原理如图：

![lb004ipvs.png](/jony.github.io/images/ebpf/lb004ipvs.png)

- 当用户访问 www.sina.com.cn 时，用户数据通过层层网络，最后通过交换机进入 LVS 服务器网卡，并进入内核网络层。
- 进入 PREROUTING 后经过路由查找，确定访问的目的 VIP 是本机 IP 地址，所以数据包进入到 INPUT 链上
- IPVS 是工作在 INPUT 链上，会根据访问的 vip+port 判断请求是否 IPVS 服务，如果是则调用注册的 IPVS HOOK 函数，进行 IPVS 相关主流程，强行修改数据包的相关数据，并将数据包发往 POSTROUTING 链上。
- POSTROUTING 上收到数据包后，根据目标 IP 地址（后端服务器），通过路由选路，将数据包最终发往后端的服务器上。

## DR 模式
问题：当用户访问 www.sina.com.cn 时，用户数据通过层层网络，确定访问的目的 VIP 是本机 IP 地址？
VIP 如果不是本机上面，流量包通过广播的方式也不应该进入到 INPUT层，如果启动了 FORWARD 是不是会有转发风暴，在同一个VLAN 里面，会将同一个数据包多次转发到目标主机，是不是浪费了很多宽带
答案：通过同程公司的 TVS 系统发现设置范围与自己猜想的类似，如果将四层作为负载均衡器，那么久需要使用到 VIP ，为发散的方式，类似于通过域名解析出真实IP，而 LVS 通过 VIP 解析出真实IP，具体还可以通过 VIP+PORT 映射到具体 IP 。或者以 kubernetes 举例，每个 service 都有自己的 IP，但是每个 service 都没有实体服务，只是起到一个负载均衡的作用，具体服务的还是后面的 POD 实例。
那么一个转发机如果有上百万、上千万个服务需要负载均衡这个怎么操控的。难道要绑定上百万个 VIP 在转发机？

问题：DR 模式只是将请求转发到后端，响应不在经过 LVS ，那么是不是可以理解只是在建立连接的时候分配了一个后端实例，建立了长链接，后面的请求就不会在走到 LVS 了。

CIP（客户端真实）+CMAC（中间路由器MAC地址） 					 =》 VIP+VMAC  查找 真实服务地址（RMAC）
    		  																	    					 ||  转发
CIP（客户端真实）+ （VIP&RIP）同网段对应MAC （DMAC）    =》 VIP + RMAC
																					 				     || 到达真实服务器，请求响应
CIP + 	（VIP&RIP）同网段 路由器 MAC    （CMAC）    		       《=    VIP + RMAC

解：通过梳理推断，LSV 只会判断 VIP 不会判断目标 MAC，而且整个转发流程 VIP 是透传的所以下次请求还是
传送到 LVS 上。其中修改的只有 MAC 地址。路由器也是修改 响应目标真实的 MAC 地址。

缺点：需要后端服务配置，增加了运维难度


## NAT 模式
NAT 模式是很多场景都会使用的模式。比如典型的网络连接就有 NAT 模式。NAT 模式双休流量都经过 LVS ，因此 NAT 模式性能会存在一定的瓶颈。不过与其他模式区别的是，NAT 支持端口映射，且支持 windows 。

CIP（源IP）   				 			=》 VIP（目的IP）
  VIP+PORT  				  			 ||    查找 RIP，查找成功将目标 VIP 修改为 RIP
  CIP（源IP）							=》 RIP（目的IP）
  												 ||   默认网关配置为 LVS 设备 IP，所以响应到网关 IP，就是 LVS
  CIP 									  《=   RIP 响应
  CIP 不是 LVS 负责的 VIP      ||    查找转发 forward 链
  根据目的 IP 和目的 port 查找服务和连接表，将源 IP（RIP）改为 VIP
  CIP  									  《=   VIP  发出去
  
  ### 优点
 
  可以支持 Windows，可以支持端口级别的映射。
 
 ###  缺点
 
 NAT 模式，进出流量都经过 LVS 负载压力比较大。
 如果存在 windows 系统，使用 LVS  必须使用 NAT 模式
 存在性能瓶颈
 
 
 ##  Tunnel 模式
 
 Tunnel 与 DR 模式一样，也是属于一种单臂模式，只有请求会经过 LVS ，响应数据直接返回给客户端，支持夸机房操作。
 
 CIP（源IP）   							=》  VIP（目的IP）
 												    ||     查找IP路由，确定是本机IP，转发到 LVS ，
 												    ||    通过目的 IP+ PORT 查找 RIP
CIP 												=》 dev tunnel
													||  DIP=CIP    RIP=目的IP  添加到 IP 头部前面，将数据包转发到 output 上
CIP（DIP+RIP）LVS                  =》    后端服务器
目标服务器卸载 tunnel 头部模块，获取 CIP  和  VIP ，
													||  判断 tunl0 配置的 IP == vip
													=》 转发到应用程序
														
CIP：Client IP，表示的是客户端 IP 地址。
VIP：Virtual IP，表示负载均衡对外提供访问的 IP 地址，一般负载均衡 IP 都会通过 Virtual IP 实现高可用。
RIP：RealServer IP，表示负载均衡后端的真实服务器 IP 地址。
DIP：Director IP，表示负载均衡与后端服务器通信的 IP 地址。
CMAC：客户端的 MAC 地址，准确的应该是 LVS 连接的路由器的 MAC 地址。
VMAC：负载均衡 LVS 的 VIP 对应的 MAC 地址。
DMAC：负载均衡 LVS 的 DIP 对应的 MAC 地址。
RMAC：后端真实服务器的 RIP 地址对应的 MAC 地址。 
 
 
  
  10.0.2.15
					   
192.168.33.11
192.168.33.8
192.168.33.10




vip=192.168.33.12
rs1=192.168.33.11
rs2=192.168.33.13
/sbin/ipvsadm -C 
/sbin/ipvsadm -A -t $vip:8088 -s rr
/sbin/ipvsadm -a -t $vip:8088 -r $rs1:8088 -g
/sbin/ipvsadm -a -t $vip:8088 -r $rs2:8088 -g

sudo ifconfig lo:0 192.168.33.12 netmask 255.255.255.0 broadcast 192.168.33.255
sudo route add -host 192.168.33.12 dev eth1:0

网络发展方向：
熟悉高性能服务器，负载均衡，网关及代理 LVS，Nginx、Haproxy，Envoy等
熟悉SDN，云原生网络系统、VPC，有DPDK开发经验
精通网络虚拟化技术 , Overlay的设计与实现
NOS、Openflow、DPDK/VPP、P4lang、LVS、K8S
可编程智能网卡（FPGA、NP、SoC）
QUIC、GOLANG

export HTTP_PROXY=http://192.168.0.177:58591; export HTTPS_PROXY=http://192.168.0.177:58591; export ALL_PROXY=socks5://192.168.0.177:51837

# 参考文档
[负载均衡 LVS 入门教程详解](https://www.linuxblogs.cn/articles/20010215.html)
[负载均衡 LVS 入门教程详解](https://www.linuxblogs.cn/articles/20010419.html)
[章文嵩 的博客](http://www.linuxvirtualserver.org/zh/lvs1.html)



