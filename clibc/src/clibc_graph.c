#include "clibc_graph.h"
#include "clibc_map.h"
#include <stdlib.h>
#include <string.h>

int clibc_graph_label_cmp(void *label1, void *label2);
int clibc_graph_edge_cmp(void *e1, void *e2);

clibc_graph *clibc_graph_new() {
  clibc_graph *g = malloc(sizeof(clibc_graph));

  g->verts = clibc_map_new(sizeof(char *), sizeof(clibc_graph_vert *),
                           clibc_graph_label_cmp);
  return g;
}

clibc_graph_vert *clibc_graph_vert_new(clibc_graph *g, char *label) {
  clibc_graph_vert *v = malloc(sizeof(clibc_graph_vert));

  v->label = label;
  v->in_edges = clibc_map_new(sizeof(clibc_graph_edge *), sizeof(void *),
                              clibc_graph_edge_cmp);
  v->out_edges = clibc_map_new(sizeof(clibc_graph_edge *), sizeof(void *),
                               clibc_graph_edge_cmp);
  clibc_map_put(g->verts, label, &v);
  return v;
}

clibc_graph_edge *clibc_graph_edge_new(char *label, int weight,
                                       clibc_graph_vert *src_vert,
                                       clibc_graph_vert *dst_vert) {
  clibc_graph_edge *e = malloc(sizeof(clibc_graph_edge));

  e->label = label;
  e->weight = weight;
  e->src_vert = src_vert;
  e->dst_vert = dst_vert;
  clibc_map_put(src_vert->out_edges, &e, NULL);
  clibc_map_put(dst_vert->in_edges, &e, NULL);
  return e;
}

int clibc_graph_label_cmp(void *label1, void *label2) {
  return strcmp(label1, label2);
}

int clibc_graph_edge_cmp(void *e1, void *e2) {
  return strcmp((*(clibc_graph_edge **)e1)->label,
                (*(clibc_graph_edge **)e2)->label);
}
