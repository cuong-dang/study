package com.cuongd.study.algs.coursera.wk6;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SAP {
    private final Digraph G;
    private final ST<Pair<Integer, Integer>, Pair<Integer, Integer>> cache;

    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        this.G = G;
        cache = new ST<>();
    }

    public int length(int v, int w) {
        return run(v, w, true);
    }

    public int ancestor(int v, int w) {
        return run(v, w, false);
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        return runSubsets(v, w, true);
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        return runSubsets(v, w, false);
    }

    private int run(int v, int w, boolean returningLength) {
        Pair<Integer, Integer> cacheKey = new Pair<>(v, w);
        if (cache.contains(cacheKey)) {
            Pair<Integer, Integer> cached = cache.get(cacheKey);
            return returningLength ? cached.first : cached.second;
        }

        Set<Integer> ancV = new HashSet<>(ancestors(v));
        Set<Integer> ancW = new HashSet<>(ancestors(w));
        Set<Integer> ancC = ancV.stream().filter(ancW::contains).collect(Collectors.toSet());

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        int sa = -1;
        int length = -1;
        for (int anc : ancC) {
            if (length == -1 || bfsV.distTo(anc) + bfsW.distTo(anc) < length) {
                length = bfsV.distTo(anc) + bfsW.distTo(anc);
                sa = anc;
            }
        }
        cache.put(cacheKey, new Pair<>(length, sa));
        return run(v, w, returningLength);
    }

    private List<Integer> ancestors(int v) {
        Queue<Integer> q = new Queue<>();
        List<Integer> r = new ArrayList<>();
        q.enqueue(v);
        while (!q.isEmpty()) {
            int c = q.dequeue();
            r.add(c);
            for (int w : G.adj(c)) {
                q.enqueue(w);
            }
        }
        return r;
    }

    private int runSubsets(Iterable<Integer> v, Iterable<Integer> w, boolean returningLength) {
        int length = -1;
        int ancestor = -1;
        for (Integer vv : v) {
            if (vv == null) {
                throw new IllegalArgumentException();
            }
            for (Integer ww : w) {
                if (ww == null) {
                    throw new IllegalArgumentException();
                }
                int len = length(vv, ww);
                int anc = ancestor(vv, ww);
                if (length == -1 || len < length) {
                    length = len;
                    ancestor = anc;
                }
            }
        }
        return returningLength ? length : ancestor;
    }

    private static class Pair<First extends Comparable<First>, Second extends Comparable<Second>>
            implements Comparable<Pair<First, Second>> {
        final First first;
        final Second second;

        Pair(First first, Second second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int compareTo(Pair<First, Second> o) {
            if (first.compareTo(o.first) != 0) {
                return first.compareTo(o.first);
            }
            return second.compareTo(o.second);
        }
    }
}
