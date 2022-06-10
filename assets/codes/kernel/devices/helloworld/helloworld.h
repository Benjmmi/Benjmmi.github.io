#include <linux/types.h>

#define SRAM_SIZE			0x1000


struct eeprom_dev {
	u64 eepram_size;
	char *eep_data;
	struct cdev cdev;	
};
