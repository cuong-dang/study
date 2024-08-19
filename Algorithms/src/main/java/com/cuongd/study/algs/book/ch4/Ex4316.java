package com.cuongd.study.algs.book.ch4;

public class Ex4316 {
    private double maxW;

    public Ex4316(EdgeWeightedGraph G, Edge e) {
        Iterable<Edge> mst = new KruskalMST(G).mst();
        EdgeWeightedGraph t = new EdgeWeightedGraph(G.V());
        mst.forEach(t::addEdge);
        t.addEdge(e);
        int currV = -1;
        maxW = Double.MIN_VALUE;
        for (int prevV : t.cycle()) {
            if (currV != -1) {
                for (Edge ee : t.adj(prevV)) {
                    if (ee.other(prevV) == currV) {
                        if (ee.weight() > maxW) {
                            maxW = ee.weight();
                        }
                    }
                }
            }
            currV = prevV;
        }
    }

    public double maxW() {
        return maxW;
    }
}
