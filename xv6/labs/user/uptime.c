#include "kernel/types.h"
#include "user/user.h"

int main() {
  int t;

  if ((t = uptime()) < 0) {
    fprintf(2, "uptime: uptime failed\n");
    exit(1);
  }
  printf("uptime: %d ticks\n", t);
  exit(0);
}
