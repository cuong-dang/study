package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.UF;

import java.util.Set;

public class Ex4333 {
    private boolean isMst;
    private boolean isSpanningTree;

    public Ex4333(EdgeWeightedGraph G, Set<Edge> edges) {
        if (!isSpanningTree(G, edges)) {
            isSpanningTree = false;
            isMst = false;
            return;
        }
        isSpanningTree = true;
        for (Edge e : edges) {
            if (!isMinCuttingEdge(G, edges, e)) {
                isMst = false;
                return;
            }
        }
        isMst = true;
    }

    private boolean isSpanningTree(EdgeWeightedGraph G, Set<Edge> edges) {
        EdgeWeightedGraph T = new EdgeWeightedGraph(G.V());
        edges.forEach(T::addEdge);
        if (T.cycle().iterator().hasNext()) {
            return false;
        }
        boolean[] marked = new boolean[G.V()];
        dfs(T, marked, 0);
        for (boolean m : marked) {
            if (!m) {
                return false;
            }
        }
        return true;
    }

    private void dfs(EdgeWeightedGraph T, boolean[] marked, int v) {
        marked[v] = true;
        for (Edge e : T.adj(v)) {
            int w = e.other(v);
            if (!marked[w]) {
                dfs(T, marked, w);
            }
        }
    }

    private boolean isMinCuttingEdge(EdgeWeightedGraph G, Set<Edge> edges, Edge e) {
        EdgeWeightedGraph T = new EdgeWeightedGraph(G.V());
        edges.forEach(T::addEdge);
        T.removeEdge(e);
        UF uf = new UF(T.V());
        T.edges().forEach(et -> uf.union(et.either(), et.other(et.either())));
        for (Edge eg : G.edges()) {
            int v = eg.either(), w = eg.other(v);
            if (!uf.connected(v, w)) {
                if (eg.weight() < e.weight()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isSpanningTree() {
        return isSpanningTree;
    }

    public boolean isMst() {
        return isMst;
    }
}
