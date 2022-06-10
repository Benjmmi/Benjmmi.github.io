---
title: Kubernetes - ISTIO2
date: 2021-11-17 16:49:53
categories: 
	- [gRPC]
tags:
  - microservices
  - Kubernetes
  - k8s
author: Jony
---


```bash
Usage:
  pilot-discovery discovery [flags]

Flags:
      --caCertFile string                          包含 x509 服务器 CA 证书的文件
      --clusterAliases stringToString              集群的别名（默认 []）
      --clusterID string                           该Istiod实例所在集群的ID（默认为 "Kubernetes"）
      --clusterRegistriesNamespace string          用于存储集群配置的ConfigMap的命名空间（默认为 "istio-system"）
      --configDir string                           观察配置yaml文件更新的目录。如果指定，这些文件将被用作配置的来源，而不是CRD客户端。
      --ctrlz_address string                       要侦听的ControlZ自检工具的IP地址。使用‘*’表示所有地址。(默认为“localhost”)
      --ctrlz_port uint16                          用于ControlZ自检设施的IP端口(默认值为9876)
      --domain string                              DNS 域后缀（默认为“cluster.local”）
      --grpcAddr string                            发现服务 gRPC 地址（默认“:15010”）
  -h, --help                                       discovery 帮助信息
      --httpAddr string                            服务发现HTTP地址(默认值“：8080”)
      --httpsAddr string                           注入和验证服务HTTPS地址(默认为“：15017”)
      --keepaliveInterval duration                 时间间隔(如果连接上没有任何活动)，它会ping对等方以查看传输是否处于活动状态(默认值为30秒)
      --keepaliveMaxServerConnectionAge duration   连接在正常关闭之前将在服务器上保持打开的最长持续时间。(默认为2562047h47m16.854775807s)
      --keepaliveTimeout duration                  在ping了keepalive检查后，客户/服务器等待keepaliveTimeout的时间，如果之后没有看到任何活动，则关闭连接。(默认为10s)
      --kubeconfig string                          使用Kubernetes配置文件而不是集群内配置
      --kubernetesApiBurst int                     与kubernetes API通信时节点流量的最大爆发量（默认为160）
      --kubernetesApiQPS float32                   与kubernetes API通信时的最大QPS（默认80）。
      # --log_as_json                                是将输出格式化为JSON还是使用简单的控制台格式
      # --log_caller string                          逗号分隔的作用域列表，其中包括调用者信息，作用域可以是[ads, adsc, all, analysis, authn, authorization, ca, controllers, default, file, gateway, grpcgen, installer, klog, kube, model, monitor, pkica, processing, proxyconfig, retry, rootcertrotator, secretcontroller, serverca, spiffe, status, telemetry, tpath, trustBundle, util, validation, validationController, validationServer, wasm, wle] 中的任何一个。
      # --log_output_level string                    以逗号分隔的要输出的每个范围的最小日志级别的消息，形式为<scope>:<level>,<scope>:<level>,... 其中范围可以是[ads, adsc, all, analysis, authn, authorization, ca, controllers, default, file, gateway, grpcgen, installer, klog, kube, model, monitor, pkica, processing, proxyconfig, retry, rootcertrotator, secretcontroller, serverca, spiffe, status, telemetry, tpath, trustBundle, util, validation, validationController, validationServer, wasm, wle]，级别可以是[debug, info, warn, error, fatal, none]之一（默认 "default: info"）。
      # --log_rotate string                          可选rotate日志文件的路径
      # --log_rotate_max_age int                     日志文件的最长使用期限(以天为单位)，超过该天数后，该文件将被 rotate(0表示没有限制)(默认值为30)
      # --log_rotate_max_backups int                 删除旧文件之前保留的日志文件备份的最大数量(0表示没有限制)(默认值为1000)
      # --log_rotate_max_size int                    日志文件的最大大小(以MB为单位)，超过此大小，文件将被轮换(默认值为104857600)
      # --log_stacktrace_level string                逗号分隔的最小每个范围的日志级别，在该级别上捕获堆栈痕迹，形式为<scope>:<level>,<scope:level>,... 其中范围可以是[ads, adsc, all, analysis, authn, authorization, ca, controllers, default, file, gateway, grpcgen, installer, klog, kube, model, monitor, pkica, processing, proxyconfig, retry, rootcertrotator, secretcontroller, serverca, spiffe, status, telemetry, tpath, trustBundle, util, validation, validationController, validationServer, wasm, wle]，级别可以是[debug, info, warn, error, fatal, none]之一（默认 "default: none"）。
      # --log_target stringArray                     输出日志的路径集。这可以是任何路径，也可以是特殊值stdout和stderr（默认[stdout]）。
      --meshConfig string                          Istio网格配置的文件名。如果没有指定，将使用默认的网格。(默认为"./etc/istio/config/mesh")
      --monitoringAddr string                      用于 Polit 自我监测信息的HTTP地址（默认为":15014"）。
  -n, --namespace string                           选择一个控制器所在的命名空间。如果没有设置，使用${POD_NAMESPACE}环境变量（默认为 "istio-system"）。
      --networksConfig string                      Istio网状网络配置的文件名。如果不指定，将使用默认的网状网络。(默认为"./etc/istio/config/meshNetworks")
      --plugins strings                            使用逗号分隔的网络插件列表(默认[ext_authz,authn,authz])
      --profile                                    通过Web界面启用分析功能 host:port/debug/pprof（默认为true）。
      --registries strings                         逗号分隔的要读取的平台服务注册表（从{Kubernetes, Mock}中选择一个或多个）（默认[Kubernetes]）。
      --resync duration                            控制器resync间隔(默认为1m0s)
      --secureGRPCAddr string                      发现服务安全的gRPC地址（默认为":15012"）。
      --shutdownDuration duration                  发现服务器需要优雅地终止的时间（默认为10s）。
      --tls-cipher-suites strings                  逗号分隔的istiod TLS服务器的密码套件列表。如果省略，将使用默认的Go密码套件。
                                                   首选值: TLS_AES_128_GCM_SHA256, TLS_AES_256_GCM_SHA384, TLS_CHACHA20_POLY1305_SHA256, TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA, TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA, TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384, TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256, TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA, TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA, TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384, TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256, TLS_RSA_WITH_AES_128_CBC_SHA, TLS_RSA_WITH_AES_128_GCM_SHA256, TLS_RSA_WITH_AES_256_CBC_SHA, TLS_RSA_WITH_AES_256_GCM_SHA384. 
                                                   不安全的值: TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256, TLS_ECDHE_ECDSA_WITH_RC4_128_SHA, TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA, TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256, TLS_ECDHE_RSA_WITH_RC4_128_SHA, TLS_RSA_WITH_3DES_EDE_CBC_SHA, TLS_RSA_WITH_AES_128_CBC_SHA256, TLS_RSA_WITH_RC4_128_SHA.
      --tlsCertFile string                         包含x509服务器证书的文件
      --tlsKeyFile string                          包含x509私钥匹配的文件 --tlsCertFile

Global Flags:
      --vklog Level   日志级别粗略程度的数字。例如： --vklog=9
```



