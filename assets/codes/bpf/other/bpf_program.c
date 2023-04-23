#include <linux/bpf.h>
#include <linux/ptrace.h>
#include <linux/version.h>
#include <bpf_helpers.h>

SEC("kprobe/do_sys_open")
int kprobe__do_sys_open(struct pt_regs *ctx)
{
	char msg[256] = "Hello World! \n";
	bpf_trace_printk(msg, sizeof(msg));
	return 0;
}
char __license[] SEC("license") = "GPL";
