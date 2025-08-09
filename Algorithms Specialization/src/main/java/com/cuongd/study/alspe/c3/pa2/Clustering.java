package com.cuongd.study.alspe.c3.pa2;

import com.cuongd.study.alspe.Graph;
import com.cuongd.study.alspe.UnionFind;

import java.util.Comparator;
import java.util.PriorityQueue;

class Clustering {
    private double minSpacing;

    public Clustering(Graph G, int k) {
        PriorityQueue<Graph.Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.weight));
        for (int v = 0; v < G.V; v++) {
            for (Graph.Edge e : G.adj(v)) {
                int w = e.other(v);
                if (v < w) {
                    pq.add(e);
                }
            }
        }
        UnionFind uf = new UnionFind(G.V);
        int numClusters = G.V;
        while (numClusters != k) {
            Graph.Edge e = pq.remove();
            if (uf.find(e.v) != uf.find(e.w)) {
                uf.union(e.v, e.w);
                numClusters--;
            }
        }
        assert !pq.isEmpty();
        minSpacing = pq.remove().weight;
    }

    public double minSpacing() {
        return minSpacing;
    }
}
