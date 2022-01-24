---
title: go-gc
date: 2021-03-10 10:36:01
categories: 
	- [go]
tags:
  - go
author: Benjamin
---

# 内存结构
`arena`: 可以理解为堆区，512G
`bitmap`: 位图区，看做为 `arena` 做标记的地方，`bitmap` 由`点`组成，每个**点**占用`一个字节`
`spans`: 页目录去，看做将 `arena` 分页后做的页目录，每个页定义为 `8KB` ，每条目录占用一个指针

**每个点内存大小为：一个字节**
**每个指针内存大小为：4字节(32位系统)、8字节(64位系统)**
**每条目录占用一个指针**
**每个页占用8KB**

`bitmap` 每个点一个字节，每个字节 8 bit，每两个 bit 对应一个指针大小的内存，所以每个`点` 最多指向四个指针
两个bit 分别表示为：**是否继续扫描**和**是否包含指针**

所以 `bitmap` 在 `arena` 为 512G 的情况下: 512/8/4 = 16G 最少需要这么多才能完整描述整个 arena


goalng 的逃逸分析技术很成熟，会自动判断哪些对象分配在栈上和堆上，需要在堆上分配对象的情况：
- 返回对象的指针
- 传递了对象的指针到其他函数
- 在闭包中使用了对象并且需要修改对象
- 使用new

*内容可能在生成该对象的函数结束后被访问, 那么这个对象就会分配在堆上*
**除此之外, GC还需要知道栈空间上哪些地方包含了指针**

go中有以下的GC Bitmap:
- bitmap区域: 涵盖了arena区域, 使用`2 bit`表示一个指针大小的内存
- 函数信息: 涵盖了函数的**栈**空间, 使用`1 bit`表示一个指针大小的内存
- 类型信息: 在**分配对象时**会**复制**到`bitmap`区域, 使用`1 bit`表示一个指针大小的内存

### mspan 
```
startAddr																							*elemsize* nelemes
|elem|-|elem|-|elem|-|elem|-|elem|-|elem|-|elem|-|elem| 
  :      :    freeindex:      :      :      :      :
  :      :      :      :      :      :      :      :
  :      :      :    |bit0|-|bit1|-|bit2|-|bit3|-|bit3|				allocCache
  :      :      :      :      :      :      :      :
  :      :      :      :      :      :      :      :
|bit |-|bit |-|bit |-|bit |-|bit |-|bit |-|bit |-|bit |  			allocBits
  :      :      :      :      :      :      :      :
  :      :      :      :      :      :      :      :
|bit |-|bit |-|bit |-|bit |-|bit |-|bit |-|bit |-|bit |				gcmarkBits
```

通常一个span包含了多个大小相同的元素, 一个元素会保存一个对象, 除非:
- span用于保存大对象, 这种情况span只有一个元素
- span用于保存极小对象且不包含指针的对象, 这种情况span会用一个元素保存多个对象

span中有一个***freeindex***标记下一次分配对象时应该*开始搜索*的地址, 分配后***freeindex***会增加
***allocBits***用于标记哪些元素是已分配的, 哪些元素是未分配的
***freeindex + allocBits***可以在分配时跳过已分配的元素, 把对象设置在未分配的元素中
为了提高效率，**allocCache**用于缓存**freeindex**开始的**bitmap**, 缓存的bit值与原值`相反`
***gcmarkBits***用于在gc时标记哪些对象存活, 每次gc以后**gcmarkBits**会变为**allocBits**.
span结构本身的内存是从系统分配的, 上面提到的spans区域和bitmap区域都只是一个***索引***

# Span 类型

```
// class  bytes/obj  bytes/span  objects  tail waste  max waste
//     1          8        8192     1024           0     87.50%
//     2         16        8192      512           0     43.75%
//     3         32        8192      256           0     46.88%
			 :				  :				  :				  :				     :				:
//    66      32768       32768        1           0     12.50%
```

