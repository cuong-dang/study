package com.cuongd.study.algs.book.ch4;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Ex4230 {
    private final List<Integer> q;

    public Ex4230(Digraph G) {
        Degrees d = new Degrees(G);
        final int[] indegrees = new int[G.V()];
        for (int v = 0; v < G.V(); v++) {
            indegrees[v] = d.indegree(v);
        }
        PriorityQueue<Integer> pq = new PriorityQueue<>((o1, o2) -> indegrees[o1] - indegrees[o2]);
        for (int v = 0; v < G.V(); v++) {
            pq.add(v);
        }
        q = new ArrayList<>();
        while (!pq.isEmpty()) {
            int v = pq.remove();
            assert indegrees[v] == 0;
            q.add(v);
            for (int w : G.adj(v)) {
                pq.remove(w);
                indegrees[w]--;
                pq.add(w);
            }
        }
    }

    public List<Integer> topologicalSort() {
        return q;
    }
}
