#include "common.h"
#include <assert.h>

void assert_fxs(int xs[], int ys[], int n, void (*f)(int *, int)) {
  f(xs, n);
  assert_xs(xs, ys, n);
}

void assert_xs(int xs[], int ys[], int n) {
  int i;

  for (i = 0; i < n; i++) {
    assert(xs[i] == ys[i]);
  }
}

void swap(int a[], int i, int j) {
  int t = a[i];

  a[i] = a[j];
  a[j] = t;
}