以类型(class)为1的span为例
`span`中的元素大小是8 byte, `span`本身占1页也就是8K, 一共可以保存1024个对象.
`span` 分配对象只会向上取整，有一定的空间浪费
最大的`span`的元素大小是32K，超过32K的对象称为"大对象", 分配大对象时, 会直接从heap分配一个特殊的`span`
这个特殊的`span`的类型(class)是0, 只包含了一个大对象, `span`的大小由对象的大小决定.

特殊的`span`加上的66个标准的`span`, 一共组成了67个`span`类型.

在分配内存中有 `noscan` 参数名，代表：
```
如果对象包含了指针, 分配对象时会使用scan的span,
如果对象不包含指针, 分配对象时会使用noscan的span.
```
对于 `noscan` 的内存分配都是从缓存中获取:
1. 首先从 P 的缓存中获取
2. P 不满足，从全局缓存中获取，如果获取成功就将缓存`挂载到P`
3. 如果全局缓存不满足，就从 `mheap` 获取，然后挂载到`全局缓存`


# 内存级别

mcache：线程绑定的缓存，无锁化分配内存
mcentral：全局缓存，多线程访问，存在并发访问问题，需要加锁
mheap：堆空间，全局内存获取第，多线程访问，存在并发访问问题，需要加锁

mcache是分配给M运行中的goroutine，是协程级所以无需加锁。为什么不用加锁呢，
是因为在M上运行的goroutine只有一个，不会存在抢占资源的情况，所以是无需加锁的。

mcentral是为mcache提供切分好的span。mcentral是全局的，也就是多个M共享mcentral，
会出现并发问题，所以此时申请都是需要加锁的。mcentral链表都在mheap进行维护

若分配内存时没有空闲的span的列表，此时需要向mheap申请。

mheap是go程序持有的整个堆空间，是go的全局变量，所以在使用的时候需要全局锁。
若mheap没有足够的内存，则会向虚拟内存申请page，然后将page组装成span再供程序使用。
mheap还存储多个heapArena ，heapArena 存储连续的span，主要是为了mheap管理span和GC垃圾回收


