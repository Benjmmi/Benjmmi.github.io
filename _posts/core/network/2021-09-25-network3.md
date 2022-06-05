---
title: 设备初始化的流程
date: 2021-09-25 07:36:57
categories: 
	- [eBPF]
	- [Linux]
	- [内核]
	- [网络代码]
tags:
  - ebpf
author: Jony
---

Linux 作为一个宏内核把模块化玩的贼溜，这个得赞叹一下。

关于模块初始化与设备注册在 `《Linux 设备与驱动程序》`
`subsys_initcall` 子模块初始化调用的接口方法，主要了解 `net_dev_init`：

```c
// 代码有删减
static int __init net_dev_init(void)
{
	int i, rc = -ENOMEM;

	BUG_ON(!dev_boot_phase);
	// 初始化统计信息的 proc 文件
	if (dev_proc_init())
		goto out;
	// 初始化 kobject
	if (netdev_kobject_init())
		goto out;
	// 初始化协议类型链表
	INIT_LIST_HEAD(&ptype_all);
	for (i = 0; i < PTYPE_HASH_SIZE; i++) //初始化协议类型hash表
		INIT_LIST_HEAD(&ptype_base[i]);
	//初始化offload列表
	INIT_LIST_HEAD(&offload_base);
	//注册网络命名空间子系统
	if (register_pernet_subsys(&netdev_net_ops))
		goto out;
	//初始化数据包接收队列
	for_each_possible_cpu(i) {
		struct work_struct *flush = per_cpu_ptr(&flush_works, i);
		struct softnet_data *sd = &per_cpu(softnet_data, i);
		//初始化清理backlog队列
		INIT_WORK(flush, flush_backlog);
		//初始化非napi接口层的缓存队列
		skb_queue_head_init(&sd->input_pkt_queue);
		//初始化数据包处理队列  
		skb_queue_head_init(&sd->process_queue);
		//初始化网络设备轮询队列
		INIT_LIST_HEAD(&sd->poll_list);
		//初始化输出队列尾部
		sd->output_queue_tailp = &sd->output_queue;
		//若支持RPS
#ifdef CONFIG_RPS
		INIT_CSD(&sd->csd, rps_trigger_softirq, sd);
		sd->cpu = i;
#endif
		//初始化 gro hash
		init_gro_hash(&sd->backlog);
		//支持非napi虚拟设备的回调和配额设置
		sd->backlog.poll = process_backlog;
		sd->backlog.weight = weight_p;
	}

	dev_boot_phase = 0;
	// 注册回环设备
	if (register_pernet_device(&loopback_net_ops))
		goto out;

	if (register_pernet_device(&default_device_ops))
		goto out;
	// 注册发送软中断
	open_softirq(NET_TX_SOFTIRQ, net_tx_action);
	// 注册接收软中断
	open_softirq(NET_RX_SOFTIRQ, net_rx_action);
	//注册响应cpu状态变化的回调
	rc = cpuhp_setup_state_nocalls(CPUHP_NET_DEV_DEAD, "net/dev:dead",
				       NULL, dev_cpu_dead);
	WARN_ON(rc < 0);
	rc = 0;
out:
	return rc;
}
subsys_initcall(net_dev_init);
```

关于 `softnet_data` 每个 CPU 都注册了一个自己的数据结构用于处理入口流量和出口流量。
每个 CPU 都有自己的处理队列也就没有锁竞争的问题。

# 接收帧

设备初始化完成之后会，网卡设备将开始工作。
目前接收包的方式共有两种 NAPI 与 非 NAPI 方式。

## NAPI 方式

第一个数据包到来是，将会产生硬中断，中断处理程序**将设备**的 `napi_struct` 结构挂在当前 `cpu`
的待接收设备 `softnet_data->poll_list` 列表中。并触发软中断，软中断会便利 `softnet_data->poll_list`
中的所有设备，依次调用 `napi_struct->poll` 虚拟函数处理收包。

