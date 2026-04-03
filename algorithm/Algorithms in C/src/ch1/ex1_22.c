#include "../util.h"
#include <stdio.h>

void run(int n);

int main(void) {
  run(1e3);
  run(1e4);
  run(1e5);
  run(1e6);
}

void run(int n) {
  int i, j, p, q, id[n], sz[n], u, c;

  for (i = 0; i < n; i++) {
    id[i] = i;
    sz[i] = 1;
  }
  u = 0;
  c = 0;
  while (u != n - 1) {
    c++;
    p = randint(0, n - 1);
    q = randint(0, n - 1);
    for (i = p; i != id[i]; i = id[i]) {
      id[i] = id[id[i]];
    }
    for (j = q; j != id[j]; j = id[j]) {
      id[j] = id[id[j]];
    }
    if (i == j) {
      continue;
    }
    u++;
    if (sz[i] < sz[j]) {
      id[i] = j;
      sz[j] += sz[i];
    } else {
      id[j] = i;
      sz[i] += sz[j];
    }
  }
  printf("N=%d. Number of edges generated: %d.\n", n, c);
}
