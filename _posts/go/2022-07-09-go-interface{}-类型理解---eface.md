---
layout: post
title: "go interface{} 类型理解 - eface"
keywords: ""
description: ""
tagline: ""
date: '2022-07-09 23:05:15'
category: linux
tags: linux
---


# eface
eface：`var a interface{}`
代表了 go 中的任意类型

```go
type nameOff int32
type typeOff int32
type textOff int32
type tflag uint8
// Needs to be in sync with ../cmd/link/internal/ld/decodesym.go:/^func.commonsize,
// ../cmd/compile/internal/reflectdata/reflect.go:/^func.dcommontype and
// ../reflect/type.go:/^type.rtype.
// ../internal/reflectlite/type.go:/^type.rtype.
type _type struct {
	size       uintptr //数据类型共占用的空间大小
	ptrdata    uintptr //含有所有指针类型前缀大小
	hash       uint32  //类型hash值；避免在哈希表中计算
	tflag      tflag   //额外类型信息标志
	align      uint8   //该类型变量对齐方式
	fieldAlign uint8   //该类型结构字段对齐方式   
	kind       uint8   //类型编号
	// function for comparing objects of this type
	// (ptr to object A, ptr to object B) -> ==?
	equal func(unsafe.Pointer, unsafe.Pointer) bool  // 比较该类型对象
	// gcdata stores the GC type data for the garbage collector.
	// If the KindGCProg bit is set in kind, gcdata is a GC program.
	// Otherwise it is a ptrmask bitmap. See mbitmap.go for details.
	gcdata    *byte    //gc数据
	str       nameOff  // 类型名字的偏移
	ptrToThis typeOff
}
// #src/runtime/type.go
type eface struct {
	_type *_type //类型信息,指向对象的类型信息
	data  unsafe.Pointer //数据信息，指向数据指针
}
```
- `_type`: 是go里面所有类型的一个抽象，里面包含GC，反射，大小等需要的细节，它也决定了data如何解释和操作。
里面包含了非常多信息 类型的大小、哈希、对齐以及种类等自动。


但是各个类型需要的类型描叙是不一样的，比如chan，除了chan本身外，还需要描述其元素类型，而map则需要key类型信息和value类型信息等:

```go
// 数组类型定义
type arraytype struct {
	typ   _type
	elem  *_type
	slice *_type
	len   uintptr
}
// chan 类型定义
type chantype struct {
	typ  _type
	elem *_type
	dir  uintptr
}
// slice 类型定义
type slicetype struct {
	typ  _type
	elem *_type
}
// 方法类型定义
type functype struct {
	typ      _type
	inCount  uint16
	outCount uint16
}
// 指针类型定义
type ptrtype struct {
	typ  _type
	elem *_type
}
// 结构体字段类型定义
type structfield struct {
	name   name
	typ    *_type
	offset uintptr
}
// 结构体类型定义
type structtype struct {
	typ     _type
	pkgPath name
	fields  []structfield
}
// 接口类型定义
type interfacetype struct {
	typ     _type
	pkgpath name
	mhdr    []imethod
}
// map 类型定义
type maptype struct {
	typ    _type
	key    *_type
	elem   *_type
	bucket *_type // internal type representing a hash bucket
	// function for hashing keys (ptr to key, seed) -> hash
	hasher     func(unsafe.Pointer, uintptr) uintptr
	keysize    uint8  // size of key slot
	elemsize   uint8  // size of elem slot
	bucketsize uint16 // size of bucket
	flags      uint32
}
```
几乎所有的类型定义都带有 `_type` 而且是第一个字段。接下来也定义了一堆类型所需要的信息（如子类信息）,这样在进行类型相关操作时，可通过一个字`(typ *_type)`即可表述所有类型，然后再通过`_type.kind`可解析出其具体类型，最后通过地址转换即可得到类型完整的”`_type`树”，参考`reflect.Type.Elem()`函数:

```go
// reflect/type.go
// reflect.rtype结构体定义和runtime._type一致  type.kind定义也一致(为了分包而重复定义)
// Elem()获取rtype中的元素类型，只针对复合类型(Array, Chan, Map, Ptr, Slice)有效
func (t *rtype) Elem() Type {
	switch t.Kind() {
	case Array:
		tt := (*arrayType)(unsafe.Pointer(t))
		return toType(tt.elem)
	case Chan:
		tt := (*chanType)(unsafe.Pointer(t))
		return toType(tt.elem)
	case Map:
		tt := (*mapType)(unsafe.Pointer(t))
		return toType(tt.elem)
	case Pointer:
		tt := (*ptrType)(unsafe.Pointer(t))
		return toType(tt.elem)
	case Slice:
		tt := (*sliceType)(unsafe.Pointer(t))
		return toType(tt.elem)
	}
	panic("reflect: Elem of invalid type " + t.String())
}
```

对于没有方法的interface赋值后的内部结构是怎样的呢？
比如：

```c
import (
	"fmt"
	"strconv"
)

type Binary uint64

func main() {
	b := Binary(200)
	any := (interface{})(b)
	fmt.Println(any)
}
```

输出:`200`
但是结构如下:

```
b := Binary(200)
interface{
	_type   -------> type(Binary)
	data 	-------> 200
}
```

---
参考：
- [深入理解Go语言(01): interface源码分析 ](https://www.cnblogs.com/jiujuan/p/12653806.html){:target='blank'}
