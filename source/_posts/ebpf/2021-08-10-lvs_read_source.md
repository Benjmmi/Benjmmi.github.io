---
title: LVS  学习： 源码阅读-iptables 整篇文档不行可以不看
date: 2021-08-10 11:18:19
categories: 'eBPF'
tags:
  - ebpf
  - lvs
author: Jony
---


# netfilter HOOK

netfilter 提供了 5 个 hook 点。包经过协议栈时就会触发内核模块注册在这里的处理函数。触发哪个 hook 取决于包的方向、
包的目的地址、以及包在上一个 hook 点时丢弃还是拒绝等。

- NF_IP_PRE_ROUTING: 接收到的包进入协议栈后立即触发此 hook，在进行任何路由判断 （将包发往哪里）之前
- NF_IP_LOCAL_IN: 接收到的包经过路由判断，如果目的是本机，将触发此 hook
- NF_IP_FORWARD: 接收到的包经过路由判断，如果目的是其他机器，将触发此 hook
- NF_IP_LOCAL_OUT: 本机产生的准备发送的包，在进入协议栈后立即触发此 hook
- NF_IP_POST_ROUTING: 本机产生的准备发送的包或者转发的包，在经过路由判断之后， 将触发此 hook

netfilter 框架，提示该对这个包做以下几个操作之一：
- NF_ACCEPT: 继续正常遍历
- NF_DROP: 丢弃数据包，不再进行遍历
- NF_STOLEN: 该模块接收了该包，不再进行遍历
- NF_QUEUE: 将数据包排队（通常用于用户空间处理）
- NF_REPEAT: 再次调用此hook

# netfilter 源码阅读

```c
// 接收到的报文
static struct sk_buff *ip_rcv_core(struct sk_buff *skb, struct net *net)
{
  const struct iphdr *iph; // 定义 ip 报文的数据头
  u32 len;

  
  if (skb->pkt_type == PACKET_OTHERHOST)
    goto drop; // 不是自己的数据包就删除

  __IP_UPD_PO_STATS(net, IPSTATS_MIB_IN, skb->len);

  skb = skb_share_check(skb, GFP_ATOMIC); // 如果数据包是共享的，则复制一个出来，复制出来的 skb 就和 socket 没有啥关系了
  if (!skb) {
    __IP_INC_STATS(net, IPSTATS_MIB_INDISCARDS);
    goto out;
  }

  if (!pskb_may_pull(skb, sizeof(struct iphdr))) // 检查iphdr 长度是否正确，总的长度 - 数据报文长度
    goto inhdr_error;

  iph = ip_hdr(skb); // 指针移向头部位置

  if (iph->ihl < 5 || iph->version != 4) // 头部长度或者协议版本号不对，这里的 5 代表 20 字节
    goto inhdr_error;

  __IP_ADD_STATS(net,
           IPSTATS_MIB_NOECTPKTS + (iph->tos & INET_ECN_MASK),
           max_t(unsigned short, 1, skb_shinfo(skb)->gso_segs)); // 数据包统计加1

  if (!pskb_may_pull(skb, iph->ihl*4))  // 检查头部长度
    goto inhdr_error;

  iph = ip_hdr(skb);

  if (unlikely(ip_fast_csum((u8 *)iph, iph->ihl)))// 检验校验和
    goto csum_error;

  len = ntohs(iph->tot_len);
  if (skb->len < len) { // 总长度小于报文头长度
    __IP_INC_STATS(net, IPSTATS_MIB_INTRUNCATEDPKTS);
    goto drop;
  } else if (len < (iph->ihl*4)) // 报文头长度小于应该的报文头长度
    goto inhdr_error;


  if (pskb_trim_rcsum(skb, len)) {
    __IP_INC_STATS(net, IPSTATS_MIB_INDISCARDS); // **对数据包包进行剪裁，防止分片数据发送过来的数据有重复数据**
    goto drop;
  }

  ...
}

int ip_rcv(struct sk_buff *skb, struct net_device *dev, struct packet_type *pt,
     struct net_device *orig_dev)
{
  struct net *net = dev_net(dev);

  skb = ip_rcv_core(skb, net);
  if (skb == NULL)
    return NET_RX_DROP; // 删除接收的包

  return NF_HOOK(NFPROTO_IPV4, NF_INET_PRE_ROUTING,
           net, NULL, skb, dev, NULL,
           ip_rcv_finish); // 触发 NF_INET_PRE_ROUTING hook 点，通过回掉函数调用 ip_rcv_finish
}

```

