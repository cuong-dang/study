#include <assert.h>
#include <stdlib.h>
#include <string.h>

int merge(int a[], int a1[], int a2[], int n1, int n2) {
  int i = 0, j = 0, k = 0, iv = 0;

  while (i < n1 && j < n2) {
    if (a1[i] <= a2[j]) {
      a[k++] = a1[i++];
    } else {
      a[k++] = a2[j++];
      iv += n1 - i;
    }
  }
  if (i == n1) {
    memcpy(a + k, a2 + j, (n2 - j) * sizeof(int));
  } else {
    memcpy(a + k, a1 + i, (n1 - i) * sizeof(int));
  }
  return iv;
}

int mergesort_(int a[], int lo, int hi) {
  int m, iv1, iv2;
  int *a1, *a2;

  if (hi - lo == 1) {
    return 0;
  }
  m = (lo + hi) / 2;
  a1 = (int *)malloc((m - lo) * sizeof(int));
  memcpy(a1, a, (m - lo) * sizeof(int));
  a2 = (int *)malloc((hi - m) * sizeof(int));
  memcpy(a2, a + (m - lo), (hi - m) * sizeof(int));
  iv1 = mergesort_(a1, lo, (lo + hi) / 2);
  iv2 = mergesort_(a2, (lo + hi) / 2, hi);
  return iv1 + iv2 + merge(a, a1, a2, m - lo, hi - m);
}

int mergesort(int a[], int n) { return mergesort_(a, 0, n); }

void assert_xs(int xs[], int ys[], int n, int iv) {
  int i;

  assert(mergesort(xs, n) == iv);
  for (i = 0; i < n; i++) {
    assert(xs[i] == ys[i]);
  }
}

int main() {
  int a[] = {3, 1, 2}, aa[] = {1, 2, 3};
  int b[] = {0, 1, 5, 7, 6, 4, 2, 3};
  int bb[] = {0, 1, 2, 3, 4, 5, 6, 7};
  int c[6], c1[] = {3, 5, 6}, c2[] = {1, 4, 7};

  assert_xs(a, aa, 3, 2);
  assert_xs(b, bb, 8, 12);
  assert(merge(c, c1, c2, 3, 3) == 5);
}
