#include <linux/init.h>
#include <linux/device.h>
#include <linux/platform_device.h>
#include <linux/uaccess.h>
#include <asm/io.h>
#include <linux/types.h>
#include <linux/module.h>
#include <linux/sched.h>
#include <linux/errno.h>
#include <linux/moduleparam.h>
#include <linux/cdev.h>
#include <linux/fs.h>
#include <linux/kernel.h>
#include <linux/slab.h>
#include <linux/kdev_t.h>

#include "dev.h"

#define DRVNAME "my_dev"
#define MAX_PINS 32 

// 模块加载是传入的参数
static int major = 0;
module_param(major, int, S_IRUGO); // 用户自定义的设备号

static int scull_quantum = 1;
module_param(scull_quantum, int, S_IRUGO); //用户设置大小

static int scull_qset = 0;
module_param(scull_qset, int, S_IRUGO); // 用户设置大小

MODULE_AUTHOR("Benjamin Yim <yan2228598786@gmail.com>");
MODULE_DESCRIPTION("This My Test Devployment Drive");
MODULE_LICENSE("Dual BSD/GPL");


struct scull_qset {
	void **data;
	struct scull_qset *next;
};

struct scull_dev {
	struct scull_qset *data; // 指向 quantum 链表第一个指针
	int quantum; // 当前 quantum 的大小
	int qset; // 当前数据的大小
	unsigned long size;  // 保存在其中的数据总量
	unsigned int access_key; // 有 sculluid 和 scullpriv 使用
	struct semaphore sem; // 互斥信号
	struct cdev cdev; // 字符设备结构
};


ssize_t scull_write(struct file *filp, const char __user *buf,
		size_t count, loff_t *f_pos) {
	struct scull_dev *dev = filp->private_data;
	struct scull_qset *dptr;
	int quantum = dev->quantum, qset = dev->qset;
	int itemsize = quantum * qset;
	int item, s_pos, q_pos, rest;
	ssize_t retval = -ENOMEM; // goto out 语句使用值
	printk(KERN_INFO "开始写事件\n");
	if(down_interruptible(&dev->sem))
		return -ERESTARTSYS;

	// 在量子集中寻找链表、qset 索引以及偏移量
	item = (long)*f_pos / itemsize;
	rest = (long)*f_pos % itemsize;
	s_pos = rest / quantum; q_pos = rest % quantum;

	// 沿着该链表前行，qset 索引以及偏移量
	// dptr = scull_follow(dev, item);
	
	if (dptr == NULL)
		goto out;
	if (!(dptr->data)) {
		dptr->data = kmalloc(qset * sizeof(char *), GFP_KERNEL);
		if(!dptr->data)
			goto out;
		memset(dptr->data, 0, qset * sizeof(char *));
	}

	if (!dptr->data[s_pos]) {
		dptr->data[s_pos] = kmalloc(quantum, GFP_KERNEL);
		if(!dptr->data[s_pos])
			goto out;
	}

	// 将数据写入该量子,直到结尾
	if (count > quantum - q_pos)
		count = quantum - q_pos;
	printk(KERN_INFO "从用户空间将内容拷贝到内核空间缓存中\n");
	if (copy_from_user(dptr->data[s_pos]+q_pos, buf, count)) {
		retval = -EFAULT;
		goto out;
	}
	*f_pos += count;
	retval = count;
	
	// 更新文件大小
	if (dev->size < *f_pos)
		dev->size = *f_pos;
out:
	up(&dev->sem);
	return retval;
}

ssize_t scull_read(struct file *filp, char __user *buf,
		size_t count, loff_t *f_pos) {
	struct scull_dev *dev = filp->private_data;
	struct scull_qset *dptr; // 第一个链表项
	int quantum = dev->quantum, qset = dev->qset;
	int itemsize = quantum * qset;// 表示该链表中有多少个字节
	int item, s_pos, q_pos,rest;
	ssize_t retval = 0;

	if (down_interruptible(&dev->sem))
		return -ERESTARTSYS;
	if (*f_pos >= dev->size)
		goto out;
	if (*f_pos + count > dev->size)
		count = dev->size - *f_pos;

	// 在量子集中寻找链表项,qset 索引以及偏移量
	item = (long)*f_pos / itemsize;
	rest = (long)*f_pos / itemsize;
	s_pos = rest /quantum;
	q_pos = rest % quantum;

	// 沿该链表前行,直到正确的位置
	// dptr = scull_follow(dev, item);
	
	if(dptr == NULL || !dptr->data || !dptr->data[s_pos])
		goto out;

	if(count > quantum - q_pos)
		count = quantum - q_pos;
	if(copy_to_user(buf, dptr->data[s_pos] + q_pos, count)) {
		retval = -EFAULT;
		goto out;
	}
	*f_pos += count;
	retval = count;
out:
	up(&dev->sem);
	return retval;

}

// 初始化清空 scull_dev
int scull_trim(struct scull_dev *dev) {
        struct scull_qset *next, *dptr;
        int qset = dev->qset;
        int i;
	// 遍历所有链表项
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

// 驱动程序打开一个设备
int scull_open(struct inode *inode, struct file *filp) {
	struct scull_dev *dev;
	printk(KERN_INFO "打开一个设备\n");
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
        .read = scull_read,
        .write = scull_write,
        .open = scull_open,
        .release = scull_release,
};

static int hello_init(void)
{
	printk(KERN_INFO "Begin Hello Drive Init");
	dev_t dev_id;
	struct scull_dev *dev = kmalloc(sizeof(struct scull_dev), GFP_KERNEL);
	// 分配一个设备 ID
	alloc_chrdev_region(&dev_id, 0, MAX_PINS, DRVNAME);
	// 设备 ID 的大版本号
	major = MAJOR(dev_id);
	// 设备 ID 的小版本号
	unsigned int minor = MINOR(dev_id);
	printk(KERN_INFO "major=%d, minor=%d", major, minor);
	// 获取一个独立的 cdev 结构
	cdev_init(&dev->cdev, &scull_ops);
	
	dev->cdev.owner = THIS_MODULE;
	printk(KERN_ALERT "Begin Start CDEV init\n");
	
	// 告诉内核当前结构信息
	int err = cdev_add(&dev->cdev, dev_id, 0);
	if(err < 0) {
		goto del;
	}

	return  0;
del:
	cdev_del(&dev->cdev);
	printk(KERN_ALERT "异常删除设备!\n");
	return -1;
}

static void hello_exit(void)
{
	printk(KERN_ALERT "Hello Exit\n");
	unregister_chrdev_region(MKDEV(major, 0), MAX_PINS);
	printk(KERN_ERR "unregister Hello Drive\n");
}


module_init(hello_init);
module_exit(hello_exit);
