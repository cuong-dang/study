#include <stdio.h>
#include <unistd.h>
#include <pthread.h>

#include "zem.h"

Zem_t z;

void *child(void *arg)
{
    printf("child\n");
    Zem_post(&z);
    sleep(1);
    return NULL;
}

int main(int argc, char *argv[])
{
    pthread_t p;
    printf("parent: begin\n");
    Zem_init(&z, 0);
    pthread_create(&p, NULL, child, NULL);
    Zem_wait(&z);
    printf("parent: end\n");
    return 0;
}
