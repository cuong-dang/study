#include "clibc_graph.h"
#include "clibc_map.h"
#include <assert.h>
#include <string.h>

int test_clibc_graph_vert_new() {
  clibc_graph *g = clibc_graph_new();
  clibc_graph_vert *v1, *v2;

  v1 = clibc_graph_vert_new(g, "a");
  assert(strcmp(v1->label, "a") == 0);
  assert(*(clibc_graph_vert **)clibc_map_get(g->verts, "a") == v1);
  v2 = clibc_graph_vert_new(g, "b");
  assert(strcmp(v2->label, "b") == 0);
  assert(*(clibc_graph_vert **)clibc_map_get(g->verts, "b") == v2);
  assert(clibc_map_keys(g->verts)->size == 2);
  return 0;
}
