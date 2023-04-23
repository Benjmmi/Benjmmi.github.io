obj-m:=mm_address.o
PWD:=$(shell pwd) 
KERNELDIR:= /lib/modules/$(shell uname -r)/build

all:
	make -C $(KERNELDIR) M=$(PWD) modules
