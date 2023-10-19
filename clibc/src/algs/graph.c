#include "clibc_algs_graph.h"
#include "clibc_array.h"
#include <stdlib.h>
#include <string.h>

int label_cmp(void *, void *);

clibc_array *prim_min_spanning_tree(clibc_graph *g) {
  clibc_array *ms_tree, *vert_labels, *incd_edges, *next_edges;
  clibc_map *seen_vert_labels;
  clibc_graph_vert *v;
  int i;

  vert_labels = clibc_map_keys(g->verts);
  if (vert_labels->size == 0) {
    return NULL;
  }
  ms_tree = clibc_array_new(sizeof(clibc_graph_edge *));
  incd_edges = clibc_array_new(sizeof(clibc_graph_edge *));
  next_edges = clibc_array_new(sizeof(clibc_graph_edge *));
  seen_vert_labels = clibc_map_new(sizeof(char *), sizeof(int), label_cmp);
  v = *(clibc_graph_vert **)clibc_map_get(g->verts,
                                          clibc_array_get(vert_labels, 0));

  clibc_array_free(vert_labels);
}

int label_cmp(void *l1, void *l2) { return strcmp(l1, l2); }
