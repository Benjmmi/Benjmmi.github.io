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
	// 根据字段找个当前整个结构
	eep = container_of(inode->i_cdev, struct eeprom_dev, cdev);
	// 缓冲区大小
	eep->eepram_size = SRAM_SIZE;
	// 从内核中分配内存
	eep->eep_data = kzalloc(eep->eepram_size, GFP_KERNEL);
	if (eep->eep_data == NULL)
	{
		pr_err("Open: memory allocation failed\n");
		return -ENOMEM;
	}
	pr_warn("文件被打开 %s \n", filp->f_path.dentry->d_iname);
	// 将文件结构与当前自定义结构关联
	filp->private_data = eep;
	return 0;
}

static int eep_release(struct inode *inode, struct file *filp)
{
	struct eeprom_dev *eep = NULL;
	eep = container_of(inode->i_cdev, struct eeprom_dev, cdev);
	//kfree(eep);
	filp->private_data = NULL;
	return 0;
}

/**
 * filp: 
 * buf: 表示来与用户空间的缓冲区
 * count: 请求数据的长度
 * f_pos: 数据在文件中应写入的开始位置
 * */
ssize_t eeprom_write(struct file *filp, const char __user *buf, 
		size_t count, loff_t *f_pos)
{
	struct eeprom_dev *eep = filp->private_data;
	pr_info("开始写用户空间的数据 \n");
	// 检查数据长度是否超过了缓冲区的限制
	if (*f_pos > eep->eepram_size)
	{
		pr_err("当前写入出现错误，f_pos 的位置已经超出了限制 \n");
		return -EINVAL;
	}
	// 根据数据长度调整当前缓冲区的剩余长度 count
	if (*f_pos + count > eep->eepram_size)
	{
		count = eep->eepram_size - *f_pos;
	}
	// 根据 f_pos 找到开始写入的位置
	
	// 将用户空间数据写入到内核缓存空间
	if (copy_from_user(eep->eep_data, buf, count) != 0){
		pr_err("将用户空间数据写入到内核缓存空间失败 \n");
		return -EFAULT;
	}
	// 缓存中的数据写入到真实的物理位置
	pr_warn("从用户空间获取的数据 %s \n", eep->eep_data);
	// 根据写入数据的长度返回复制的字节数
	
	return count;
}

ssize_t eep_read(struct file *filp, char __user *buf,
	       	size_t count, loff_t *f_pos)
{
	struct eeprom_dev *eep = filp->private_data;
	pr_err("开始读取内核空间数据\n");
	if (*f_pos > eep->eepram_size)
	{
		pr_err("读取内核数据失败 f_pos 超出范围\n");
		return -EFAULT;
	}
	if (*f_pos + count > eep->eepram_size)
	{
		pr_err("读取内核数据失败 f_pos+count 超出范围\n");
                return -EFAULT;
	}
	if (copy_to_user(buf, eep->eep_data, count) != 0)
	{
		 pr_err("读取内核数据失败\n");
		 return -EIO;
	}
	return count;
}

static struct file_operations eep_fops = {
	.owner = THIS_MODULE,
	.open = eep_open,
	.write = eeprom_write,
	.read = eep_read,
	.release = eep_release,
};

static int __init helloworld_init(void)
{
	int i = 0;
	dev_t curr_dev;
	pr_info("Hello World\n");
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
	dev_t curr_dev;
	int i;
	unregister_chrdev_region(dev_num, EEP_NBANK);
	for(i = 0; i < EEP_NBANK; i++)
	{
		curr_dev = MKDEV(MAJOR(dev_num), MINOR(dev_num) + i);
		device_destroy(eep_class, curr_dev);
		cdev_del(&eep_cdev[i]);
	}
	class_destroy(eep_class);

	pr_info("End of the World");
}

module_init(helloworld_init);
module_exit(helloworld_exit);
MODULE_AUTHOR("Banjamin Yim<2228598786@qq.com>");
MODULE_LICENSE("GPL");
