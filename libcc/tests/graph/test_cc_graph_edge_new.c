#include "cc_array.h"
#include "cc_graph.h"
#include "cc_rbtree.h"
#include <assert.h>
#include <string.h>

void get_edge_ps(cc_rbtree *edges, cc_array *edge_ps);

int test_cc_graph_edge_new() {
  cc_graph *g = cc_graph_new();
  cc_graph_vert *a, *b, *c, *d;
  cc_graph_edge *ab, *bc, *ad;
  cc_array *edge_ps;

  a = cc_graph_vert_new(g, "a");
  b = cc_graph_vert_new(g, "b");
  c = cc_graph_vert_new(g, "c");
  d = cc_graph_vert_new(g, "d");
  ab = cc_graph_edge_new("ab", 1, a, b);
  assert(strcmp(ab->label, "ab") == 0);
  assert(ab->weight == 1);
  assert(ab->src_vert == a);
  assert(ab->dst_vert == b);
  edge_ps = cc_array_new(sizeof(cc_graph_edge **));
  get_edge_ps(a->in_edges, edge_ps);
  assert(edge_ps->size == 0);
  get_edge_ps(a->out_edges, edge_ps);
  assert(edge_ps->size == 1);
  assert(*(cc_graph_edge **)cc_array_get(edge_ps, 0) == ab);
  get_edge_ps(b->in_edges, edge_ps);
  assert(edge_ps->size == 1);
  assert(*(cc_graph_edge **)cc_array_get(edge_ps, 0) == ab);
  get_edge_ps(b->out_edges, edge_ps);
  assert(edge_ps->size == 0);

  bc = cc_graph_edge_new("bc", 2, b, c);
  assert(bc->weight == 2);
  assert(bc->src_vert == b);
  assert(bc->dst_vert == c);
  get_edge_ps(b->out_edges, edge_ps);
  assert(edge_ps->size == 1);
  assert(*(cc_graph_edge **)cc_array_get(edge_ps, 0) == bc);
  get_edge_ps(c->in_edges, edge_ps);
  assert(edge_ps->size == 1);
  assert(*(cc_graph_edge **)cc_array_get(edge_ps, 0) == bc);
  get_edge_ps(c->out_edges, edge_ps);
  assert(edge_ps->size == 0);

  ad = cc_graph_edge_new("ad", 3, a, d);
  assert(ad->weight == 3);
  assert(ad->src_vert == a);
  assert(ad->dst_vert == d);
  get_edge_ps(a->out_edges, edge_ps);
  assert(edge_ps->size == 2);
  assert(*(cc_graph_edge **)cc_array_get(edge_ps, 1) == ad);
  get_edge_ps(d->in_edges, edge_ps);
  assert(edge_ps->size == 1);
  assert(*(cc_graph_edge **)cc_array_get(edge_ps, 0) == ad);
  get_edge_ps(d->out_edges, edge_ps);
  assert(edge_ps->size == 0);

  cc_array_free(edge_ps);
  cc_graph_free(g);
  return 0;
}

void get_edge_ps(cc_rbtree *edges, cc_array *edge_ps) {
  cc_array_clear(edge_ps);
  cc_rbtree_keys(edges, edge_ps);
}
