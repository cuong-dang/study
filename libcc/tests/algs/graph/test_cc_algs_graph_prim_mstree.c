#include "cc_algs_graph.h"
#include "cc_array.h"
#include "cc_graph.h"
#include <assert.h>

int test_cc_algs_graph_prim_mstree() {
  cc_graph *g;
  cc_graph_vert *a, *b, *c, *d, *e, *f;
  cc_graph_edge *ab, *ae, *af, *bc, *bf, *cd, *cf, *de, *df, *ef;
  char *label;
  cc_array *mstree;

  g = cc_graph_new();
  mstree = cc_array_new(sizeof(cc_graph_edge *));

  a = cc_graph_vert_new(g, "a");
  b = cc_graph_vert_new(g, "b");
  ab = cc_graph_edge_new("ab", 3, a, b);
  prim_mstree(g, mstree);
  assert(mstree->size == 1);
  assert(*(cc_graph_edge **)cc_array_get(mstree, 0) == ab);

  c = cc_graph_vert_new(g, "c");
  bc = cc_graph_edge_new("bc", 1, b, c);
  prim_mstree(g, mstree);
  assert(mstree->size == 2);
  assert(*(cc_graph_edge **)cc_array_get(mstree, 0) == ab);
  assert(*(cc_graph_edge **)cc_array_get(mstree, 1) == bc);

  d = cc_graph_vert_new(g, "d");
  e = cc_graph_vert_new(g, "e");
  f = cc_graph_vert_new(g, "f");
  ae = cc_graph_edge_new("ae", 6, a, e);
  af = cc_graph_edge_new("af", 5, a, f);
  bf = cc_graph_edge_new("bf", 4, b, f);
  cd = cc_graph_edge_new("cd", 6, c, d);
  cf = cc_graph_edge_new("cf", 4, c, f);
  de = cc_graph_edge_new("de", 8, d, e);
  df = cc_graph_edge_new("df", 5, d, f);
  ef = cc_graph_edge_new("ef", 2, e, f);
  prim_mstree(g, mstree);
  assert(mstree->size == 5);
  assert(*(cc_graph_edge **)cc_array_get(mstree, 0) == ab);
  assert(*(cc_graph_edge **)cc_array_get(mstree, 1) == bc);
  assert(*(cc_graph_edge **)cc_array_get(mstree, 2) == bf);
  assert(*(cc_graph_edge **)cc_array_get(mstree, 3) == ef);
  assert(*(cc_graph_edge **)cc_array_get(mstree, 4) == df);

  cc_array_free(mstree);
  cc_graph_free(g);
}
