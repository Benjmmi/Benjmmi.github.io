---
layout: post
title: "Linux 提交 patch"
keywords: ""
description: ""
tagline: ""
date: '2022-11-06 19:27:17'
category: linux
tags: linux
---



export HTTP_PROXY=http://192.168.0.102:58591; export HTTPS_PROXY=http://192.168.0.102:58591; export ALL_PROXY=socks5://192.168.0.102:51837


Author: Jason Wang <wangborong@cdjrlc.com>
Date:   Tue Aug 17 20:11:06 2021 +0800

net: tcp_drop adds `reason`  v5

I used the suggestion from `Brendan Gregg`. In addition to the 
`reason` parameter there is also the `field` parameter pointing 
to `SNMP` to distinguish the `tcp_drop` cause. I know what I 
submitted is not accurate, so I am submitting the current 
patch to get comments and criticism from everyone so that I 
can submit better code and solutions.And of course to make me 
more familiar and understand the `linux` kernel network code.  
Thank you everyone!


git send-email 0001-net-tcp_drop-adds-reason-and-SNMP-parameters-for-tra.patch --to 2228598786@qq.com

git send-email 0001-net-tcp_drop-adds-reason-and-SNMP-parameters-for-tra.patch --to edumazet@google.com --cc netdev@vger.kernel.org,linux-kernel@vger.kernel.org,kuba@kernel.org,rostedt@goodmis.org,mingo@redhat.com,davem@davemloft.net,yoshfuji@linux-ipv6.org,dsahern@kernel.org,hengqi.chen@gmail.com,yhs@fb.com,brendan.d.gregg@gmail.com,2228598786@qq.com




Author: Jason Wang <wangborong@cdjrlc.com>
Date:   Tue Aug 17 20:11:06 2021 +0800

net: tcp_drop adds `reason` parameter for tracing v2

In this version, fix and modify some code issues. Changed the reason for `tcp_drop` from an enumeration to a mask and enumeration usage in the trace output.
By shifting `__LINE__` left by 6 bits to accommodate the `tcp_drop` call method source, 6 bits are enough to use. This allows for a more granular identification of the reason for calling `tcp_drop` without conflicts and essentially without overflow.
Example.
```
enum {
TCP_OFO_QUEUE = 1
}
reason = __LINE__ << 6
reason |= TCP_OFO_QUEUE
```
Suggestions from Jakub Kicinski, Eric Dumazet, much appreciated.

Modified the expression of the enumeration, and the use of the output under the trace definition.
Suggestion from Steven Rostedt. Thanks.





    

net/mlx4:  trailing whitespace

trailing whitespace

    Signed-off-by: Jason Wang <wangborong@cdjrlc.com>
    Reviewed-by: Tariq Toukan <tariqt@nvidia.com>
    Link: https://lore.kernel.org/r/20210817121106.44189-1-wangborong@cdjrlc.com
    Signed-off-by: Jakub Kicinski <kuba@kernel.org>


    net/mlx4: Use ARRAY_SIZE to get an array's size
    
    The ARRAY_SIZE macro is defined to get an array's size which is
    more compact and more formal in linux source. Thus, we can replace
    the long sizeof(arr)/sizeof(arr[0]) with the compact ARRAY_SIZE.
    
    Signed-off-by: Jason Wang <wangborong@cdjrlc.com>
    Reviewed-by: Tariq Toukan <tariqt@nvidia.com>
    Link: https://lore.kernel.org/r/20210817121106.44189-1-wangborong@cdjrlc.com
    Signed-off-by: Jakub Kicinski <kuba@kernel.org>

0001-net-mlx4-tcp_drop-replace-of-tcp_drop_new.patch

