#include <assert.h>

int largest_(int a[], int lo, int hi) {
  int m, i, j;

  if (hi - lo == 1)
    return lo;
  m = (lo + hi) / 2;
  i = largest_(a, lo, m);
  j = largest_(a, m, hi);
  if (a[i] < a[j])
    return j;
  else
    return i;
}

int largest(int a[], int n) { return largest_(a, 0, n); }

int main() {
  int a[] = {0};
  int b[] = {0, 1};
  int c[] = {0, 1, 2};
  int d[] = {3, 5, 7, 6, 8, 9, 1, 2, 0, 4};

  assert(largest(a, 1) == 0);
  assert(largest(b, 2) == 1);
  assert(largest(c, 3) == 2);
  assert(largest(d, 10) == 5);
}