run
```bash
discovery
--registries
Mock
```





```yaml
 ---
proxyListenPort: 15001
connectTimeout: 10s
protocolDetectionTimeout: 0s
ingressClass: istio
ingressService: istio-ingressgateway
ingressControllerMode: STRICT
enableTracing: true
defaultConfig:
  configPath: "./etc/istio/proxy"
  binaryPath: "/usr/local/bin/envoy"
  serviceCluster: istio-proxy
  drainDuration: 45s
  parentShutdownDuration: 60s
  discoveryAddress: istiod.istio-system.svc:15012
  proxyAdminPort: 15000
  controlPlaneAuthPolicy: MUTUAL_TLS
  statNameLength: 189
  concurrency: 2
  tracing:
    zipkin:
      address: zipkin.istio-system:9411
  statusPort: 15020
  terminationDrainDuration: 5s
outboundTrafficPolicy:
  mode: ALLOW_ANY
enableAutoMtls: true
trustDomain: cluster.local
trustDomainAliases: []
defaultServiceExportTo:
- "*"
defaultVirtualServiceExportTo:
- "*"
defaultDestinationRuleExportTo:
- "*"
rootNamespace: istio-system
localityLbSetting:
  enabled: true
dnsRefreshRate: 5s
certificates: []
thriftConfig: {}
serviceSettings: []
extensionProviders:
- name: prometheus
  prometheus: {}
- name: stackdriver
  stackdriver: {}
- name: envoy
  envoyFileAccessLog:
    path: "/dev/stdout"
defaultProviders: {}




 ---
ServerOptions:
  HTTPAddr: ":8080"
  HTTPSAddr: ":15017"
  GRPCAddr: ":15010"
  MonitoringAddr: ":15014"
  EnableProfiling: true
  TLSOptions:
    CaCertFile: ''
    CertFile: ''
    KeyFile: ''
    TLSCipherSuites:
    CipherSuits:
  SecureGRPCAddr: ":15012"
InjectionOptions:
  InjectionDirectory: "./var/lib/istio/inject"
PodName: ''
Namespace: istio-system
Revision: ''
MeshConfigFile: "./etc/istio/config/mesh"
NetworksConfigFile: "./etc/istio/config/meshNetworks"
RegistryOptions:
  FileDir: ''
  Registries:
  - Mock
  KubeOptions:
    SystemNamespace: ''
    MeshServiceController:
    ResyncPeriod: 60000000000
    DomainSuffix: cluster.local
    ClusterID: Kubernetes
    ClusterAliases: {}
    Metrics:
    XDSUpdater:
    NetworksWatcher:
    MeshWatcher:
    EndpointMode: 0
    KubernetesAPIQPS: 80
    KubernetesAPIBurst: 160
    SyncInterval: 0
    SyncTimeout:
    DiscoveryNamespacesFilter:
  ClusterRegistriesNamespace: istio-system
  KubeConfig: ''
  DistributionCacheRetention: 60000000000
  DistributionTrackingEnabled: true
CtrlZOptions:
  Port: 9876
  Address: localhost
Plugins:
- ext_authz
- authn
- authz
KeepaliveOptions:
  Time: 30000000000
  Timeout: 10000000000
  MaxServerConnectionAge: 9223372036854776000
  MaxServerConnectionAgeGrace: 10000000000
ShutdownDuration: 10000000000
JwtRule: ''


```

