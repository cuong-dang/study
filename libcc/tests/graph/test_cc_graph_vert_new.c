#include "cc_graph.h"
#include "cc_rbtree.h"
#include <assert.h>
#include <string.h>

int test_cc_graph_vert_new() {
  cc_graph *g = cc_graph_new();
  cc_graph_vert *v1, *v2, *v3;
  cc_array *graph_labels;
  char *key;

  key = "a";
  v1 = cc_graph_add_vert(g, key);
  assert(strcmp(v1->label, key) == 0);
  assert(*(cc_graph_vert **)cc_rbtree_get(g->verts, &key) == v1);
  key = "b";
  v2 = cc_graph_add_vert(g, key);
  assert(strcmp(v2->label, key) == 0);
  assert(*(cc_graph_vert **)cc_rbtree_get(g->verts, &key) == v2);
  graph_labels = cc_array_new(sizeof(char *));
  cc_rbtree_keys(g->verts, graph_labels);
  assert(graph_labels->size == 2);
  key = "c";
  assert(cc_rbtree_get(g->verts, &key) == NULL);

  /* Regression tests */
  /* We failed to pass the address of the label (char **) and passed the first
     character of the label (char *) instead. Thus, it was not failing if the
     label length is less than 8. */
  key = "12345678";
  v3 = cc_graph_add_vert(g, key);
  assert(*(cc_graph_vert **)cc_rbtree_get(g->verts, &key) == v3);

  cc_array_free(graph_labels);
  cc_graph_free(g);
  return 0;
}
