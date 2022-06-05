---
title: Other - Golang HTTP 请求过程分析
date: 2021-04-01 17:29:11
categories: 
	- [Other]
tags:
  - http
  - glang
author: Jony
---

# 背景
源于项目需求：将传统项目对接到注册中心，最好在无感的情况下。
传统项目之间的访问大多数都是通过 `HTTP*` 协议来通信，即域名级别的服务发现，传统项目
在访问时遇到如下问题：
1. 传统项目只能通过域名级别
2. 传统项目只能通过 DNS 来做寻址发现
3. 传统项目无法判断远程服务是否存活，只能根据 DNS 结果来请求，容灾效果差

改造难点：
- 传统开发模式不改变的情况下，最好较少改动或者不改动
- 提搞容灾能力，在域名级别访问的情况下降提高容灾能力

解决方案：
- 设置统一网关模式，由网关统一管理后端节点信息转发
- iptables 模式，每个客户端实例下都部署 agent 来监控，实时更新 iptables
- 代理 DNS 解析

# 代理 DNS 解析

golang 在发起 http 请求的时候必然需要经过 DNS 解析。常用的请求方式如下：

```golang
// GET 请求
httpClient.Get("http://fjlfkdafjlkf:8000/")
// POST 请求
http.Post("http://fjlfkdafjlkf:8000/","application/json",nil)
// 自定义请求
req, err := http.NewRequest("DELETE", endpoint, nil)
  if err != nil {
    return err
  }
  // use httpClient to send request
rsp, err := client.Do(req)  
```

解决 DNS 代理之前先了解下 `RoundTripper` 和  `Dialer`。

### RoundTripper

先看代码
```golang
type RoundTripper interface {
  RoundTrip(*Request) (*Response, error)
}
```
**RoundTripper** 注释：`RoundTripper` 是 `goroutines-safe`的。其中唯一的 `RoundTrip()` 
方法只负责 `HTTP` 请求的建立、发送、接收`HTTP`响应以及关闭；但是不会对 `HTTP` 响应，
例如：`redirect`、`authentication` 或者 `cookies` 等上层协议细节。
在非必要情况下，不应该在 `RoundTrip()` 方法中改写传入的请求对象 `*Request`。

> 注意：任何实现了 `RoundTrip()` 方法的类型都实现了 `http.RoundTripper` 接口。

通过对源码的跟踪发现 `http.Transport` 也是通过实现 `RoundTrip()` 方法，而继承了该接口。

### Dialer

在 golang 中无论什么协议建立什么形式的连接，都只需要调用 `net.Dial()` 就可以。在 `net.Dial()`
实际上并没有发起真正的连接，只是在为连接做一些列的准备，比如：解析网络类型、解析 `IP` 地址，实际发
起连接的方法在具体的 `tcpsock_posix.go`、`udpsock_posix.go` 文件下。代码如下：

```golang
func (d *Dialer) DialContext(ctx context.Context, network, address string) 
(Conn, error) {
  ...
  // 先对目标地址进行解析
  addrs, err := d.resolver().resolveAddrList(resolveCtx, "dial", network, address, d.LocalAddr)
  ...
  if d.dualStack() && network == "tcp" { // 表示同时支持 ipv4 和 ipv6
    // 将addrs分成两个切片，前者包含ipv4地址，后者包含ipv6地址
    primaries, fallbacks = addrs.partition(isIPv4) 
  } else {
    primaries = addrs
  }
  ...
  var c Conn
  if len(fallbacks) > 0 { //有ipv6的情况，v4和v6一起dial
    c, err = sd.dialParallel(ctx, primaries, fallbacks)
  } else {
    c, err = sd.dialSerial(ctx, primaries)
  }
  ...
  if tc, ok := c.(*TCPConn); ok && d.KeepAlive >= 0 {
    setKeepAlive(tc.fd, true)
    ...
  }
  return c, nil  
}
```

