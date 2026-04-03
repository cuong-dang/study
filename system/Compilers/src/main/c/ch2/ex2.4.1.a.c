#include <stdio.h>
#include <stdlib.h>

int token;

int gettoken(void) {
  return token = getchar();
}

void s(void) {
  if (token == 'a')
    gettoken();
  else if (token == '+' || token == '-') {
    gettoken();
    s();
    s();
  } else {
    fprintf(stderr, "Syntax error.\n");
  }
}

int main(void) {
  while (1) {
    printf("> ");
    fflush(stdout);
    gettoken();
    if (token == EOF) return 0;
    s();
  }
  return 0;
}
