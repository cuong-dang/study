#include <stdio.h>

#include "hs/util.h"

int tk;
void gettk(void) {
  tk = getchar();
}

int has_syntax_err = 0;
void syntax_err(void) {
  printf("syntax error\n");
  fflush(stdin);
  has_syntax_err = 1;
}
