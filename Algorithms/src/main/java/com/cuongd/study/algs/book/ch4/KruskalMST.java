package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.UF;

import java.util.Iterator;

public class KruskalMST {
    private Queue<Edge> mst;

    public KruskalMST(EdgeWeightedGraph G) {
        mst = new Queue<>();
        Edge[] edges = new Edge[G.E()];
        Iterator<Edge> iter = G.edges().iterator();
        for (int i = 0; iter.hasNext(); i++) {
            edges[i] = iter.next();
        }
        MinPQ<Edge> pq = new MinPQ<>(edges);
        UF uf = new UF(G.V());
        while (!pq.isEmpty() && mst.size() < G.V() - 1) {
            Edge e = pq.delMin();
            int v = e.either(), w = e.other(v);
            if (uf.connected(v, w)) {
                continue;
            }
            mst.enqueue(e);
            uf.union(v, w);
        }
    }
}
