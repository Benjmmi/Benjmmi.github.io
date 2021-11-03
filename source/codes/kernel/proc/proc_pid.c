#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/sched/signal.h>

MODULE_LICENSE("Dual BSD/GPL");

static __init int procpid_init(void)
{
	pid_t pid = current->pid;
	struct signal_struct *sig = current->signal; 
	printk("Hello Proc pid = %d!!! limit %ld  !!!\n", pid, sig->rlim[RLIMIT_CPU].rlim_max);
	return 0;
}
static __exit void procpid_exit(void)
{	pid_t pid = current->pid;
	printk("Good Bye Proc pid = %d !!!\n", pid);
}

module_init(procpid_init);
module_exit(procpid_exit);