可以看到 `DialContext` 在真正创建连接之前对先使用了  `Resolver *Resolver` 解析 `DNS`
`Resolver \*Resolver` 是一个`struct`，不是个`interface`。然后在根据解析结果，创建 
`TCPConn` 。

> 具体其他详情查看，参考文档

对 `RoundTripper` 和 `Dialer` 了解之后发现好像并没有什么帮助，其实不然。直接来跟踪 `Get`、
`Post` 方法就会发现最终调用的都是 `client.Do(req *Request)` 方法。
 `Do(req *Request)` 实际上就一个包裹方法，将内部实现对外开放的接口。跟踪代码如下：

```golang
func (c *Client) Post(url, contentType string, body io.Reader) 
    (resp *Response, err error) {
  ...
  return c.Do(req)
}
func (c *Client) Get(url string) (resp *Response, err error) {
  ...
  return c.Do(req)
}
func (c *Client) Do(req *Request) (*Response, error) {
  return c.do(req)
}
func (c *Client) do(req *Request) (retres *Response, reterr error) {
  if resp, didTimeout, err = c.send(req, deadline); err != nil {
    ...
  }
}
```

最终的请求是通过 `send` 方法，来发送请求和获取响应。这里的 `send` 同样也是一个包裹函数，
内部调用了一个同名的 `send` 方法。代码如下：

```golang
func (c *Client) send(req *Request, deadline time.Time) (resp *Response, didTimeout 
  func() bool, err error) {
  ...
  resp, didTimeout, err = send(req, c.transport(), deadline)
  if err != nil {
    return nil, didTimeout, err
  }  
  ...
}
```

内部 `send()` 调用参数包含了一个 `c.transport()` 。而 `c.transport()` 就是我们上面
`RoundTripper` 的一个实现。查看 `c.transport()` 代码实现如下：

```golang
func (c *Client) transport() RoundTripper {
  if c.Transport != nil {
    return c.Transport
  }
  return DefaultTransport
}
```

通过代码得知，如果用户没有实现 `Transport` 的情况下就会使用默认的 `DefaultTransport` 来代替

```golang
var DefaultTransport RoundTripper = &Transport{
  Proxy: ProxyFromEnvironment,
  DialContext: (&net.Dialer{
    Timeout:   30 * time.Second,
    KeepAlive: 30 * time.Second,
  }).DialContext,
  ForceAttemptHTTP2:     true,
  MaxIdleConns:          100,
  IdleConnTimeout:       90 * time.Second,
  TLSHandshakeTimeout:   10 * time.Second,
  ExpectContinueTimeout: 1 * time.Second,
}
```

这里发现 `DialContext` 是开放字段也就是说默认情况下,DialContext 的策略是可以修改的。
所以我们实现 `DialContext` 功能，在创建 `TCPConn` 之前的地址解析通过释放为我们指定的
注册中心即可，基本上不会影响到用户的使用。

大致实现如下:

```golang
  dialContext :=func(ctx context.Context, network, addr string) (net.Conn, error) {

    rtcpAddr, err := net.ResolveTCPAddr("tcp", "172.22.3.222:8848")
    if err != nil{
     return nil, err
    }
    conn, err := net.DialTCP("tcp", nil, rtcpAddr)

    return conn, err
  }
  http.DefaultTransport.(*http.Transport).DialContext = dialContext
```

通过上面这种方式就可以完成对 `Golang` 域名解析的私有化定制。

参考文档：
- [Golang Http 库指定 dns 服务器进行解析](https://gocn.vip/topics/12017)
- [Golang DNS解析](https://zhuanlan.zhihu.com/p/54989059)
- [Go Transport](https://blog.csdn.net/benben_2015/article/details/84886088)
- [golang net/dial.go 阅读笔记](https://www.jianshu.com/p/b4ce0794fa32)
- [golang net/dial.go 阅读笔记二](https://www.jianshu.com/p/79fe0a4a4850)