```golang
type mcache struct {	
	nextSample uintptr // 分配多少大小的堆时触发堆采样
	scanAlloc  uintptr // 分配的可扫描堆的字节数
	tiny       uintptr // <16byte 申请小对象的起始地址
	tinyoffset uintptr // 当前tiny 块的位置
	tinyAllocs uintptr // tinyAllocs是拥有此缓存的P所进行的 "tiny "分配的数量。
	alloc [numSpanClasses]*mspan // 申请的134个span
	stackcache [_NumStackOrders]stackfreelist //栈缓存
	flushGen uint32 // 表示上次刷新 mcache 的 sweepgen（清扫生成）
}

type mcentral struct {
	spanclass spanClass // 67*2
	partial [2]spanSet // 具有空闲对象的的 span 列表，用于 GC 扫描
	full    [2]spanSet // 没有空闲对象的 span 列表，用于 GC 扫描
}

type mheap struct {
	lock  mutex // 互斥锁
	pages pageAlloc  // 页面分配的数据结构
	sweepgen uint32  // GC相关
	allspans []*mspan  // 所有申请的span
	pagesInUse         atomic.Uint64 // 统计数据中的 span 页 mSpanInUse
	pagesSwept         atomic.Uint64 // 本轮清扫的页数
	pagesSweptBasis    atomic.Uint64 //  用作清扫率
	sweepHeapLiveBasis uint64 // 用作扫描率的heap_live 值
	sweepPagesPerByte  float64   // 清扫率
	scavengeGoal uint64 // 保留的堆内存总量（预先设定的），runtime 将试图返还内存给OS
	reclaimIndex atomic.Uint64 // 指回收的下一页在allAreans 中的索引
	reclaimCredit atomic.Uintptr // 多余页面的备用信用
	arenas [1 << arenaL1Bits]*[1 << arenaL2Bits]*heapArena // 堆arena 映射。它指向整个可用虚拟地址空间的每个 arena 帧的堆元数据
	heapArenaAlloc linearAlloc // 是为分配heapArena对象而预先保留的空间。仅仅用于32位系统。
	arenaHints *arenaHint // 试图添加更多堆 arenas 的地址列表。它最初由一组通用少许地址填充，并随实 heap arena 的界限而增长。
	arena linearAlloc
	allArenas []arenaIdx // 每个映射arena的arenaIndex 索引。可以用以遍历地址空间
	sweepArenas []arenaIdx // 在清扫周期开始时保留的 allArenas 快照
	markArenas []arenaIdx // 在标记周期开始时保留的 allArenas 快照
	curArena struct { // heap当前增长时的 arena，它总是与physPageSize对齐
		base, end uintptr
	}
	_ uint32 
	central [numSpanClasses]struct { //  mcentral ，每种规格大小的块对应一个 mcentral
		mcentral mcentral
		pad      [cpu.CacheLinePadSize - unsafe.Sizeof(mcentral{})%cpu.CacheLinePadSize]byte
	}

	spanalloc             fixalloc // 分配的 span 链表
	cachealloc            fixalloc // mcache 链表
	specialfinalizeralloc fixalloc // specialfinalizer 链表
	specialprofilealloc   fixalloc // specialprofile 链表
	specialReachableAlloc fixalloc // specialReachable 链表
	speciallock           mutex    // 特殊记录分配器的锁
	arenaHintAlloc        fixalloc  // arenaHints 链表

	unused *specialfinalizer 
}

type specialfinalizer struct {
	special special
	fn      *funcval // May be a heap pointer.
	nret    uintptr
	fint    *_type   // May be a heap pointer, but always live.
	ot      *ptrtype // May be a heap pointer, but always live.
}

type specialprofile struct {
	special special
	b       *bucket
}

type specialReachable struct {
	special   special
	done      bool
	reachable bool
}

type arenaHint struct {
	addr uintptr
	down bool
	next *arenaHint
}

type fixalloc struct {
	size   uintptr
	first  func(arg, p unsafe.Pointer) // called first time p is returned
	arg    unsafe.Pointer
	list   *mlink
	chunk  uintptr // use uintptr instead of unsafe.Pointer to avoid write barriers
	nchunk uint32  // bytes remaining in current chunk
	nalloc uint32  // size of new chunks in bytes
	inuse  uintptr // in-use bytes now
	stat   *sysMemStat
	zero   bool // zero allocations
}

type mspan struct {
	next *mspan      //链表下一个span地址
	prev *mspan      // 链表前一个span地址
	list *mSpanList  // 链表地址

	startAddr uintptr // 该span在arena区域的起始地址
	npages    uintptr // 该span占用arena区域page的数量

	manualFreeList gclinkptr // 空闲对象列表

	freeindex uintptr //freeindex是0到nelems之间的位置索引,标记下一个空对象索引
	nelems uintptr // 管理的对象数
	allocCache uint64 //从freeindex开始的位标记
	allocBits  *gcBits //该mspan中对象的位图
	gcmarkBits *gcBits //该mspan中标记的位图,用于垃圾回收
	sweepgen    uint32 //扫描计数值，用户与mheap的sweepgen比较，根据差值确定该span的扫描状态
	divMul      uint32        
	allocCount  uint16    // 已分配的对象的个数     
	spanclass   spanClass     // span分类
	state       mSpanStateBox // mspaninuse etc
	needzero    uint8         // 分配之前需要置零
	elemsize    uintptr       // 对象的大小
	limit       uintptr       // 申请大对象内存块会用到，mspan的数据截止位置
	speciallock mutex         
	specials    *special      
}

// 用于管理golang堆内存的地址空间
type pageAlloc struct {
	summary [summaryLevels][]pallocSum
	chunks [1 << pallocChunksL1Bits]*[1 << pallocChunksL2Bits]pallocData
	searchAddr offAddr
	start, end chunkIdx
	inUse addrRanges
	scav struct {
		lock mutex
		inUse addrRanges
		gen uint32
		reservationBytes uintptr
		released uintptr
		scavLWM offAddr
		freeHWM offAddr
	}
	mheapLock *mutex
	sysStat *sysMemStat
	test bool
}
```








