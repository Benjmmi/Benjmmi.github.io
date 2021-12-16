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
#include <linux/netdevice.h>
#include <linux/skbuff.h>
#include <linux/etherdevice.h>

#include "netdev.h"

#define SNULL_TX_INTR 1
#define SNULL_RX_INTR 2

MODULE_AUTHOR("Banjamin Yim <yan2228598786@gmail.com>");
MODULE_DESCRIPTION("This My Test Devployment Drive");
MODULE_LICENSE("Dual BSD/GPL");

struct snull_packet {
        struct snull_packet *next;
	struct snull_packet *pre;
	int datalen;
        char *data;
};

// snull_priv 数据结构
struct snull_priv {
	struct net_device_stats stats;
	int status;
	struct snull_packet *ppool;
	struct snull_packet *rx_queue;
	int rx_int_enabled;
	int tx_packetlen;
	u8  *tx_packetdata;
	struct sk_buff *skb;
	spinlock_t lock;
};

struct net_device *snull_devs[2];

static void snull_init(struct net_device *dev)
{
	struct snull_priv *priv;
	ether_setup(dev);
	dev->flags |= IFF_NOARP;
	dev->features |= NETIF_F_IP_CSUM;
	priv = netdev_priv(dev);
	memset(priv, 0, sizeof(struct snull_priv));
	spin_lock_init(&priv->lock);
}

void snull_cleanup(void)
{
        int i;
        for(i=0; i<2; i++)
        {
                if (snull_devs[i])
                {
                        unregister_netdev(snull_devs[i]);
                        free_netdev(snull_devs[i]);
                }
        }
}

static int __init netdev_init(void)
{
	int i;
	char name[IFNAMSIZ];
	sprintf(name, "sl%d", 0);
	snull_devs[0] = alloc_netdev(sizeof(struct snull_priv), 
				     name,
				     NET_NAME_UNKNOWN,
				     snull_init);
	pr_info("初始化设备 %s\n", name);
	sprintf(name, "sl%d", 1);
	snull_devs[1] = alloc_netdev(sizeof(struct snull_priv),
			             name,
				     NET_NAME_UNKNOWN,
				     snull_init);
	pr_info("初始化设备 %s\n", name);
	if (snull_devs[0] == NULL || snull_devs[1] == NULL)
	{
		goto out;
	}
	
	// 初始化完成后注册设备
	for (i=0; i<2; i++)
	{
		int result;
		pr_info("注册设备 %s \n",snull_devs[i]->name);
		result = register_netdev(snull_devs[i]);
		if(result)
		{
			printk("snull: error %i registering device \" %s \" \n",
					result, snull_devs[i]->name);
		}
	}
	return 0;
out:
	snull_cleanup();
	return 0;
}

static void __init netdev_exit(void)
{
	pr_info("清理设备\n");
	// 清理设备
	//snull_cleanup();

}




module_init(netdev_init);
module_exit(netdev_exit);
