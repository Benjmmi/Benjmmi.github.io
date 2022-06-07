---
title: 负载均衡 katran： BPF 代码解析
date: 2021-09-04 22:33:46
categories: 
  - [eBPF]
tags:
  - ebpf
  - katran
  - lvs
author: Jony
---

# 主要功能

- 连接维护和删除
- 流量转发，ip-in-ip 模式
- MAP 读写和配置

**主要解析文件：[balancer_kern.c](https://github.com/facebookincubator/katran/blob/master/katran/lib/bpf/balancer_kern.c)**

# XDP 复习

## 工作模式

- Native XDP：最长使用的工作模式，在网络驱动的早期接受路径上。10G 或更高速的网卡基本都支持
- Offloaded XDP：XDP 直接工作 offload 到网卡，类似工作 DPDK 
- Generic XDP： 在不支持 native 和 offload 的情况下，由内核提供 generic XDP 选项。工作
  在网络协议栈的很后面，经常用于开发和测试

## 触发点

内核由两个触发点，分别作用于 ingress 和 egress。XDP 只能作用于 ingress 触发点：

- ingress 触发点：`sch_handle_ingress()`：由 `__netif_receive_skb_core()` 触发
- egress 触发点：`sch_handle_egress()`：由 `_dev_queue_xmit` 触发。

## 返回码

执行 XDP 程序后返回一个结果，用于接下来的数据处理。

- `XDP_DROP`: 表示无条件丢弃包
- `XDP_PASS`: 表示包可以接收将送入网络协议栈。当前函数会分配一个 skb，然后送到 `GRO` 引擎
- `XDP_TX`: 收到包的网卡直接将包在发送出去。多用于 `防火墙` + `负载均衡` 程序，过程中可以
  重写包，支持发卡模式，从哪个设备进来从哪出去
- `XDP_REDIRECT`：与 `XDP_TX` 类似，通过另一个网卡发出去。还支持重定向到另一个 CPU 处理
  网络协议包
- `XDP_ABORTED`: 表示程序异常，

# katran BPF 程序分析

```c
SEC("xdp-balancer")
int balancer_ingress(struct xdp_md *ctx) {
  void *data = (void *)(long)ctx->data; //  数据开始位置
  void *data_end = (void *)(long)ctx->data_end; // 数据结束位置
  struct ethhdr *eth = data;
  __u32 eth_proto;
  __u32 nh_off;
  nh_off = sizeof(struct ethhdr);

  if (data + nh_off > data_end) {
    // 数据不完整
    return XDP_DROP;
  }

  eth_proto = eth->h_proto;

  if (eth_proto == BE_ETH_P_IP) {
    // 处理 IPv4 数据包
    return process_packet(data, nh_off, data_end, false, ctx);
  } else if (eth_proto == BE_ETH_P_IPV6) {
    // 处理 IPv5 数据包
    return process_packet(data, nh_off, data_end, true, ctx);
  } else {
    // 直接送入网络协议栈
    return XDP_PASS;
  }
}
```

```c
static inline int process_packet(void *data, __u64 off, void *data_end,
                                 bool is_ipv6, struct xdp_md *xdp) {

  struct ctl_value *cval; // ctl数组的值，可以包含例如默认路由器的mac地址或其他标志。
  struct real_definition *dst = NULL; // 在ch环中查找客户端数据包
  struct packet_description pckt = {}; // 客户的包元数据
  struct vip_definition vip = {}; // vip定义查找
  struct vip_meta *vip_info; // vip的查找结果
  struct lb_stats *data_stats; // 每个VIP统计
  __u64 iph_len;
  __u8 protocol;
  __u16 original_sport;

  int action;
  __u32 vip_num;
  __u32 mac_addr_pos = 0;
  __u16 pkt_bytes;
  // 处理 l3 协议头
  action = process_l3_headers(
    &pckt, &protocol, off, &pkt_bytes, data, data_end, is_ipv6);
  // 如果对其有 XDP 处理结果
  if (action >= 0) {
    // 返回结果
    return action;
  }
  // 数据流协议。指向 L4 协议内容
  protocol = pckt.flow.proto;

  #ifdef INLINE_DECAP_IPIP // 允许在XDP上下文中进行内联ipip解封装
  /* This is to workaround a verifier issue for 5.2.
   * The reason is that 5.2 verifier does not handle register
   * copy states properly while 5.6 handles properly.
   *
   * For the following source code:
   *   if (protocol == IPPROTO_IPIP || protocol == IPPROTO_IPV6) {
   *     ...
   *   }
   * llvm12 may generate the following simplified code sequence
   *   100  r5 = *(u8 *)(r9 +51)  // r5 is the protocol
   *   120  r4 = r5
   *   121  if r4 s> 0x10 goto target1
   *   122  *(u64 *)(r10 -184) = r5
   *   123  if r4 == 0x4 goto target2
   *   ...
   *   target2:
   *   150  r1 = *(u64 *)(r10 -184)
   *   151  if (r1 != 4) { __unreachable__}
   *
   * For the path 123->150->151, 5.6 correctly noticed
   * at insn 150: r4, r5, *(u64 *)(r10 -184) all have value 4.
   * while 5.2 has *(u64 *)(r10 -184) holding "r5" which could be
   * any value 0-255. In 5.2, "__unreachable" code is verified
   * and it caused verifier failure.
   */
  if (protocol == IPPROTO_IPIP) {
    bool pass = true;
    action = check_decap_dst(&pckt, is_ipv6, &pass);
    if (action >= 0) {
      return action;
    }
    return process_encaped_ipip_pckt(
        &data, &data_end, xdp, &is_ipv6, &protocol, pass);
  } else if (protocol == IPPROTO_IPV6) {
    bool pass = true;
    action = check_decap_dst(&pckt, is_ipv6, &pass);
    if (action >= 0) {
      return action;
    }
    return process_encaped_ipip_pckt(
        &data, &data_end, xdp, &is_ipv6, &protocol, pass);
  }
#endif // INLINE_DECAP_IPIP

// 转化协议包至数据结构
  if (protocol == IPPROTO_TCP) {
    if (!parse_tcp(data, data_end, is_ipv6, &pckt)) {
      // 转换成 TCP 结构失败
      return XDP_DROP;
    }
  } else if (protocol == IPPROTO_UDP) {
    // 转成 UDP 结构失败
    if (!parse_udp(data, data_end, is_ipv6, &pckt)) {
      return XDP_DROP;
    }
  #ifdef INLINE_DECAP_GUE
    if (pckt.flow.port16[1] == bpf_htons(GUE_DPORT)) {
      bool pass = true;
      action = check_decap_dst(&pckt, is_ipv6, &pass);
      if (action >= 0) {
        return action;
      }
      return process_encaped_gue_pckt(&data, &data_end, xdp, is_ipv6, pass);
    }
  #endif // of INLINE_DECAP_GUE
  } else {
    // send to tcp/ip stack
    return XDP_PASS;
  }
  // 已经转换成 4 层协议结构
  // 获取目标 VIP ，需要根据协议内容判断是否是 IPv6
  if (is_ipv6) {
    memcpy(vip.vipv6, pckt.flow.dstv6, 16);
  } else {
    // 获取 VIP 
    vip.vip = pckt.flow.dst;
  }
  // 端口协议
  vip.port = pckt.flow.port16[1];
  vip.proto = pckt.flow.proto;
  // 查找 VIP 后面的 Real IP
  vip_info = bpf_map_lookup_elem(&vip_map, &vip);
  if (!vip_info) {
    // Real IP 不存在
    vip.port = 0; // 这步的目的因为 DR 模式忽略了端口号
    vip_info = bpf_map_lookup_elem(&vip_map, &vip);
    if (!vip_info) {
      // 如果还没有找到就送到上传协议栈处理
      return XDP_PASS;
    }

    // 如果只使用DST端口进行哈希计算
    if (!(vip_info->flags & F_HASH_DPORT_ONLY)) {
      // VIP, which doesnt care about dst port (all packets to this VIP w/ diff
      // dst port but from the same src port/ip must go to the same real
      // VIP，它不关心dst端口（所有到这个VIP的数据包都有不同的dst端口，但来自同一个src端口/ip的数据包必须转到同一个real
      pckt.flow.port16[1] = 0;
    }
  }

  // 超出最大数据包承载量
  // 如果定义了可以使用 ICMP 处理，那么就会返回 ICMP 处理结果
  if (data_end - data > MAX_PCKT_SIZE) {
    REPORT_PACKET_TOOBIG(xdp, data, data_end - data, false);
#ifdef ICMP_TOOBIG_GENERATION
    __u32 stats_key = MAX_VIPS + ICMP_TOOBIG_CNTRS;
    data_stats = bpf_map_lookup_elem(&stats, &stats_key);
    if (!data_stats) {
      return XDP_DROP;
    }
    // 啥意思
    if (is_ipv6) {
      data_stats->v2 += 1;
    } else {
      data_stats->v1 += 1;
    }
    return send_icmp_too_big(xdp, is_ipv6, data_end - data);
#else
    return XDP_DROP;
#endif
  }

  __u32 stats_key = MAX_VIPS + LRU_CNTRS;  // 512 + 0 ? lru缓存命中相关计数器的偏移量
  // 查找每个 VIP 的数据统计 , v1 v2 指的是什么意思？
  data_stats = bpf_map_lookup_elem(&stats, &stats_key);
  if (!data_stats) {
    return XDP_DROP;
  }

  // total packets // 总的处理的数据包数量
  data_stats->v1 += 1;

  // Lookup dst based on id in packet QUIC 负载均衡
  if ((vip_info->flags & F_QUIC_VIP)) {
    __u32 quic_stats_key = MAX_VIPS + QUIC_ROUTE_STATS;
    struct lb_stats* quic_stats = bpf_map_lookup_elem(&stats, &quic_stats_key);
    if (!quic_stats) {
      return XDP_DROP;
    }
    int real_index;
    real_index = parse_quic(data, data_end, is_ipv6, &pckt);
    if (real_index > 0) {
      increment_quic_cid_version_stats(real_index);
      __u32 key = real_index;
      __u32 *real_pos = bpf_map_lookup_elem(&server_id_map, &key);
      if (real_pos) {
        key = *real_pos;
        if (key == 0) {
          increment_quic_cid_drop_real_0();
          // increment counter for the CH based routing
          quic_stats->v1 += 1;
        } else {
          pckt.real_index = key;
          dst = bpf_map_lookup_elem(&reals, &key);
          if (!dst) {
            increment_quic_cid_drop_no_real();
            REPORT_QUIC_PACKET_DROP_NO_REAL(xdp, data, data_end - data, false);
            return XDP_DROP;
          }
          quic_stats->v2 += 1;
        }
      } else {
        // increment counter for the CH based routing
        quic_stats->v1 += 1;
      }
    } else {
      quic_stats->v1 += 1;
    }
  }

  // save the original sport before making real selection, possibly changing its value.
  // 在做出真正的选择之前，保存原来的旧值，可能会改变它的价值。
  original_sport = pckt.flow.port16[0];

  if (!dst) {
    // 使用quic的连接处理
    if ((vip_info->flags & F_HASH_NO_SRC_PORT)) {
      // service, where diff src port, but same ip must go to the same real,
      // e.g. gfs
      pckt.flow.port16[0] = 0;
    }
    __u32 cpu_num = bpf_get_smp_processor_id();
    void *lru_map = bpf_map_lookup_elem(&lru_mapping, &cpu_num);
    if (!lru_map) {
      lru_map = &fallback_cache;
      __u32 lru_stats_key = MAX_VIPS + FALLBACK_LRU_CNTR;
      struct lb_stats *lru_stats = bpf_map_lookup_elem(&stats, &lru_stats_key);
      if (!lru_stats) {
        return XDP_DROP;
      }
      // We were not able to retrieve per cpu/core lru and falling back to
      // default one. This counter should never be anything except 0 in prod.
      // We are going to use it for monitoring.
      lru_stats->v1 += 1;
    }
#ifdef TCP_SERVER_ID_ROUTING
    // First try to lookup dst in the tcp_hdr_opt (if enabled)
    if (pckt.flow.proto == IPPROTO_TCP && !(pckt.flags & F_SYN_SET)) {
      __u32 routing_stats_key = MAX_VIPS + TCP_SERVER_ID_ROUTE_STATS;
      struct lb_stats* routing_stats =
          bpf_map_lookup_elem(&stats, &routing_stats_key);
      if (!routing_stats) {
        return XDP_DROP;
      }
      if (tcp_hdr_opt_lookup(
              data,
              data_end,
              is_ipv6,
              &dst,
              &pckt,
              vip_info->flags & F_LRU_BYPASS,
              lru_map) == FURTHER_PROCESSING) {
        routing_stats->v1 += 1;
      } else {
        routing_stats->v2 += 1;
      }
    }
#endif // TCP_SERVER_ID_ROUTING

    // Next, try to lookup dst in the lru_cache
    if (!dst && !(pckt.flags & F_SYN_SET) &&
        !(vip_info->flags & F_LRU_BYPASS)) {
      // 连接表，查找
      connection_table_lookup(&dst, &pckt, lru_map);
    }

    // if dst is not found, route via consistent-hashing of the flow.
    if (!dst) {
      if (pckt.flow.proto == IPPROTO_TCP) {
        __u32 lru_stats_key = MAX_VIPS + LRU_MISS_CNTR;
        struct lb_stats *lru_stats = bpf_map_lookup_elem(
          &stats, &lru_stats_key);
        if (!lru_stats) {
          return XDP_DROP;
        }
        if (pckt.flags & F_SYN_SET) {
          // miss because of new tcp session
          lru_stats->v1 += 1;
        } else {
          // miss of non-syn tcp packet. could be either because of LRU trashing
          // or because another katran is restarting and all the sessions
          // have been reshuffled
          REPORT_TCP_NONSYN_LRUMISS(xdp, data, data_end - data, false);
          lru_stats->v2 += 1;
        }
      }
      if(!get_packet_dst(&dst, &pckt, vip_info, is_ipv6, lru_map)) {
        return XDP_DROP;
      }
      // lru misses (either new connection or lru is full and starts to trash)
      data_stats->v2 += 1;
    }
  }
  // 默认第 0 ifindex
  cval = bpf_map_lookup_elem(&ctl_array, &mac_addr_pos);

  if (!cval) {
    return XDP_DROP;
  }
  // vip 序列号
  vip_num = vip_info->vip_num;
  data_stats = bpf_map_lookup_elem(&stats, &vip_num);
  if (!data_stats) {
    return XDP_DROP;
  }
  data_stats->v1 += 1; // 包数量
  data_stats->v2 += pkt_bytes; // 包大小

  // per real statistics
  // 每个 Real IP 统计
  data_stats = bpf_map_lookup_elem(&reals_stats, &pckt.real_index);
  if (!data_stats) {
    return XDP_DROP;
  }
  data_stats->v1 += 1; // 包数量
  data_stats->v2 += pkt_bytes; // 包大小
#ifdef LOCAL_DELIVERY_OPTIMIZATION
  if ((vip_info->flags & F_LOCAL_VIP) && (dst->flags & F_LOCAL_REAL)) {
    return XDP_PASS;
  }
#endif
  // restore the original sport value to use it as a seed for the GUE sport
  // 恢复旧值，作为 GUE 的替换
  pckt.flow.port16[0] = original_sport;
  // 修改为目标 mac 
  // 重新计算校验和
  if (dst->flags & F_IPV6) {
    if(!PCKT_ENCAP_V6(xdp, cval, is_ipv6, &pckt, dst, pkt_bytes)) {
      return XDP_DROP;
    }
  } else {
    if(!PCKT_ENCAP_V4(xdp, cval, &pckt, dst, pkt_bytes)) {
      return XDP_DROP;
    }
  }

  return XDP_TX;
}
```





