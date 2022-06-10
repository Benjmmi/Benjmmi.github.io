#include <linux/init.h>
#include <linux/module.h>
#include <linux/mm.h>
#include <linux/mm_types.h>

MODULE_LICENSE("Dual BSD/GPL");

static __init int mm_init(void)
{
	printk(KERN_INFO "Hello World\n");
	printk(KERN_INFO "PAGE_SHIFT=%d", PAGE_SHIFT);
	printk(KERN_INFO "PMD_SHIFT=%d", PMD_SHIFT);
	printk(KERN_INFO "PUD_SHIFT=%d", PUD_SHIFT);
	printk(KERN_INFO "P4D_SHIFT=%d", P4D_SHIFT);
	printk(KERN_INFO "PGDIR_SHIFT=%d", PGDIR_SHIFT);
	printk(KERN_INFO "PTRS_PER_PTE=%d", PTRS_PER_PTE);
	printk(KERN_INFO "PTRS_PER_PMD=%d", PTRS_PER_PMD);
	printk(KERN_INFO "PTRS_PER_PUD=%d", PTRS_PER_PUD);
	printk(KERN_INFO "PTRS_PER_PGD=%d", PTRS_PER_PGD);
	return 0;
}

static __exit void mm_exit(void)
{
	printk(KERN_ALERT "Goobye , cruel world\n");
}

module_init(mm_init);
module_exit(mm_exit);
