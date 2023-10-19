#include "clibc_graph.h"
#include <assert.h>

int test_clibc_graph_new() {
  clibc_graph *g = clibc_graph_new();

  assert(g->verts->root == NULL);

  clibc_graph_free(g);
  return 0;
}
