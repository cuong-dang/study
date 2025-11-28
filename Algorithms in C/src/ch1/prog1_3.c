/* Weighted quick-union */
#include <stdio.h>
#define N 10000

int main(void) {
  int i, j, k, t, p, q, id[N], sz[N];

  for (i = 0; i < N; i++) {
    id[i] = i;
    sz[i] = 1;
  }
  while (scanf("%d %d\n", &p, &q) == 2) {
    for (i = p; i != id[i]; i = id[i])
      ;
    for (j = q; j != id[j]; j = id[j])
      ;
    if (i == j) {
      continue;
    }
    if (sz[i] < sz[j]) {
      id[i] = j;
      i = j;
    } else {
      id[j] = i;
      sz[i] += sz[j];
    }
    printf("%d %d\n", p, q);
  }
}
