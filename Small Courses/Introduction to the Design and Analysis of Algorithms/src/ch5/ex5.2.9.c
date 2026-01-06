#include "common.h"

void rwb(int a[], int n) {
  int i = 0, k = 0, j = n - 1;

  while (k <= j) {
    if (a[k] == 'W') {
      k++;
    } else if (a[k] == 'R') {
      swap(a, k, i++);
    } else {
      swap(a, k, j--);
    }
  }
}

int main() {
  int a[] = {'R', 'B', 'W', 'R', 'B', 'W', 'B', 'R', 'B', 'R', 'W', 'W'};
  int aa[] = {'R', 'R', 'R', 'R', 'W', 'W', 'W', 'W', 'B', 'B', 'B', 'B'};
  int b[] = {'R', 'W', 'B'};
  int bb[] = {'R', 'W', 'B'};
  int c[] = {'B', 'R', 'W'};
  int d[] = {'W', 'W', 'R', 'R', 'B', 'W', 'R', 'B', 'B', 'R', 'W', 'B'};

  assert_fxs(a, aa, 12, rwb);
  assert_fxs(b, bb, 3, rwb);
  assert_fxs(c, bb, 3, rwb);
  assert_fxs(d, aa, 12, rwb);
}
