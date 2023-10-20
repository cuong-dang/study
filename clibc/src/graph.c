#include "clibc_array.h"
#include "clibc_graph.h"
#include "clibc_map.h"
#include <stdlib.h>
#include <string.h>

int clibc_graph_label_cmp(void *label1, void *label2);
int clibc_graph_edge_cmp(void *e1, void *e2);
void add_edges(clibc_array *dst, clibc_map *src);

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
  clibc_map_put(g->verts, &label, &v);
  return v;
}

clibc_graph_edge *clibc_graph_edge_new(char *label, int weight,
                                       clibc_graph_vert *src_vert,
                                       clibc_graph_vert *dst_vert) {
  clibc_graph_edge *e = malloc(sizeof(clibc_graph_edge));
  static void *v = NULL;

  e->label = label;
  e->weight = weight;
  e->src_vert = src_vert;
  e->dst_vert = dst_vert;
  clibc_map_put(src_vert->out_edges, &e, &v);
  clibc_map_put(dst_vert->in_edges, &e, &v);
  return e;
}

void clibc_graph_incd_edges(clibc_graph_vert *v, clibc_array *incd_edges) {
  add_edges(incd_edges, v->in_edges);
  add_edges(incd_edges, v->out_edges);
}

void clibc_graph_free(clibc_graph *g) {
  int i, j;
  clibc_array *vert_labels, *edge_ps;
  clibc_graph_vert *v;
  clibc_graph_edge *e;

  vert_labels = clibc_array_new(sizeof(char *));
  clibc_map_keys(g->verts, vert_labels);
  for (i = 0; i < vert_labels->size; i++) {
    v = *(clibc_graph_vert **)clibc_map_get(g->verts,
                                            clibc_array_get(vert_labels, i));
    edge_ps = clibc_array_new(sizeof(clibc_graph_edge *));
    clibc_map_keys(v->out_edges, edge_ps);
    for (j = 0; j < edge_ps->size; j++) {
      e = *(clibc_graph_edge **)clibc_array_get(edge_ps, j);
      free(e);
    }
    clibc_array_free(edge_ps);
    free(v);
  }
  clibc_array_free(vert_labels);
  free(g);
}

int clibc_graph_label_cmp(void *label1, void *label2) {
  return strcmp(*(char **)label1, *(char **)label2);
}

int clibc_graph_edge_cmp(void *e1, void *e2) {
  return strcmp((*(clibc_graph_edge **)e1)->label,
                (*(clibc_graph_edge **)e2)->label);
}

void add_edges(clibc_array *dst, clibc_map *src) {
  clibc_array *tmp;
  int i;
  clibc_graph_edge *e;

  tmp = clibc_array_new(sizeof(clibc_graph_edge *));
  clibc_map_keys(src, tmp);
  for (i = 0; i < tmp->size; i++) {
    e = *(clibc_graph_edge **)clibc_array_get(tmp, i);
    clibc_array_add(dst, &e);
  }
  clibc_array_free(tmp);
}
