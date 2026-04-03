package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.UF;

public class CrossingEdge {
    private boolean isCrossingEdge;

    public CrossingEdge(EdgeWeightedGraph G, Edge e) {
        UF uf = new UF(G.V());
        for (Edge ee : G.edges()) {
            if (ee == e) {
                continue;
            }
            uf.union(ee.either(), ee.other(ee.either()));
        }
        isCrossingEdge = !uf.connected(e.either(), e.other(e.either()));
    }

    public boolean isCrossingEdge() {
        return isCrossingEdge;
    }
}
