#include <assert.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "rwline.h"
#include "util.h"

//
// Your code goes in the structure and functions below
//

// This implementation is first-come, first-served.
#define READER 0
#define WRITER 1
#define MAX_THREADS 128

//
// Don't change the code below (just use it!)
//

int loops;
int value = 0;

rwline_t lock;

void *reader(void *arg)
{
    int i;
    for (i = 0; i < loops; i++)
    {
        rwline_acquire_readlock(&lock);
        printf("read %d\n", value);
        rwline_release_readlock(&lock);
    }
    return NULL;
}

void *writer(void *arg)
{
    int i;
    for (i = 0; i < loops; i++)
    {
        rwline_acquire_writelock(&lock);
        value++;
        printf("write %d\n", value);
        rwline_release_writelock(&lock);
    }
    return NULL;
}

int main(int argc, char *argv[])
{
    assert(argc == 4);
    int num_readers = atoi(argv[1]);
    int num_writers = atoi(argv[2]);
    loops = atoi(argv[3]);

    pthread_t pr[num_readers], pw[num_writers];

    rwline_init(&lock);

    printf("begin\n");

    int i;
    for (i = 0; i < MIN(num_readers, num_writers); i++)
    {
        pthread_create(&pr[i], NULL, reader, NULL);
        pthread_create(&pw[i], NULL, writer, NULL);
    }

    int more_readers = num_writers < num_readers ? 1 : 0;
    for (; i < MAX(num_readers, num_writers); i++)
    {
        if (more_readers)
        {
            pthread_create(&pw[i], NULL, reader, NULL);
        }
        else
        {
            pthread_create(&pw[i], NULL, writer, NULL);
        }
    }

    for (i = 0; i < num_readers; i++)
        pthread_join(pr[i], NULL);
    for (i = 0; i < num_writers; i++)
        pthread_join(pw[i], NULL);

    printf("end: value %d\n", value);

    return 0;
}
