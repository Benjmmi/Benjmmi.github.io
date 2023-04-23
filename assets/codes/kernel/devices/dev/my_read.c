#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>

int main(void)
{
	int fd,num;
	fd = open("/dev/my_dev",O_RDWR,S_IRUSR | S_IWUSR);
	if(fd < 0){
		printf("打开文件失败 %d\n",fd);
	}
	num = 1234;
	write(fd,&num,sizeof(int));
	close(fd);
	return 0;
}
