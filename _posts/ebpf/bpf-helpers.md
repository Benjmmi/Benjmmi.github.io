---
title: libbpf 学习使用
date: 2021-07-13 16:25:19
categories: 
	- [eBPF]
tags:
  - ebpf
  - cilium
author: Jony
---


## long bpf_skb_load_bytes(const void \*skb, u32 offset, void \*to,u32 len)

**描述**
这个辅助函数是作为一种从数据包中加载数据的简单方法提供的。它可用于将 len 字节从与 skb 关联的数据包的偏移量加载到指向的缓冲区中。

自Linux 4.7以来，这个辅助函数的使用大多被 "直接数据包访问 "所取代，使得数据包可以通过`skb->data`和`skb->data_end`分别
指向数据包的第一个字节和数据包的最后一个字节后的字节进行操作。然而，如果人们希望一次从数据包中读取大量数据到eBPF堆栈中，它仍然是有用的。

如果成功，则返回0；如果失败，则返回负错误。



## long bpf_skb_change_proto(struct sk_buff \*skb, \_\_be16 proto, u64 flags)

**描述**

将skb的协议改为**proto**。目前支持从IPv4转换到IPv6，以及从IPv6转换到IPv4。该辅助函数负责转换的基础工作，包括调整套接字缓冲区的大小。
EBPF程序应通过 `skb_store_bytes()` 填充新的标头(如果有的话)，并使用`bpf_l3_csum_place()`和`bpf_l4_csum_place()`重新计算校验和。
这个辅助函数的主要用例是在eBPF程序之外执行NAT64操作。

在内部，GSO类型被标记为错误的，以便GSO/GRO引擎检查报头并重新计算分段。 GSO目标的大小也被调整。

所有的标志值都是为将来使用而保留的，必须保留为零。

对这个辅助函数的调用很可能会改变底层的数据包缓冲区。因此，在加载时，如果该辅助函数与直接数据包访问结合使用，校验器之前对指针的所有检查都无效，必须重新执行。

成功时返回 0，失败时返回负错误。





## long bpf_skb_load_bytes_relative(const void \*skb, u32 offset,void \*to, u32 len, u32 start_header)

**描述**
这个辅助函数与`bpf_skb_load_bytes()`类似，它提供了一个简单的方法，从与skb相关的数据包的偏移量中加载len字节到to所指的缓冲区。与`bpf_skb_load_bytes()`不同的是，它有第五个参数`start_header`，以便选择一个基本的偏移量来开始。START_HEADER可以是以下之一：

**BPF_HDR_START_MAC**
	从skb的mac中加载数据的基本偏移量头
**BPF_HDR_START_NET**
 	加载数据的基准偏移量是skb的网络头。

一般来说，"直接数据包访问 "是访问数据包的首选方法，然而，这个辅助函数在套接字过滤器中特别有用，因为skb->data并不总是指向mac头的开始，而且 "直接数据包访问 "也不可用。

成功时返回0，如果是则返回负错误失败


## long bpf_skb_store_bytes(struct sk_buff \*skb, u32 offset, const void \*from, u32 len, u64 flags)

**描述**
将来自地址的len字节存储到与SKB关联的数据包中的偏移量。标志是`BPF_F_RECOMPUTE_CSUM`(在存储字节后自动重新计算数据包的校验和)和`BPF_F_INVALIDATE_HASH`(将SKB->HASH、SKB->SWHASH和SKB->L4HASH设置为0)的组合。

对此辅助函数的调用容易更改底层数据包缓冲区。因此，在加载时，如果辅助函数与直接分组访问结合使用，则校验器先前对指针进行的所有检查都将无效，必须再次执行。

成功时返回0，如果是则返回负错误失败。


## long bpf_skb_change_type(struct sk_buff \*skb, u32 type)

**描述**
改变与skb相关的数据包的类型。这归结为将skb->pkt_type设置为type，除了eBPF程序再这个辅助函数中修改否则没有对skb->pkt_type的写入权限。
在这里使用一个辅助函数允许优雅地处理错误。

主要的用例是以编程的方式将传入的skb \*s改为 \*\*PACKET_HOST\*，而不是通过redirect(..., BPF_F_INGRESS)等方式进行再循环。

注意，type只允许某些值。在这时间,他们是:
**PACKET_HOST** : 这包是给我们的。
**PACKET_BROADCAST** 这包是给所有人的。
**PACKET_MULTICAST** 这包是给某个分组的。
**PACKET_OTHERHOST**  发送数据包给其他人

成功时返回0，如果是则返回负错误失败



## long bpf_skb_output(void \*ctx, struct bpf_map \*map, u64 flags,void \*data, u64 size)

**描述**
将原始数据blob写入一个特殊的BPF perf事件中 由类型为`BPF_MAP_TYPE_PERF_EVENT_ARRAY`的map。
这个perf事件必须有以下属性。 `PERF_SAMPLE_RAW` 作为 `sample_type`，`PERF_TYPE_SOFTWARE` 作为 `type`，`PERF_COUNT_SW_BPF_OUTPUT`作为 config。

这些 `flags` 用于指示 map 中必须放置其值的索引，并使用`BPF_F_INDEX_MASK`进行掩码。或者，可以将 flags 设置为`BPF_F_CURRENT_CPU`，以指示应该使用当前CPU内核的索引。

要写的数据大小 `size`，通过eBPF tack，并且指向`data`。


## long bpf_skb_pull_data(struct sk_buff \*skb, u32 len)

**描述**
如果skb是非线性的，并且不是所有的len都是线性部分，则引入非线性数据。
使SKB中的len字节可读和可写。如果为len传递零值，则拉取整个SKB长度。

只有使用直接数据包访问读取和写入，才需要此辅助函数。

对于直接数据包访问，在数据包边界中测试偏移量（`skb->data_end` 的测试），如果偏移无效，或者如果请求的数据处于SKB的非线性部分，很容易失败。
在失败时，程序可以直接退出，或者在使用非线性缓冲区的情况下，使用辅助函数使数据可用。

`bpf_skb_load_bytes()` 辅助函数是访问数据的第一个解决方案。
另一种方法是使用 `bpf_skb_pull_data`  pull 非线性部分，然后重新测试并最终访问数据。

同时，这也确保了skb没有被克隆，这是直接写的必要条件。 

由于这需要对写部分不变，校验器检测写入并添加一个调用`bpf_skb_pull_data()`的序幕 以从一开始就有效地解除对skb的克隆。以防它确实被克隆了。

对这个辅助函数的调用很容易改变底层的数据包缓冲区。 因此，在加载时，如果辅助函数与直接数据包访问结合使用，则校验器先前对指针所做的所有检查都将失效，并且必须再次执行。

成功时返回 0，失败时返回负错误。