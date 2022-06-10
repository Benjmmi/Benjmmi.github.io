#include <linux/module.h>
#include <linux/mm.h>
#include <linux/fs.h>
#include <asm/io.h>
#include <asm/uaccess.h>

char modename[] = "dram";
int my_major = 85;
unsigned long dram_size;

loff_t my_llseek(struct file *file, loff_t offset, int whence);
ssize_t my_read(struct file *file, char *buff, size_t count, loff_t *pos);

struct file_operations
my_fops = {
	owner:	THIS_MODULE,
	llseek:	my_llseek,
	read:	my_read;
};

static int __int init(void)
{
	printk( "<1>\nInstalling \'%s\' module ", modname );
	printk( "(major=%d)\n", my_major );

	dram_size = num_physpages * PAGE_SIZE;
	printk( "<1>  ramtop=%016lX (%lu MB)\n", dram_size, dram_size >> 20 );
	return 	register_chrdev(my_major, modname, &my_fops);
}

static void __exit cleanup_module(void)
{
	unregister_chrdev( my_major, modname );
	printk( "<1>Removing \'%s\' module\n", modname );
}

ssize_t my_read(struct file *file, char *buf, size_t count, loff_t *ops)
{
	
}
