#include <linux/ioctl.h>

// 使用 k 作为幻数
#define SCULL_IOC_MAGIC 'k'

// 
#define SCULL_IOCRESET __IO(SCULL_IOC_MAGIC, 0)

/*
 * S 表示通过指针设置
 * T 表示直接用参数
 * G 表示“获取GET” 通过设置指针来应答
 * Q 表示“查询”通过返回值应答
 * X 表示交换，原子地交换 G 和 S
 * H 表示切换，原子的交互 T 和 Q
 */
#define SCULL_IOCSQUANTUM 	__IOW(SCULL_IOC_MAGIC, 	1, int)
#define SCULL_IOCSQSET	 	__IOW(SCULL_IOC_MAGIC, 	2, int)
#define SCULL_IOCTQUANTUM	__IO(SCULL_IOC_MAGIC, 	3)
#define SCULL_IOCTQSET		__IO(SCULL_IOC_MAGIC, 	4)
#define SCULL_IOCGQUANTUM 	__IOR(SCULL_IOC_MAGIC, 	5, int)
#define SCULL_IOCGQSET 		__IOR(SCULL_IOC_MAGIC, 	6, int)
#define SCULL_IOCQQUANTUM 	__IO(SCULL_IOC_MAGIC, 	7)
#define SCULL_IOCQQSET 		__IO(SCULL_IOC_MAGIC, 	8)
#define SCULL_IOCXQUANTUM 	__IOWR(SCULL_IOC_MAGIC,	9, int)
#define SCULL_IOCXQSET 		__IOWR(SCULL_IOC_MAGIC, 10, int)
#define SCULL_IOCHQUANTUM 	__IO(SCULL_IOC_MAGIC, 	11)
#define SCULL_IOCHQSET	 	__IO(SCULL_IOC_MAGIC, 	12)

#define SCULL_IOC_MAXNR 14
