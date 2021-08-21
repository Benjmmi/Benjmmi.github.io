---
title: LVS  学习： netfilter 与 ipvs 源码理解修改
date: 2021-08-20 13:31:19
categories: 
  - [eBPF]
tags:
  - ebpf
  - lvs
author: Jony
---

参考文档：
[深入Linux网络核心堆栈--netfilter详解(整理)](https://blog.csdn.net/XscKernel/article/details/8186679)
[LVS原理与实现 - 实现篇](https://github.com/liexusong/linux-source-code-analyze/blob/master/lvs-principle-and-source-analysis-part2.md)
[Linux服务器集群系统](http://www.linuxvirtualserver.org/zh/index.html)
[Linux 源码](https://elixir.bootlin.com/linux/v5.11.2/source/net/netfilter/ipvs)
[Netfilter机制](https://www.cnblogs.com/hadis-yuki/p/5529737.html)

**此文章较长**

# 介绍

Netfilter是Linux 2.4内核的一个子系统，Netfiler使得诸如数据包过滤、网络地址转换(NAT)
以及网络连接跟踪等技巧成为可能，这些功能仅通过使用内核网络代码提供的各式各样的hook既可以完成。
这些hook位于内核代码中，要么是静态链接的，要么是以动态加载的模块的形式存在。


Linux Virtual Server(LVS) 针对高可伸缩、高可用网络服务的需求，给出了基于IP层和基于内容请求分发的负载
平衡调度解决方法，并在Linux内核中实现了这些方法，将一组服务器构成一个实现可伸缩的、高可用网络服务的虚拟
服务器。由于负载调度技术是在Linux内核中实现的，我们称之为Linux虚拟服务器（Linux Virtual Server）。

LVS 项目的目标 ：使用集群技术和Linux操作系统实现一个高性能、高可用的服务器，
它具有很好的可伸缩性（Scalability）、可靠性（Reliability）和可管理性（Manageability）

在LVS框架中，提供了含有IP负载均衡技术的`IP虚拟服务器软件IPVS`。

# Netfilter
Netfilter中定义了五个关于IPv4的hook，对这些符号的声明可以在 [netfilter_ipv4.h](https://elixir.bootlin.com/linux/v5.11.2/source/include/uapi/linux/netfilter_ipv4.h#L18) 中找到，可用的 IP HOOK 如下：


> #define NF_IP_PRE_ROUTING   0   在进行完整性检查之后，可以截获接收的所有报文，包括目的地址是自己的报文和需要转发的报文；目的IP地址转换在此点
> #define NF_IP_LOCAL_IN      1   路由决策后，可以截获目的地址是自己的报文，INPUT 包过滤在这里进行
> #define NF_IP_FORWARD       2   截获所有转发的报文，FORWARD 在这里进行过滤
> #define NF_IP_LOCAL_OUT     3   可以截获自身发出的所有报文(不包括转发)，OUTPUT 过滤在这里进行
> #define NF_IP_POST_ROUTING  4   可以截获发送的所有报文，包括自身发出的报文和转发的报文

在hook函数完成了对数据包所需的任何的操作之后，它们必须返回下列预定义的Netfilter返回值中的一个：

> #define NF_DROP     0     丢弃数据包，不在继续
> #define NF_ACCEPT   1     正常传输报文
> #define NF_STOLEN   2     Netfilter 模块接管该报文，不再继续传输
> #define NF_QUEUE    3     对该数据报进行排队，通常用于将数据报提交给用户空间进程处理
> #define NF_REPEAT   4     再次调用该钩子函数
> #define NF_STOP     5     继续正常传输报文

`Note`：NF_ACCEPT和NF_STOP都表示报文通过了检查，可以正常向下流通。
> `NF_ACCEPT` 表示报文通过了某个 `HOOK` 函数的处理，下一个 `HOOK` 函数可以接着处理了
> `NF_STOP` 表示报文通过了某个 `HOOK` 函数的处理，后面的 `HOOK` 函数你们就不要处理了

场景解释：假设有两个 `hook` 分别是 `hook1`、`hook2`，`hook1` > `hook2` 优先级。
`hook1` 设定的处理结果是`NF_STOP`，那么报文就会有 `hook1` 提交给应用程序或者其他处理，因为`hook1`放行了，根本不会给`hook2`处理的机会。数据包依然有效

处理代码体现：
```c
// https://elixir.bootlin.com/linux/v5.11.2/source/net/netfilter/nf_queue.c#L237
static unsigned int nf_iterate(struct sk_buff *skb, struct nf_hook_state *state,
             const struct nf_hook_entries *hooks, unsigned int *index)
{
  const struct nf_hook_entry *hook;
  unsigned int verdict, i = *index;

  while (i < hooks->num_hook_entries) {
    hook = &hooks->hooks[i];
repeat:
    verdict = nf_hook_entry_hookfn(hook, skb, state); // 调用 hook 函数
    if (verdict != NF_ACCEPT) {
      *index = i;
      if (verdict != NF_REPEAT) // 不是重试直接返回
        return verdict;
      goto repeat;   // 重试
  ...
}
```

## 注册/注销 hook

注册一个hook函数是围绕nf_hook_ops数据结构,数据结构的定义如下：


```c
// https://elixir.bootlin.com/linux/v5.11.2/source/include/linux/netfilter.h#L77
typedef unsigned int nf_hookfn(void *priv,
             struct sk_buff *skb,
             const struct nf_hook_state *state);

struct nf_hook_ops {
  nf_hookfn   *hook;
  struct net_device *dev;
  void      *priv;
  u_int8_t    pf;
  unsigned int    hooknum;
  int     priority;
};

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

> priv：私有数据 
> skb：正在处理的报文
> state：将相关参数都将存储到state中
> hook：hook 函数
> dev：设备
> pf：协议族
> hooknum：hook 触发点的编号
> priority：优先级
> net_device \*in：用于描述数据包到达的接口
> net_device \*out：用于描述数据包离开的接口

参数 `in` 只用于`NF_IP_PRE_ROUTING`和`NF_IP_LOCAL_IN`，参数`out`只用于`NF_IP_LOCAL_OUT`和`NF_IP_POST_ROUTING`

注册一个Netfilter `hook` 需要调用 `nf_register_net_hook()` 函数，以及用到一个 `nf_hook_ops` 数据结构。
`nf_register_net_hook()`函数以一个 `nf_hook_ops` 数据结构的地址作为参数并且返回一个整型的值。 代码如下：

```c
// https://elixir.bootlin.com/linux/v5.11.2/source/net/netfilter/core.c#L557
int nf_register_net_hooks(struct net *net, const struct nf_hook_ops *reg,
        unsigned int n)
{
...
  for (i = 0; i < n; i++) {
    err = nf_register_net_hook(net, &reg[i]); // 注册 hook 函数
    if (err)
      goto err; // 注册失败
  }
  return 0;
err:
  if (i > 0) // 注销 hook 函数
    nf_unregister_net_hooks(net, reg, i);
  return err;
}

```

## hook 触发

因为制作 hook 触发需要将程序加载到内核中，所以先了解下 linux 内核模块化，加载和卸载。

创建两个个源代码文件：

```c
// hds.c
#include <linux/module.h> // 任何模块都必须包含，定义了可动态加载到内核的模块所需要的必要信息
#include <linux/init.h> // 必须包含，包含了宏__init(指定初始化函数)和__exit(指定清除函数)
#include <linux/kernel.h> //里面包含常用的内核API，例如内核打印函数printk()

static int __init hds_init(void) //__init将函数hds_init()标记为初始化函数，在模块被装载到内核时调用hds_init()
{
        printk(KERN_CRIT "Hello Kernell\n");
        return 0;
}

static void __exit hds_exit(void) //清除函数,在模块被卸载之前调用
{
        printk(KERN_ALERT "GoodBye Kernel\n");
}
module_init(hds_init);
module_exit(hds_exit);
MODULE_LICENSE("GPL");
MODULE_AUTHOR("jony");
MODULE_DESCRIPTION("for fun");
```

```bash
// Makefile
obj-m:=hds.o #根据make的自动推导原则，make会自动将源程序hds.c编译成目标程序hds.o
            #所有在配置文件中标记为-m的模块将被编译成可动态加载进内核的模块。即后缀为.ko的文件
CURRENT_PATH:=$(shell pwd)        #参数化，将模块源码路径保存在CURRENT_PATH中
LINUX_KERNEL:=$(shell uname -r)   #参数化，将当前内核版本保存在LINUX_KERNEL中
LINUX_KERNEL_PATH:=/usr/src/linux-headers-$(LINUX_KERNEL)      #参数化，将内核源代码的绝对路径保存在LINUX_KERNEL_PATH中

all:
        make -C $(LINUX_KERNEL_PATH) M=$(CURRENT_PATH) modules    #编译模块
clean:
        make -C $(LINUX_KERNEL_PATH) M=$(CURRENT_PATH) clean  #清理
```

两个文件创建完成后，执行编译加载命令：

```bash
$ make    # 执行编译
$ sudo insmod hds.ko  # 模块加载到内核
$ lsmod |grep hds   # 查看是否加载成功
$ dmesg   # 查看内核输出
$ sudo rmmod hds # 卸载内核模块
```
**制作 HOOK**

制作一个轻量级防火墙，根据 `net_device` 中的 `name` 字段来制作防火墙。比如当 `in->name` 等于 `eth0` 的时候我们
就返回 `NF_DROP` ，数据包会自动销毁。

```c
// drop_if_lo.c
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/version.h>
#include <linux/skbuff.h>
#include <linux/netfilter.h>
#include <linux/netfilter_ipv4.h>
#include <linux/netdevice.h>


static struct nf_hook_ops nf_drop;

static char *if_name = "eth0";

unsigned int hook_func(void *priv,
                struct sk_buff *skb,
                const struct nf_hook_state *state)
{
        if(state->out != NULL && strcmp(state->out->name,if_name) == 0)
        {
                return NF_ACCEPT;
        }
        else
        {
                return NF_DROP;
        }
        return 0;
}

static int __init drop_if_lo_init(void)
{
        printk(KERN_CRIT "drop_if_lo_init");
        nf_drop.hook = &hook_func;
        nf_drop.pf = PF_INET;
        nf_drop.hooknum = NF_INET_LOCAL_OUT;
        nf_drop.priority = NF_IP_PRI_FIRST;

        nf_register_net_hook(&init_net, &nf_drop);

        return 0;
}

static void __exit drop_if_lo_exit(void)
{
        printk(KERN_ALERT "drop if lo exit\n");
        nf_unregister_net_hook(&init_net, &nf_drop);
}


module_init(drop_if_lo_init);
module_exit(drop_if_lo_exit);
MODULE_LICENSE("GPL");
MODULE_AUTHOR("jony");
MODULE_DESCRIPTION("drop if eth0");
```

```bash
//Makefile
obj-m:=drop_if_lo.o

CURRENT_PATH:=$(shell pwd)
LINUX_KERNEL:=$(shell uname -r)
LINUX_KERNEL_PATH:=/usr/src/linux-headers-$(LINUX_KERNEL)

all:
        make -C $(LINUX_KERNEL_PATH) M=$(CURRENT_PATH) modules
clean:
        make -C $(LINUX_KERNEL_PATH) M=$(CURRENT_PATH) clean

```

问题：理想状态是只会删除 eth0 的数据包，不会删除其他网卡的数据包，但是实际情况是任何数据包都被删除了。查下原因本地访问会自动转为 lo 设备。

总结：Netfilter 基本上特点大概都了解了一遍，基本上 Netfilter 原理大致都了解，也可以通过编写内核模块来编写 Netfilter 插件，加载到内核中执行。

> **`Note：在linux内核中默认情况下，会有一个默认的网络命名空间，其名为init_net`**

# IPVS 

学完 Netfilter 之后，在看 IPVS 基本已经没有秘密，IPVS 也是在基于 Netfilter 编写的一款插件。
关于注册就不在考虑如何注册整个流程，只学习下几个核心功能。

> `[ip_vs_service](https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L612)`：服务配置对象，主要用于保存 LVS 的配置信息，如 支持的 传输层协议、虚拟IP 和 端口 等。
> `[ip_vs_dest](https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L654)`：真实服务器对象，主要用于保存真实服务器 (Real-Server) 的配置，如 真实IP、端口 和 权重 等。
> `[ip_vs_scheduler](https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L696)`：调度器对象，主要通过使用不同的调度算法来选择合适的真实服务器对象。
> `[ip_vs_conn](https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L502)`：连接对象，主要为了维护相同的客户端与真实服务器之间的连接关系。这是由于 TCP 协议是面向连接的，所以同一个的客户端每次选择真实服务器的时候必须保存一致，否则会出现连接中断的情况，而连接对象就是为了维护这种关系。
> `[ip_vs_app](https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L742)`：应用模块对象
> `[netns_ipvs](https://elixir.bootlin.com/linux/v5.11.2/source/include/net/ip_vs.h#L832)`：命名空间的网络信息

来自 [LVS  学习： 源码理解修改](./lvs3.md)

## ip_vs_service

> 注：Persistence(PE) 设置持久连接，这个模式可以使来自客户的多个请求被送到同一个真实服务器

`ip_vs_service` 的创建通过 `ip_vs_add_service()` 函数来完成，真实代码如下：

```c
// https://elixir.bootlin.com/linux/v5.11.2/source/net/netfilter/ipvs/ip_vs_ctl.c#L1286
static int
ip_vs_add_service(struct netns_ipvs *ipvs, // 所属哪个命名空间
  struct ip_vs_service_user_kern *u, // 用户通过命令行配置的规则信息
      struct ip_vs_service **svc_p
      )
{
  int ret = 0, i;
  struct ip_vs_scheduler *sched = NULL;
  struct ip_vs_pe *pe = NULL;
  struct ip_vs_service *svc = NULL;
  int ret_hooks = -1;
  if (strcmp(u->sched_name, "none")) { // 根据调度器名称获取调度策略对象
    sched = ip_vs_scheduler_get(u->sched_name);
  }
  if (u->pe_name && *u->pe_name) {  // 根据持久化名称获取持久化管理方法
    pe = ip_vs_pe_getbyname(u->pe_name);
  }
  if ((u->af == AF_INET && !ipvs->num_services) ||
      (u->af == AF_INET6 && !ipvs->num_services6)) {
    ret = ip_vs_register_hooks(ipvs, u->af);  // 如果是首次创建 SVC，那么就将调度策略注册到 Netfilter
    if (ret < 0)
      goto out_err;
    ret_hooks = ret;
  }
  ...
  svc = kzalloc(sizeof(struct ip_vs_service), GFP_KERNEL); // 申请一个 ip_vs_service 对象
  svc->af = u->af; // 3 层协议
  svc->protocol = u->protocol; // 4 层协议
  ip_vs_addr_copy(svc->af, &svc->addr, &u->addr); // svc IP
  svc->port = u->port; // svc 端口
  svc->fwmark = u->fwmark; // 防火墙标记，持久化对象
  svc->flags = u->flags; // 标志位
  svc->timeout = u->timeout * HZ; // 超时时间
  svc->netmask = u->netmask; // 网络掩码
  svc->ipvs = ipvs; 

  INIT_LIST_HEAD(&svc->destinations);
  spin_lock_init(&svc->sched_lock);
  spin_lock_init(&svc->stats.lock);

  if (sched) {
    ret = ip_vs_bind_scheduler(svc, sched); // 绑定到指定的调度器
    if (ret)
      goto out_err;
    sched = NULL;
  }  
  RCU_INIT_POINTER(svc->pe, pe);  // 初始化持久化方式
  pe = NULL;
  ...
  ip_vs_svc_hash(svc); // 添加 ip_vs_service 到 hash 表
  *svc_p = svc; // 返回 svc
  ipvs->enable = 1; 
  return 0;    
}
```
上面的代码主要完成一下几个工作：

- 通过调用 `ip_vs_scheduler_get` 函数来获取一个 `ip_vs_scheduler` 调度器
- 申请 `ip_vs_service` 对象，并初始化。然后将上面获取到调度器，与当前 `svc` 绑定
- 最终将 `ip_vs_service` 对象添加到全局 hash 表中。（思考：内存决定了 hash 表的上线，如果使用 LRU 是否可以进一步扩展至硬盘）

## ip_vs_dest
 
真实服务器对象，主要用于创建保存真实服务器 (Real-Server) 的相关配置信息。
创建 `ip_vs_dest` 对象通过 `ip_vs_add_dest()` 创建，具体代码细节如下：

```c
//https://elixir.bootlin.com/linux/v5.11.2/source/net/netfilter/ipvs/ip_vs_ctl.c#L1038
static int
ip_vs_add_dest(struct ip_vs_service *svc, struct ip_vs_dest_user_kern *udest)
{
  struct ip_vs_dest *dest;
  union nf_inet_addr daddr;
  __be16 dport = udest->port;
  int ret;

  ip_vs_addr_copy(udest->af, &daddr, &udest->addr);

  dest = ip_vs_lookup_dest(svc, udest->af, &daddr, dport);

  dest = ip_vs_trash_get_dest(svc, udest->af, &daddr, dport);
    
}
```

