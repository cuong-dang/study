#include "ch1.h"

int ex1110(int a, int b) {
  if (b == 0) {
    return a;
  }
  while (a >= b) {
    a -= b;
  }
  return ex1110(b, a);
}