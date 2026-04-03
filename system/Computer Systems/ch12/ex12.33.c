#include "../lib/csapp.h"

#define TIMEOUT_SECS 5

static void *thread_fgets(void *vargp);
static void *thread_timeout(void *vargp);
static int read_success = 0;
static pthread_t fgets_tid;
static pthread_t timeout_tid;
static sem_t thread_fgets_ready;
static sem_t thread_timeout_ready;

char *tfgets(char *s, int size, FILE *stream)
{
    pthread_t tid;
    void *vargp = Malloc(sizeof(char *) + sizeof(int));

    Sem_init(&thread_fgets_ready, 0, 0);
    Sem_init(&thread_timeout_ready, 0, 0);
    memcpy(vargp, &s, sizeof(char *));
    memcpy((char *)vargp + sizeof(char *), &size, sizeof(int));
    Pthread_create(&fgets_tid, NULL, thread_fgets, vargp);
    Pthread_create(&timeout_tid, NULL, thread_timeout, NULL);
    Pthread_join(fgets_tid, NULL);
    Pthread_join(timeout_tid, NULL);
    if (read_success)
        return s;
    return NULL;
}

void *thread_fgets(void *vargp)
{
    char *s = *(char **)vargp;
    int size = *(int *)((char *)vargp + sizeof(char *));

    V(&thread_fgets_ready);
    P(&thread_timeout_ready);
    fgets(s, size, stdin);
    read_success = 1;
    Pthread_cancel(timeout_tid);
}

void *thread_timeout(void *vargp)
{
    V(&thread_timeout_ready);
    P(&thread_fgets_ready);
    sleep(5);
    Pthread_cancel(fgets_tid);
}
