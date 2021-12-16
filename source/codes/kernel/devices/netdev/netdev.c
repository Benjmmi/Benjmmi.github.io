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

//int snull_open(struct net_device *dev)
//{
//	memcpy(dev->dev_addr, "\0SNUL0", ETH_ALEN);
//	if (dev == snull_devs[1])
//	{
//		dev->dev_addr[ETH_ALEN-1]++;
//	}
//	netif_start_queue(dev);
//	return 0;
//}

//int snull_release(struct net_device *dev)
//{
//	netif_stop_queue(dev);
//	return 0;
//}

//int snull_tx(struct sk_buff *skb, struct net_device *dev)
//{
//	int len;
//	char *data,shortpkt[ETH_ZLEN];
//	struct snull_priv *priv = netdev_priv(dev);
//	
//	data = skb->data;
//	len = skb->len;
//	if (len<ETH_ZLEN)
//	{
//		memset(shortpkt, 0, ETH_ZLEN);
//		memcpy(shortpkt, skb->data, skb->len);
//		len = ETH_ZLEN;
//		data = shortpkt;
//	}
//	// dev->start_time = jiffies;
//	priv->skb = skb;
//
//	// snull_hw_tx(data, len, dev);
//	
//	return 0;
//}

//void snull_tx_timeout(struct net_device *dev, unsigned int txqueue)
//{
//	struct snull_priv *priv = netdev_priv(dev);
//	priv->status = SNULL_TX_INTR;
//	//snull_interrupt(0, dev, NULL);
//	priv->stats.tx_errors++;
//	netif_wake_queue(dev);
//	return;
//}

// 中断模式接收或者轮询模式接收
// 中断模式 
// @pkt 指向数据的指针
//void snull_rx(struct net_device *dev, struct snull_packet *pkt)
//{
//	struct sk_buff *skb;
//	struct snull_priv *priv = netdev_priv(dev);
//
//	// 已经从传输介质获取数据包。建立封装它的 skb 使得上层应用可以处理它
//	skb = dev_alloc_skb(pkt->datalen - 1);
//	if(!skb)
//	{
//		//分配失败
//		if (printk_ratelimit())
//		{
//			pr_err("snull rx: 内存不足");
//			priv->stats.rx_dropped++;
//			goto out;
//		}
//	}
//	memcpy(skb_put(skb, pkt->datalen), pkt->data, pkt->datalen);
//
//	// 写数据，然后将其传递给接收层
//	skb->dev = dev;
//	// 解析当前协议
//	skb->protocol = eth_type_trans(skb, dev);
//	// cum
//	skb->ip_summed = CHECKSUM_UNNECESSARY;
//	priv->stats.rx_packets++;
//	priv->stats.rx_bytes += pkt->datalen;
//	// 传递到上层解析
//	netif_rx(skb);
//out:
//	return;
//}

//static void snull_regular_interrupt(int irq, void *dev_id, struct pt_regs *regs)
//{
//	int statusword;
//	struct snull_priv *priv;
//	struct snull_packet *pkt = NULL;
//
//	struct net_device *dev = (struct net_device *)dev_id;
//
//	if(!dev)
//	{
//		return;
//	}
//	// 设备加锁
//	priv = netdev_priv(dev);
//	spin_lock(&priv->lock);
//
//	statusword = priv->status;
//	priv->status = 0;
//	if (statusword & SNULL_RX_INTR) {
//		// 是接收数据包
//		pkt = priv->rx_queue;
//		if (pkt) {
//			priv->rx_queue = pkt->next;
//			snull_rx(dev, pkt);
//		}
//	} else if( statusword & SNULL_TX_INTR) {
//		priv->stats.tx_packets++;
//		priv->stats.tx_bytes += priv->tx_packetlen;
//		dev_kfree_skb(priv->skb);
//	}
//	// 对设备解锁
//	spin_unlock(&priv->lock);
//	if (pkt) {
//	//	snull_release_buffer(pkt);
//	}
//	return;
//}