以 [e100](https://elixir.bootlin.com/linux/v5.14/source/drivers/net/ethernet/intel/e100.c#L2195) 为例：

```c
static irqreturn_t e100_intr(int irq, void *dev_id)
{
	struct net_device *netdev = dev_id;
	...
	if (likely(napi_schedule_prep(&nic->napi))) {
		// 禁用中断
		e100_disable_irq(nic);
		//将该网络设备加入到sd的poll_list中
		__napi_schedule(&nic->napi);
	}

	return IRQ_HANDLED;
}
void __napi_schedule(struct napi_struct *n)
{
	unsigned long flags;

	local_irq_save(flags);
	____napi_schedule(this_cpu_ptr(&softnet_data), n);
	local_irq_restore(flags);
}
// 调用时 IRQ 被禁用
static inline void ____napi_schedule(struct softnet_data *sd,
				     struct napi_struct *napi)
{
	...
	// 添加设备到 poll_list
	list_add_tail(&napi->poll_list, &sd->poll_list);
	// 激活软中断，即：net_rx_action
	__raise_softirq_irqoff(NET_RX_SOFTIRQ);
}
```

## net_rx_action

`net_rx_action` 负责处理收包程序，当该函数被触发调用时，说明有设备的数据包到达，此时本处理程序便利
`softnet_data->poll_list` 中的收包设备，并执行 `napi` 中的 `poll` 调度，
```c
static __latent_entropy void net_rx_action(struct softirq_action *h)
{
	// 指向当前 CPU 的数据结构
	struct softnet_data *sd = this_cpu_ptr(&softnet_data);
	unsigned long time_limit = jiffies +
		usecs_to_jiffies(netdev_budget_usecs);
	int budget = netdev_budget;
	LIST_HEAD(list);
	// 需要重新 poll 的设备列表
	LIST_HEAD(repoll);
	// 待处理设备列表 poll_list 统一合并到 list，处理过程不允许中断，
	// 并且重新初始化 poll_ist
	// 合并方式，改变了一下链表的头节点和尾节点
	local_irq_disable();
	list_splice_init(&sd->poll_list, &list);
	local_irq_enable();
	// 遍历列表
	for (;;) {
		struct napi_struct *n;
		// 如果待处理设备列表为空，就直接跳出
		if (list_empty(&list)) {
			if (!sd_has_rps_ipi_waiting(sd) && list_empty(&repoll))
				// 如果重新 poll 的设备链表也为空，就没必要继续执行了直接结束
				return;
			break;
		}
		// 每次都取出 头节点
		n = list_first_entry(&list, struct napi_struct, poll_list);
		// 调用该设备结构 poll 虚拟函数
		// 如果没有处理结束，就挂到 repoll 上
		budget -= napi_poll(n, &repoll);

		// 如果公平的中断时间结束就结束处理
		if (unlikely(budget <= 0 ||
			     time_after_eq(jiffies, time_limit))) {
			sd->time_squeeze++;
			break;
		}
	}
	// 下面操作不可中断
	local_irq_disable();
	// 将未处理完的设备列表，重新拼接到 poll_list 等待下次处理
	list_splice_tail_init(&sd->poll_list, &list);
	list_splice_tail(&repoll, &list);
	// FIFO 的顺序，当前没有完成的放到前面
	list_splice(&list, &sd->poll_list);
	// 如果 poll_list 不为空，触发下一次收包中断
	if (!list_empty(&sd->poll_list))
		// 当前过程就是为了方式 CPU 占用时间过长，重新中断
		__raise_softirq_irqoff(NET_RX_SOFTIRQ);
	// 启用中断
	net_rps_action_and_irq_enable(sd);
}
```
为了防止处理数据包时 CPU 占用过高，设置了公平原则，当占用时间达到一定额度的时候就会跳出循环释放 CPU 
重新触发中断流程。

**napi_poll** 就是调用设备对应的 `napi_struct->poll` 回调接收数据包，接收**数量根据配额**进行**`限制`**。
关键代码为 `work = __napi_poll(n, &do_repoll);`

```c
static int napi_poll(struct napi_struct *n, struct list_head *repoll)
{
	bool do_repoll = false;
	void *have;
	int work;
	// 链表去除 napi 关联
	list_del_init(&n->poll_list);

	have = netpoll_poll_lock(n);

	work = __napi_poll(n, &do_repoll);

	if (do_repoll)
		list_add_tail(&n->poll_list, repoll);

	netpoll_poll_unlock(have);

	return work;
}
```
在处理数据包的过程中全程上锁的，那么这里可能会被多线程调用，说明除了驱动之外还有别的地方调用
```c
static int __napi_poll(struct napi_struct *n, bool *repoll)
{
	int work, weight;
	// 获取配额，可处理数据包的最高数量
	weight = n->weight;

	work = 0;
	// 检查 NAPI 是否在调度状态
	if (test_bit(NAPI_STATE_SCHED, &n->state)) {
		work = n->poll(n, weight);
		trace_napi_poll(n, work, weight);
	}
	// 收包数量大于配额，说明出现了异常，打印出异常信息
	if (unlikely(work > weight))
		pr_err_once("NAPI poll function %pS returned %d, exceeding its budget of %d.\n",
			    n->poll, work, weight);
	// 收包数量小于配置，返回真正处理收包数量
	if (likely(work < weight))
		return work;

	// 如果 napi 用完了整个配额，那么久强制修改 NAPI
	// 为完成状态态，并返回实际处理数据包数量
	if (unlikely(napi_disable_pending(n))) {
		napi_complete(n);
		return work;
	}

	/* 检查 napi 当前状态，如果外部强制中断那么应该尽早推出
	 */
	if (napi_prefer_busy_poll(n)) {
		if (napi_complete_done(n, work)) {
			/* 如果没有设置超时，我们需要确保NAPI被重新排定。
			 */
			napi_schedule(n);
		}
		return work;
	}

	if (n->gro_bitmask) {
		/* flush too old packets
		 * If HZ < 1000, flush all packets.
		 */
		napi_gro_flush(n, HZ >= 1000);
	}
	// 通过 gro 合并到 skb
	gro_normal_list(n);

	/* Some drivers may have called napi_schedule
	 * prior to exhausting their budget.
	 */
	if (unlikely(!list_empty(&n->poll_list))) {
		pr_warn_once("%s: Budget exhausted after napi rescheduled\n",
			     n->dev ? n->dev->name : "backlog");
		return work;
	}

	*repoll = true;

	return work;
}
```


## 非 NAPI 方式
每个数据包到来都会产生硬件中断，中断处理程序将**收到的包**放入到当前 CPU 的收包队列中 `softnet_data->input_pkg_queue`
中，并且将非 `napi` 设备对应的虚拟设备 	`napi` 结构`softnet->backlog` 结构挂在当前 `cpu` 的待收包设备链表 
`softnet->poll_list` 中，并触发软中断，软中断处理过程中，会调用 `backlog` 的回调处理函数 `process_backlog` 

以 `3c509` 为例：

```c
static int
el3_rx(struct net_device *dev)
{
	while ((rx_status = inw(ioaddr + RX_STATUS)) > 0) {
		if (rx_status & 0x4000) { 
			...
		} else {
			short pkt_len = rx_status & 0x7ff;
			struct sk_buff *skb;
			skb = netdev_alloc_skb(dev, pkt_len + 5);
			if (skb != NULL) {
				skb_reserve(skb, 2);     /* 16 字节对齐 */

				insl(ioaddr + RX_FIFO, skb_put(skb,pkt_len),
					 (pkt_len + 3) >> 2);
				skb->protocol = eth_type_trans(skb,dev);
				netif_rx(skb);
				dev->stats.rx_bytes += pkt_len;
				dev->stats.rx_packets++;
				continue;
			}
			dev->stats.rx_dropped++;
		}
	}
}	
```

`netif_rx->netif_rx_internal->enqueue_to_backlog` 中断处理程序最终调用函数处理收到的包，`enqueue_to_backlog`
将收到的包加入到当前的 `CPU` 的 `softnet->input_pkt_queue` 宏，并将默认设备 `backlog` 加入到 `softnet_data`
结构的 `poll_list` 链表。

中断处理程序会调用 `netif_rx` 来讲数据包加入到收包对重：

```c
int netif_rx(struct sk_buff *skb)
{
	return netif_rx_internal(skb);
}
```
```c
static int netif_rx_internal(struct sk_buff *skb)
{
	int ret;

	net_timestamp_check(netdev_tstamp_prequeue, skb);
	{
		unsigned int qtail;

		ret = enqueue_to_backlog(skb, get_cpu(), &qtail);
		put_cpu();
	}
	return ret;
}
```
`enqueue_to_backlog` 将 `skb` 加入到当前的 `cpu` 的 `softnet_data->input_pkt_queue` 中，
并将 `softnet_data->backlog` 结构加入到 `softnet_data->poll_list` 链表中，并触发收包软中断：

```c
static int enqueue_to_backlog(struct sk_buff *skb, int cpu,
			      unsigned int *qtail)
{
	struct softnet_data *sd;
	unsigned long flags;
	unsigned int qlen;

	sd = &per_cpu(softnet_data, cpu);

	local_irq_save(flags);

	rps_lock(sd);
	// 检查设备状态
	if (!netif_running(skb->dev))
		goto drop;
	// 获取队列长度
	qlen = skb_queue_len(&sd->input_pkt_queue);
	// 如果队列未满 && 未达到 skb 限制
	if (qlen <= netdev_max_backlog && !skb_flow_limit(skb, qlen)) {
		// 长度不为空，设备已经得到了调度
		if (qlen) {
enqueue:
			// skb 队列
			__skb_queue_tail(&sd->input_pkt_queue, skb);
			input_queue_tail_incr_save(sd, qtail);
			rps_unlock(sd);
			local_irq_restore(flags);
			return NET_RX_SUCCESS;
		}
		// 检测当前 NAPI 状态
		if (!__test_and_set_bit(NAPI_STATE_SCHED, &sd->backlog.state)) {
			if (!rps_ipi_queued(sd))
				// 调用 NAPI 处理包
				____napi_schedule(sd, &sd->backlog);
		}
		// 设置调度之后，入队
		goto enqueue;
	}

drop:
	sd->dropped++;
	rps_unlock(sd);

	local_irq_restore(flags);

	atomic_long_inc(&skb->dev->rx_dropped);
	kfree_skb(skb);
	return NET_RX_DROP;
}
```
net_rx_action->napi_poll 实际上执行的是 `process_backlog` 。`net_rx_action` 与 `napi` 方式
相同，这里略过，主要看 `poll` 回调函数。

符合一次中断多次处理的想法
```c
static int process_backlog(struct napi_struct *napi, int quota)
{
	struct softnet_data *sd = container_of(napi, struct softnet_data, backlog);
	bool again = true;
	int work = 0;

	if (sd_has_rps_ipi_waiting(sd)) {
		local_irq_disable();
		net_rps_action_and_irq_enable(sd);
	}
	//设置设备接收配额
	napi->weight = dev_rx_weight;
	while (again) {
		struct sk_buff *skb;
		//从队列中取skb向上层输入
		while ((skb = __skb_dequeue(&sd->process_queue))) {
			rcu_read_lock();
			__netif_receive_skb(skb);
			rcu_read_unlock();
			input_queue_head_incr(sd);
			//如果达到配额，则完成
			if (++work >= quota)
				return work;

		}

		local_irq_disable();
		rps_lock(sd);
		//如果输入队列为空，没有需要处理
		if (skb_queue_empty(&sd->input_pkt_queue)) {
			//重置状态，处理完毕
			napi->state = 0;
			again = false;
		} else {
			//合并输入队列到处理队列，继续走循环处理
			skb_queue_splice_tail_init(&sd->input_pkt_queue,
						   &sd->process_queue);
		}
		rps_unlock(sd);
		local_irq_enable();
	}
	//返回实际处理的包数
	return work;
}
```


