#include "cc_array.h"
#include "cc_graph.h"
#include "cc_rbtree.h"
#include <stdlib.h>
#include <string.h>

int cc_graph_label_cmp(void *label1, void *label2);
int cc_graph_edge_cmp(void *e1, void *e2);
void add_edges(cc_array *dst, cc_rbtree *src);

cc_graph *cc_graph_new() {
  cc_graph *g = malloc(sizeof(cc_graph));

  g->verts = cc_rbtree_new(sizeof(char *), sizeof(cc_graph_vert *),
                           cc_graph_label_cmp);
  return g;
}

int cc_graph_num_verts(cc_graph *g) { return cc_rbtree_size(g->verts); }

cc_graph_vert *cc_graph_add_vert(cc_graph *g, char *label) {
  cc_graph_vert *v = malloc(sizeof(cc_graph_vert));

  v->label = label;
  v->in_edges =
      cc_rbtree_new(sizeof(cc_graph_edge *), sizeof(void *), cc_graph_edge_cmp);
  v->out_edges =
      cc_rbtree_new(sizeof(cc_graph_edge *), sizeof(void *), cc_graph_edge_cmp);
  cc_rbtree_add(g->verts, &label, &v);
  return v;
}

cc_graph_edge *cc_graph_add_edge(char *label, int weight,
                                 cc_graph_vert *src_vert,
                                 cc_graph_vert *dst_vert) {
  cc_graph_edge *e = malloc(sizeof(cc_graph_edge));
  static void *v = NULL;

  e->label = label;
  e->weight = weight;
  e->src_vert = src_vert;
  e->dst_vert = dst_vert;
  cc_rbtree_add(src_vert->out_edges, &e, &v);
  cc_rbtree_add(dst_vert->in_edges, &e, &v);
  return e;
}

void cc_graph_vert_labels(cc_graph *g, cc_array *verts) {
  cc_rbtree_keys(g->verts, verts);
}

void cc_graph_adj_verts(cc_graph_vert *v, cc_array *adj_verts) {
  cc_array *tmp;
  cc_graph_vert *dst_v;
  int i;

  cc_array_clear(adj_verts);
  tmp = cc_array_new(sizeof(cc_graph_edge *));
  cc_rbtree_keys(v->out_edges, tmp);
  for (i = 0; i < tmp->size; i++) {
    dst_v = (*(cc_graph_edge **)cc_array_get(tmp, i))->dst_vert;
    cc_array_add(adj_verts, &dst_v);
  }
  cc_array_free(tmp);
}

void cc_graph_incd_edges(cc_graph_vert *v, cc_array *incd_edges) {
  cc_array_clear(incd_edges);
  add_edges(incd_edges, v->in_edges);
  add_edges(incd_edges, v->out_edges);
}

void cc_graph_free(cc_graph *g) {
  int i, j;
  cc_array *vert_labels, *edge_ps;
  cc_graph_vert *v;
  cc_graph_edge *e;

  vert_labels = cc_array_new(sizeof(char *));
  cc_rbtree_keys(g->verts, vert_labels);
  for (i = 0; i < vert_labels->size; i++) {
    v = *(cc_graph_vert **)cc_rbtree_get(g->verts,
                                         cc_array_get(vert_labels, i));
    edge_ps = cc_array_new(sizeof(cc_graph_edge *));
    cc_rbtree_keys(v->out_edges, edge_ps);
    for (j = 0; j < edge_ps->size; j++) {
      e = *(cc_graph_edge **)cc_array_get(edge_ps, j);
      free(e);
    }
    cc_array_free(edge_ps);
    free(v);
  }
  cc_array_free(vert_labels);
  free(g);
}

int cc_graph_label_cmp(void *label1, void *label2) {
  return strcmp(*(char **)label1, *(char **)label2);
}

int cc_graph_edge_cmp(void *e1, void *e2) {
  return strcmp((*(cc_graph_edge **)e1)->label, (*(cc_graph_edge **)e2)->label);
}

void add_edges(cc_array *dst, cc_rbtree *src) {
  cc_array *tmp;
  int i;
  cc_graph_edge *e;

  tmp = cc_array_new(sizeof(cc_graph_edge *));
  cc_rbtree_keys(src, tmp);
  for (i = 0; i < tmp->size; i++) {
    e = *(cc_graph_edge **)cc_array_get(tmp, i);
    cc_array_add(dst, &e);
  }
  cc_array_free(tmp);
}
