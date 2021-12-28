---
title: Linux-内核网络 1
date: 2021-09-08 07:36:57
categories: 
	- [eBPF]
	- [Linux]
	- [内核]
	- [网络代码]
tags:
  - ebpf
author: Jony
---

# Linux-内核网络-理论篇-设备模型

## 初衷

设备模型诞生的初衷：为了**解决**越来越复杂的设备拓扑结构问题。
问题描述：最早的内核中没有独立的数据结构来让内核获得整体系统的配合信息。虽然一开始可以正常
的工作，但是随着拓扑结构越来越复杂，以及要支持更多的新特性内核就优点力不从心了，陷入无法维
护的情况。
**总结**来说设备模型是为了内核更好的管理设备和让开发者更好的扩展新设备新功能

建立设备模型后内核系统具有了如下的优点：
1. 代码重复降到最低
2. 提供了统一的引用计数机制
3. 可以很方便的罗列出所有的设备，很方便的检查他们的状态、连接的总线
4. 可以将系统中的所有设备以树形结构展示。包括了总线和内部连接
5. 可以将设备和对应的驱动程序相互联系起来
6. 可以将设备按照类型分类
7. 可以从设备树的叶节点向根遍历

案例：关闭电源，若是想正确的关闭电源肯定要建立一个优先级的顺序和结构，先依次向下遍历叶节点中
的子系统比如:`USB`、`PCI`。再逐个网上遍历父级系统，最终关闭根电源系统。（反思一下，如果这
些关闭方式都由各个子系统自己负责，那是多么混乱）

设备模型有几个简单的数据结构：`kobject`、`kset`、`ktype` 和 `子系统`

## kobject

`kobject` 是设备模型中最核心的也是最基本的数据结构。最初只负责`引用计数`的功能，但是随着系
统的更新 `kobject` 负责的任务越来越多。所以字段也增加了很多，数据结构：

```c
struct kobject {
	const char		*name; // kobject 的名称
	struct list_head	entry; // 
	struct kobject		*parent; // kobject 的父对象
	struct kset		*kset; 
	struct kobj_type	*ktype;
	struct kernfs_node	*sd; // 指向 sysfs_dirent 结构体
	struct kref		kref; // 引用计数器
#ifdef CONFIG_DEBUG_KOBJECT_RELEASE
	struct delayed_work	release;
#endif
	unsigned int state_initialized:1;
	unsigned int state_in_sysfs:1;
	unsigned int state_add_uevent_sent:1;
	unsigned int state_remove_uevent_sent:1;
	unsigned int uevent_suppress:1;
};
```

`kobject` 如果单独使用的话好像并没有什么意义，所以 `kobject` 一般是嵌入到其他结构中。可以
认为通过 `kobject` 结构，让开发者使用面向对象的方式思考开发步骤，降低了开发难度。比如：

```c
struct cdev {
	struct kobject kobj;
	struct module *owner;
	const struct file_operations *ops;
	struct list_head list;
	dev_t dev;
	unsigned int count;
} __randomize_layout;
```

## ktype

`ktype` 是 `kobject` 依赖的一个特殊类型。`ktype` 原型为 `kobj_type` 结构体，主要定义了
`kobject` 普遍的特性。比如：释放、`sysfs` 文件操作、属性字段等。
`ktype` 特点：同类型的 `kobject` 可以共享 `ktype`

```c
struct kobj_type {
	void (*release)(struct kobject *kobj); // 析构函数
	const struct sysfs_ops *sysfs_ops; // sysfs 文件读写时的特性
	struct attribute **default_attrs;	 // kobject 相关的默认属性
	const struct attribute_group **default_groups; // 属性分组
	const struct kobj_ns_type_operations *(*child_ns_type)(struct kobject *kobj);
	const void *(*namespace)(struct kobject *kobj);
	void (*get_ownership)(struct kobject *kobj, kuid_t *uid, kgid_t *gid);
};
```

