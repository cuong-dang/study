#include "cc_algs_str.h"
#include <string.h>

void build_shift_tbl(int *shift_tbl, char *p, int plen);

int horspool_match(char *s, char *p) {
  int slen, plen;
  int shift_tbl[95], i, j;

  slen = strlen(s);
  plen = strlen(p);
  if (plen == 0) {
    return -1;
  }
  build_shift_tbl(shift_tbl, p, plen);
  for (i = plen - 1; i < slen;) {
    for (j = 0; j < plen; j++) {
      if (s[i - j] != p[plen - 1 - j]) {
        i += shift_tbl[s[i] - ' '];
        break;
      }
    }
    if (j == plen) {
      return i - plen + 1;
    }
  }
  return -1;
}

void build_shift_tbl(int *shift_tbl, char *p, int plen) {
  int i;

  for (i = 0; i < 95; i++) {
    shift_tbl[i] = plen;
  }
  for (i = 0; i < plen - 1; i++) {
    shift_tbl[p[i] - ' '] = plen - 1 - i;
  }
}
