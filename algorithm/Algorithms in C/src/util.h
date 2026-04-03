#ifndef UTIL_H
#define UTIL_H

#include <stdlib.h>
#include <time.h>

int randint(int min, int max) { return min + rand() % (max - min + 1); }

double timeit(void (*fp)(void)) {
  struct timespec start, end;
  double elapsed;

  clock_gettime(CLOCK_MONOTONIC, &start);
  (*fp)();
  clock_gettime(CLOCK_MONOTONIC, &end);

  elapsed = (end.tv_sec - start.tv_sec) + (end.tv_nsec - start.tv_nsec) / 1e9;
  return elapsed;
}

double timeit_n(void (*fp)(void), int n) {
  double elapsed;
  int i;

  elapsed = 0;
  for (i = 0; i < n; i++) {
    elapsed += timeit(fp);
  }
  return elapsed / n;
  ;
}

#endif // UTIL_H
