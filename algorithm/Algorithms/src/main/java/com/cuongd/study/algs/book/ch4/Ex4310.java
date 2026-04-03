package com.cuongd.study.algs.book.ch4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Ex4310 {
    private final int V;
    private int E;
    private final Edge[][] m;

    public Ex4310(int V) {
        this.V = V;
        m = new Edge[V][V];
        for (int i = 0; i < V; i++) {
            m[i] = new Edge[V];
        }
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    public void addEdge(Edge e) {
        int v = e.either(), w = e.other(v);
        if (m[v][w] != null) {
            throw new IllegalArgumentException("Parallel edges not allowed");
        }
        m[v][w] = e;
        m[w][v] = e;
        E++;
    }

    public Iterable<Edge> adj(int v) {
        return Arrays.stream(m[v]).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Iterable<Edge> edges() {
        List<Edge> es = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            for (int j = i; j < V; j++) {
                if (m[i][j] != null) {
                    es.add(m[i][j]);
                }
            }
        }
        return es;
    }
}
