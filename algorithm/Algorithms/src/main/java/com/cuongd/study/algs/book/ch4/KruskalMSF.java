package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.UF;

import java.util.HashMap;
import java.util.Map;

public class KruskalMSF {
    private int[] id;
    private boolean[] marked;
    private Map<Integer, Iterable<Edge>> msf;
    private int count;

    public KruskalMSF(EdgeWeightedGraph G) {
        id = new int[G.V()];
        marked = new boolean[G.V()];
        msf = new HashMap<>();
        cc(G);
        for (int c = 0; c < count; c++) {
            msf.put(c, mst(G, c));
        }
    }

    public Map<Integer, Iterable<Edge>> msf() {
        return msf;
    }

    private void cc(EdgeWeightedGraph G) {
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
                dfs(G, v);
                count++;
            }
        }
    }

    private void dfs(EdgeWeightedGraph G, int v) {
        marked[v] = true;
        id[v] = count;
        for (Edge e : G.adj(v)) {
            int w = e.other(v);
            if (!marked[w]) {
                dfs(G, w);
            }
        }
    }

    private Iterable<Edge> mst(EdgeWeightedGraph G, int c) {
        MinPQ<Edge> pq = new MinPQ<>();
        Bag<Edge> mst = new Bag<>();
        int numVertices = 0;
        for (int v = 0; v < G.V(); v++) {
            if (id[v] != c) {
                continue;
            }
            numVertices++;
            for (Edge e : G.adj(v)) {
                int w = e.other(v);
                if (v < w) {
                    pq.insert(e);
                }
            }
        }
        UF uf = new UF(G.V());
        while (!pq.isEmpty() && mst.size() < numVertices - 1) {
            Edge e = pq.delMin();
            int v = e.either(), w = e.other(v);
            if (uf.connected(v, w)) {
                continue;
            }
            mst.add(e);
            uf.union(v, w);
        }
        return mst;
    }
}
