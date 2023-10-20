#include "clibc_algs_graph.h"
#include "clibc_array.h"
#include "clibc_graph.h"
#include <assert.h>

int test_clibc_algs_graph_prim_mstree() {
  clibc_graph *g;
  clibc_graph_vert *a, *b, *c, *d, *e, *f;
  clibc_graph_edge *ab, *ae, *af, *bc, *bf, *cd, *cf, *de, *df, *ef;
  char *label;
  clibc_array *mstree;

  g = clibc_graph_new();
  mstree = clibc_array_new(sizeof(clibc_graph_edge *));

  a = clibc_graph_vert_new(g, "a");
  b = clibc_graph_vert_new(g, "b");
  ab = clibc_graph_edge_new("ab", 3, a, b);
  /* prim_min_spanning_tree(g, mstree); */
  /* assert(mstree->size == 1); */
  /* assert(*(clibc_graph_edge **)clibc_array_get(mstree, 0) == ab); */

  c = clibc_graph_vert_new(g, "c");
  bc = clibc_graph_edge_new("bc", 1, b, c);
  /* prim_min_spanning_tree(g, mstree); */
  /* assert(mstree->size == 2); */
  /* assert(*(clibc_graph_edge **)clibc_array_get(mstree, 0) == ab); */
  /* assert(*(clibc_graph_edge **)clibc_array_get(mstree, 1) == bc); */

  d = clibc_graph_vert_new(g, "d");
  e = clibc_graph_vert_new(g, "e");
  f = clibc_graph_vert_new(g, "f");
  ae = clibc_graph_edge_new("ae", 6, a, e);
  af = clibc_graph_edge_new("af", 5, a, f);
  bf = clibc_graph_edge_new("bf", 4, b, f);
  cd = clibc_graph_edge_new("cd", 6, c, d);
  cf = clibc_graph_edge_new("cf", 4, c, f);
  de = clibc_graph_edge_new("de", 8, d, e);
  df = clibc_graph_edge_new("df", 5, d, f);
  ef = clibc_graph_edge_new("ef", 2, e, f);
  prim_mstree(g, mstree);
  assert(mstree->size == 5);
  assert(*(clibc_graph_edge **)clibc_array_get(mstree, 0) == ab);
  assert(*(clibc_graph_edge **)clibc_array_get(mstree, 1) == bc);
  assert(*(clibc_graph_edge **)clibc_array_get(mstree, 2) == bf);
  assert(*(clibc_graph_edge **)clibc_array_get(mstree, 3) == ef);
  assert(*(clibc_graph_edge **)clibc_array_get(mstree, 4) == df);

  clibc_array_free(mstree);
  clibc_graph_free(g);
}
