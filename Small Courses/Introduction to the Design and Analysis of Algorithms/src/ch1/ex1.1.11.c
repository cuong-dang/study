#include "ch1.h"
#include <stdio.h>
#include <stdlib.h>

int ex1111(int a, int b, int *x, int *y) {
  int xx, yy, g;

  if (b == 0) {
    *x = 1;
    *y = 0;
    return a;
  }
  g = ex1111(b, a % b, &xx, &yy);
  *x = yy;
  *y = xx - yy * (a / b);
  return g;
}

void ex1111b(int a, int b, int c, int *x, int *y) {
  int g;

  g = ex1111(a, b, x, y);
  if (c % g) {
    printf("No solutions\n");
    exit(1);
  }
  *x = c / g * (*x);
  *y = c / g * (*y);
}
