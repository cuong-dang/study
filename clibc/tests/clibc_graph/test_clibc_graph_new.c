#include "clibc_graph.h"
#include <assert.h>

int test_clibc_graph_new() {
  clibc_graph *g = clibc_graph_new();

  assert(g->verts->root == NULL);
  return 0;
}
