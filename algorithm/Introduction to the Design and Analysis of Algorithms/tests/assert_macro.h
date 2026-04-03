#ifndef ASSERT_MACRO_H
#define ASSERT_MACRO_H

#include <stdio.h>

#define assertEqual(...)                                                       \
  do {                                                                         \
    if (!(__VA_ARGS__)) {                                                      \
      fprintf(stderr, "Unit test assert [ ");                                  \
      fprintf(stderr, #__VA_ARGS__);                                           \
      fprintf(stderr, " ] failed in line [ ");                                 \
      fprintf(stderr, "%d", __LINE__);                                         \
      fprintf(stderr, " ] file [ ");                                           \
      fprintf(stderr, __FILE__);                                               \
      fprintf(stderr, " ]\n");                                                 \
      ec = 1;                                                                  \
    }                                                                          \
  } while (0)

#endif // ASSERT_MACRO_H
