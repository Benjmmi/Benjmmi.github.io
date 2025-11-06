---
layout: post
title: "Linux 提交 patch-3"
keywords: ""
description: ""
tagline: ""
date: '2022-11-06 19:28:19'
category: linux
tags: linux
---



```c
/*
 *	Process Router Attention IP option (RFC 2113)
 */
bool ip_call_ra_chain(struct sk_buff *skb)
{
	struct ip_ra_chain *ra;
	u8 protocol = ip_hdr(skb)->protocol;
	struct sock *last = NULL;
	struct net_device *dev = skb->dev;
	struct net *net = dev_net(dev);
	for (ra = rcu_dereference(net->ipv4.ra_chain); ra; ra = rcu_dereference(ra->next)) {
		struct sock *sk = ra->sk;
		/* If socket is bound to an interface, only report
		 * the packet if it came  from that interface.
		 */
		if (sk && inet_sk(sk)->inet_num == protocol &&
		    (!sk->sk_bound_dev_if ||
		     sk->sk_bound_dev_if == dev->ifindex)) {
			if (ip_is_fragment(ip_hdr(skb))) {
				if (ip_defrag(net, skb, IP_DEFRAG_CALL_RA_CHAIN))
					return true;
			}
			if (last) {
				struct sk_buff *skb2 = skb_clone(skb, GFP_ATOMIC);
				if (skb2)
					raw_rcv(last, skb2);
			}
			last = sk;
		}
	}
	if (last) {
		raw_rcv(last, skb);
		return true;
	}
	return false;
}
void ip_protocol_deliver_rcu(struct net *net, struct sk_buff *skb, int protocol)
{
	const struct net_protocol *ipprot;
	int raw, ret;
resubmit:
	raw = raw_local_deliver(skb, protocol);
	ipprot = rcu_dereference(inet_protos[protocol]);
	if (ipprot) {
		if (!ipprot->no_policy) {
			if (!xfrm4_policy_check(NULL, XFRM_POLICY_IN, skb)) {
				kfree_skb(skb);
				return;
			}
			nf_reset(skb);
		}
		ret = ipprot->handler(skb);
		if (ret < 0) {
			protocol = -ret;
			goto resubmit;
		}
		__IP_INC_STATS(net, IPSTATS_MIB_INDELIVERS);
	} else {
		if (!raw) {
			if (xfrm4_policy_check(NULL, XFRM_POLICY_IN, skb)) {
				__IP_INC_STATS(net, IPSTATS_MIB_INUNKNOWNPROTOS);
				icmp_send(skb, ICMP_DEST_UNREACH,
					  ICMP_PROT_UNREACH, 0);
			}
			kfree_skb(skb);
		} else {
			__IP_INC_STATS(net, IPSTATS_MIB_INDELIVERS);
			consume_skb(skb);
		}
	}
}
static int ip_local_deliver_finish(struct net *net, struct sock *sk, struct sk_buff *skb)
{
	__skb_pull(skb, skb_network_header_len(skb));
	rcu_read_lock();
	ip_protocol_deliver_rcu(net, skb, ip_hdr(skb)->protocol);
	rcu_read_unlock();
	return 0;
}
/*
 * 	Deliver IP Packets to the higher protocol layers.
 */
int ip_local_deliver(struct sk_buff *skb)
{
	/*
	 *	Reassemble IP fragments.
	 */
	struct net *net = dev_net(skb->dev);
	if (ip_is_fragment(ip_hdr(skb))) {
		if (ip_defrag(net, skb, IP_DEFRAG_LOCAL_DELIVER))
			return 0;
	}
	return NF_HOOK(NFPROTO_IPV4, NF_INET_LOCAL_IN,
		       net, NULL, skb, skb->dev, NULL,
		       ip_local_deliver_finish);
}
static inline bool ip_rcv_options(struct sk_buff *skb)
{
	struct ip_options *opt;
	const struct iphdr *iph;
	struct net_device *dev = skb->dev;
	/* It looks as overkill, because not all
	   IP options require packet mangling.
	   But it is the easiest for now, especially taking
	   into account that combination of IP options
	   and running sniffer is extremely rare condition.
					      --ANK (980813)
	*/
	// 如果缓冲区是共享的，那么就会 clone 一份副本，因为处理 options 时可能会修改 ip 报头，
	if (skb_cow(skb, skb_headroom(skb))) { 
		__IP_INC_STATS(dev_net(dev), IPSTATS_MIB_INDISCARDS);
		goto drop;
	}
	iph = ip_hdr(skb);
	// 取出 inet_skb_parm 数据结构（也是 skb->cb）
	opt = &(IPCB(skb)->opt);
	opt->optlen = iph->ihl*4 - sizeof(struct iphdr);
	// 解析 optinos
	if (ip_options_compile(dev_net(dev), opt, skb)) {
		__IP_INC_STATS(dev_net(dev), IPSTATS_MIB_INHDRERRORS);
		goto drop;
	}
	if (unlikely(opt->srr)) { // 不是严格源路由
		struct in_device *in_dev = __in_dev_get_rcu(dev); //  获取输入设备
		if (in_dev) {
			if (!IN_DEV_SOURCE_ROUTE(in_dev)) { // 如果当前输入设备不在路由表下
				if (IN_DEV_LOG_MARTIANS(in_dev))
					net_info_ratelimited("source route option %pI4 -> %pI4\n",
							     &iph->saddr,
							     &iph->daddr);
				goto drop;
			}
		}
		if (ip_options_rcv_srr(skb))
			goto drop;
	}
	return false;
drop:
	return true;
}
static int ip_rcv_finish_core(struct net *net, struct sock *sk,
			      struct sk_buff *skb, struct net_device *dev)
{
	const struct iphdr *iph = ip_hdr(skb);
	int (*edemux)(struct sk_buff *skb);
	struct rtable *rt;
	int err;
	if (net->ipv4.sysctl_ip_early_demux &&
	    !skb_dst(skb) &&  // 检查目的地是否存在
	    !skb->sk &&
	    !ip_is_fragment(iph)) {
		const struct net_protocol *ipprot;
		int protocol = iph->protocol;
		ipprot = rcu_dereference(inet_protos[protocol]);
		if (ipprot && (edemux = READ_ONCE(ipprot->early_demux))) {
			err = edemux(skb);
			if (unlikely(err))
				goto drop_error;
			/* must reload iph, skb->head might have changed */
			iph = ip_hdr(skb);
		}
	}
	/*
	 *	Initialise the virtual path cache for the packet. It describes
	 *	how the packet travels inside Linux networking.
	 */
	if (!skb_valid_dst(skb)) {
		err = ip_route_input_noref(skb, iph->daddr, iph->saddr,
					   iph->tos, dev);
		if (unlikely(err))
			goto drop_error;
	}
// 更新 TC 层的一些信息
#ifdef CONFIG_IP_ROUTE_CLASSID
	if (unlikely(skb_dst(skb)->tclassid)) {
		struct ip_rt_acct *st = this_cpu_ptr(ip_rt_acct);
		u32 idx = skb_dst(skb)->tclassid;
		st[idx&0xFF].o_packets++;
		st[idx&0xFF].o_bytes += skb->len;
		st[(idx>>16)&0xFF].i_packets++;
		st[(idx>>16)&0xFF].i_bytes += skb->len;
	}
#endif
	// 如果 IP 报头大于20 字节，就说明有 options 需要处理，ip_rcv_options处理 Option
	if (iph->ihl > 5 && ip_rcv_options(skb))
		goto drop;
	rt = skb_rtable(skb); // 获取路由类型
	if (rt->rt_type == RTN_MULTICAST) {
		__IP_UPD_PO_STATS(net, IPSTATS_MIB_INMCAST, skb->len);
	} else if (rt->rt_type == RTN_BROADCAST) {
		__IP_UPD_PO_STATS(net, IPSTATS_MIB_INBCAST, skb->len);
	} else if (skb->pkt_type == PACKET_BROADCAST ||
		   skb->pkt_type == PACKET_MULTICAST) {
		struct in_device *in_dev = __in_dev_get_rcu(dev);
		/* RFC 1122 3.3.6:
		 *
		 *   When a host sends a datagram to a link-layer broadcast
		 *   address, the IP destination address MUST be a legal IP
		 *   broadcast or IP multicast address.
		 *
		 *   A host SHOULD silently discard a datagram that is received
		 *   via a link-layer broadcast (see Section 2.4) but does not
		 *   specify an IP multicast or broadcast destination address.
		 *
		 * This doesn't explicitly say L2 *broadcast*, but broadcast is
		 * in a way a form of multicast and the most common use case for
		 * this is 802.11 protecting against cross-station spoofing (the
		 * so-called "hole-196" attack) so do it for both.
		 */
		if (in_dev &&
		    IN_DEV_ORCONF(in_dev, DROP_UNICAST_IN_L2_MULTICAST))
			goto drop;
	}
	return NET_RX_SUCCESS;
drop:
	kfree_skb(skb);
	return NET_RX_DROP;
drop_error:
	if (err == -EXDEV)
		__NET_INC_STATS(net, LINUX_MIB_IPRPFILTER);
	goto drop;
}
// 决定 IP 包是否是本地还是转发出去
static int ip_rcv_finish(struct net *net, struct sock *sk, struct sk_buff *skb)
{
	struct net_device *dev = skb->dev;
	int ret;
	/* if ingress device is enslaved to an L3 master device pass the
	 * skb to its handler for processing
	 */
	skb = l3mdev_ip_rcv(skb);
	if (!skb)
		return NET_RX_SUCCESS;
	ret = ip_rcv_finish_core(net, sk, skb, dev);
	if (ret != NET_RX_DROP)
		ret = dst_input(skb);
	return ret;
}
/*
 * 	Main IP Receive routine.
 *  做一些基本的健康检查
 */
static struct sk_buff *ip_rcv_core(struct sk_buff *skb, struct net *net)
{
	const struct iphdr *iph;
	u32 len;
	/* When the interface is in promisc. mode, drop all the crap
	 * that it receives, do not try to analyse it.
	 */ 
	if (skb->pkt_type == PACKET_OTHERHOST)
		goto drop; // 如果是其他 hosts 就删除
	__IP_UPD_PO_STATS(net, IPSTATS_MIB_IN, skb->len);
	skb = skb_share_check(skb, GFP_ATOMIC); // 如果是可以共享的 skb ,就 clone 新的一份
	if (!skb) { // 否则就删除
		__IP_INC_STATS(net, IPSTATS_MIB_INDISCARDS);
		goto out;
	}
	// 检查实际data 数据最小也有 iphdr 大，每个 IP 包必须包含完整的 IP 头
	if (!pskb_may_pull(skb, sizeof(struct iphdr)))
		goto inhdr_error; 
	iph = ip_hdr(skb);
	/*
	 *	RFC1122: 3.2.1.2 MUST silently discard any IP frame that fails the checksum.
	 *
	 *	Is the datagram acceptable?
	 *
	 *	1.	Length at least the size of an ip header
	 *	2.	Version of 4
	 *	3.	Checksums correctly. [Speed optimisation for later, skip loopback checksums]
	 *	4.	Doesn't have a bogus length
	 */
	if (iph->ihl < 5 || iph->version != 4)
		goto inhdr_error;
	BUILD_BUG_ON(IPSTATS_MIB_ECT1PKTS != IPSTATS_MIB_NOECTPKTS + INET_ECN_ECT_1);
	BUILD_BUG_ON(IPSTATS_MIB_ECT0PKTS != IPSTATS_MIB_NOECTPKTS + INET_ECN_ECT_0);
	BUILD_BUG_ON(IPSTATS_MIB_CEPKTS != IPSTATS_MIB_NOECTPKTS + INET_ECN_CE);
	__IP_ADD_STATS(net,
		       IPSTATS_MIB_NOECTPKTS + (iph->tos & INET_ECN_MASK),
		       max_t(unsigned short, 1, skb_shinfo(skb)->gso_segs));
	// 再次检查，上面的检查是为了确定 IPHDR  的存在
	if (!pskb_may_pull(skb, iph->ihl*4))
		goto inhdr_error;
	iph = ip_hdr(skb);
	// 快速计算校验和
	if (unlikely(ip_fast_csum((u8 *)iph, iph->ihl))) 
		goto csum_error;
	// 确保缓冲区的长度大于等于IP 报头中记录的长度
	// tot_len 是 L2 填补的有效负荷包括了IP报头和后面的数据
	len = ntohs(iph->tot_len);
	if (skb->len < len) {
		__IP_INC_STATS(net, IPSTATS_MIB_INTRUNCATEDPKTS);
		goto drop;
	} else if (len < (iph->ihl*4))
		goto inhdr_error;
	/* Our transport medium may have padded the buffer out. Now we know it
	 * is IP we can trim to the true length of the frame.
	 * Note this now means skb->len holds ntohs(iph->tot_len).
	 * L2 可能会将达到最小长度的数据包填充到最小长度，。如果有就删减去强制csum 检查失败。
	 * 因为填充其实应该在 NIC 中就已经被去除了
	 */
	if (pskb_trim_rcsum(skb, len)) {
		__IP_INC_STATS(net, IPSTATS_MIB_INDISCARDS);
		goto drop;
	}
	iph = ip_hdr(skb);
	// L4 的 Header 指针位置
	skb->transport_header = skb->network_header + iph->ihl*4;
	/* Remove any debris in the socket control block */
	memset(IPCB(skb), 0, sizeof(struct inet_skb_parm));
	IPCB(skb)->iif = skb->skb_iif;
	/* Must drop socket now because of tproxy. */
	skb_orphan(skb);
	return skb;
csum_error:
	__IP_INC_STATS(net, IPSTATS_MIB_CSUMERRORS);
inhdr_error:
	__IP_INC_STATS(net, IPSTATS_MIB_INHDRERRORS);
drop:
	kfree_skb(skb);
out:
	return NULL;
}
/*
 * IP receive entry point
 */
int ip_rcv(struct sk_buff *skb, struct net_device *dev, struct packet_type *pt,
	   struct net_device *orig_dev)
{
	struct net *net = dev_net(dev);
	skb = ip_rcv_core(skb, net);
	if (skb == NULL)
		return NET_RX_DROP;
	return NF_HOOK(NFPROTO_IPV4, NF_INET_PRE_ROUTING,
		       net, NULL, skb, dev, NULL,
		       ip_rcv_finish);
}
static void ip_sublist_rcv_finish(struct list_head *head)
{
	struct sk_buff *skb, *next;
	list_for_each_entry_safe(skb, next, head, list) {
		skb_list_del_init(skb);
		dst_input(skb);
	}
}
static void ip_list_rcv_finish(struct net *net, struct sock *sk,
			       struct list_head *head)
{
	struct dst_entry *curr_dst = NULL;
	struct sk_buff *skb, *next;
	struct list_head sublist;
	INIT_LIST_HEAD(&sublist);
	list_for_each_entry_safe(skb, next, head, list) {
		struct net_device *dev = skb->dev;
		struct dst_entry *dst;
		skb_list_del_init(skb);
		/* if ingress device is enslaved to an L3 master device pass the
		 * skb to its handler for processing
		 */
		skb = l3mdev_ip_rcv(skb);
		if (!skb)
			continue;
		if (ip_rcv_finish_core(net, sk, skb, dev) == NET_RX_DROP)
			continue;
		dst = skb_dst(skb);
		if (curr_dst != dst) {
			/* dispatch old sublist */
			if (!list_empty(&sublist))
				ip_sublist_rcv_finish(&sublist);
			/* start new sublist */
			INIT_LIST_HEAD(&sublist);
			curr_dst = dst;
		}
		list_add_tail(&skb->list, &sublist);
	}
	/* dispatch final sublist */
	ip_sublist_rcv_finish(&sublist);
}
static void ip_sublist_rcv(struct list_head *head, struct net_device *dev,
			   struct net *net)
{
	NF_HOOK_LIST(NFPROTO_IPV4, NF_INET_PRE_ROUTING, net, NULL,
		     head, dev, NULL, ip_rcv_finish);
	ip_list_rcv_finish(net, NULL, head);
}
/* Receive a list of IP packets */
void ip_list_rcv(struct list_head *head, struct packet_type *pt,
		 struct net_device *orig_dev)
{
	struct net_device *curr_dev = NULL;
	struct net *curr_net = NULL;
	struct sk_buff *skb, *next;
	struct list_head sublist;
	INIT_LIST_HEAD(&sublist);
	list_for_each_entry_safe(skb, next, head, list) {
		struct net_device *dev = skb->dev;
		struct net *net = dev_net(dev);
		skb_list_del_init(skb);
		skb = ip_rcv_core(skb, net);
		if (skb == NULL)
			continue;
		if (curr_dev != dev || curr_net != net) {
			/* dispatch old sublist */
			if (!list_empty(&sublist))
				ip_sublist_rcv(&sublist, curr_dev, curr_net);
			/* start new sublist */
			INIT_LIST_HEAD(&sublist);
			curr_dev = dev;
			curr_net = net;
		}
		list_add_tail(&skb->list, &sublist);
	}
	/* dispatch final sublist */
	ip_sublist_rcv(&sublist, curr_dev, curr_net);
}
```