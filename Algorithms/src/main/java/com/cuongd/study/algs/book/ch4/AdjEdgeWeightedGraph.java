package com.cuongd.study.algs.book.ch4;

import java.util.ArrayList;
import java.util.List;

public class AdjEdgeWeightedGraph {
    private double weights[][];
    private int V;

    public AdjEdgeWeightedGraph(int V) {
        this.V = V;
        weights = new double[V][V];
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                weights[i][j] = Double.POSITIVE_INFINITY;
            }
        }
    }

    public int V() {
        return V;
    }

    public double weight(int v, int w) {
        return weights[v][w];
    }

    public Iterable<Integer> adj(int v) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            if (weights[v][i] != Double.POSITIVE_INFINITY) {
                result.add(i);
            }
        }
        return result;
    }

    public void addEdge(int v, int w, double e) {
        weights[v][w] = e;
    }
}
