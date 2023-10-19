#include "clibc_array.h"
#include "clibc_graph.h"
#include "clibc_map.h"
#include <assert.h>
#include <string.h>

void get_edge_ps(clibc_map *edges, clibc_array *edge_ps);

int test_clibc_graph_edge_new() {
  clibc_graph *g = clibc_graph_new();
  clibc_graph_vert *a, *b, *c, *d;
  clibc_graph_edge *ab, *bc, *ad;
  clibc_array *edge_ps;

  a = clibc_graph_vert_new(g, "a");
  b = clibc_graph_vert_new(g, "b");
  c = clibc_graph_vert_new(g, "c");
  d = clibc_graph_vert_new(g, "d");
  ab = clibc_graph_edge_new("ab", 1, a, b);
  assert(strcmp(ab->label, "ab") == 0);
  assert(ab->weight == 1);
  assert(ab->src_vert == a);
  assert(ab->dst_vert == b);
  edge_ps = clibc_array_new(sizeof(clibc_graph_edge **));
  get_edge_ps(a->in_edges, edge_ps);
  assert(edge_ps->size == 0);
  get_edge_ps(a->out_edges, edge_ps);
  assert(edge_ps->size == 1);
  assert(*(clibc_graph_edge **)clibc_array_get(edge_ps, 0) == ab);
  get_edge_ps(b->in_edges, edge_ps);
  assert(edge_ps->size == 1);
  assert(*(clibc_graph_edge **)clibc_array_get(edge_ps, 0) == ab);
  get_edge_ps(b->out_edges, edge_ps);
  assert(edge_ps->size == 0);

  bc = clibc_graph_edge_new("bc", 2, b, c);
  assert(bc->weight == 2);
  assert(bc->src_vert == b);
  assert(bc->dst_vert == c);
  get_edge_ps(b->out_edges, edge_ps);
  assert(edge_ps->size == 1);
  assert(*(clibc_graph_edge **)clibc_array_get(edge_ps, 0) == bc);
  get_edge_ps(c->in_edges, edge_ps);
  assert(edge_ps->size == 1);
  assert(*(clibc_graph_edge **)clibc_array_get(edge_ps, 0) == bc);
  get_edge_ps(c->out_edges, edge_ps);
  assert(edge_ps->size == 0);

  ad = clibc_graph_edge_new("ad", 3, a, d);
  assert(ad->weight == 3);
  assert(ad->src_vert == a);
  assert(ad->dst_vert == d);
  get_edge_ps(a->out_edges, edge_ps);
  assert(edge_ps->size == 2);
  assert(*(clibc_graph_edge **)clibc_array_get(edge_ps, 1) == ad);
  get_edge_ps(d->in_edges, edge_ps);
  assert(edge_ps->size == 1);
  assert(*(clibc_graph_edge **)clibc_array_get(edge_ps, 0) == ad);
  get_edge_ps(d->out_edges, edge_ps);
  assert(edge_ps->size == 0);

  clibc_array_free(edge_ps);
  clibc_graph_free(g);
  return 0;
}

void get_edge_ps(clibc_map *edges, clibc_array *edge_ps) {
  clibc_array_clear(edge_ps);
  clibc_map_keys(edges, edge_ps);
}
