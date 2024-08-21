package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.MaxPQ;

public class Ex4324 {
    private Bag<Edge> mst;

    public Ex4324(EdgeWeightedGraph G) {
        mst = new Bag<>();
        MaxPQ<Edge> pq = new MaxPQ<>();
        G.edges().forEach(pq::insert);
        while (!pq.isEmpty()) {
            Edge e = pq.delMax();
            if (new CrossingEdge(G, e).isCrossingEdge()) {
                mst.add(e);
            } else {
                G.removeEdge(e);
            }
        }
    }

    public Iterable<Edge> mst() {
        return mst;
    }
}
