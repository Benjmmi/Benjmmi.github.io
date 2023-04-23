#include <linux/bpf.h>
#include <bpf/bpf_helpers.h>
#include <net/sock.h>

typedef struct backlog_key {
    u32 backlog;
    u64 slot;
} backlog_key_t;

int do_entry(struct pt_regs *ctx)
{
	
}


char LICENSE[] SEC("license") = "Dual BSD/GPL";