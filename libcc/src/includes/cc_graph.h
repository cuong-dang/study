#ifndef CC_GRAPH
#define CC_GRAPH

#include "cc_array.h"
#include "cc_rbtree.h"

typedef struct {
  char *label;
  cc_rbtree *in_edges;
  cc_rbtree *out_edges;
} cc_graph_vert;

typedef struct {
  char *label;
  int weight;
  cc_graph_vert *src_vert;
  cc_graph_vert *dst_vert;
} cc_graph_edge;

typedef struct {
  cc_rbtree *verts;
} cc_graph;

cc_graph *cc_graph_new();
int cc_graph_num_verts(cc_graph *g);
cc_graph_vert *cc_graph_add_vert(cc_graph *g, char *label);
cc_graph_edge *cc_graph_add_edge(char *label, int weight,
                                 cc_graph_vert *src_vert,
                                 cc_graph_vert *dst_vert);
void cc_graph_vert_labels(cc_graph *g, cc_array *verts);
void cc_graph_adj_verts(cc_graph_vert *v, cc_array *adj_verts);
void cc_graph_incd_edges(cc_graph_vert *v, cc_array *incd_edges);
void cc_graph_free(cc_graph *g);
#endif
