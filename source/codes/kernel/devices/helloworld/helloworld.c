#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/fs.h>
#include <linux/cdev.h>
#include <linux/slab.h>

#include "helloworld.h"

/**/
#define EEP_NBANK 8
/* 设备名称 */
#define EEP_DEVICE_NAME "eep-mem"
/* 设备类型 */
#define EEP_CLASS "eep-class"

struct class *eep_class;
struct cdev eep_cdev[EEP_NBANK];
dev_t dev_num;

static int YIM;
module_param(YIM, int, S_IRUGO);

static int eep_open(struct inode *inode, struct file *filp)
{
	struct eeprom_dev *eep = NULL;
	eep = container_of(inode->i_cdev, struct eeprom_dev, cdev);
	eep->eepram_size = SRAM_SIZE;
	eep->eep_data = kzalloc(eep->eepram_size, GFP_KERNEL);
	filp->private_data = eep;
	return 0;
}

static int eep_release(struct inode *inode, struct file *filp)
{
	filp->private_data = NULL;
	return 0;
}

ssize_t eeprom_write(struct file *filp, const char __user *buf, 
		size_t count, loff_t *f_pos)
{
	return 0;
}

static struct file_operations eep_fops = {
	.owner = THIS_MODULE,
	.open = eep_open,
};

static int __init helloworld_init(void)
{
	pr_info("Hello World\n");
	int i = 0;
	dev_t curr_dev;
	/* 为 EEP_NBANK 设备请求分配设备号 */
	alloc_chrdev_region(&dev_num, 0, 
			EEP_NBANK, EEP_DEVICE_NAME);
	
	/* 创建设备的类型，在 /sys/class 目录下查看 */
	eep_class = class_create(THIS_MODULE, EEP_CLASS);

	/* 每个 eeprom bank 表示一个字符设备 (cdev) */
	for(i = 0; i < EEP_NBANK; i++)
	{
		// 将 file_operations 绑定到 cdev
		cdev_init(&eep_cdev[i], &eep_fops);
		eep_cdev[i].owner = THIS_MODULE;
		
		// 将 cdev 添加到核心的设备号
		curr_dev = MKDEV(MAJOR(dev_num), MINOR(dev_num) + i);

		// 让用户访问设备
		cdev_add(&eep_cdev[i], curr_dev, 1);

		device_create(eep_class,
				NULL,
				curr_dev,
				NULL,
				EEP_DEVICE_NAME "%d",i);
	}
	return 0;
}

static void __exit helloworld_exit(void)
{
	unregister_chrdev_region(&dev_num, EEP_NBANK);
	pr_info("End of the World");
}

module_init(helloworld_init);
module_exit(helloworld_exit);
MODULE_AUTHOR("Banjamin Yim<2228598786@qq.com>");
MODULE_LICENSE("GPL");
