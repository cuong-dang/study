#include "cc_graph.h"
#include <assert.h>

int test_cc_graph_adj_verts() {
  cc_graph *g = cc_graph_new();
  cc_graph_vert *a, *b, *c, *d;
  cc_graph_edge *ab, *bc, *ad;
  cc_array *verts;

  verts = cc_array_new(sizeof(cc_graph_vert *));
  a = cc_graph_add_vert(g, "a");
  b = cc_graph_add_vert(g, "b");
  c = cc_graph_add_vert(g, "c");
  d = cc_graph_add_vert(g, "d");
  ab = cc_graph_add_edge("ab", 1, a, b);
  bc = cc_graph_add_edge("bc", 2, b, c);
  ad = cc_graph_add_edge("ad", 3, a, d);

  cc_graph_adj_verts(a, verts);
  assert(verts->size == 2);
  assert(*(cc_graph_vert **)cc_array_get(verts, 0) == b);
  assert(*(cc_graph_vert **)cc_array_get(verts, 1) == d);

  cc_graph_adj_verts(b, verts);
  assert(verts->size == 1);
  assert(*(cc_graph_vert **)cc_array_get(verts, 0) == c);

  cc_graph_adj_verts(c, verts);
  assert(verts->size == 0);

  cc_graph_free(g);
  return 0;
}
