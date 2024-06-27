package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Queue;

import java.util.*;
import java.util.stream.Collectors;

public class LCA {
    private final Digraph G;

    public LCA(Digraph G) {
        this.G = G;
    }

    public int lca(int v, int w) {
        Set<Integer> vAnc = new HashSet<>(ancestors(v));
        Set<Integer> wAnc = new HashSet<>(ancestors(w));
        Map<Integer, Integer> cAnc = vAnc
                .stream()
                .filter(wAnc::contains)
                .collect(Collectors.toMap(k -> k, v_ -> 0));

        Integer ans = null;
        Integer maxLength = null;
        for (int root : cAnc.keySet()) {
            DirectedDFS dfs = new DirectedDFS(G, root);
            for (int a : cAnc.keySet()) {
                if (ans == null || dfs.length(a) > maxLength) {
                    ans = a;
                    maxLength = dfs.length(a);
                }
            }
        }
        return ans == null ? -1 : ans;
    }

    private List<Integer> ancestors(int v) {
        Digraph R = G.reverse();

        Queue<Integer> q = new Queue<>();
        List<Integer> r = new ArrayList<>();
        q.enqueue(v);
        while (!q.isEmpty()) {
            int c = q.dequeue();
            r.add(c);
            for (int w : R.adj(c)) {
                q.enqueue(w);
            }
        }
        return r;
    }
}
