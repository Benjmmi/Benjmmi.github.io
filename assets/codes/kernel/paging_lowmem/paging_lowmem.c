#include <linux/init.h>
#include <linux/module.h>
#include <linux/mm.h>
#include <linux/mm_types.h>
#include <linux/sched.h>
#include <linux/export.h>

MODULE_LICENSE("Dual BSD/GPL");

static unsigned long cr0,cr3;

static unsigned long vaddr = 0;

static void get_pgtable_macro(void){
	cr0 = read_cr0();// 保护模式位
	cr3 = read_cr3_pa();// 分页模式位

	printk("cr0 = 0x%lx, cr3 = 0x%lx", cr0, cr3);

	printk("PGDIR_SHIFT = %d\n", PGDIR_SHIFT);// 页全目录地址所能映射大小的对数
	printk("P4D_SHIFT = %d\n", P4D_SHIFT);  // P4D所能映射大小的对数
	printk("PUD_SHIFT = %d\n", PUD_SHIFT); // 上级目录所能映射大小的对数
	printk("PMD_SHIFT = %d\n", PMD_SHIFT); // 中间目录所能映射大小的对数
	printk("PAGE_SHIFT = %d\n", PAGE_SHIFT); // page 所能映射大小的对数


	printk("PTRS_PER_PGD = %d\n", PTRS_PER_PGD); // 全局页目录的数量
	printk("PTRS_PER_P4D = %d\n", PTRS_PER_P4D); // 页目录项的个数
	printk("PTRS_PER_PUD = %d\n", PTRS_PER_PUD); // 页目录项的个数
	printk("PTRS_PER_PMD = %d\n", PTRS_PER_PMD); // 页目录项的个数
	printk("PTRS_PER_PAGE = %d\n", PTRS_PER_PTE); // 页目录项的个数
	printk("PAGE_MASK = 0x%lx\n", PAGE_MASK); // 页内偏移掩码
}

/**
 * 虚拟地址（线性地址转页地址）
 */
static unsigned long vaddrtopaddr(unsigned long vaddr)
{
	pgd_t *pgd;
	p4d_t *p4d;
	pud_t *pud;
	pmd_t *pmd;
	pte_t *pte;

	unsigned long paddr = 0;
	unsigned long page_addr = 0;
	unsigned long page_offset = 0;
	// current->mm 代表 当前进出的 mm_struct 结构
	//  
	pgd = pgd_offset(current->mm,vaddr);
	printk("pgd_val = 0x%lx, pgd_index = %lu\n", 
			pgd_val(*pgd), 
			pgd_index(vaddr));
	if (pgd_none(*pgd)){
		printk("not mapped in pgd\n");
		return -1;
	}

	p4d = p4d_offset(pgd, vaddr);
	printk("p4d_val = 0x%lx, p4g_index = %lu\n",
                        p4d_val(*p4d),
                        p4d_index(vaddr));
        if (p4d_none(*p4d)){
                printk("not mapped in p4d\n");
                return -1;
        }


        pud = pud_offset(p4d, vaddr);
        printk("pud_val = 0x%lx, pug_index = %lu\n",
                        pud_val(*pud),
                        pud_index(vaddr));
        if (pud_none(*pud)){
                printk("not mapped in pud\n");
                return -1;
        }


        pmd = pmd_offset(pud, vaddr);
        printk("pmd_val = 0x%lx, pmg_index = %lu\n",
                        pmd_val(*pmd),
                        pmd_index(vaddr));
        if (pmd_none(*pmd)){
                printk("not mapped in pmd\n");
                return -1;
        }


        pte = pte_offset_kernel(pmd, vaddr);
        printk("pte_val = 0x%lx, pte_index = %lu\n",
                        pte_val(*pte),
                        pte_index(vaddr));
        if (pte_none(*pte)){
                printk("not mapped in pte\n");
                return -1;
        }

	page_addr = pte_val(*pte) & PAGE_MASK;
	page_offset = vaddr & ~PAGE_MASK;
	paddr = page_addr | page_offset;
	printk("page_addr = %lx, page_offset = %lx\n",
			page_addr,
			page_offset);
	printk("vaddr = %lx, paddr = %lx\n", vaddr, paddr);
	return paddr;
}

static int __init v2p_init(void)
{
	unsigned long vaddr = 0;
	printk("vaddr to paddr moudle is running...\n");
	get_pgtable_macro();
	printk("\n");
	vaddr = __get_free_page(GFP_KERNEL);
	if(vaddr == -1){
		printk("__get_free_page fail\n");
		return 0;
	}
	sprintf((char *)vaddr, "hello world from kernel");
	printk("get_page_vaddr 0x%lx\n", vaddr);
	vaddrtopaddr(vaddr);
	return 0;
}

static void __exit v2p_exit(void)
{
	printk("vaddr to paddr moudle is learning...\n");
	free_page(vaddr);
}

module_init(v2p_init);
module_exit(v2p_exit);
