package com.cuongd.study.alspe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PrimMST {
    private final Graph.Edge[] edgeTo;

    public PrimMST(Graph G) {
        boolean[] marked = new boolean[G.V];
        edgeTo = new Graph.Edge[G.V];
        double[] distTo = new double[G.V];
        Arrays.fill(distTo, Integer.MAX_VALUE);
        Heap<Integer> pq = new Heap<>(Comparator.comparingDouble(v -> distTo[v]));

        pq.insert(0);
        while (!pq.isEmpty()) {
            int v = pq.remove();
            marked[v] = true;
            for (Graph.Edge e : G.adj(v)) {
                int w = e.other(v);
                if (marked[w]) continue;
                if (e.weight < distTo[w]) {
                    pq.remove(w);
                    distTo[w] = e.weight;
                    edgeTo[w] = e;
                    pq.insert(w);
                }
            }
        }
    }

    public List<Graph.Edge> mst() {
        return new ArrayList<>(Arrays.asList(edgeTo).subList(1, edgeTo.length));
    }

    public static void main(String[] args) {
        Graph G = new Graph(4);
        G.addEdge(new Graph.Edge(0, 1, 1));
        G.addEdge(new Graph.Edge(0, 2, 4));
        G.addEdge(new Graph.Edge(0, 3, 3));
        G.addEdge(new Graph.Edge(1, 3, 2));
        G.addEdge(new Graph.Edge(2, 3, 5));
        for (Graph.Edge e : new PrimMST(G).mst()) {
            System.out.println(e.weight);
        }
    }
}
