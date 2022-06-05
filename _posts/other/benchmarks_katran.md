---
title: Other - katran 基准测试
date: 2021-04-01 17:29:11
categories: 
	- [Other]
tags:
  - http
  - glang
author: Jony
---

Intel(R) Xeon(R) CPU E5-2680 v4 @ 2.40GHz

# katran

wrk -c 10000 -t 32 -d 300s http://10.210.9.103/t  -v 
Requests/sec: 2356919.98
Transfer/sec:    388.61MB

wrk -c 10000 -t 32 -d 300s http://10.210.9.103/t  -v 
Requests/sec: 2351944.52
Transfer/sec:    387.86MB



# LVS 

wrk -c 10000 -t 32 -d 300s http://10.210.9.103/t  -v
Requests/sec: 2021869.63
Transfer/sec:    333.26MB

wrk -c 10000 -t 32 -d 300s http://10.210.9.103/t  -v
Requests/sec: 2093134.23
Transfer/sec:    344.92MB

[performance of katran](https://github.com/facebookincubator/katran/issues/21)