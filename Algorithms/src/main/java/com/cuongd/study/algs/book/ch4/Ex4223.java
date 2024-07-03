package com.cuongd.study.algs.book.ch4;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ex4223 {
    private final Digraph G;
    private final int[] id;
    private int count;

    public Ex4223(Digraph G) {
        this.G = G;
        id = new int[G.V()];
        Arrays.fill(id, -1);
        for (int v = 0; v < G.V(); v++) {
            if (id[v] != -1) continue;
            for (int w : strongCC(v)) {
                id[w] = count;
            }
            count++;
        }
    }

    public Set<Integer> strongCC(int v) {
        return strongCC_(v);
    }

    public int id(int v) {
        return id[v];
    }

    private Set<Integer> strongCC_(int v) {
        DirectedDFS dfs = new DirectedDFS(G, v), revDfs = new DirectedDFS(G.reverse(), v);
        Set<Integer> reachable = IntStream
                .range(0, G.V())
                .filter(dfs::marked)
                .boxed()
                .collect(Collectors.toSet());
        Set<Integer> revReachable = IntStream
                .range(0, G.V())
                .filter(revDfs::marked)
                .boxed()
                .collect(Collectors.toSet());
        return reachable.stream().filter(revReachable::contains).collect(Collectors.toSet());
    }
}
