#ifndef CC_ALGS_GRAPH
#define CC_ALGS_GRAPH
#include "cc_array.h"
#include "cc_graph.h"

void prim_mstree(cc_graph *g, cc_array *ms_tree);
int hamiltonian_cc(cc_graph *g, cc_array *cc);

#endif