查看 NF_HOOK 如何处理，也就是进入了 netfilter 处理过程。

```c

static inline int
NF_HOOK(uint8_t pf, unsigned int hook, struct net *net, struct sock *sk, struct sk_buff *skb,
  struct net_device *in, struct net_device *out,
  int (*okfn)(struct net *, struct sock *, struct sk_buff *)){

  int ret = nf_hook(pf, hook, net, sk, skb, in, out, okfn);
  if (ret == 1) // 如果没有 HOOK 点就直接调用 okfn ，相对 NF_INET_PRE_ROUTING 来说就是 ip_rcv_finish
    ret = okfn(net, sk, skb);
  return ret;
  
}

static inline int nf_hook(u_int8_t pf, unsigned int hook, struct net *net,
        struct sock *sk, struct sk_buff *skb,
        struct net_device *indev, struct net_device *outdev,
        int (*okfn)(struct net *, struct sock *, struct sk_buff *))
{
  struct nf_hook_entries *hook_head = NULL;
  int ret = 1;
  rcu_read_lock();
  switch (pf) {   
    // 根据协议获取不同的 nf_hookfn         取得对应的 hook， 
    // #define NF_IP_PRE_ROUTING 0        路由前，进入本机的数据
    // #define NF_IP_LOCAL_IN 1           路由后，进入本机的数据
    // #define NF_IP_FORWARD 2            路由后，本机转发的数据
    // #define NF_IP_LOCAL_OUT 3          路由前，本机本地进程发出的数据
    // #define NF_IP_POST_ROUTING 4       路由后，本机发出的数据
  case NFPROTO_IPV4:
    hook_head = rcu_dereference(net->nf.hooks_ipv4[hook]);
    break;
  case NFPROTO_IPV6:
    hook_head = rcu_dereference(net->nf.hooks_ipv6[hook]);
    break;
  default:
    WARN_ON_ONCE(1);
    break;
  }

  if (hook_head) {  // 如果存在 hook
    struct nf_hook_state state;

    nf_hook_state_init(&state, hook, pf, indev, outdev,
           sk, net, okfn); // 初始化 state

    ret = nf_hook_slow(skb, &state, hook_head, 0);
  }
  rcu_read_unlock();

  return ret;
}      


int nf_hook_slow(struct sk_buff *skb, struct nf_hook_state *state,
     const struct nf_hook_entries *hook_head, unsigned int s)
{
  unsigned int verdict;
  int ret;
  // 遍历 hook 列表
  for (; s < hook_head->num_hook_entries; s++) {
    // 调用 hook 对应的 函数，依次调用指定 hook 点下的所有 entry->hook 函数。
    // 资料限制 entry 关联了 filter 和 mangle 表
    verdict = nf_hook_entry_hookfn(&hook_head->hooks[s], skb, state);
    switch (verdict & NF_VERDICT_MASK) { // 根据函数返回的结果做对应的处理
    case NF_ACCEPT:
      break;
    case NF_DROP:
      kfree_skb(skb);  // 释放 skb
    ...
}

```

**`关键数据结构`**：

```c
struct nf_hook_entry {
  nf_hookfn     *hook;  // 注册的函数
  void        *priv;    // 优先级
};

struct nf_hook_entries {
  u16       num_hook_entries;           // 总共由多少 hook 统计
  struct nf_hook_entry    hooks[];      // hook 表
};
```


上面的代码可以看见整个 NF_HOOK 的调用过程，但是另一个问题还没有解开就是：**“`内核是如何注册 HOOK 函数的`”**。
解答这个问题前：先看下 netfilter 有几个表。
iptable 总共有五个表：
- `[filter](https://elixir.bootlin.com/linux/v5.11.2/source/net/ipv4/netfilter/iptable_filter.c)`： 最常用的 table ，用于判断是否允许一个包通过
- `[nat](https://elixir.bootlin.com/linux/v5.11.2/source/net/ipv4/netfilter/iptable_nat.c)`： 用于实现网络地址规则转换
- `[mangle](https://elixir.bootlin.com/linux/v5.11.2/source/net/ipv4/netfilter/iptable_mangle.c)`；用于修改包的 IP 头。例如：修改 TTL
- `[raw](https://elixir.bootlin.com/linux/v5.11.2/source/net/ipv4/netfilter/iptable_raw.c)`：iptables 防火墙是有状态的：对每个包进行判断的时候依赖以及判断过的包。根据 连接跟踪 ct 使得 iptables 将包看作已有的连接或者会话的一部分，而不是独立的包
- `[security](https://elixir.bootlin.com/linux/v5.11.2/source/net/ipv4/netfilter/iptable_security.c#L114)`：table 的作用是给包做 SELinux 标记，以此影响 SELinux 


