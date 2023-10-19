#include "clibc_graph.h"
#include "clibc_map.h"
#include <assert.h>
#include <string.h>

int test_clibc_graph_edge_new() {
  clibc_graph *g = clibc_graph_new();
  clibc_graph_vert *a, *b, *c, *d;
  clibc_graph_edge *ab, *bc, *ad;

  a = clibc_graph_vert_new(g, "a");
  b = clibc_graph_vert_new(g, "b");
  c = clibc_graph_vert_new(g, "c");
  d = clibc_graph_vert_new(g, "d");
  ab = clibc_graph_edge_new("ab", 1, a, b);
  assert(strcmp(ab->label, "ab") == 0);
  assert(ab->weight == 1);
  assert(ab->src_vert == a);
  assert(ab->dst_vert == b);
  assert(clibc_map_keys(a->in_edges)->size == 0);
  assert(clibc_map_keys(a->out_edges)->size == 1);
  assert(*(clibc_graph_edge **)clibc_array_get(clibc_map_keys(a->out_edges),
                                               0) == ab);
  assert(clibc_map_keys(b->in_edges)->size == 1);
  assert(clibc_map_keys(b->out_edges)->size == 0);
  assert(*(clibc_graph_edge **)clibc_array_get(clibc_map_keys(b->in_edges),
                                               0) == ab);

  bc = clibc_graph_edge_new("bc", 2, b, c);
  assert(bc->weight == 2);
  assert(bc->src_vert == b);
  assert(bc->dst_vert == c);
  assert(clibc_map_keys(b->out_edges)->size == 1);
  assert(*(clibc_graph_edge **)clibc_array_get(clibc_map_keys(b->out_edges),
                                               0) == bc);
  assert(clibc_map_keys(c->in_edges)->size == 1);
  assert(clibc_map_keys(c->out_edges)->size == 0);
  assert(*(clibc_graph_edge **)clibc_array_get(clibc_map_keys(c->in_edges),
                                               0) == bc);

  ad = clibc_graph_edge_new("ad", 3, a, d);
  assert(ad->weight == 3);
  assert(ad->src_vert == a);
  assert(ad->dst_vert == d);
  assert(clibc_map_keys(a->out_edges)->size == 2);
  assert(*(clibc_graph_edge **)clibc_array_get(clibc_map_keys(a->out_edges),
                                               1) == ad);
  assert(clibc_map_keys(d->in_edges)->size == 1);
  assert(clibc_map_keys(d->out_edges)->size == 0);
  assert(*(clibc_graph_edge **)clibc_array_get(clibc_map_keys(d->in_edges),
                                               0) == ad);
  return 0;
}
