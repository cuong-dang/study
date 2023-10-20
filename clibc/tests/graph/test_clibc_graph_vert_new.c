#include "clibc_graph.h"
#include "clibc_map.h"
#include <assert.h>
#include <string.h>

int test_clibc_graph_vert_new() {
  clibc_graph *g = clibc_graph_new();
  clibc_graph_vert *v1, *v2, *v3;
  clibc_array *graph_labels;
  char *key;

  key = "a";
  v1 = clibc_graph_vert_new(g, key);
  assert(strcmp(v1->label, key) == 0);
  assert(*(clibc_graph_vert **)clibc_map_get(g->verts, &key) == v1);
  key = "b";
  v2 = clibc_graph_vert_new(g, key);
  assert(strcmp(v2->label, key) == 0);
  assert(*(clibc_graph_vert **)clibc_map_get(g->verts, &key) == v2);
  graph_labels = clibc_array_new(sizeof(char *));
  clibc_map_keys(g->verts, graph_labels);
  assert(graph_labels->size == 2);
  key = "c";
  assert(clibc_map_get(g->verts, &key) == NULL);

  /* Regression tests */
  /* We failed to pass the address of the label (char **) and passed the first
     character of the label (char *) instead. Thus, it was not failing if the
     label length is less than 8. */
  key = "12345678";
  v3 = clibc_graph_vert_new(g, key);
  assert(*(clibc_graph_vert **)clibc_map_get(g->verts, &key) == v3);

  clibc_array_free(graph_labels);
  clibc_graph_free(g);
  return 0;
}
