#include "clibc_graph.h"
#include "clibc_map.h"
#include <assert.h>
#include <string.h>

int test_clibc_graph_vert_new() {
  clibc_graph *g = clibc_graph_new();
  clibc_graph_vert *v1, *v2;
  clibc_array *graph_labels;

  v1 = clibc_graph_vert_new(g, "a");
  assert(strcmp(v1->label, "a") == 0);
  assert(*(clibc_graph_vert **)clibc_map_get(g->verts, "a") == v1);
  v2 = clibc_graph_vert_new(g, "b");
  assert(strcmp(v2->label, "b") == 0);
  assert(*(clibc_graph_vert **)clibc_map_get(g->verts, "b") == v2);
  graph_labels = clibc_array_new(sizeof(char *));
  clibc_map_keys(g->verts, graph_labels);
  assert(graph_labels->size == 2);
  assert(clibc_map_get(g->verts, "c") == NULL);

  clibc_array_free(graph_labels);
  clibc_graph_free(g);
  return 0;
}
