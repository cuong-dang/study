#include "clibc_array.h"
#include "clibc_graph.h"
#include "clibc_map.h"
#include <assert.h>
#include <string.h>

int test_clibc_graph_incd_edges() {
  clibc_graph *g = clibc_graph_new();
  clibc_graph_vert *a, *b, *c, *d;
  clibc_graph_edge *ab, *bc, *ad;
  clibc_array *edges;

  a = clibc_graph_vert_new(g, "a");
  b = clibc_graph_vert_new(g, "b");
  c = clibc_graph_vert_new(g, "c");
  d = clibc_graph_vert_new(g, "d");
  ab = clibc_graph_edge_new("ab", 1, a, b);
  bc = clibc_graph_edge_new("bc", 2, b, c);
  ad = clibc_graph_edge_new("ad", 3, a, d);

  edges = clibc_array_new(sizeof(clibc_graph_edge *));
  clibc_graph_incd_edges(a, edges);
  assert(edges->size == 2);
  assert(*(clibc_graph_edge **)clibc_array_get(edges, 0) == ab);
  assert(*(clibc_graph_edge **)clibc_array_get(edges, 1) == ad);

  clibc_array_clear(edges);
  clibc_graph_incd_edges(b, edges);
  assert(edges->size == 2);
  assert(*(clibc_graph_edge **)clibc_array_get(edges, 0) == ab);
  assert(*(clibc_graph_edge **)clibc_array_get(edges, 1) == bc);

  clibc_array_free(edges);
  clibc_graph_free(g);
  return 0;
}
