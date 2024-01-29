#include "cc_algs_graph.h"
#include "cc_array.h"
#include "cc_graph.h"
#include "cc_pqueue.h"
#include "cc_rbtree.h"
#include "cc_set.h"
#include <stdlib.h>
#include <string.h>

/* prim_mstree */
int label_cmp(void *, void *);
int edge_cmp(void *, void *);
cc_graph_edge *get_nx_edge(cc_set *seen_vx, cc_pqueue *edges);
int has_not_seen(cc_set *seen_vx, cc_graph_vert *v);
void add_next_edges(cc_pqueue *nx_edges, cc_array *id_edges, cc_set *seen_vx);

void prim_mstree(cc_graph *g, cc_array *mstree) {
  cc_array *vx_labels, *id_edges;
  cc_set *seen_vx;
  cc_pqueue *nx_edges;
  cc_graph_vert *nx_vert;
  cc_graph_edge *nx_edge;
  int i, j;

  /* Get all vertices */
  vx_labels = cc_array_new(sizeof(char *));
  cc_rbtree_keys(g->verts, vx_labels);
  if (vx_labels->size == 0) {
    cc_array_free(vx_labels);
    return;
  }
  /* Init */
  cc_array_clear(mstree);
  seen_vx = cc_set_new(sizeof(char *), label_cmp);
  nx_edges = cc_pqueue_new(sizeof(cc_graph_edge *), edge_cmp);
  id_edges = cc_array_new(sizeof(cc_graph_edge *));
  /* Select init vx */
  nx_vert =
      *(cc_graph_vert **)cc_rbtree_get(g->verts, cc_array_get(vx_labels, 0));
  cc_set_add(seen_vx, &nx_vert->label);
  cc_graph_incd_edges(nx_vert, id_edges);
  add_next_edges(nx_edges, id_edges, seen_vx);
  for (i = 0; i < vx_labels->size - 1; i++) {
    nx_edge = get_nx_edge(seen_vx, nx_edges);
    cc_array_add(mstree, &nx_edge);
    nx_vert = has_not_seen(seen_vx, nx_edge->src_vert) ? nx_edge->src_vert
                                                       : nx_edge->dst_vert;
    cc_set_add(seen_vx, &nx_vert->label);
    cc_graph_incd_edges(nx_vert, id_edges);
    add_next_edges(nx_edges, id_edges, seen_vx);
  }

  cc_pqueue_free(nx_edges);
  cc_array_free(id_edges);
  cc_set_free(seen_vx);
  cc_array_free(vx_labels);
}

int label_cmp(void *l1, void *l2) { return strcmp(*(char **)l1, *(char **)l2); }

int edge_cmp(void *e1, void *e2) {
  return (*(cc_graph_edge **)e2)->weight - (*(cc_graph_edge **)e1)->weight;
}

cc_graph_edge *get_nx_edge(cc_set *seen_vx, cc_pqueue *edges) {
  int i;
  cc_graph_edge *e;

  while (1) {
    cc_pqueue_rm(edges, &e);
    if (has_not_seen(seen_vx, e->src_vert) ||
        has_not_seen(seen_vx, e->dst_vert)) {
      break;
    }
  }
  return e;
}

int has_not_seen(cc_set *seen_vx, cc_graph_vert *v) {
  return cc_set_contains(seen_vx, &v->label) == 0;
}

void add_next_edges(cc_pqueue *nx_edges, cc_array *id_edges, cc_set *seen_vx) {
  int i;
  cc_graph_edge *e;

  for (i = 0; i < id_edges->size; i++) {
    e = *(cc_graph_edge **)cc_array_get(id_edges, i);
    if (has_not_seen(seen_vx, e->src_vert) ||
        has_not_seen(seen_vx, e->dst_vert)) {
      cc_pqueue_add(nx_edges, &e);
    }
  }
}

/* hamiltonian_cc */
int hamiltonian_cc_solve(cc_array *cc, cc_graph_vert *start_vert,
                         cc_graph_vert *this_vert, cc_array *seen_verts,
                         int num_verts);
int has_not_seen_(cc_array *seen_verts, cc_graph_vert *v);

int hamiltonian_cc(cc_graph *g, cc_array *cc) {
  cc_array *verts, *seen_verts;
  char *v_label;
  int i;

  verts = cc_array_new(sizeof(char *));
  seen_verts = cc_array_new(sizeof(cc_graph_vert *));
  cc_graph_vert_labels(g, verts);
  for (i = 0; i < verts->size; i++) {
    v_label = *(char **)cc_array_get(verts, i);
    if (hamiltonian_cc_solve(
            cc, *(cc_graph_vert **)cc_rbtree_get(g->verts, &v_label), NULL,
            seen_verts, verts->size)) {
      cc_array_free(seen_verts);
      cc_array_free(verts);
      return 1;
    }
  }
  cc_array_free(seen_verts);
  cc_array_free(verts);
  return 0;
}

int hamiltonian_cc_solve(cc_array *cc, cc_graph_vert *start_vert,
                         cc_graph_vert *this_vert, cc_array *seen_verts,
                         int num_verts) {
  cc_array *adj_verts;
  cc_graph_vert *v;
  int i;

  if (this_vert == start_vert && cc->size == num_verts + 1) {
    return 1;
  }
  if (start_vert == this_vert) {
    return 0;
  }
  adj_verts = cc_array_new(sizeof(cc_graph_vert *));
  if (this_vert == NULL) {
    cc_array_add(cc, &start_vert);
  }
  cc_graph_adj_verts(this_vert == NULL ? start_vert : this_vert, adj_verts);
  for (i = 0; i < adj_verts->size; i++) {
    v = *(cc_graph_vert **)cc_array_get(adj_verts, i);
    if (has_not_seen_(seen_verts, v)) {
      cc_array_add(cc, &v);
      cc_array_add(seen_verts, &v);
      if (hamiltonian_cc_solve(cc, start_vert, v, seen_verts, num_verts)) {
        cc_array_free(adj_verts);
        return 1;
      }
      cc_array_rm(cc, cc->size - 1);
      cc_array_rm(seen_verts, seen_verts->size - 1);
    }
  }
  cc_array_free(adj_verts);
  return 0;
}

int has_not_seen_(cc_array *seen_verts, cc_graph_vert *v) {
  int i;

  for (i = 0; i < seen_verts->size; i++) {
    if (*(cc_graph_vert **)cc_array_get(seen_verts, i) == v) {
      return 0;
    }
  }
  return 1;
}
