#include "clibc_algs_graph.h"
#include "clibc_array.h"
#include "clibc_graph.h"
#include "clibc_map.h"
#include <stdlib.h>
#include <string.h>

int label_cmp(void *, void *);
int get_nx_edge(clibc_map *seen_vx, clibc_array *edges);
int has_not_seen(clibc_map *seen_vx, clibc_graph_vert *v);
void add_next_edges(clibc_array *nx_edges, clibc_array *id_edges,
                    clibc_map *seen_vx);

void prim_mstree(clibc_graph *g, clibc_array *mstree) {
  clibc_array *vx_labels, *id_edges, *nx_edges;
  clibc_map *seen_vx;
  clibc_graph_vert *nx_vert;
  clibc_graph_edge *nx_edge;
  int i, j, null;

  /* Get all vertices */
  vx_labels = clibc_array_new(sizeof(char *));
  clibc_map_keys(g->verts, vx_labels);
  if (vx_labels->size == 0) {
    clibc_array_free(vx_labels);
    return;
  }
  /* Init */
  clibc_array_clear(mstree);
  seen_vx = clibc_map_new(sizeof(char *), sizeof(int), label_cmp);
  id_edges = clibc_array_new(sizeof(clibc_graph_edge *));
  nx_edges = clibc_array_new(sizeof(clibc_graph_edge *));
  /* Select init vx */
  nx_vert = *(clibc_graph_vert **)clibc_map_get(g->verts,
                                                clibc_array_get(vx_labels, 0));
  clibc_map_put(seen_vx, &nx_vert->label, &null);
  clibc_graph_incd_edges(nx_vert, nx_edges);
  for (i = 0; i < vx_labels->size - 1; i++) {
    j = get_nx_edge(seen_vx, nx_edges);
    nx_edge = *(clibc_graph_edge **)clibc_array_get(nx_edges, j);
    clibc_array_add(mstree, &nx_edge);
    nx_vert = has_not_seen(seen_vx, nx_edge->src_vert) ? nx_edge->src_vert
                                                       : nx_edge->dst_vert;
    clibc_map_put(seen_vx, &nx_vert->label, &null);
    clibc_array_rm(nx_edges, j);
    clibc_graph_incd_edges(nx_vert, id_edges);
    add_next_edges(nx_edges, id_edges, seen_vx);
    clibc_array_clear(id_edges);
  }

  clibc_array_free(nx_edges);
  clibc_array_free(id_edges);
  clibc_map_free(seen_vx);
  clibc_array_free(vx_labels);
}

int label_cmp(void *l1, void *l2) { return strcmp(*(char **)l1, *(char **)l2); }

int get_nx_edge(clibc_map *seen_vx, clibc_array *edges) {
  int i, j;
  clibc_graph_edge *e;

  while (1) {
    j = 0;
    for (i = 1; i < edges->size; i++) {
      if ((*(clibc_graph_edge **)clibc_array_get(edges, i))->weight <
          (*(clibc_graph_edge **)clibc_array_get(edges, j))->weight) {
        j = i;
      }
    }
    e = *(clibc_graph_edge **)clibc_array_get(edges, j);
    if (has_not_seen(seen_vx, e->src_vert) ||
        has_not_seen(seen_vx, e->dst_vert)) {
      break;
    }
    clibc_array_rm(edges, j);
  }
  return j;
}

int has_not_seen(clibc_map *seen_vx, clibc_graph_vert *v) {
  return clibc_map_get(seen_vx, &v->label) == NULL;
}

void add_next_edges(clibc_array *nx_edges, clibc_array *id_edges,
                    clibc_map *seen_vx) {
  int i, null;
  clibc_graph_edge *e;

  for (i = 0; i < id_edges->size; i++) {
    e = *(clibc_graph_edge **)clibc_array_get(id_edges, i);
    if (has_not_seen(seen_vx, e->src_vert) ||
        has_not_seen(seen_vx, e->dst_vert)) {
      clibc_array_add(nx_edges, &e);
    }
  }
}
