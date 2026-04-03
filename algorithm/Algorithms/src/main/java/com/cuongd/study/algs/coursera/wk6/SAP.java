package com.cuongd.study.algs.coursera.wk6;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SAP {
    private final Digraph G;
    private final BFSWalk bfsV;
    private final BFSWalk bfsW;
    private final Map<Set<Set<Integer>>, List<Integer>> cache;

    public SAP(Digraph G) {
        this.G = new Digraph(G);
        bfsV = new BFSWalk(G);
        bfsW = new BFSWalk(G);
        cache = new HashMap<>();
    }

    public int length(int v, int w) {
        return findSAP(List.of(v), List.of(w), true);
    }

    public int ancestor(int v, int w) {
        return findSAP(List.of(v), List.of(w), false);
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return findSAP(v, w, true);
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return findSAP(v, w, false);
    }

    private int findSAP(Iterable<Integer> vs, Iterable<Integer> ws, boolean returningLength) {
        checkVertices(vs);
        checkVertices(ws);

        Set<Integer> setV = new HashSet<>(), setW = new HashSet<>();
        vs.forEach(setV::add);
        ws.forEach(setW::add);
        Set<Set<Integer>> cacheKey = new HashSet<>();
        cacheKey.add(setV);
        cacheKey.add(setW);
        if (cache.containsKey(cacheKey)) {
            List<Integer> cached = cache.get(cacheKey);
            return returningLength ? cached.get(0) : cached.get(1);
        }
        bfsV.reset(vs);
        bfsW.reset(ws);

        List<Integer> ans = new ArrayList<>(2);
        ans.add(-1);
        ans.add(-1);
        while (true) {
            boolean steppedV = tryAStep(ans, bfsV, bfsW);
            boolean steppedW = tryAStep(ans, bfsW, bfsV);
            if (!steppedV && !steppedW) {
                break;
            }
        }

        cache.put(cacheKey, ans);
        return findSAP(vs, ws, returningLength);
    }

    private boolean tryAStep(List<Integer> ans, BFSWalk bfsThis, BFSWalk bfsThat) {
        int length = ans.get(0);
        Set<Integer> landedOn = new HashSet<>();

        if (bfsThis.nextSteps().iterator().hasNext() && (length == -1 || bfsThis.currentDist() < length)) {
            bfsThis.step(landedOn);
            for (int v : landedOn) {
                if (bfsThat.distTo(v) != -1) {
                    int d = bfsThis.distTo(v) + bfsThat.distTo(v);
                    if (length == -1 || d < length) {
                        ans.set(0, d);
                        ans.set(1, v);
                        length = ans.get(0);
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void checkVertices(Iterable<Integer> x) {
        if (x == null) {
            throw new IllegalArgumentException();
        }
        x.forEach(i -> {
            if (i == null || i < 0 || i >= G.V()) {
                throw new IllegalArgumentException();
            }
        });
    }

    public static void main(String[] args) {
        // Intentionally empty.
    }

    private static class BFSWalk {
        private final Digraph G;
        private final int[] distTo;
        private final Queue<Integer> q;
        private final Queue<Integer> nextQ;
        private final Queue<Integer> oldSources;
        private int currentDist;

        public BFSWalk(Digraph G) {
            this.G = G;
            distTo = new int[G.V()];
            Arrays.fill(distTo, -1);
            q = new Queue<>();
            nextQ = new Queue<>();
            oldSources = new Queue<>();
        }

        public void reset(Iterable<Integer> sources) {
            clear(q);
            if (!oldSources.isEmpty()) {
                unmark(oldSources);
            }
            currentDist = 0;
            sources.forEach(q::enqueue);
            sources.forEach(i -> distTo[i] = currentDist);
            sources.forEach(oldSources::enqueue);
        }

        public void step(Set<Integer> landedOn) {
            clear(nextQ);
            while (!q.isEmpty()) {
                int v = q.dequeue();
                landedOn.add(v);
                for (int w : G.adj(v)) {
                    if (distTo[w] == -1) {
                        distTo[w] = distTo[v] + 1;
                        nextQ.enqueue(w);
                    }
                }
            }
            nextQ.forEach(q::enqueue);
            currentDist++;
        }

        public int distTo(int v) {
            return distTo[v];
        }

        public Iterable<Integer> nextSteps() {
            return q;
        }

        public int currentDist() {
            return currentDist;
        }

        private void unmark(Queue<Integer> sources) {
            while (!sources.isEmpty()) {
                int v = sources.dequeue();
                distTo[v] = -1;
                for (int w : G.adj(v)) {
                    if (distTo[w] != -1) {
                        sources.enqueue(w);
                    }
                }
            }
        }

        private static void clear(Queue<Integer> q) {
            while (!q.isEmpty()) q.dequeue();
        }
    }
}
