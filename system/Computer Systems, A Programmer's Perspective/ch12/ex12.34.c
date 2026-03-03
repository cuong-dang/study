#include "../lib/csapp.h"
#include "../lib/sbuf.h"

#define N 1<<10
#define MAX_VALUE N
#define NTHREADS 4

int a[N][N];
int b[N][N];
int c[N][N];
sem_t all_done;

void *thread(void *vargp);
void fill_matrix(int a[N][N]);
int dot_product(int a[N][N], int b[N][N], int i, int j);
void show_matrix(int a[N][N]);

int main(void)
{
    sbuf_t sbuf;
    int i;
    pthread_t tid;
    clock_t begin, end;

    Sem_init(&all_done, 0, 0);
    srand(time(NULL));
    fill_matrix(a);
    fill_matrix(b);

    printf("Running on %d threads...\n", NTHREADS);
    begin = clock();
    sbuf_init(&sbuf, N);
    for (i = 0; i < NTHREADS; ++i)
        Pthread_create(&tid, NULL, thread, (void *)&sbuf);
    for (i = 0; i < N; ++i)
        sbuf_insert(&sbuf, i);
    for (i = 0; i < N; ++i)
        P(&all_done);
    end = clock();

    printf("Elapsed time: %f\n", (double)(end - begin) / CLOCKS_PER_SEC);

    exit(0);
}

void fill_matrix(int a[N][N])
{
    int i, j;

    for (i = 0; i < N; ++i)
        for (j = 0; j < N; ++j)
            a[i][j] = rand() % MAX_VALUE;
}

void *thread(void *vargp)
{
    sbuf_t *sp = (sbuf_t *)vargp;
    int i, j;

    while (1) {
        i = sbuf_remove(sp);
        for (j = 0; j < N; j++)
            c[i][j] = dot_product(a, b, i, j);
        V(&all_done);
    }
}

int dot_product(int a[N][N], int b[N][N], int i, int j)
{
    int k, result = 0;

    for (k = 0; k < N; k++)
        result += a[i][k] * b[k][j];
    return result;
}

void show_matrix(int a[N][N])
{
    int i, j;

    for (i = 0; i < N; ++i)
        for (j = 0; j < N; ++j)
            if (j == N - 1) printf("%d\n", a[i][j]);
            else printf("%d ", a[i][j]);
    printf("\n");
}
