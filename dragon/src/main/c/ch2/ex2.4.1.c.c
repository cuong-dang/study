#include <stdio.h>

#include "../hs/util.h"

void s(void);

int main(void) {
  while (1) {
    printf("> ");
    fflush(stdout);
    gettk();
    if (tk == EOF) return 0;
    s();
    if (!has_syntax_err && tk != '\n') {
      syntax_err();
    }
    has_syntax_err = 0;
  }
  return 0;
}

void s(void) {
  if (tk != '0') {
    syntax_err();
    return;
  }
  gettk();
  if (tk == '0')
    s();
  if (has_syntax_err) return;
  if (tk != '1') {
    syntax_err();
    return;
  }
  gettk();
}
