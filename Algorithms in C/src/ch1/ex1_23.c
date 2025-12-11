#include "../util.h"
#include <stdio.h>

int run(int n);

int main(void) {
  int n;
  FILE *gp = popen("gnuplot -persistent", "w");
  fprintf(gp, "set xlabel 'n'\n");
  fprintf(gp, "set ylabel 'num edges'\n");
  fprintf(gp, "plot '-' with linespoints\n");

  for (n = 100; n <= 1000; n++) {
    fprintf(gp, "%d %d\n", n, run(n));
  }
  fprintf(gp, "e\n");
  fflush(gp);
  pclose(gp);
  return 0;
}

int run(int n) {
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
  return c;
}
