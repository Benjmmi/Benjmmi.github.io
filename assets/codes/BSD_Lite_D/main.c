#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>

#define BUFFSIZE 150

int main(int argc, char *argv[])
{
    struct sockaddr_in serv;
    char buff[BUFFSIZE];
    int sockfd, n;
    if((sockfd = socket(AF_INET, SOCK_DGRAM, 0) < 0 )) {
        printf("socket error");
        return -1;
    }
    memset(&serv, 0, sizeof(struct sockaddr_in));
    serv.sin_family = AF_INET;
    serv.sin_addr.s_addr = inet_addr("127.0.0.1");
    serv.sin_port = 9090;

    if(sendto(sockfd, buff, BUFFSIZE, 0, (struct sockaddr *) &serv, sizeof(serv)) != BUFFSIZE) {
        printf("sendto error");
        return -1;
    }
    if((n = recvfrom(sockfd, buff, BUFFSIZE, 0, (struct sockaddr *) NULL, 0) < 2)
    {
        printf("recvfrom error");
        return -1;
    }
    buff[n-2] = 0;
    printf("%s\n",buff);
    return 0;
}
