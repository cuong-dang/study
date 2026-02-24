#include <stdlib.h>

int main(int argc, char **argv) {
  int n, i, j, k, count;
  ;

  n = strtol(argv[1], NULL, 10);
  count = 0;
  for (i = 0; i < n; i++)
    for (j = 0; j < n; j++)
      for (k = 0; k < n; k++)
        count++;
  return 0;
}
