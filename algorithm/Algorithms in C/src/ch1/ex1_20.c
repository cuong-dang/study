#include "../util.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define N 1000
#define M 1000000
#define T 10000

void by_size(void);
void by_height(void);

static int pq[M][2], i, j;

int main(void) {
  srand(time(NULL));

  for (i = 0; i < M; i++) {
    pq[i][0] = randint(0, N - 1);
    pq[i][1] = randint(0, N - 1);
  }
  printf("By size avg time: %g\n", timeit_n(&by_size, T));
  printf("By height avg time: %g\n", timeit_n(&by_height, T));
}

void by_size(void) {
  int i, p, q, u, v, id[N], sz[N];

  for (i = 0; i < N; i++) {
    id[i] = 0;
    sz[i] = 1;
  }
  for (i = 0; i < N; i++) {
    p = pq[i][0];
    q = pq[i][1];
    for (u = p; u != id[u]; u = id[u])
      ;
    for (v = p; v != id[v]; v = id[v])
      ;
    if (sz[u] < sz[v]) {
      id[u] = v;
      sz[v] += sz[u];
    } else {
      id[v] = u;
      sz[u] += sz[v];
    }
  }
}

void by_height(void) {
  int i, p, q, u, v, id[N], h[N];

  for (i = 0; i < N; i++) {
    id[i] = 0;
    h[i] = 0;
  }
  for (i = 0; i < N; i++) {
    p = pq[i][0];
    q = pq[i][1];
    for (u = p; u != id[u]; u = id[u])
      ;
    for (v = p; v != id[v]; v = id[v])
      ;
    if (h[u] < h[v]) {
      id[u] = v;
      h[v]++;
    } else {
      id[v] = u;
      h[u]++;
    }
  }
}
