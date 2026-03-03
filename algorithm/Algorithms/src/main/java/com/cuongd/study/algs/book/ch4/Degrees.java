package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Bag;

import java.util.HashMap;
import java.util.Map;

public class Degrees {
    private final Map<Integer, Integer> outdegrees;
    private final Map<Integer, Integer> indegrees;
    private final Bag<Integer> sources;
    private final Bag<Integer> sinks;
    private boolean isMap = true;

    public Degrees(Digraph G) {
        outdegrees = new HashMap<>();
        indegrees = new HashMap<>();
        sources = new Bag<>();
        sinks = new Bag<>();
        for (int v = 0; v < G.V(); v++) {
            int count = 0;
            for (int w : G.adj(v)) {
                count++;
            }
            outdegrees.put(v, count);
        }
        Digraph reverse = G.reverse();
        for (int v = 0; v < reverse.V(); v++) {
            int count = 0;
            for (int w : reverse.adj(v)) {
                count++;
            }
            if (count != 1) {
                isMap = false;
            }
            indegrees.put(v, count);
        }
        for (int v = 0; v < G.V(); v++) {
            if (indegree(v) == 0) {
                sources.add(v);
            }
            if (outdegree(v) == 0) {
                sinks.add(v);
            }
        }
    }

    public int outdegree(int v) {
        return outdegrees.get(v);
    }

    public int indegree(int v) {
        return indegrees.get(v);
    }

    public Iterable<Integer> sources() {
        return sources;
    }

    public Iterable<Integer> sinks() {
        return sinks;
    }

    public boolean isMap() {
        return isMap;
    }
}
