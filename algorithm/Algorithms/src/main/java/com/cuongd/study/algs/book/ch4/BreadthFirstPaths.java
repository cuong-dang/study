package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BreadthFirstPaths extends Paths {
    private final boolean[] marked;
    private final int[] edgeTo;
    private final int s;
    private final Map<Integer, Integer> distTo;

    public BreadthFirstPaths(Graph G, int s) {
        marked = new boolean[G.V()];
        edgeTo = new int[G.V()];
        this.s = s;
        bfs(G, s);
        distTo = new HashMap<>();
        for (int v = 0; v < G.V(); v++) {
            if (hasPathTo(v)) {
                int len = -1;
                for (int w : pathTo(v)) {
                    len += 1;
                }
                distTo.put(v, len);
            } else {
                distTo.put(v, -1);
            }
        }
    }

    private void bfs(Graph G, int s) {
        Queue<Integer> queue = new Queue<>();
        marked[s] = true;
        queue.enqueue(s);
        while (!queue.isEmpty()) {
            int v = queue.dequeue();
            for (int w : G.adj(v)) {
                if (!marked[w]) {
                    edgeTo[w] = v;
                    marked[w] = true;
                    queue.enqueue(w);
                }
            }
        }
    }

    @Override
    public boolean hasPathTo(int v) {
        return marked[v];
    }

    @Override
    public Iterable<Integer> pathTo(int v) {
        if (!hasPathTo(v)) {
            return null;
        }
        Stack<Integer> path = new Stack<>();
        for (int x = v; x != s; x = edgeTo[x]) {
            path.push(x);
        }
        path.push(s);
        return path;
    }

    public int distTo(int v) {
        return distTo.get(v);
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        Paths.main(args, BreadthFirstPaths.class);
    }
}
