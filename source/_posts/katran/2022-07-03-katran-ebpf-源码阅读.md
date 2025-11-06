---
title: katran ebpf 源码阅读 1
date: 2021-04-01 17:29:11
categories: 'Other'
tags:
  - katran
  - lvs
author: Jony
---

katran 是优秀 L4 负载均衡器，由 Facebook 开源出来的，在研究 ebpf 的同时，一起研究下其实现方式：
基础的 ebpf 中的数据结构如下：
```c
// 浏览元数据，应该存储的是 4 元组数据信息
struct flow_key {
  union {
    __be32 src; // 来源地址
    __be32 srcv6[4];
  };
  union {
    __be32 dst;//目标地址
    __be32 dstv6[4];
  };
  union {
    __u32 ports; // 端口号，具体是来源的还是目标的往下看
    __u16 port16[2];
  };
  __u8 proto; // 协议
};
```

```c
// 客户端包的描述结构
struct packet_description {
  struct flow_key flow; // 基础的流信息
  __u32 real_index; // 真实的网卡
  __u8 flags; // 什么标记??
  // dscp / ToS value in client's packet
  __u8 tos; // TOS 
};
```

```c
// tl 数组的值，可以包含例如 默认路由器的mac地址或其他标志
struct ctl_value {
  union {
    __u64 value;
    __u32 ifindex;
    __u8 mac[6];
  };
};

```

```c
// vip 的定义，用于查找
struct vip_definition {
  union { // 虚拟 IP 
    __be32 vip;
    __be32 vipv6[4];
  };
  __u16 port; // 虚拟 IP 端口
  __u8 proto; // 虚拟IP支持的协议
};

```

```c
// VIP  查找结果
struct vip_meta {
  __u32 flags; // 
  __u32 vip_num; //VIP 的数量？？
};

```

```c
// 从 LRU_MAP 发送客户端数据包的位置
struct real_pos_lru {
  __u32 pos;
  __u64 atime;
};

```

```c
// 从 ch ring 中的查找中将客户端的数据包发送到哪里。
struct real_definition {
  union { //
    __be32 dst;
    __be32 dstv6[4];
  };
  __u8 flags;
};

```

```c
// 每vip统计
struct lb_stats {
  __u64 v1;
  __u64 v2;
};

```

```c
// 用于 ipv4 lpm 查找的密钥
struct v4_lpm_key {
  __u32 prefixlen;
  __be32 addr;
};

```

```c
// 用于 ipv6 lpm 查找的密钥
struct v6_lpm_key {
  __u32 prefixlen;
  __be32 addr[4];
};

```

```c
// 地址
struct address {
  union {
    __be32 addr;
    __be32 addrv6[4];
  };
};

```

```c
// 关于数据包的元数据，通过事件管道复制到用户空间
struct event_metadata {
  __u32 event;
  __u32 pkt_size;
  __u32 data_len;
} __attribute__((__packed__));

```

```c
// GUE报文保存的路由信息
struct flow_debug_info {
  union {
    __be32 l4_hop;
    __be32 l4_hopv6[4];
  };
  union {
    __be32 this_hop;
    __be32 this_hopv6[4];
  };
};
```

虽然上面的数据结构给出了结构定义，但是并没给出如何关联起来。所以如何关联起来应该使用了  MAP 查找

看下 MAP 结构：

```c
// map，其中包含我们正在为其进行负载平衡的所有vip
struct {
  __uint(type, BPF_MAP_TYPE_HASH);
  __type(key, struct vip_definition);
  __type(value, struct vip_meta);
  __uint(max_entries, MAX_VIPS);
  __uint(map_flags, NO_FLAGS);
} vip_map SEC(".maps");

```
**所有的 VIP 定义都存在这个HASH 中**

```c
// 包含 cpu 核心到 lru 映射的映射
struct {
  __uint(type, BPF_MAP_TYPE_ARRAY_OF_MAPS);
  __uint(key_size, sizeof(__u32));
  __uint(value_size, sizeof(__u32));
  __uint(max_entries, MAX_SUPPORTED_CPUS);
  __uint(map_flags, NO_FLAGS);
} lru_mapping SEC(".maps");

```
**为了提高查询速度，为每个 CPU 单独设置了一个 MAP，这样的情况下就避免了多CPU 获取锁的问题**