`release`:指针指向 `kobject` 的析构函数，当应用计数降为 `0` 的时候将自动调用
`sysfs_ops`:指向 `sysfs_ops` 结构体。主要描述了读写 `sysfs` 文件读写时的特性
`default_attrs`: 定义了 `kobject` 默认属性，会随着 `kobject` 导出到 `sysfs` 而导出


## `kobject` 使用

按照惯例都需要初始化，`kobject` 也不例外在初始化之前需要清空`kobject` ，如果没有整个清空
在使用的时候经常会发生一些奇奇怪怪的事情，然后调用 `kobject_init` 函数。

`void kobject_init(struct kobject *kobj, struct kobj_type *ktype);`

第一个参数就是需要初始的 `kobject` 对象，第二个参数就是 `ktype` ，调用步骤如下参考:

```c
struct kobject *kobj;
kobj = kzmalloc(sizeof(*kobj), GFP_KERNEL);
if(!kobj)
	return -ENOMEM;
kobj->set = mykset;
kobject_init(kobj, myktyp);
```
通过上述的步骤就可以获得一个初始化后的 `kobject` 当然现在为了方便使用已经将很多操作步骤
整合为一个函数了: `kobject_create` 。当调用这个函数时会自动返回一个 `kobject` 使用起
来相当便捷:

```c
struct kobject *kobj;
kobj = kobject_create();
if(!kobj)
	return -ENOMEM;
```
然后在使用 `kobject` 的时候最起码要给 `kobject` 一个名字，使用如下函数:

```c
int kobject_set_name(struct kobject *kobj, const char *name, ...);
int kobject_set_name_vargs(struct kobject *kobj, const char *fmt,va_list vargs);
```

`kobject` 主要功能之一就是提供了统一的引用基数。初始化之后 `kobject` 的引用基数就会被
自动初始化为 `1` 。只要引用计数不为 `0`，那么 `kobject` 就会永远的保留在内存中。底层对引用
计数的操作函数有:

```c
struct kobject *kobject_get(struct kobject *kobj);
void kobject_put(struct kobject *kobj);
```
`kobject_get` 调用成功后会增加 `kobject` 引用计数，并返回指向 `kobject` 的指针，如果
`kobject` 处于销毁状态那么就会调用失败，返回 `NULL` 。当引用被释放时调用 `kobject_put`
减少引用计数，当计数为 `0` 的情况之下可能会自动调用 `release` 释放当前对象. 所以 `release`
不可以为空，如果为空将会带来不可预知的后果。

## kset

`kset` 是 `kobject` 对象的集合体，可以把 `kset` 看做一个容器，可将所有相关的 `kobject` 对象
置于同一父级结构下。代码：

```c
struct kset {
	struct list_head list;
	spinlock_t list_lock;
	struct kobject kobj;
	const struct kset_uevent_ops *uevent_ops;
} __randomize_layout;
```
`list`: 连接该集合中所有的 `kobject` 对象
`list_lock`:是保护这个链表中元素的自旋锁
`kobj`:指向的 `kobject` 为 `kset` 基础类型.
`uevent_ops`:用于处理集合中 `kobject` 对象的热插拔操作。
`uevent` 就是 `user event` 的意思，提供与用户空间热插拔信息进行通信的机制
`kset` 也有一个名字，它保存在内嵌的 `kobject` 中。如果要设置 `kset` 的名字方式如下:

```c
kobject_set_name(&my_set->kobj, "myname");
```

`kset` 中也有一个指针指向 `ktype` ，用来描述它所包含的 `kobject` ，该类型的使用优先于 `kobject`
的 `ktype` 。所以大多时候 `kobject` 中的 `ktype` 被设置为 `NULL`

## 子系统

子系统是对整个内核中一些高级部分的描述。子系统通常显示在 `sysfs` 分层结构的顶层。比如：
`/sys/devices`、`/sys/block` 还有其他的子系统通过查看 `/sys/` 目录可见，因为子系统
相当稳定，基本不需要开发新的子系统，所以不在讲解

最终呈现的视图如下：

[!image](/jony.github.io/source/images/core/17fig01.gif)


# 参考
[Linux 内核设计与实现]
[Linux 驱动设计]
[http://www.iakovlev.org/index.html?p=1025]










