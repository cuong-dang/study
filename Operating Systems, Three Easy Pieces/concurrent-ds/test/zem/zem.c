#include "assert_macro.h"
#include "zem.h"

pthread_mutex_t lock;
int sentinel;

void *inc(void *arg)
{
    Zem_t *z = (Zem_t *)arg;

    Zem_wait(z);
    pthread_mutex_lock(&lock);
    ++sentinel;
    pthread_mutex_unlock(&lock);
    return NULL;
}

int zem(int argc, char **argv)
{
    int err_code = 0;
    pthread_t t1, t2, t3;
    Zem_t z;

    pthread_mutex_init(&lock, NULL);
    sentinel = 0;
    Zem_init(&z, 0);

    pthread_create(&t1, NULL, inc, (void *)&z);
    while (z.value >= 0)
    {
        ;
    }
    assertEqual(sentinel == 0);
    assertEqual(z.value == -1);

    pthread_create(&t2, NULL, inc, (void *)&z);
    while (z.value >= -1)
    {
        ;
    }
    assertEqual(sentinel == 0);
    assertEqual(z.value == -2);

    Zem_post(&z);
    pthread_join(t1, NULL);
    assertEqual(sentinel == 1);
    assertEqual(z.value == -1);

    pthread_create(&t1, NULL, inc, (void *)&z);
    while (z.value >= -1)
    {
        ;
    }
    assertEqual(sentinel == 1);
    assertEqual(z.value == -2);

    pthread_create(&t3, NULL, inc, (void *)&z);
    while (z.value >= -2)
    {
        ;
    }
    assertEqual(sentinel == 1);
    assertEqual(z.value == -3);

    Zem_post(&z);
    Zem_post(&z);
    Zem_post(&z);
    pthread_join(t1, NULL);
    pthread_join(t2, NULL);
    pthread_join(t3, NULL);
    assertEqual(sentinel == 4);
    assertEqual(z.value == 0);

    return err_code;
}
