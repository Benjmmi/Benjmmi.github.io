export HTTP_PROXY=http://192.168.0.103:58591; export HTTPS_PROXY=http://192.168.0.103:58591; export ALL_PROXY=socks5://192.168.0.103:51837

export HTTP_PROXY=http://192.168.10.6:58591; export HTTPS_PROXY=http://192.168.10.6:58591; export ALL_PROXY=socks5://192.168.10.6:51837

go run github.com/cilium/ebpf/cmd/bpf2go -cc clang -cflags "-fdebug-prefix-map=/ebpf=." bpf ./bpf/kprobe_example.c -- -I../headers

dlv debug --headless --listen=:2345 --api-version=2 --accept-multiclient


#include <linux/bpf.h>
#define SEC(NAME) __attribute__((section(NAME), used))

static int (*bpf_trace_printk)(const char *fmt, int fmt_size, ...) = (void *)BPF_FUNC_trace_printk;

SEC("tracepoint/syscalls/sys_enter_execve")
int bpf_prog(void *ctx) {
  char msg[] = "Hello, BPF World!";
  bpf_trace_printk(msg, sizeof(msg));
  return 0;
}



// ../../lib/bpf/adapter_integration_test_kern.c \
// ../../lib/bpf/balancer_kern.c \
// ../../lib/bpf/healthchecking_ipip.c \
// ../../lib/bpf/healthchecking_kern.c \
// ../../lib/bpf/xdp_pktcntr.c \
// ../../lib/bpf/xdp_root.c \
     
//go:generate go run github.com/cilium/ebpf/cmd/bpf2go -cc clang -cflags "-fdebug-prefix-map=/ebpf=." bpf ../../lib/bpf/adapter_integration_test_kern.c -- -I../../lib/linux_includes
//go:generate go run github.com/cilium/ebpf/cmd/bpf2go -cc clang -cflags "-fdebug-prefix-map=/ebpf=." bpf ../../lib/bpf/balancer_kern.c -- -I../../lib/linux_includes
//go:generate go run github.com/cilium/ebpf/cmd/bpf2go -cc clang -cflags "-fdebug-prefix-map=/ebpf=." bpf ../../lib/bpf/healthchecking_ipip.c -- -I../../lib/linux_includes
//go:generate go run github.com/cilium/ebpf/cmd/bpf2go -cc clang -cflags "-fdebug-prefix-map=/ebpf=." bpf ../../lib/bpf/healthchecking_kern.c -- -I../../lib/linux_includes
//go:generate go run github.com/cilium/ebpf/cmd/bpf2go -cc clang -cflags "-fdebug-prefix-map=/ebpf=." bpf ../../lib/bpf/xdp_pktcntr.c -- -I../../lib/linux_includes
//go:generate go run github.com/cilium/ebpf/cmd/bpf2go -cc clang -cflags "-fdebug-prefix-map=/ebpf=." bpf ../../lib/bpf/xdp_root.c -- -I../../lib/linux_includes