**通过 iptable 常见的 4 个表：filter、nat、mangle、raw。排个序 Raw > mangle > nat > filter**
**netfilter 对应的 5 个 HOOK ： PREROUTING、INPUT、FORWARD、OUTPUT、POSTROUTING**

在内核代码中的结构：

```c
enum nf_inet_hooks {
  NF_INET_PRE_ROUTING,  //  PREROUTING
  NF_INET_LOCAL_IN,     //  INPUT
  NF_INET_FORWARD,      //  FORWARD
  NF_INET_LOCAL_OUT,    //  OUTPUT
  NF_INET_POST_ROUTING, // POSTROUTING
  NF_INET_NUMHOOKS,
  NF_INET_INGRESS = NF_INET_NUMHOOKS,
};
```


这五个表分别触发的对应的 HOOK：

1. filter -> INPUT、FORWARD、OUTPUT
2. Nat -> PREROUTING、POSTROUTING、OUTPUT
3. Mangle ->  PREROUTING、POSTROUTING、INPUT、OUTPUT、FORWARD
4. Raw -> OUTPUT、PREROUTING


## 内核是如何注册 HOOK 函数的：filter

要在内核中使用filter表，首先要向内核注册这个表，然后该表在`NF_IP_LOCAL_IN`、`NF_IP_FORWARD`、 `NF_IP_LOCAL_OUT`三个Hook点
注册相应的钩子函数，在内核filter模块的初始化函数（iptable_filter.c），完成了这一功能：

```c
static int __net_init iptable_filter_table_init(struct net *net)
{
  ...
  if (repl == NULL)
    return -ENOMEM;
  /* Entry 1 is the FORWARD hook */
  ((struct ipt_standard *)repl->entries)[1].target.verdict =
    forward ? -NF_ACCEPT - 1 : -NF_DROP - 1;
    // 注册 filter 表
  err = ipt_register_table(net, &packet_filter, repl, filter_ops,
         &net->ipv4.iptable_filter);
  kfree(repl);
  return err;
}

int ipt_register_table(struct net *net, const struct xt_table *table,
           const struct ipt_replace *repl,
           const struct nf_hook_ops *ops, struct xt_table **res)
{
  ...

  // 注册 filter 表的 hook
  ret = nf_register_net_hooks(net, ops, hweight32(table->valid_hooks));
  ...
}

int nf_register_net_hooks(struct net *net, const struct nf_hook_ops *reg,
        unsigned int n)
{
  ...
  for (i = 0; i < n; i++) {
    err = nf_register_net_hook(net, &reg[i]);
  }
...
}
```


# 表注册

```c
struct xt_table {
  // 链表成员
  struct list_head list;

  /* 位向量，表示当前表影响了哪些（个）HOOK 类型 */
  unsigned int valid_hooks;

  /* iptable的数据区 */
  struct xt_table_info __rcu *private;

  /* 是否在模块中定义，若否，则为NULL */
  struct module *me;

  u_int8_t af;    /* address/protocol family */
  int priority;   /* 优先级 */

  /* 在给定的网络中需要表是调用 */
  int (*table_init)(struct net *net);

  /* 表名，如“filter”、“nat” */
  const char name[XT_TABLE_MAXNAMELEN];
};

struct xt_table_info {
  /* 每个表的大小 */
  unsigned int size;
  /* 表中的规则数. --RR */
  unsigned int number;
  /*  初始的规则数，用于模块计数 */
  unsigned int initial_entries;

  /* 记录所影响的HOOK的规则入口相对于下面的entries变量的偏移量 */
  unsigned int hook_entry[NF_INET_NUMHOOKS];
  // 与hook_entry相对应的规则表上限偏移量，当无规则录入时，相应的hook_entry和underflow均为0
  unsigned int underflow[NF_INET_NUMHOOKS];

  /*
   * Number of user chains. Since tables cannot have loops, at most
   * @stacksize jumps (number of user chains) can possibly be made.
   */
  unsigned int stacksize;
  void ***jumpstack;
  // 每个CPU的Hook点规则表入口
  unsigned char entries[] __aligned(8);
};
```



# 参考文档
[Linux中Netfilter的原理介绍](http://blog.chinaunix.net/uid-26744085-id-3086405.html)