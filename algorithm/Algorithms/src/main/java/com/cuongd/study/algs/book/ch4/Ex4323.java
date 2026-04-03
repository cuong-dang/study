package com.cuongd.study.algs.book.ch4;

import java.util.HashSet;
import java.util.Set;

public class Ex4323 {
    private Set<Edge> mst;

    public Ex4323(EdgeWeightedGraph G) {
        mst = new HashSet<>();
        for (Edge e : G.edges()) {
            mst.add(e);
            mst.remove(cycleEdge(mst, G.V()));
        }
    }

    public Iterable<Edge> mst() {
        return mst;
    }

    private Edge cycleEdge(Set<Edge> edges, int V) {
        EdgeWeightedGraph G = new EdgeWeightedGraph(V);
        edges.forEach(G::addEdge);
        Edge heaviest = null;
        for (Edge e : G.cycle()) {
            if (heaviest == null || e.weight() > heaviest.weight()) {
                heaviest = e;
            }
        }
        return heaviest;
    }
}
