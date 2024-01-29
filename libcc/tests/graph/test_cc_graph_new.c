#include "cc_graph.h"
#include <assert.h>

int test_cc_graph_new() {
  cc_graph *g = cc_graph_new();

  assert(g->verts->root == NULL);

  cc_graph_free(g);
  return 0;
}
