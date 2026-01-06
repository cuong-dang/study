#include "common.h"
#include <assert.h>

void negpos(int a[], int n) {
  int i = 0, j = n - 1;

  while (i < j) {
    if (a[i] >= 0) {
      swap(a, i, j--);
    } else {
      i++;
    }
  }
}

int main() {
  int a[] = {-3, 2, -1, 4, 0, -5, -6, 7};
  int aa[] = {-3, -6, -1, -5, 0, 4, 7, 2};
  int b[] = {2, -1, 0, 3, -5, 0, -4};
  int bb[] = {-4, -1, -5, 3, 0, 0, 2};

  assert_fxs(a, aa, 8, negpos);
  assert_fxs(b, bb, 7, negpos);
}
