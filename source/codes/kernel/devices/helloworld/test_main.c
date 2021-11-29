#include <unistd.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/select.h>

#define NUMBER_OF_BYTE 0x111
#define CHAR_DEVICE "/dev/eep-mem0"

char data[NUMBER_OF_BYTE];
int my_write(int fd,char *buf);
int my_read(int fd,char buf[]);

int main(int argc, char **argv)
{
	int fd;
	char *buf = "Banjamin my_char_device";
	char retval[NUMBER_OF_BYTE];
	int err;
	fd = open(CHAR_DEVICE, O_RDWR);
	err = my_write(fd, buf);
	if (err < 0)
	{
		printf("写数据失败\n");
		return -1;
	}
	printf("写数据成功\n");
	err = my_read(fd, retval);
	if (err < 0)
        {
                printf("读取数据失败\n");
                return -1;
        }
	printf("读取数据返回 %s\n", retval);
	return 0;
}

int my_write(int fd, char *buf)
{
        if (fd < 0)
        {
                printf("文件 %s 打开失败\n", CHAR_DEVICE);
                return -1;
        }
        if (write(fd, buf,sizeof(buf)) < 0){
                printf("写到设备失败\n");
                return -1;
        }
	return 0;
}

int my_read(int fd,char buf[])
{
        if (fd < 0)
        {
                printf("文件 %s 打开失败\n", CHAR_DEVICE);
                return -1;
        }
	if (read(fd, buf, NUMBER_OF_BYTE) < 0)
	{
		printf("从设备读取失败\n");
                return -1;
	}
	return 0;
}