```c
// 备份 lru。 用户单元测试
struct {
  __uint(type, BPF_MAP_TYPE_LRU_HASH);
  __type(key, struct flow_key);
  __type(value, struct real_pos_lru);
  __uint(max_entries, DEFAULT_LRU_SIZE);
  __uint(map_flags, NO_FLAGS);
} fallback_cache SEC(".maps");

```

```c
// 包含所有 vip 到真实映射的映射
struct {
  __uint(type, BPF_MAP_TYPE_ARRAY);
  __type(key, __u32);
  __type(value, __u32);
  __uint(max_entries, CH_RINGS_SIZE);
  __uint(map_flags, NO_FLAGS);
} ch_rings SEC(".maps");

```

这里map 映射了 vip-realip 。通过指针的方式进行了存储。

```c
// 包含不透明真实的 id 到真实映射的映射
struct {
  __uint(type, BPF_MAP_TYPE_ARRAY);
  __type(key, __u32);
  __type(value, struct real_definition);
  __uint(max_entries, MAX_REALS);
  __uint(map_flags, NO_FLAGS);
} reals SEC(".maps");

```

```c
// 映射每个真实的 pps/bps 统计数据
struct {
  __uint(type, BPF_MAP_TYPE_PERCPU_ARRAY);
  __type(key, __u32);
  __type(value, struct lb_stats);
  __uint(max_entries, MAX_REALS);
  __uint(map_flags, NO_FLAGS);
} reals_stats SEC(".maps");

```

```c
// 带有每个VIP统计数据的地图
struct {
  __uint(type, BPF_MAP_TYPE_PERCPU_ARRAY);
  __type(key, __u32);
  __type(value, struct lb_stats);
  __uint(max_entries, STATS_MAP_SIZE);
  __uint(map_flags, NO_FLAGS);
} stats SEC(".maps");

```

```c
//将服务器 ID 映射到真实的 ID 映射。 id 可以嵌入到 QUIC 或 
// TCP（如果启用）数据包的标头中，用于路由现有流的数据包
struct {
  __uint(type, BPF_MAP_TYPE_ARRAY);
  __type(key, __u32);
  __type(value, __u32);
  __uint(max_entries, MAX_QUIC_REALS);
  __uint(map_flags, NO_FLAGS);
} server_id_map SEC(".maps");

```

```c
// 用于最长匹配查找，查找出对应的 VIP
struct {
  __uint(type, BPF_MAP_TYPE_LPM_TRIE);
  __type(key, struct v4_lpm_key);
  __type(value, __u32);
  __uint(max_entries, MAX_LPM_SRC);
  __uint(map_flags, BPF_F_NO_PREALLOC);
} lpm_src_v4 SEC(".maps");

```

```c
struct {
  __uint(type, BPF_MAP_TYPE_LPM_TRIE);
  __type(key, struct v6_lpm_key);
  __type(value, __u32);
  __uint(max_entries, MAX_LPM_SRC);
  __uint(map_flags, BPF_F_NO_PREALLOC);
} lpm_src_v6 SEC(".maps");


```

```c
// 全局LRU ，支持 per CPU 的并发查找
struct {
  __uint(type, BPF_MAP_TYPE_ARRAY_OF_MAPS);
  __uint(key_size, sizeof(__u32));
  __uint(value_size, sizeof(__u32));
  __uint(max_entries, MAX_SUPPORTED_CPUS);
  __uint(map_flags, NO_FLAGS);
} global_lru_maps SEC(".maps");

```

```c
struct {
  __uint(type, BPF_MAP_TYPE_LRU_HASH);
  __type(key, struct flow_key);
  __type(value, __u32);
  __uint(max_entries, DEFAULT_GLOBAL_LRU_SIZE);
  __uint(map_flags, NO_FLAGS);
} fallback_glru SEC(".maps");
```