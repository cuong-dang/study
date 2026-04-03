#include <stdio.h>
#include <unistd.h>
#include <pthread.h>

#include "zem.h"

Zem_t z1, z2;

void *child_1(void *arg)
{
    printf("child 1: before\n");
    Zem_post(&z1);
    Zem_wait(&z2);
    printf("child 1: after\n");
    return NULL;
}

void *child_2(void *arg)
{
    sleep(2);
    printf("child 2: before\n");
    Zem_post(&z2);
    Zem_wait(&z1);
    printf("child 2: after\n");
    return NULL;
}

int main(int argc, char *argv[])
{
    pthread_t p1, p2;

    Zem_init(&z1, 0);
    Zem_init(&z2, 0);

    printf("parent: begin\n");
    pthread_create(&p1, NULL, child_1, NULL);
    pthread_create(&p2, NULL, child_2, NULL);
    pthread_join(p1, NULL);
    pthread_join(p2, NULL);
    printf("parent: end\n");
    return 0;
}
