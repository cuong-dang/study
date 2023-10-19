#ifndef CLIBC_GRAPH
#define CLIBC_GRAPH

#include "clibc_map.h"

typedef struct {
  char *label;
  clibc_map *in_edges;
  clibc_map *out_edges;
} clibc_graph_vert;

typedef struct {
  char *label;
  int weight;
  clibc_graph_vert *src_vert;
  clibc_graph_vert *dst_vert;
} clibc_graph_edge;

typedef struct {
  clibc_map *verts;
} clibc_graph;

clibc_graph *clibc_graph_new();
clibc_graph_vert *clibc_graph_vert_new(clibc_graph *g, char *label);
clibc_graph_edge *clibc_graph_edge_new(char *label, int weight,
                                       clibc_graph_vert *src_vert,
                                       clibc_graph_vert *dst_vert);
#endif
