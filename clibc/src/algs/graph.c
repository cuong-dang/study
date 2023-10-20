#include "clibc_algs_graph.h"
#include "clibc_array.h"
#include "clibc_map.h"
#include <stdlib.h>
#include <string.h>

int label_cmp(void *, void *);

void prim_min_spanning_tree(clibc_graph *g, clibc_array *ms_tree) {
  clibc_array *vert_labels, *incd_edges, *next_edges;
  clibc_map *seen_vert_labels;
  clibc_graph_vert *v;
  int i;
  int null;

  vert_labels = clibc_array_new(sizeof(char *));
  clibc_map_keys(g->verts, vert_labels);
  if (vert_labels->size == 0) {
    clibc_array_free(vert_labels);
    return;
  }
  seen_vert_labels = clibc_map_new(sizeof(char *), sizeof(int), label_cmp);
  incd_edges = clibc_array_new(sizeof(clibc_graph_edge *));
  next_edges = clibc_array_new(sizeof(clibc_graph_edge *));
  v = *(clibc_graph_vert **)clibc_map_get(g->verts,
                                          clibc_array_get(vert_labels, 0));
  clibc_map_put(seen_vert_labels, v->label, &null);
  clibc_graph_incd_edges(g, v, incd_edges);

  clibc_map_free(seen_vert_labels);
  clibc_array_free(vert_labels);
}

int label_cmp(void *l1, void *l2) { return strcmp(l1, l2); }