Eric Dumazet <edumazet@google.com> (maintainer:NETWORKING [TCP])
Steven Rostedt <rostedt@goodmis.org> (maintainer:TRACING)
Ingo Molnar <mingo@redhat.com> (maintainer:TRACING)
"David S. Miller" <davem@davemloft.net> (maintainer:NETWORKING [IPv4/IPv6])
Hideaki YOSHIFUJI <yoshfuji@linux-ipv6.org> (maintainer:NETWORKING [IPv4/IPv6])
David Ahern <dsahern@kernel.org> (maintainer:NETWORKING [IPv4/IPv6])
Jakub Kicinski <kuba@kernel.org> (maintainer:NETWORKING [GENERAL])
netdev@vger.kernel.org (open list:NETWORKING [TCP])
linux-kernel@vger.kernel.org (open list)

git send-email 0001-net-tcp_drop-adds-reason-parameter-for-tracing-v2.patch --to 2228598786@qq.com

git send-email 0001-net-tcp_drop-adds-reason-parameter-for-tracing-v2.patch --to kuba@kernel.org --cc netdev@vger.kernel.org,linux-kernel@vger.kernel.org,edumazet@google.com,rostedt@goodmis.org,mingo@redhat.com,davem@davemloft.net,yoshfuji@linux-ipv6.org,dsahern@kernel.org,hengqi.chen@gmail.com,yhs@fb.com


git send-email 0001-net-tcp_drop-adds-reason-parameter-for-tracing.patch --to 2228598786@qq.com

./scripts/checkpatch.pl 0001-net-mlx4-tcp_drop-replace-of-tcp_drop_new.patch
./scripts/get_maintainer.pl 0001-net-mlx4-Use-ARRAY_SIZE-to-get-an-array-s-size.patch


enum tcp_drop_reason {
       TCP_OFO_QUEUE = 1,
       TCP_DATA_QUEUE_OFO = 2,
       TCP_DATA_QUEUE = 3,
       TCP_PRUNE_OFO_QUEUE = 4,
       TCP_VALIDATE_INCOMING = 5,
       TCP_RCV_ESTABLISHED = 6,
       TCP_RCV_SYNSENT_STATE_PROCESS = 7,
       TCP_RCV_STATE_PROCESS = 8
};


TRACE_EVENT(tcp_drop,

    TP_PROTO(struct sock *sk, struct sk_buff *skb, enum tcp_drop_reason reason),

    TP_ARGS(sk, skb, reason),

    TP_STRUCT__entry(
        __array(__u8, saddr, sizeof(struct sockaddr_in6))
        __array(__u8, daddr, sizeof(struct sockaddr_in6))
        __field(__u16, sport)
        __field(__u16, dport)
        __field(__u32, mark)
        __field(__u16, data_len)
        __field(__u32, snd_nxt)
        __field(__u32, snd_una)
        __field(__u32, snd_cwnd)
        __field(__u32, ssthresh)
        __field(__u32, snd_wnd)
        __field(__u32, srtt)
        __field(__u32, rcv_wnd)
        __field(__u64, sock_cookie)
        __field(__u32, reason)
    ),

    TP_fast_assign(
        const struct tcphdr *th = (const struct tcphdr *)skb->data;
        const struct inet_sock *inet = inet_sk(sk);
        const struct tcp_sock *tp = tcp_sk(sk);

        memset(__entry->saddr, 0, sizeof(struct sockaddr_in6));
        memset(__entry->daddr, 0, sizeof(struct sockaddr_in6));

        TP_STORE_ADDR_PORTS(__entry, inet, sk);

        /* For filtering use */
        __entry->sport = ntohs(inet->inet_sport);
        __entry->dport = ntohs(inet->inet_dport);
        __entry->mark = skb->mark;

        __entry->data_len = skb->len - __tcp_hdrlen(th);
        __entry->snd_nxt = tp->snd_nxt;
        __entry->snd_una = tp->snd_una;
        __entry->snd_cwnd = tp->snd_cwnd;
        __entry->snd_wnd = tp->snd_wnd;
        __entry->rcv_wnd = tp->rcv_wnd;
        __entry->ssthresh = tcp_current_ssthresh(sk);
        __entry->srtt = tp->srtt_us >> 3;
        __entry->sock_cookie = sock_gen_cookie(sk);
        __entry->reason = reason;
    ),

    TP_printk("src=%pISpc dest=%pISpc mark=%#x data_len=%d snd_nxt=%#x snd_una=%#x snd_cwnd=%u ssthresh=%u snd_wnd=%u srtt=%u rcv_wnd=%u sock_cookie=%llx reason=%d",
          __entry->saddr, __entry->daddr, __entry->mark,
          __entry->data_len, __entry->snd_nxt, __entry->snd_una,
          __entry->snd_cwnd, __entry->ssthresh, __entry->snd_wnd,
          __entry->srtt, __entry->rcv_wnd, __entry->sock_cookie, __entry->reason)
);