```bash
curl 10.42.77.70:8080/debug
/debug/adsz ADS的状态和调试接口
/debug/adsz?push=true 启动向所有连接的端点推送当前状态的程序
/debug/authorizationz 内部授权政策
/debug/cachez 关于内部XDS缓存的信息
/debug/config_distribution 连接到这个Pilot实例的所有Envoy的版本状态。     
/debug/config_dump 以Envoy admin config dump API的形式对传递的proxyID进行配置转储。     
/debug/configz 对配置的调试支持     
/debug/edsz EDS的状态和调试接口     
/debug/endpointShardz 关于端点分片的信息     
/debug/endpointz 对端点的调试支持     
/debug/inject 活跃的注入模板     
/debug/instancesz 对服务实例的调试支持     
/debug/ndsz NDS的状态和调试接口     
/debug/pprof/ 显示pprof索引     
/debug/pprof/cmdline 当前程序的命令行调用。     
/debug/pprof/profile CPU简介     
/debug/pprof/symbol 符号查找请求中列出的程序计数器     
/debug/pprof/trace 对当前程序的执行情况进行跟踪。     
/debug/push_status 最后的PushContext细节     
/debug/registryz 对注册表的调试支持     
/debug/resourcesz 对被监视资源的调试支持     
/debug/syncz 连接到这个Pilot实例的所有Envoys的同步状态     
```



