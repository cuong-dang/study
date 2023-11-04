#include "cc_algs_graph.h"
#include "cc_array.h"
#include "cc_graph.h"
#include <assert.h>

int test_cc_algs_graph_hamiltonian_cc() {
  cc_graph *g1, *g2;
  cc_graph_vert *a, *b, *c, *d, *e, *f, *g;
  cc_array *cc;

  g1 = cc_graph_new();
  a = cc_graph_add_vert(g1, "a");
  b = cc_graph_add_vert(g1, "b");
  c = cc_graph_add_vert(g1, "c");
  d = cc_graph_add_vert(g1, "d");
  e = cc_graph_add_vert(g1, "e");
  f = cc_graph_add_vert(g1, "f");
  g = cc_graph_add_vert(g1, "g");

  /* a edges */
  cc_graph_add_edge("ab", 0, a, b);
  cc_graph_add_edge("ba", 0, b, a);
  cc_graph_add_edge("ac", 0, a, c);
  cc_graph_add_edge("ca", 0, c, a);
  cc_graph_add_edge("ad", 0, a, d);
  cc_graph_add_edge("da", 0, d, a);
  cc_graph_add_edge("af", 0, a, f);
  cc_graph_add_edge("fa", 0, f, a);

  /* b edges */
  cc_graph_add_edge("bd", 0, b, d);
  cc_graph_add_edge("db", 0, d, b);
  cc_graph_add_edge("be", 0, b, e);
  cc_graph_add_edge("eb", 0, e, b);
  cc_graph_add_edge("bg", 0, b, g);
  cc_graph_add_edge("gb", 0, g, b);

  /* c edges */
  cc_graph_add_edge("cf", 0, c, f);
  cc_graph_add_edge("fc", 0, f, c);

  /* d edges */
  cc_graph_add_edge("df", 0, d, f);
  cc_graph_add_edge("fd", 0, f, d);
  cc_graph_add_edge("dg", 0, d, g);
  cc_graph_add_edge("gd", 0, g, d);

  /* e edges */
  cc_graph_add_edge("eg", 0, e, g);
  cc_graph_add_edge("ge", 0, g, e);

  /* f edges */
  cc_graph_add_edge("fg", 0, f, g);
  cc_graph_add_edge("gf", 0, g, f);

  cc = cc_array_new(sizeof(cc_graph_vert *));
  assert(hamiltonian_cc(g1, cc) == 1);
  assert(*(cc_graph_vert **)cc_array_get(cc, 0) == a);
  assert(*(cc_graph_vert **)cc_array_get(cc, 1) == b);
  assert(*(cc_graph_vert **)cc_array_get(cc, 2) == e);
  assert(*(cc_graph_vert **)cc_array_get(cc, 3) == g);
  assert(*(cc_graph_vert **)cc_array_get(cc, 4) == d);
  assert(*(cc_graph_vert **)cc_array_get(cc, 5) == f);
  assert(*(cc_graph_vert **)cc_array_get(cc, 6) == c);
  assert(*(cc_graph_vert **)cc_array_get(cc, 7) == a);

  g2 = cc_graph_new();
  a = cc_graph_add_vert(g2, "a");
  b = cc_graph_add_vert(g2, "b");
  c = cc_graph_add_vert(g2, "c");
  d = cc_graph_add_vert(g2, "d");
  e = cc_graph_add_vert(g2, "e");
  f = cc_graph_add_vert(g2, "f");

  /* a edges */
  cc_graph_add_edge("ab", 0, a, b);
  cc_graph_add_edge("ba", 0, b, a);
  cc_graph_add_edge("ac", 0, a, c);
  cc_graph_add_edge("ca", 0, c, a);
  cc_graph_add_edge("ad", 0, a, d);
  cc_graph_add_edge("da", 0, d, a);

  /* b edges */
  cc_graph_add_edge("bc", 0, b, c);
  cc_graph_add_edge("cb", 0, c, b);
  cc_graph_add_edge("bf", 0, b, f);
  cc_graph_add_edge("fb", 0, f, b);

  /* c edges */
  cc_graph_add_edge("cd", 0, c, d);
  cc_graph_add_edge("dc", 0, d, c);
  cc_graph_add_edge("ce", 0, c, e);
  cc_graph_add_edge("ec", 0, e, c);

  /* d edges */
  cc_graph_add_edge("de", 0, d, e);
  cc_graph_add_edge("ed", 0, e, d);

  /* e edges */
  cc_graph_add_edge("ef", 0, e, f);
  cc_graph_add_edge("fe", 0, f, e);

  cc = cc_array_new(sizeof(cc_graph_vert *));
  assert(hamiltonian_cc(g2, cc) == 1);
  assert(*(cc_graph_vert **)cc_array_get(cc, 0) == a);
  assert(*(cc_graph_vert **)cc_array_get(cc, 1) == b);
  assert(*(cc_graph_vert **)cc_array_get(cc, 2) == f);
  assert(*(cc_graph_vert **)cc_array_get(cc, 3) == e);
  assert(*(cc_graph_vert **)cc_array_get(cc, 4) == c);
  assert(*(cc_graph_vert **)cc_array_get(cc, 5) == d);
  assert(*(cc_graph_vert **)cc_array_get(cc, 6) == a);

  cc_array_free(cc);
  cc_graph_free(g2);
  cc_graph_free(g1);
  return 0;
}
