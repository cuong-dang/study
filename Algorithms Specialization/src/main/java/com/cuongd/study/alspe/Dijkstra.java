package com.cuongd.study.alspe;

import java.util.Arrays;

public class Dijkstra {
    private final double[] distTo;
    private final Heap<Integer> pq;

    public Dijkstra(DiGraph G, int s) {
        distTo = new double[G.V];
        Arrays.fill(distTo, Double.POSITIVE_INFINITY);
        distTo[s] = 0;
        pq = new Heap<>((v, w) -> Double.compare(distTo[v], distTo[w]));
        pq.insert(s);
        while (!pq.isEmpty()) {
            relax(G, pq.remove());
        }
    }

    private void relax(DiGraph G, int v) {
        for (DiGraph.DiEdge e : G.adj(v)) {
            int w = e.to;
            if (distTo[w] > distTo[v] + e.weight) {
                distTo[w] = distTo[v] + e.weight;
                pq.remove(w);
                pq.insert(w);
            }
        }
    }

    public double distTo(int v) {
        return distTo[v];
    }

    public static void main(String[] args) {
        DiGraph G = new DiGraph(4);
        G.addEdge(new DiGraph.DiEdge(0, 1, 1));
        G.addEdge(new DiGraph.DiEdge(0, 2, 4));
        G.addEdge(new DiGraph.DiEdge(1, 2, 2));
        G.addEdge(new DiGraph.DiEdge(1, 3, 6));
        G.addEdge(new DiGraph.DiEdge(2, 3, 3));
        Dijkstra sp = new Dijkstra(G, 0);
        assert sp.distTo(0) == 0;
        assert sp.distTo(1) == 1;
        assert sp.distTo(2) == 3;
        assert sp.distTo(3) == 6;
    }
}
