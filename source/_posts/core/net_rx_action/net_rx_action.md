---
title: net_rx_action
date: 2022-01-08 23:47:45
categories: 
	- [eBPF]
	- [Linux]
	- [内核]
	- [网络代码]
tags:
  - ebpf
author: Jony
---


# net_rx_action
软中断触发，开始接收处理帧队列
```c
static __latent_entropy void net_rx_action(struct softirq_action *h)
{
	struct softnet_data *sd = this_cpu_ptr(&softnet_data); // 获取绑定 CPU 的 softnet_data
	unsigned long time_limit = jiffies + usecs_to_jiffies(netdev_budget_usecs); // 单次处理时长限制
	int budget = netdev_budget; // 单次处理 skb 上限，默认 300
	LIST_HEAD(list); // 移动到列表头
	LIST_HEAD(repoll);
	local_irq_disable();
	list_splice_init(&sd->poll_list, &list);
	local_irq_enable();
	for (;;) {
		struct napi_struct *n; // 
		if (list_empty(&list)) {
			if (!sd_has_rps_ipi_waiting(sd) && list_empty(&repoll))
				goto out;
			break;
		}
		n = list_first_entry(&list, struct napi_struct, poll_list); // 
		budget -= napi_poll(n, &repoll); // NAPI poll 虚拟函数处理，并返回处理数量
		if (unlikely(budget <= 0 || time_after_eq(jiffies, time_limit))) {
			sd->time_squeeze++;
			break;
		}
	}
	local_irq_disable();
	list_splice_tail_init(&sd->poll_list, &list); // 连接两个列表，并重新初始化空列表
	list_splice_tail(&repoll, &list); // 连接两个列表，每个列表是一个队列
	list_splice(&list, &sd->poll_list);
	if (!list_empty(&sd->poll_list))
		__raise_softirq_irqoff(NET_RX_SOFTIRQ);
	net_rps_action_and_irq_enable(sd);
out:
	__kfree_skb_flush();
}

static int napi_poll(struct napi_struct *n, struct list_head *repoll)
{
	void *have;
	int work, weight;
	list_del_init(&n->poll_list);
	have = netpoll_poll_lock(n);
	weight = n->weight;
	work = 0;
	if (test_bit(NAPI_STATE_SCHED, &n->state)) {
		// NAPI 调用 poll 虚拟函数
		work = n->poll(n, weight);
		trace_napi_poll(n, work, weight);
	}
	WARN_ON_ONCE(work > weight);
	if (likely(work < weight))
		goto out_unlock;
	if (unlikely(napi_disable_pending(n))) {
		napi_complete(n); // 将设备从 poll_list 中把设备删除，并触发 NET_RX_SOFTIRQ
		goto out_unlock;
	}
	if (n->gro_bitmask) {
		// 是否使用 gro 功能
		napi_gro_flush(n, HZ >= 1000);
	}

	if (unlikely(!list_empty(&n->poll_list))) {
		pr_warn_once("%s: Budget exhausted after napi rescheduled\n",
			     n->dev ? n->dev->name : "backlog");
		goto out_unlock;
	}
	list_add_tail(&n->poll_list, repoll);
out_unlock:
	netpoll_poll_unlock(have);
	return work;
}
```
`napi_poll` 调用的是虚拟函数 `poll` 是 `NAPI` 采用的标准形式，`process_backlog` 是在 `NAPI`
没有使用的情况下使用的一种帧处理函数操作。与 `NAPI` 的区别就是 `NAPI` 的处理函数被调用的过程中
中断是关闭的，但是 `process_backlog` 处理的过程中断是开启的。所以在对 `softnet_data` 操作时需要
对数据做保护而开启中断，然后关闭中断防止操作数据时出现不可预测的问题。

