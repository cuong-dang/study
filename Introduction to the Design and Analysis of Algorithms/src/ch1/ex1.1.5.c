#include "ch1.h"

void ex115(int *a, int m, int *b, int n, int *c, int *t) {
  int i = 0, j = 0, k = 0;

  while (1) {
    if (i >= m || j >= n) {
      *t = k;
      break;
    }
    if (a[i] == b[j]) {
      c[k++] = a[i];
      i++;
      j++;
    } else if (a[i] < b[j]) {
      i++;
    } else {
      j++;
    }
  }
}
