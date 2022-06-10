#include <linux/bpf.h>

#ifndef __section
#define __section(NAME)    __attribute__((section(NAME), used))
#endif

static int (*bpf_trace_printk)(const char *fmt, int fmt_size, ...) = (void *)BPF_FUNC_trace_printk;

__section("prog")
int xdp_pass(struct xdp_md *ctx){
	char msg[] = "Hello, BPF World!";
  	bpf_trace_printk(msg, sizeof(msg));
	return XDP_PASS;
}

char __license[] __section("license") = "GPL";