cp net/ipv4/tcp_input.c ../net-push/net/ipv4/tcp_input.c
cp include/trace/events/tcp.h ../net-push/include/trace/events/tcp.h
cp include/net/tcp.h ../net-push/include/net/tcp.h

On Tue, Aug 24, 2021 at 5:08 PM Zhongya Yan <yan2228598786@gmail.com> wrote:
> >


 --cc netdev@vger.kernel.org,linux-kernel@vger.kernel.org,kuba@kernel.org,mingo@redhat.com,davem@davemloft.net,yoshfuji@linux-ipv6.org,dsahern@kernel.org,hengqi.chen@gmail.com,yhs@fb.com,2228598786@qq.com



mutt --to edumazet@google.com,rostedt@goodmis.org,brendan.d.gregg@gmail.com, --cc netdev@vger.kernel.org,linux-kernel@vger.kernel.org,kuba@kernel.org,mingo@redhat.com,davem@davemloft.net,yoshfuji@linux-ipv6.org,dsahern@kernel.org,yhs@fb.com,2228598786@qq.com -s "Re: [PATCH] net: tcp_drop adds \`reason\` and SNMP parameters for tracing v4"


Steven Rostedt <rostedt@goodmis.org> wrote:
> Just curious. Is "Tcp" the normal way to write "TCP" in the kernel? I see
> it in snmp_seq_show_tcp_udp() in net/ipv4/proc.c, but no where else
> (besides doing CamelCase words). Should these be written using "TCP"
> instead of "Tcp". It just looks awkward to me.
>
> But hey, I'm not one of the networking folks, so what do I know?
>
> -- Steve

I will do it. I guess any other suggestions? I feel like my description may not be accurate. 
Thanks.
    --Zhongya Yan



git send-email  0001-tcp-tcp_drop-adds-SNMP-and-reason-parameter-for-trac.patch --to edumazet@google.com,rostedt@goodmis.org,brendan.d.gregg@gmail.com, --cc netdev@vger.kernel.org,linux-kernel@vger.kernel.org,kuba@kernel.org,mingo@redhat.com,davem@davemloft.net,yoshfuji@linux-ipv6.org,dsahern@kernel.org,yhs@fb.com,2228598786@qq.com 

git send-email  0001-tcp-tcp_drop-adds-SNMP-and-reason-parameter-for-trac.patch --to 2228598786@qq.com 


docker tag registry.aliyuncs.com/google_containers/kube-apiserver:v1.22.1 k8s.gcr.io/kube-apiserver:v1.22.1
docker tag registry.aliyuncs.com/google_containers/kube-controller-manager:v1.22.1 k8s.gcr.io/kube-controller-manager:v1.22.1
docker tag registry.aliyuncs.com/google_containers/kube-scheduler:v1.22.1 k8s.gcr.io/kube-scheduler:v1.22.1
docker tag registry.aliyuncs.com/google_containers/kube-proxy:v1.22.1 k8s.gcr.io/kube-proxy:v1.22.1
docker tag registry.aliyuncs.com/google_containers/pause:3.5 k8s.gcr.io/pause:3.5
docker tag registry.aliyuncs.com/google_containers/etcd:3.5.0-0 k8s.gcr.io/etcd:3.5.0-0
docker tag coredns/coredns:v1.8.4 k8s.gcr.io/coredns/coredns:v1.8.4



// 生成 Patch
git log filename 
查看文件提交历史，确定子系统
[这个讲的比较好](https://www.dazhuanlan.com/doraemonfj/topics/1139017)