```c
static int process_backlog(struct napi_struct *napi, int quota)
{
	struct softnet_data *sd = container_of(napi, struct softnet_data, backlog); // 取得 softnet_data
	bool again = true;
	int work = 0;

	if (sd_has_rps_ipi_waiting(sd)) { // 对接收 packet 进行负载均衡
		local_irq_disable();
		net_rps_action_and_irq_enable(sd);
	}
	napi->weight = dev_rx_weight; 
	while (again) {
		struct sk_buff *skb;
		while ((skb = __skb_dequeue(&sd->process_queue))) {
			rcu_read_lock(); // 获取读锁
			__netif_receive_skb(skb); // 接收数据包
			rcu_read_unlock(); // 释放读锁
			input_queue_head_incr(sd); //
			if (++work >= quota) // 更新 work 次数，与处理次数上限比较
				return work;
		}
		local_irq_disable(); // 禁用中断
		rps_lock(sd); // 负载均衡也上锁?当禁用中断的情况先是否需要对 rps 上锁，而且上锁的力度是否需要这么粗
		if (skb_queue_empty(&sd->input_pkt_queue)) { // 如果等于空就结束
			napi->state = 0;
			again = false;
		} else {
			skb_queue_splice_tail_init(&sd->input_pkt_queue,
						   &sd->process_queue);
		}
		rps_unlock(sd);
		local_irq_enable();
	}
	return work;
}
```
`__netif_receive_skb` 是辅助函数，`poll` 会用他来处理入口帧。`__netif_receive_skb` 主要有三个任务:

1. 把帧的副本传给每个协议分流器，如果正在运行的话
2. 把帧的副本传给 `skb->protocol` 所关联的 `L3` 协议处理函数
3. 负责当前层必须处理的一些功能，比如桥接功能。

