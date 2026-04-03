package com.cuongd.study.algs.book.ch4;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Ex4332 {
    private List<Edge> mst;

    public Ex4332(EdgeWeightedGraph G, Set<Edge> es) {
        mst = new ArrayList<>();
        EdgeWeightedGraph T = new EdgeWeightedGraph(G.V());
        new PrimMST(G).edges().forEach(T::addEdge);
        for (Edge e : es) {
            if (T.containsEdge(e)) {
                continue;
            }
            T.addEdge(e);
            Edge toRemove = null;
            for (Edge ee : T.cycle()) {
                if (!es.contains(ee) && (toRemove == null || toRemove.weight() < ee.weight())) {
                    toRemove = ee;
                }
            }
            if (toRemove != null) {
                T.removeEdge(toRemove);
            }
        }
        T.edges().forEach(mst::add);
    }

    public Iterable<Edge> mst() {
        return mst;
    }
}
