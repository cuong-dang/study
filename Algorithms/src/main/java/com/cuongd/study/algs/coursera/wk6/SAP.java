package com.cuongd.study.algs.coursera.wk6;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SAP {
    private final ST<Pair<Iterable<Integer>, Iterable<Integer>>, Pair<Integer, Integer>> cache;
    private final BFSWalk bfsV;
    private final BFSWalk bfsW;
    private final Digraph G;

    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        bfsV = new BFSWalk(G);
        bfsW = new BFSWalk(G);
        this.G = G;
        cache = new ST<>();
    }

    public int length(int v, int w) {
        return run(List.of(v), List.of(w), true);
    }

    public int ancestor(int v, int w) {
        return run(List.of(v), List.of(w), false);
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return run(v, w, true);
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return run(v, w, false);
    }

    private int run(Iterable<Integer> v, Iterable<Integer> w, boolean returningLength) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }

//        Pair<Iterable<Integer>, Iterable<Integer>> cacheKey = new Pair<>(v, w);
//        if (cache.contains(cacheKey)) {
//            Pair<Integer, Integer> cached = cache.get(cacheKey);
//            return returningLength ? cached.first() : cached.second();
//        }

        bfsV.reset(v);
        bfsW.reset(w);
        Pair<Integer, Integer> ans = new Pair<>();
        Set<Integer> landedOnV = new HashSet<>();
        Set<Integer> landedOnW = new HashSet<>();
        while (bfsV.shouldWalk() || bfsW.shouldWalk()) {
            landedOnV.clear();
            landedOnW.clear();
            if (bfsV.shouldWalk()) {
                bfsV.step(landedOnV);
                check(landedOnV, bfsV, bfsW, ans);
            }
            if (bfsW.shouldWalk()) {
                bfsW.step(landedOnW);
                check(landedOnW, bfsW, bfsV, ans);
            }
        }
        if (ans.first != null) {
//            cache.put(cacheKey, ans);
//            return run(v, w, returningLength);
            return returningLength ? ans.first : ans.second;
        }
        return -1;
    }

    private void check(Set<Integer> landed, BFSWalk bfsThis, BFSWalk bfsThat, Pair<Integer, Integer> ans) {
        for (int v : landed) {
            if (bfsThat.distTo(v) != -1) { // a common ancestor
                int dist = bfsThis.distTo(v) + bfsThat.distTo(v);
                if (ans.first == null || dist < ans.first) {
                    ans.setFirst(dist);
                    ans.setSecond(v);
                }
            }
        }
        if (ans.first != null && bfsThis.nextStepDist() >= ans.first) {
            bfsThis.setTooFar();
        }
    }

    private static class BFSWalk {
        private final Digraph G;
        private final int[] distTo;
        private int nextStepDist;
        private boolean isTooFar;
        private Queue<Integer> q;
        private Iterable<Integer> oldSources;

        public BFSWalk(Digraph G) {
            this.G = G;
            distTo = new int[G.V()];
            Arrays.fill(distTo, -1);
            q = new Queue<>();
        }

        public boolean shouldWalk() {
            return canWalk() && !isTooFar;
        }

        private boolean canWalk() {
            return !q.isEmpty();
        }

        public void step(Set<Integer> landedOn) {
            Queue<Integer> nextQ = new Queue<>();
            while (!q.isEmpty()) {
                int v = q.dequeue();
                distTo[v] = nextStepDist;
                landedOn.add(v);
                for (int w : G.adj(v)) {
                    if (distTo[w] == -1) {
                        nextQ.enqueue(w);
                    }
                }
            }
            q = nextQ;
            nextStepDist++;
        }

        public int nextStepDist() {
            return nextStepDist;
        }

        public int distTo(int v) {
            return distTo[v];
        }

        public void reset(Iterable<Integer> sources) {
            while (!q.isEmpty()) {
                q.dequeue();
            }
            if (oldSources != null) {
                unmark(oldSources);
            }
            sources.forEach(q::enqueue);
            nextStepDist = 0;
            isTooFar = false;
            oldSources = sources;
        }

        public void setTooFar() {
            this.isTooFar = true;
        }

        private void unmark(Iterable<Integer> sources) {
            sources.forEach(q::enqueue);
            while (!q.isEmpty()) {
                int v = q.dequeue();
                distTo[v] = -1;
                for (int w : G.adj(v)) {
                    if (distTo[w] != -1) {
                        q.enqueue(w);
                    }
                }
            }
        }
    }

    private static class Pair<F, S> implements Comparable<Pair<Iterable<Integer>, Iterable<Integer>>> {
        private F first;
        private S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        public Pair() {
            first = null;
            second = null;
        }

        public F first() {
            return first;
        }

        public S second() {
            return second;
        }

        public void setFirst(F first) {
            this.first = first;
        }

        public void setSecond(S second) {
            this.second = second;
        }

        @Override
        public int compareTo(Pair<Iterable<Integer>, Iterable<Integer>> that) {
            Pair<Iterable<Integer>, Iterable<Integer>> iterPair = (Pair<Iterable<Integer>, Iterable<Integer>>) this;
            int firstCmp = compareTo(iterPair.first, that.first);
            return firstCmp != 0 ? firstCmp : compareTo(iterPair.second, that.second);
        }

        private int compareTo(Iterable<Integer> xs, Iterable<Integer> ys) {
            Iterator<Integer> xIter = xs.iterator(), yIter = ys.iterator();

            while (xIter.hasNext() && yIter.hasNext()) {
                int x = xIter.next(), y = yIter.next();
                if (x != y) {
                    return x - y;
                }
            }
            if (yIter.hasNext()) {
                return -1;
            } else if (xIter.hasNext()) {
                return 1;
            }
            return 0;
        }
    }
}
