```c

bool ip_call_ra_chain(struct sk_buff *skb)
{
	struct ip_ra_chain *ra;
	u8 protocol = ip_hdr(skb)->protocol;
	struct sock *last = NULL;
	struct net_device *dev = skb->dev;
	struct net *net = dev_net(dev);
	// 依赖全局列表去处理 ra 选项
	for (ra = rcu_dereference(net->ipv4.ra_chain); ra; ra = rcu_dereference(ra->next)) {
		// 当前 skb 关联的 sock
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

static int ip_forward_finish(struct net *net, struct sock *sk, struct sk_buff *skb)
{
	struct ip_options *opt	= &(IPCB(skb)->opt);
	__IP_INC_STATS(net, IPSTATS_MIB_OUTFORWDATAGRAMS);
	__IP_ADD_STATS(net, IPSTATS_MIB_OUTOCTETS, skb->len);
#ifdef CONFIG_NET_SWITCHDEV
	if (skb->offload_l3_fwd_mark) {
		consume_skb(skb);
		return 0;
	}
#endif
	if (unlikely(opt->optlen))
		ip_forward_options(skb);
	skb->tstamp = 0;
	return dst_output(net, sk, skb);
}

int ip_forward(struct sk_buff *skb)
{
	u32 mtu;
	struct iphdr *iph;	/* Our header */
	struct rtable *rt;	/* Route we use */
	struct ip_options *opt	= &(IPCB(skb)->opt);
	struct net *net;
	/* that should never happen */
	// 检查确定封包是给 L2 层处理的
	// 当数据帧的目的地址是接收接口的 L2 地址时，skb->pkg_type 会被分配值 PACKET_HOST
	if (skb->pkt_type != PACKET_HOST)
		goto drop;
	if (unlikely(skb->sk))
		goto drop;
	if (skb_warn_if_lro(skb))
		goto drop;
	if (!xfrm4_policy_check(NULL, XFRM_POLICY_FWD, skb))
		goto drop;
	// 路由器告警选项？Yes -> 交给 ip_call_ra_chain 处理，直接返回  NET_RX_SUCCESS
	if (IPCB(skb)->opt.router_alert && ip_call_ra_chain(skb))
		return NET_RX_SUCCESS;
	// 路由转发的流程属于 L3 所以L4校验和不需要关心
	// 设置CHECKSUM_NONE 指定当前校验和没有问题
	// 后面修改了 IP 报头后再传输之前会重新计算校验和
	skb_forward_csum(skb);
	net = dev_net(skb->dev);
	/*
	 *	According to the RFC, we must first decrease the TTL field. If
	 *	that reaches zero, we must reply an ICMP control message telling
	 *	that the packet's lifetime expired.
	 */
	// TTL 小于 1 ，删除包并响应 ICMP 消息，告知来源地，丢弃了封包
	if (ip_hdr(skb)->ttl <= 1)
		goto too_many_hops;
	// IPsec 策略检查
	if (!xfrm4_route_forward(skb))
		goto drop;

	rt = skb_rtable(skb);
	// 是否是严格源路由，rt->rt_uses_gateway 指向下一个跳点
	if (opt->is_strictroute && rt->rt_uses_gateway)
		goto sr_failed;

	IPCB(skb)->flags |= IPSKB_FORWARDED;
	// 获取当前转发设备的 MTU 
	mtu = ip_dst_mtu_maybe_forward(&rt->dst, true);
	// 超出这个 MTU 删除包，响应 ICMP 信息
	if (ip_exceeds_mtu(skb, mtu)) {
		IP_INC_STATS(net, IPSTATS_MIB_FRAGFAILS);
		icmp_send(skb, ICMP_DEST_UNREACH, ICMP_FRAG_NEEDED,
			  htonl(mtu));
		goto drop;
	}
	/* We are about to mangle packet. Copy it! */
	// 包可以共享时，或者封包报头的可用空间不足以存储 L2 报头
	if (skb_cow(skb, LL_RESERVED_SPACE(rt->dst.dev)+rt->dst.header_len))
		goto drop;
	iph = ip_hdr(skb);
	/* Decrease ttl after skb cow done */
	// 递减 TTL 更新 csum
	ip_decrease_ttl(iph);
	/*
	 *	We now generate an ICMP HOST REDIRECT giving the route
	 *	we calculated.
	 */
	// 响应 ICMP 路由重定向消息
	if (IPCB(skb)->flags & IPSKB_DOREDIRECT && !opt->srr &&
	    !skb_sec_path(skb))
		ip_rt_send_redirect(skb);
	// 优先级设置
	if (net->ipv4.sysctl_ip_fwd_update_priority)
		skb->priority = rt_tos2priority(iph->tos);
	return NF_HOOK(NFPROTO_IPV4, NF_INET_FORWARD,
		       net, NULL, skb, skb->dev, rt->dst.dev,
		       ip_forward_finish);
sr_failed:
	/*
	 *	Strict routing permits no gatewaying
	 */
	 icmp_send(skb, ICMP_DEST_UNREACH, ICMP_SR_FAILED, 0);
	 goto drop;
too_many_hops:
	/* Tell the sender its packet died... */
	__IP_INC_STATS(net, IPSTATS_MIB_INHDRERRORS);
	icmp_send(skb, ICMP_TIME_EXCEEDED, ICMP_EXC_TTL, 0);
drop:
	kfree_skb(skb);
	return NET_RX_DROP;
}
```