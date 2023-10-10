#include <assert.h>

void swap(int a[], int i, int j) {
  int t = a[i];

  a[i] = a[j];
  a[j] = t;
}

int partition(int a[], int l, int r) {
  int p = a[l], i = l, j = r;

  while (i < j) {
    while (a[++i] < p)
      ;
    while (a[--j] > p)
      ;
    swap(a, i, j);
  }
  swap(a, i, j);
  swap(a, l, j);
  return j;
}

void quicksort_(int a[], int l, int r) {
  int s;

  if (l < r) {
    s = partition(a, l, r);
    quicksort_(a, l, s);
    quicksort_(a, s + 1, r);
  }
}

void quicksort(int a[], int n) { quicksort_(a, 0, n); }

void assert_xs(int xs[], int ys[], int n) {
  int i;

  quicksort(xs, n);
  for (i = 0; i < n; i++) {
    assert(xs[i] == ys[i]);
  }
}

int main() {
  int a[] = {3, 1, 5, 4, 7, 6, 2};
  int aa[] = {1, 2, 3, 4, 5, 6, 7};

  assert_xs(a, aa, 7);
}
