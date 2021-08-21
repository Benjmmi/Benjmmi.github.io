---
title: LVS  学习： 源码理解修改
date: 2021-08-16 13:31:19
categories: 
  - [eBPF]
tags:
  - ebpf
  - lvs
author: Jony
---

# Hook 点理解
![netfilter-hooks.png](https://raw.githubusercontent.com/liexusong/linux-source-code-analyze/master/images/netfilter-hooks.png)
这5个阶段分为：

- PER_ROUTING：路由前阶段，发生在内核对数据包进行路由判决前。
- LOCAL_IN：本地上送阶段，发生在内核通过路由判决后。如果数据包是发送给本机的，那么就把数据包上送到上层协议栈。
- FORWARD：转发阶段，发生在内核通过路由判决后。如果数据包不是发送给本机的，那么就把数据包转发出去。
- LOCAL_OUT：本地发送阶段，发生在对发送数据包进行路由判决之前。
- POST_ROUTING：路由后阶段，发生在对发送数据包进行路由判决之后。

在 PER_ROUTING 、 LOCAL_IN 、 FORWARD 之间有个路由 HOOK，这里应该是判断 IP 的走向是否是本机，如果是会发送 LOCAL_IN，如果不是就发送到 FORWARD，
所以 PREROUTING 工作在链路层，后 LOCAL_IN 工作在网络层，那么 FORWARD 也应该是在网络层，通过链路层 POST ROUTING 将数据包发送出去。

netfilter 5 个阶段注册钩子函数，内核会在处理数据包时，根据所在的不通阶段来调用这些钩子。注册 hook 通过 `nf_register_net_hook`

梳理了一下，整个 hook 被注册的过程：
1. module_init(ip_vs_init)
2. ip_vs_init(void) -> ip_vs_register_nl_ioctl()
3. ip_vs_register_nl_ioctl(void) -> nf_register_sockopt(&ip_vs_sockopts)
4. nf_register_sockopt(&ip_vs_sockopts) -> ip_vs_sockopts.do_ip_vs_set_ctl
5. ip_vs_sockopts.do_ip_vs_set_ctl -> ip_vs_add_service(ipvs, &usvc, &svc)
6. ip_vs_add_service(ipvs, &usvc, &svc) ->  ip_vs_register_hooks(ipvs, u->af)
7. ip_vs_register_hooks(ipvs, u->af) -> nf_register_net_hooks(ipvs->net, ops, count)
8. nf_register_net_hooks(ipvs->net, ops, count) -> nf_register_net_hook(net, &reg[i])
9. nf_register_net_hook(net, &reg[i]) -> \_\_nf_register_net_hook(net, NFPROTO_IPV4, reg);

现在在来看 ipvs 的初始化清晰了不少，但是细节方面还没有了解。最终调用的注册函数的原型：

```c
int nf_register_net_hook(struct net *net, const struct nf_hook_ops *reg)
```

`nf_hook_ops` 数据结构查看：

```c
struct nf_hook_ops {
  nf_hookfn   *hook;
  struct net_device *dev;
  void      *priv;
  u_int8_t    pf;
  unsigned int    hooknum;
  int     priority;
};
```
[struct net](https://elixir.bootlin.com/linux/latest/source/include/net/net_namespace.h#L55) 是网络命名空间，大概猜测应该是 `namespace` 隔离的。
`nf_register_net_hook` 会根据需要将不 hook 注册到不同的 `namespace` 。

各字段含义：
- **hook**: 钩子函数指针
- **dev**: 注册的设备列表，因为将设备抽离了出来
- **priv**:  
- **pf**: 协议，IPV4 或者 IPV6
- **hooknum**: 当前阶段，处于 HOOK 的哪个阶段
- **priority**: 优先级，值越大优先级越小

```c
typedef unsigned int nf_hookfn(void *priv,
             struct sk_buff *skb,
             const struct nf_hook_state *state);

struct nf_hook_state {
  unsigned int hook;
  u_int8_t pf;
  struct net_device *in;
  struct net_device *out;
  struct sock *sk;
  struct net *net;
  int (*okfn)(struct net *, struct sock *, struct sk_buff *);
};
```

- **skb**: 要处理的数据包
- **in**： 输入设备
- **out**：输出设备
- **okfn**：如果 hook 执行成功且没有异常，那么就会调用这个方法处理后续流程。

上一篇文章指出了每个 hook 对应的函数。
NF_INET_LOCAL_IN      ->    ip_vs_reply4                NF_IP_PRI_NAT_SRC - 2   
NF_INET_LOCAL_IN      ->    ip_vs_remote_request4       NF_IP_PRI_NAT_SRC - 1
NF_INET_LOCAL_OUT     ->    ip_vs_local_reply4          NF_IP_PRI_NAT_SRC + 1
NF_INET_LOCAL_OUT     ->    ip_vs_local_request4        NF_IP_PRI_NAT_DST + 2
NF_INET_FORWARD       ->    ip_vs_forward_icmp          99
NF_INET_FORWARD       ->    ip_vs_reply4                100

根据优先级字段，越小的优先级越高。所以触发的顺序：`ip_vs_reply4` > `ip_vs_remote_request4` > `ip_vs_local_reply4` > `ip_vs_forward_icmp` > `ip_vs_reply4`
ip_vs_reply4、ip_vs_local_reply4 只用于 NAT 修改 source
ip_vs_remote_request4 用于 DR、NAT（修改 Dest）、Tunnel 修改包数据


查看 `ipvs` 大部分的数据结构：
https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L37

```c
struct ip_vs_iphdr {
  int hdr_flags;  /* ipvs flags */
  __u32 off;  /* Where IP or IPv4 header starts */
  __u32 len;  /* IPv4只是L4的起点
       * IPv6，其中L4传输头开始 */
  __u16 fragoffs; /* IPv6分片偏移，如果第一个分片为0(或不是分片)*/
  __s16 protocol;
  __s32 flags;
  union nf_inet_addr saddr;
  union nf_inet_addr daddr;
};

struct ip_vs_seq {
  __u32     init_seq; /* 从这个seq中添加增量 */
  __u32     delta;    /* 序列号中的增量 */
  __u32     previous_delta; /* 序列号中的增量
             * 上次调整pkt大小之前 */
};
// ipvs 连接参数，用于保持 ct 的参数
struct ip_vs_conn_param {
  struct netns_ipvs   *ipvs;  
  const union nf_inet_addr  *caddr;  // 来源地址
  const union nf_inet_addr  *vaddr;  // 虚拟地址
  __be16        cport;  
  __be16        vport;
  __u16       protocol;
  u16       af;

  const struct ip_vs_pe   *pe;  // 持久化结构
  char        *pe_data;   // 持久化数据
  __u8        pe_data_len;  // 持久化长度
};
```
`[ip_vs_conn](https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L502)`：连接对象，主要为了维护相同的客户端与真实服务器之间的连接关系。这是由于 TCP 协议是面向连接的，所以同一个的客户端每次选择真实服务器的时候必须保存一致，否则会出现连接中断的情况，而连接对象就是为了维护这种关系。
`[ip_vs_service](https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L612)`：服务配置对象，主要用于保存 LVS 的配置信息，如 支持的 传输层协议、虚拟IP 和 端口 等。
`[ip_vs_dest](https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L654)`：真实服务器对象，主要用于保存真实服务器 (Real-Server) 的配置，如 真实IP、端口 和 权重 等。
`[ip_vs_scheduler](https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L696)`：调度器对象，主要通过使用不同的调度算法来选择合适的真实服务器对象。
`[ip_vs_app](https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L742)`：应用模块对象
`[netns_ipvs](https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L832)`：命名空间的网络信息

相互之间依赖如：

```bash
ip_vs_service.destinations <-> ip_vs_dest.n_list
n_list 属于 ip_vs_dest
n_list 包含 ip_vs_dest
ip_vs_service.scheduler  -> ip_vs_scheduler
ip_vs_scheduler.scheduler -> 调度算法
调度算法 调用  ip_vs_dest

struct list_head {
  struct list_head *next, *prev;
};
```
ip_vs_service 对象的 destinations 字段用于保存 ip_vs_dest 对象的列表，而 scheduler 字段指向了一个 ip_vs_scheduler 对象。
ip_vs_scheduler 对象的 schedule 字段指向了一个调度算法函数，通过这个调度函数可以从 ip_vs_service 对象的 ip_vs_dest 对象列表中选择一个合适的真实服务器。

通常操作：
```bash
node1 ]# ipvsadm -A -t node1:80 -s wrr
node1 ]# ipvsadm -a -t node1:80 -r node2 -m -w 3
node1 ]# ipvsadm -a -t node1:80 -r node3 -m -w 5
```

`ipvsadm -A -t node1:80` 用于添加一个虚拟服务，使用 ip_vs_service 保存。
` -s wrr`：用于添加一个调度算法，追加到  `ip_vs_service.scheduler`
`ipvsadm -a -t node1:80 -r node2` 用于添加一个 `ip_vs_service.destinations` 实例，使用 `ip_vs_dest` 保存。

看看实际添加操作，除了初始化外，可能还好奇用户空间的接口，可以在这里查看 [ip_vs_ctl.c](https://elixir.bootlin.com/linux/v5.11.2/source/net/netfilter/ipvs/ip_vs_ctl.c#L3896), by 让内核不在神秘

2021年8月19日
思考：今天在考虑网络流量包进来以后是如何触发 `NF_HOOK` 方法的，然后分别触发每个 `ipvs` 的 hook，例如： `[ip_rcv](https://elixir.bootlin.com/linux/v5.11.2/source/net/ipv4/ip_input.c#L530)` 触发 `NF_INET_PRE_ROUTING` 方法。
总结：思考太过于纠结底层，倒是错失了很多有用的信息，已经不止一次这样思考。根据 《Linux 内核网络协议》 书中就可以解开这个问题。因为网络数据包都是经过网卡 DMA 的形式将
数据包放置到指定内存的位置，然后通过引脚触发 CPU 中断，中断后执行上半段函数，然后根据情况在 `硬/软中断` 中执行下半段函数。这一段就跳过吧。看了没什么意思。



接着上面的分析。

> 根据优先级字段，越小的优先级越高。所以触发的顺序：`ip_vs_reply4` > `ip_vs_remote_request4` > `ip_vs_local_reply4` > `ip_vs_forward_icmp` > `ip_vs_reply4`

`ip_vs_reply4` 被注册到 `NF_INET_LOCAL_IN` HOOK，`NF_INET_LOCAL_IN` 执行 `LOCAL_IN` HOOK 点。

```c
static unsigned int
ip_vs_reply4(void *priv, struct sk_buff *skb,
       const struct nf_hook_state *state)
{
  return ip_vs_out(state->net->ipvs, state->hook, skb, AF_INET);
}

static inline int
nf_hook_entry_hookfn(const struct nf_hook_entry *entry, struct sk_buff *skb,
         struct nf_hook_state *state)
{
  return entry->hook(entry->priv, skb, state);
}
```

触发调用链路：`ip_rcv` -> `NF_HOOK(NFPROTO_IPV4, NF_INET_PRE_ROUTING,...)` -> `nf_hook` -> `nf_hook_slow` -> `nf_hook_entry_hookfn`

最终 `entry->hook(entry->priv, skb, state)` 调用到 `ip_vs_reply4`，
priv: 好像没什么用
skb: 就是数据包
state: 见下面代码，好像是把当前连接的所有上下文都保存在里面了

```c
static inline void nf_hook_state_init(struct nf_hook_state *p,
              unsigned int hook,
              u_int8_t pf,
              struct net_device *indev,
              struct net_device *outdev,
              struct sock *sk,
              struct net *net,
              int (*okfn)(struct net *, struct sock *, struct sk_buff *))
{
  p->hook = hook;
  p->pf = pf;
  p->in = indev;
  p->out = outdev;
  p->sk = sk;
  p->net = net;
  p->okfn = okfn;
}

```

调用：`[ip_vs_out](https://elixir.bootlin.com/linux/v5.11.2/source/net/netfilter/ipvs/ip_vs_core.c#L1345)` 函数。

```c
static unsigned int
ip_vs_out(struct netns_ipvs *ipvs, unsigned int hooknum, struct sk_buff *skb, int af)
{
  ...
  // 检查是否存在现有的连接
  cp = INDIRECT_CALL_1(pp->conn_out_get, ip_vs_conn_out_get_proto,
           ipvs, af, skb, &iph);
  // 存在直接调用当前连接
  if (likely(cp))
    return handle_response(af, skb, pd, cp, &iph, hooknum);
  ...
}
```

在阅读源码时从参考文档上看已经有很多不一样的概念和流程了。所以 [LVS原理与实现 - 实现篇
](https://github.com/liexusong/linux-source-code-analyze/blob/master/lvs-principle-and-source-analysis-part2.md) 可以做个参考，了解一下入门及其思路。
整体流程看下来核心的点：
1. 接收报文、转发报文
2. 连接的追踪、销毁
3. 负载均衡算法的调度

在查看 NF_HOOK 返回值的时候经常看见 `NF_STOLEN` 返回值，一直不了解什么意思，索性查了一下相关资料：

> NF_INET_PRE_ROUTING(位置1)：可以截获接收的所有报文，包括目的地址是自己的报文和需要转发的报文。
> NF_INET_LOCAL_IN(位置2)：可以截获目的地址是自己的报文。
> NF_INET_FORWARD(位置3)：可以截获所有转发的报文。
> NF_INET_LOCAL_OUT(位置4)：可以截获自身发出的所有报文。
> NF_INET_POST_ROUTING(位置5)：可以截获发送的所有报文，包括自身发出的报文和转发的报文。

> #define NF_DROP 0 // 丢弃该报文，不再继续传输
> #define NF_ACCEPT 1 // 继续正常传输报文
> #define NF_STOLEN 2 //Netfilter 模块接管该报文，不再继续传输
> #define NF_QUEUE 3 // 对该数据报进行排队，通常用于将数据报提交给用户空间进程处理
> #define NF_REPEAT 4 // 再次调用该钩子函数
> #define NF_STOP 5 // 继续正常传输报文

**NF_ACCEPT表示报文通过了某个钩子函数的处理，下一个钩子函数可以接着处理了。**
**NF_STOP表示报文通过了某个钩子函数的处理，后面的钩子函数你们就不要处理了**

来源:[Netfilter机制](https://www.cnblogs.com/hadis-yuki/p/5529737.html)

经跟着看下一章。