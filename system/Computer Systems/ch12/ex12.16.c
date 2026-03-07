#include "../lib/csapp.h"

#define MAXTHREADS 64

void *thread(void *vargp);

int main(int arc, char **argv)
{
        pthread_t tids[MAXTHREADS];
        int i, n = atoi(argv[1]);

        for (int i = 0; i < n; ++i)
                Pthread_create(&tids[i], NULL, thread, (void *)i);
        for (int i = 0; i < n; ++i) {
                Pthread_join(tids[i], NULL);
                printf("Thread id %d joined\n", i);
        }
        exit(0);
}

void *thread(void *vargp)
{
        int id = (int)vargp;

        printf("Hello from thread id %d\n", id);
        return NULL;
}
