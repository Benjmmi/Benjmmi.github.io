#include <linux/init.h>
#include <linux/module.h>
#include <linux/sched.h>
#include <linux/errno.h>
#include <linux/moduleparam.h>
#include <linux/cdev.h>
#include <linux/fs.h>
#include <linux/kernel.h>
#include <linux/slab.h>
#include <linux/kdev_t.h>

static char *msg = "Param message";
static int how = 1;
static int scull_quantum = 1;
static int scull_qset = 0;
static int major;
static int minor;
dev_t dev_id;
unsigned int count = 4;
char  *name = "my_dev";

module_param(how, int, S_IRUGO);
module_param(msg, charp, S_IRUGO);
module_param(scull_quantum, int, S_IRUGO);
module_param(scull_qset, int, S_IRUGO);

struct scull_qset {
	void **data;
	struct scull_qset *next;
};

struct scull_dev{
	struct scull_qset *data;
	int quantum;
	int qset;
	unsigned long size;
	unsigned int access_key;
	struct semaphore sem;
	struct cdev cdev;
} *dev;

int scull_trim(struct scull_dev *dev) {
        struct scull_qset *next, *dptr;
        int qset = dev->qset;
        int i;
        for (dptr = dev->data; dptr; dptr = next) {
                if (dptr->data) {
                        for(i = 0; i < qset; i++)
                                kfree(dptr->data[i]);
                        kfree(dptr->data);
                        dptr->data = NULL;
                }
                next = dptr->next;
                kfree(dptr);
        }
        dev->size = 0;
        dev->quantum = scull_quantum;
        dev->qset = scull_qset;
        dev->data = NULL;
        return 0;
}

int scull_open(struct inode *inode, struct file *filp) {
	struct scull_dev *dev;
	// 将抽象的 i_cdev 内核的内部结果转换为包含 cdev 的 scull_dev
	dev = container_of(inode->i_cdev, struct scull_dev, cdev);
	// 将指针保存到 file 结构的 private_data 
        filp->private_data = dev;

        if( (filp->f_flags & O_ACCMODE) == O_WRONLY ) {
                scull_trim(dev);
        }
        return 0;
}

int scull_release(struct inode *inode, struct file *filp) {
	return 0;
}

// operator file desc
const struct file_operations scull_ops = {
        .owner = THIS_MODULE,
        .llseek = NULL,
        .read = NULL,
        .write = NULL,
        .open = scull_open,
        .release = scull_release,
};

static int hello_init(void)
{
	int err;
	pid_t pid = current->pid;
	printk(KERN_ALERT "当前进程pid= %i,驱动加载是代入的参数=msg %s\n", pid, msg);
	// 分配一个设备 ID
	alloc_chrdev_region(&dev_id, 0, count, name);
	// 设备 ID 的大版本号
	major = MAJOR(dev_id);
	// 设备 ID 的小版本号
	minor = MINOR(dev_id);
	printk("alloc_chrdev_region dev_t=%d major=%d minor=%d count=%d\n", dev_id, count, major, minor);
	// 获取一个独立的 cdev 结构
	dev->cdev = *cdev_alloc();
	dev->cdev.ops = &scull_ops;
	dev->cdev.owner = THIS_MODULE;
	// 初始化已分配到的结构
	cdev_init(&dev->cdev, &scull_ops);
	
	// 告诉内核当前结构信息
	err = cdev_add(&dev->cdev, dev_id, 0);
	if(err < 0) {
		goto del;
	}

	return  0;
del:
	cdev_del(&dev->cdev);
	return -1;
}

static void hello_exit(void)
{
	printk(KERN_ALERT "Hello Exit\n");
	unregister_chrdev_region(dev_id, count);
	printk("unregister_chrdev_region dev_t=%d count=%d\n", dev_id, count);
}

MODULE_LICENSE("Dual BSD/GPL");

module_init(hello_init);
module_exit(hello_exit);
