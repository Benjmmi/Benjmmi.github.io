#include <linux/init.h>
#include <linux/module.h>
#include <linux/sched.h>
#include <linux/errno.h>
#include <linux/moduleparam.h>
#include <linux/cdev.h>
#include <linux/fs.h>


static char *msg = "Param message";
static int how = 1;
dev_t dev_id;
unsigned int count = 4;
char  *name = "my_dev";

module_param(how, int, S_IRUGO);
module_param(msg, charp, S_IRUGO);

struct cdev *my_dev = cdev_aaloc();


static int hello_init(void)
{
	pid_t pid = current->pid;
	printk(KERN_ALERT "Hello World %i,param msg %s\n", pid, msg);
	alloc_chrdev_region(&dev_id, 0, count, name);
	printk("alloc_chrdev_region dev_t=%d count=%d", dev_id, count);
	return  0;
}

static void hello_exit(void)
{
	printk(KERN_ALERT "Hello Exit\n");
	unregister_chrdev_region(&dev_id, count);
	printk("unregister_chrdev_region dev_t=%d count=%d", dev_id, count);
}

MODULE_LICENSE("Dual BSD/GPL");

module_init(hello_init);
module_exit(hello_exit);
