#include "cc_array.h"
#include "cc_graph.h"
#include "cc_rbtree.h"
#include <assert.h>
#include <string.h>

int test_cc_graph_incd_edges() {
  cc_graph *g = cc_graph_new();
  cc_graph_vert *a, *b, *c, *d;
  cc_graph_edge *ab, *bc, *ad;
  cc_array *edges;

  a = cc_graph_vert_new(g, "a");
  b = cc_graph_vert_new(g, "b");
  c = cc_graph_vert_new(g, "c");
  d = cc_graph_vert_new(g, "d");
  ab = cc_graph_edge_new("ab", 1, a, b);
  bc = cc_graph_edge_new("bc", 2, b, c);
  ad = cc_graph_edge_new("ad", 3, a, d);

  edges = cc_array_new(sizeof(cc_graph_edge *));
  cc_graph_incd_edges(a, edges);
  assert(edges->size == 2);
  assert(*(cc_graph_edge **)cc_array_get(edges, 0) == ab);
  assert(*(cc_graph_edge **)cc_array_get(edges, 1) == ad);

  cc_array_clear(edges);
  cc_graph_incd_edges(b, edges);
  assert(edges->size == 2);
  assert(*(cc_graph_edge **)cc_array_get(edges, 0) == ab);
  assert(*(cc_graph_edge **)cc_array_get(edges, 1) == bc);

  cc_array_free(edges);
  cc_graph_free(g);
  return 0;
}
