package com.cuongd.study.algs.book.ch4;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

public class Ex4136 {
    private Set<Pair<Integer, Integer>> edges;
    private boolean isEdgeConnected = true;

    public Ex4136(Graph G) {
        edges = new HashSet<>();

        for (int v = 0; v < G.V(); v++) {
            for (int w : G.adj(v)) {
                if (v > w) {
                    continue;
                }
                edges.add(new ImmutablePair<>(v, w));
            }
        }

        for (Pair<Integer, Integer> edge : edges) {
            if (!connectedWithoutEdge(G, edge)) {
                isEdgeConnected = false;
                break;
            }
        }
    }

    public boolean isEdgeConnected() {
        return isEdgeConnected;
    }

    private boolean connectedWithoutEdge(Graph G, Pair<Integer, Integer> edge) {
        boolean[] marked = new boolean[G.V()];
        dfs(G, edge, marked, 0);
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
                return false;
            }
        }
        return true;
    }

    private void dfs(Graph G, Pair<Integer, Integer> edge, boolean[] marked, int s) {
        marked[s] = true;
        for (int w : G.adj(s)) {
            if ((s == edge.getLeft() && w == edge.getRight()) || (s == edge.getRight() && w == edge.getLeft())) {
                continue;
            }
            if (!marked[w]) {
                dfs(G, edge, marked, w);
            }
        }
    }
}