```c
/*
* @skb: 数据帧
* @pfmemalloc: 是否使用紧急内存
* @ppt_prev: 网络类型，用于返回到上层使用，分配给不同的协议栈处理
*/
static int __netif_receive_skb_core(struct sk_buff *skb, bool pfmemalloc,
				    struct packet_type **ppt_prev)
{
	struct packet_type *ptype, *pt_prev; // 帧协议类型
	rx_handler_func_t *rx_handler; // 帧接收处理器
	struct net_device *orig_dev; // 来源设备列表
	bool deliver_exact = false; // 分流
	int ret = NET_RX_DROP;
	__be16 type;
	net_timestamp_check(!netdev_tstamp_prequeue, skb); // 如果没设置 timestamp 则对 skb->stamp 做初始化
	trace_netif_receive_skb(skb);
	orig_dev = skb->dev; 
	skb_reset_network_header(skb); // 初始化 skb->(n,nh,mac_len)
	if (!skb_transport_header_was_set(skb))  
		skb_reset_transport_header(skb); 
	skb_reset_mac_len(skb); 
	pt_prev = NULL;
another_round:
	skb->skb_iif = skb->dev->ifindex; // 网卡接口
	__this_cpu_inc(softnet_data.processed);
	if (skb->protocol == cpu_to_be16(ETH_P_8021Q) ||
	    skb->protocol == cpu_to_be16(ETH_P_8021AD)) {
		skb = skb_vlan_untag(skb);
		if (unlikely(!skb))
			goto out;
	}
	if (skb_skip_tc_classify(skb)) // 是否需要 tc 分流器
		goto skip_classify; // 不需要分流器就跳过
	if (pfmemalloc)
		goto skip_taps;
	list_for_each_entry_rcu(ptype, &ptype_all, list) {
		if (pt_prev)
			ret = deliver_skb(skb, pt_prev, orig_dev); 
		pt_prev = ptype;
	}
	list_for_each_entry_rcu(ptype, &skb->dev->ptype_all, list) {
		if (pt_prev)
			ret = deliver_skb(skb, pt_prev, orig_dev);
		pt_prev = ptype;
	}
skip_taps:
#ifdef CONFIG_NET_INGRESS
	if (static_branch_unlikely(&ingress_needed_key)) {
		skb = sch_handle_ingress(skb, &pt_prev, &ret, orig_dev);
		if (!skb)
			goto out;
		if (nf_ingress(skb, &pt_prev, &ret, orig_dev) < 0)
			goto out;
	}
#endif
	skb_reset_tc(skb);
skip_classify:
	// ETH_P_ARP、ETH_P_IP、ETH_P_IPV6、ETH_P_8021Q、ETH_P_8021AD 允许使用紧急内存，其他不允许
	if (pfmemalloc && !skb_pfmemalloc_protocol(skb))
		goto drop;
	if (skb_vlan_tag_present(skb)) { // 是否  vlan 协议
		if (pt_prev) {
			ret = deliver_skb(skb, pt_prev, orig_dev);
			pt_prev = NULL;
		}
		if (vlan_do_receive(&skb)) // 做 vlan 的协议解析，然后重新走一遍处理 __netif_receive_skb_core 流程
			goto another_round;
		else if (unlikely(!skb))
			goto out;
	}
	rx_handler = rcu_dereference(skb->dev->rx_handler); // 获取rcu保护的指针解引用
	if (rx_handler) {
		if (pt_prev) {
			ret = deliver_skb(skb, pt_prev, orig_dev); 
			pt_prev = NULL;
		}
		switch (rx_handler(&skb)) {
		case RX_HANDLER_CONSUMED:
			ret = NET_RX_SUCCESS;
			goto out;
		case RX_HANDLER_ANOTHER:
			goto another_round;
		case RX_HANDLER_EXACT:
			deliver_exact = true;
		case RX_HANDLER_PASS:
			break;
		default:
			BUG();
		}
	}
	if (unlikely(skb_vlan_tag_present(skb))) {
		if (skb_vlan_tag_get_id(skb))
			skb->pkt_type = PACKET_OTHERHOST;
		/* Note: we might in the future use prio bits
		 * and set skb->priority like in vlan_do_receive()
		 * For the time being, just ignore Priority Code Point
		 */
		__vlan_hwaccel_clear_tag(skb);
	}
	type = skb->protocol;
	/* 仅在指定时提供完全匹配 */
	if (likely(!deliver_exact)) {
		deliver_ptype_list_skb(skb, &pt_prev, orig_dev, type,
				       &ptype_base[ntohs(type) &
						   PTYPE_HASH_MASK]);
	}
	deliver_ptype_list_skb(skb, &pt_prev, orig_dev, type,
			       &orig_dev->ptype_specific);
	if (unlikely(skb->dev != orig_dev)) {
		deliver_ptype_list_skb(skb, &pt_prev, orig_dev, type,
				       &skb->dev->ptype_specific);
	}
	if (pt_prev) {
		if (unlikely(skb_orphan_frags_rx(skb, GFP_ATOMIC)))
			goto drop;
		*ppt_prev = pt_prev;
	} else {
drop:
		if (!deliver_exact)
			atomic_long_inc(&skb->dev->rx_dropped);
		else
			atomic_long_inc(&skb->dev->rx_nohandler);
		kfree_skb(skb);
		/* Jamal, now you will not able to escape explaining
		 * me how you were going to use this. :-)
		 */
		ret = NET_RX_DROP;
	}
out:
	return ret;
}
```
**diverter 允许内核改变原本发往其他主机帧的 L2 目的地址，使得帧可以改道发往本地主机。这种功能可以**
**用于：所有 IP 包、所有 TCP 包、特定端口的 TCP 包、所有 UDP 包、特定端口的 UDP 包**

# 帧传输

```c
...
		skb_tx_timestamp(skb);
		iowrite32_rep(ioaddr + TX_FIFO, skb->data, (skb->len + 3) >> 2); // ioaddr:地址空间
		dev_consume_skb_any (skb);
		if (ioread16(ioaddr + TxFree) > 1536) { // 如果可用空间大于 1536 字节
			netif_start_queue (dev);	/* AKPM: redundant? */
		} else {
			/* Interrupt us when the FIFO has room for max-sized packet. */
			netif_stop_queue(dev); // 停止传输
			iowrite16(SetTxThreshold + (1536>>2), ioaddr + EL3_CMD);
		}
...
```


# 扩展阅读
[Linux中rps/rfs的原理及实现](https://tqr.ink/2017/07/09/implementation-of-rps-and-rfs/)
[伙伴系统分配器 - PF_MEMALLOC 标志位](https://blog.csdn.net/kickxxx/article/details/9303845)


