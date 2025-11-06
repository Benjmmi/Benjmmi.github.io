---
layout: post
title: "go interface{} 类型理解 - iface"
keywords: ""
description: ""
tagline: ""
date: '2022-07-11 23:55:33'
category: linux
tags: linux
---
> {{ page.description }}

# iface
iface: 用来表示非空的 `interface{}` 表示继承的接口。示例：

```go
type studenter interface {
    GetName() string
    GetAge()  int
}
```


```go 
// name is an encoded type name with optional extra data.
// See reflect/type.go for details.
type name struct {
	bytes *byte
}

type imethod struct {
	name nameOff
	ityp typeOff
}

type interfacetype struct {
	typ     _type
	pkgpath name
	mhdr    []imethod // 接口方法声明列表，按字典序排序
}

// layout of Itab known to compilers
// allocated in non-garbage-collected memory
// Needs to be in sync with
// ../cmd/compile/internal/reflectdata/reflect.go:/^func.WriteTabs.
// runtime/runtime2.go
// 非空接口的类型信息
type itab struct {
	//inter 和 _type 确定唯一的 _type类型
	inter *interfacetype	// 接口自身定义的类型信息，用于定位到具体interface类型
	_type *_type			// 接口实际指向值的类型信息-实际对象类型，用于定义具体interface类型
	hash  uint32 			//_type.hash的拷贝，用于快速查询和判断目标类型和接口中类型是一致
	_     [4]byte
	fun   [1]uintptr 		//动态数组，接口方法实现列表(方法集)，即函数地址列表，按字典序排序
							//如果数组中的内容为空表示 _type 没有实现 inter 接口
}

// runtime/runtime2.go
// 非空接口
type iface struct {
    tab  *itab
    data unsafe.Pointer //指向原始数据指针
}
```

属性`interfacetype`类似于`_type`，其作用就是interface的**公共描述**，例如上面展示的 `maptype`、`arraytype`、`chantype`其都是各个结构的公共描述，可以理解为一种外在的表现信息。

	imethod 存的是func 的声明抽象，而 itab 中的 fun 字段才是存储 func 的真实切片。

非空接口(iface)本身除了可以容纳满足其接口的对象之外，还需要保存其接口的方法，因此除了data字段，iface通过tab字段描述非空接口的细节，包括接口方法定义，接口方法实现地址，接口所指类型等。iface是非空接口的实现，而不是类型定义，iface的真正类型为interfacetype，其第一个字段仍然为描述其自身类型的_type字段。

# iface整体结构图
![存储结构](/assets/images/1568960115779-4ac8ef27-c181-494e-93f4-f63de5b64177.jpeg)

	图片来自：[https://blog.csdn.net/i6448038/article/details/82916330](https://blog.csdn.net/i6448038/article/details/82916330)

含有方法的interface赋值后的内部结构是怎样的呢？

```go
package main

import (
	"fmt"
	"strconv"
)

type Binary uint64
func (i Binary) String() string {
	return strconv.FormatUint(i.Get(), 10)
}

func (i Binary) Get() uint64 {
	return uint64(i)
}

func main() {
	b := Binary(200)
	any := fmt.Stringer(b)
	fmt.Println(any)
}
```
首先，代码运行结果为:200。
其次，了解到fmt.Stringer是一个包含String方法的接口。

```go
type Stringer interface {
	String() string
}
```
最后，赋值后接口Stringer的内部结构为：
![1568960115948-fe5174b9-9dae-4da5-9c2f-e026d7204a1a.jpeg](/assets/images/1568960115948-fe5174b9-9dae-4da5-9c2f-e026d7204a1a.jpeg)

---
参考：
- [深入理解Go语言(01): interface源码分析](https://www.cnblogs.com/jiujuan/p/12653806.html)