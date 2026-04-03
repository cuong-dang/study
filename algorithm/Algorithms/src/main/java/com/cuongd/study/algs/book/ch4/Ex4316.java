package com.cuongd.study.algs.book.ch4;

public class Ex4316 {
    private double maxW;

    public Ex4316(EdgeWeightedGraph G, Edge e) {
        Iterable<Edge> mst = new KruskalMST(G).mst();
        EdgeWeightedGraph t = new EdgeWeightedGraph(G.V());
        mst.forEach(t::addEdge);
        t.addEdge(e);
        maxW = Double.MIN_VALUE;
        for (Edge ee : t.cycle()) {
            if (ee.weight() > maxW) {
                maxW = ee.weight();
            }
        }
    }

    public double maxW() {
        return maxW;
    }
}
