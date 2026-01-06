package com.cuongd.study.alspe.book;

import com.cuongd.study.alspe.DiGraph;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

class Problem811 {
    private final int[] id;

    public Problem811(DiGraph G) {
        id = new int[G.V];
        int[] in = new int[G.V];
        Queue<Integer> q = new ArrayDeque<>();
        Arrays.fill(in, 0);
        for (int v = 0; v < G.V; v++) {
            for (DiGraph.DiEdge e : G.adj(v)) {
                in[e.to]++;
            }
        }
        for (int v = 0; v < G.V; v++) {
            if (in[v] == 0) {
                q.add(v);
            }
        }
        int i = 0;
        while (!q.isEmpty()) {
            int v = q.remove();
            id[v] = i++;
            for (DiGraph.DiEdge e : G.adj(v)) {
                int w = e.to;
                if (--in[w] == 0) {
                    q.add(w);
                }
            }
        }
    }

    public int id(int v) {
        return id[v];
    }

    public int[] topologicalOrder() {
        int[] r = new int[id.length];
        for (int i = 0; i < id.length; i++) {
            r[id[i]] = i;
        }
        return r;
    }

    public static void main(String[] args) {
        DiGraph G = new DiGraph(1);
        Problem811 ans = new Problem811(G);
        assert Arrays.equals(ans.topologicalOrder(), new int[]{0});

        G = new DiGraph(2);
        G.addEdge(new DiGraph.DiEdge(0, 1));
        ans = new Problem811(G);
        assert Arrays.equals(ans.topologicalOrder(), new int[]{0, 1});

        G = new DiGraph(3);
        G.addEdge(new DiGraph.DiEdge(0, 1));
        G.addEdge(new DiGraph.DiEdge(1, 2));
        ans = new Problem811(G);
        assert Arrays.equals(ans.topologicalOrder(), new int[]{0, 1, 2});

        G = new DiGraph(3);
        G.addEdge(new DiGraph.DiEdge(0, 1));
        G.addEdge(new DiGraph.DiEdge(0, 2));
        ans = new Problem811(G);
        assert Arrays.equals(ans.topologicalOrder(), new int[]{0, 1, 2});

        G = new DiGraph(4);
        G.addEdge(new DiGraph.DiEdge(0, 1));
        G.addEdge(new DiGraph.DiEdge(0, 2));
        G.addEdge(new DiGraph.DiEdge(1, 3));
        G.addEdge(new DiGraph.DiEdge(2, 3));
        ans = new Problem811(G);
        assert Arrays.equals(ans.topologicalOrder(), new int[]{0, 1, 2, 3});

        G = new DiGraph(13);
        G.addEdge(new DiGraph.DiEdge(8, 7));
        G.addEdge(new DiGraph.DiEdge(7, 6));
        G.addEdge(new DiGraph.DiEdge(2, 0));
        G.addEdge(new DiGraph.DiEdge(2, 3));
        G.addEdge(new DiGraph.DiEdge(3, 5));
        G.addEdge(new DiGraph.DiEdge(0, 1));
        G.addEdge(new DiGraph.DiEdge(0, 5));
        G.addEdge(new DiGraph.DiEdge(0, 6));
        G.addEdge(new DiGraph.DiEdge(6, 4));
        G.addEdge(new DiGraph.DiEdge(6, 9));
        G.addEdge(new DiGraph.DiEdge(9, 10));
        G.addEdge(new DiGraph.DiEdge(9, 11));
        G.addEdge(new DiGraph.DiEdge(9, 12));
        G.addEdge(new DiGraph.DiEdge(11, 12));
        G.addEdge(new DiGraph.DiEdge(5, 4));
        ans = new Problem811(G);
        assert Arrays.equals(ans.topologicalOrder(), new int[]{2, 8, 0, 3, 7, 1, 5, 6, 4, 9, 10, 11, 12});
    }
}
