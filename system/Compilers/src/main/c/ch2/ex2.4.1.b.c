#include <stdio.h>

int tk;
int err;

void gettk(void) {
  tk = getchar();
}

void opt_s(void);
void se(void);

void s(void) {
  if (tk != '(') {
    se();
    return;
  }
  gettk();
  opt_s();
  if (tk != ')') {
    se();
    return;
  }
  gettk();
  opt_s();
}

void opt_s(void) {
  if (tk == '(') s();
}

void se() {
  fprintf(stderr, "syntax error\n");
  fflush(stdin);
  err = 1;
}

int main(void) {
  while (1) {
    err = 0;
    printf("> ");
    fflush(stdout);
    gettk();
    if (tk == EOF) return 0;
    s();
    if (err == 0 && tk != '\n')
      se();
  }
  return 0;
}
