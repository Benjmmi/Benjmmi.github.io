---
title: 
date: 2021-09-08 07:36:57
categories: 
	- [eBPF]
	- [Linux]
	- [内核]
	- [网络代码]
tags:
  - ebpf
author: Benjamin Yim
---

内核代码虽然阅读比较困难，但是也很有技巧性可言。先了解基本的数据结构和过往的一些知识信息整理基本上
参考资料就可以了解整个内核运行的过程。
这里主要为了了解网络技术内幕数据需要从 [`sk_buff`](https://elixir.bootlin.com/linux/v5.14/source/include/linux/skbuff.h#L720) 开始，了解这个数据结构下的细枝末节。内核版本

# `sk_buff` 数据结构

```c
struct sk_buff {
	union {
		struct {
			/* These two members must be first. */
			struct sk_buff		*next;
			struct sk_buff		*prev;

			union {
				struct net_device	*dev;
				/* Some protocols might use this space to store information,
				 * while device pointer would be NULL.
				 * UDP receive path is one user.
				 */
				unsigned long		dev_scratch;
			};
		};
		struct rb_node		rbnode; /* used in netem, ip4 defrag, and tcp stack */
		struct list_head	list;
	};

```

`sk_buff` 的想要表达的就是 **socket bufffer** 。socket 就是我们熟悉的套接字工具。

`c` 中对数据结构的要求是很严格的特别是在大项目中的内存对齐，开头就使用了 `union` 联合体。

**结构体(struct)**中所有变量是“共存”的——优点是“有容乃大”，全面；缺点是struct内存空间的
分配是粗放的，不管用不用，全分配。

**联合体(union)**中是各变量是“互斥”的——缺点就是不够“包容”；但优点是内存使用更为精细灵活，
也节省了内存空间。**特点：共用内存首地址、内存分配按照最大的变量分配**

参考:[结构体struct和联合体union最全讲解
](https://blog.csdn.net/liguangxianbin/article/details/80510669)

这里再看下 `rb_node	` 和 `list_head` 结构体：

```c
// https://elixir.bootlin.com/linux/v5.14/source/include/linux/rbtree.h#L24
struct rb_node {
	unsigned long  __rb_parent_color;
	struct rb_node *rb_right;
	struct rb_node *rb_left;
} __attribute__((aligned(sizeof(long))));
```

```c
// https://elixir.bootlin.com/linux/v5.14/source/include/linux/types.h#L178
struct list_head {
	struct list_head *next, *prev;
};
```
通过对 `union` 中的整个结构体分析可以看见整个数据结构的最大占用内存为 `两个指针+long 变量长度`

指针的占用内存大小是跟随系统变化的，比如在 `32` 位系统中占用 `4` 字节，`64` 位系统中占 `8` 字节。
`long` 类型 `32` 位系统中占用 `4` 字节，`64` 位系统中占 `8` 字节
按照 `64` 位系统计算，最多占用 `128`  字节。

