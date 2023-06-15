#ifndef UTIL_H
#define UTIL_H

#include <stdarg.h>
#include <stdio.h>

#ifndef MIN
#define MIN(a, b) ((a) < (b) ? (a) : (b))
#endif
#ifndef MAX
#define MAX(a, b) ((a) > (b) ? (a) : (b))
#endif

void printd(char *s, ...);

#endif