//static int snull_poll(struct net_device *dev, int *budget)
//{
//	int npackets = 0, quota = min(dev->real_num_rx_queues, *budget);
//	struct sk_buff *skb;
//	struct snull_priv *priv = netdev_priv(dev);
//	struct snull_packet *pkt;
//	
//	while(npackets < quota && priv->rx_queue) {
//		//pkt = snull_dequeue_buf(dev);
//		skb = dev_alloc_skb(pkt->datalen + 2);
//		if (!skb) {
//			if (printk_ratelimit()) {
//				pr_err("snull: packet dropped\n");
//			}
//			priv->stats.rx_dropped++;
//	//		snull_release_buffer(pkt);
//			continue;
//		}
//		memcpy(skb_put(skb, pkt->datalen), pkt->data, pkt->datalen);
//		skb->dev = dev;
//		skb->protocol = eth_type_trans(skb, dev);
//		skb->ip_summed = CHECKSUM_UNNECESSARY;
//		netif_receive_skb(skb);
//
//		// 维护统计信息
//
//npackets++;
//		priv->stats.rx_packets++;
//		priv->stats.rx_bytes += pkt->datalen;
//	//	snull_release_buffer(pkt);
//	}
//
//	// 重新打开中断
//	*budget -= npackets;
//	dev->real_num_rx_queues -= npackets;
//	if (!priv->rx_queue) {
//		//netif_rx_complete(dev);
//		//snull_rx_ints(dev, 1);
//		return 0;
//	}
//	// 包未处理完
//	return 1;
//}

static void snull_init(struct net_device *dev)
{
//	struct snull_priv *priv;
//	struct net_device_ops *ops = dev->netdev_ops;
/*	ops->ndo_open = snull_open;
	ops->ndo_stop = snull_release;
	// dev->set_config = snull_config;
	ops->ndo_start_xmit = snull_tx;
	// dev->do_ioctl = snull_ioctl;
	// dev->get_stats = snull_stats;
	// dev->rebuild_header = snull_rebuild_header;
	// dev->hard_header = snull_header;
	ops->ndo_tx_timeout = snull_tx_timeout;
	// dev->watchdog_timeo = timeout;
	dev->flags |= IFF_NOARP;
	dev->features |= NETIF_F_IP_CSUM;
	priv = netdev_priv(dev);
	memset(priv, 0, sizeof(struct snull_priv));
	spin_lock_init(&priv->lock);
	//snull_rs_ints(dev, 1);
*/
}

void snull_cleanup(void)
{
        int i;
        for(i=0; i<2; i++)
        {
                if (snull_devs[i])
                {
                        unregister_netdev(snull_devs[i]);
                        //snull_teradown_pool(snull_devs[i]);
                        free_netdev(snull_devs[i]);
                }
        }
}

static int __init netdev_init(void)
{
	int i;
	snull_devs[0] = alloc_netdev(sizeof(struct snull_priv), 
				     "sn%d",
				     NET_NAME_UNKNOWN,
				     snull_init);
	snull_devs[1] = alloc_netdev(sizeof(struct snull_priv),
			             "sn%d",
				     NET_NAME_UNKNOWN,
				     snull_init);
	if (snull_devs[0] == NULL || snull_devs[1] == NULL)
	{
		goto out;
	}
	
	// 初始化完成后注册设备
	for (i=0; i<2; i++)
	{
		int result;
		if((result = register_netdev(snull_devs[i])))
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
/*
void snull_cleanup(void)
{
	int i;
	for(i=0; i<2; i++)
	{
		if (snull_devs[i])
		{
			unregister_netdev(snull_devs[i]);
			//snull_teradown_pool(snull_devs[i]);
			free_netdev(snull_devs[i]);
		}
	}
}
*/
static void __init netdev_exit(void)
{
	// 清理设备
	snull_cleanup();

}




module_init(netdev_init);
module_exit(netdev_exit);
