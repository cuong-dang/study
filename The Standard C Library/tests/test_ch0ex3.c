#include "ch0ex2.h"

#include <stdio.h>

double a[] = {1.0, 2.0};
double *p = a;
double sqr(x) { return x * x; }
#define sqr(x) x *x

int main(void) {
    printf("%f\n", sqr(3.0));
    printf("%d\n", sqr(3));
    printf("%d\n", sqr(3 + 3));
    return 0;
}
