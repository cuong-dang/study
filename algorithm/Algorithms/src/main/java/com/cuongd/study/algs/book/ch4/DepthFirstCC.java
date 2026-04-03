package com.cuongd.study.algs.book.ch4;

import java.lang.reflect.InvocationTargetException;

public class DepthFirstCC extends CC {
    private boolean[] marked;
    private int[] id;
    private int count;

    public DepthFirstCC(Graph G) {
        marked = new boolean[G.V()];
        id = new int[G.V()];
        for (int s = 0; s < G.V(); s++) {
            if (!marked[s]) {
                dfs(G, s);
                count++;
            }
        }
    }

    private void dfs(Graph G, int v) {
        marked[v] = true;
        id[v] = count;
        for (int w : G.adj(v)) {
            if (!marked[w]) {
                dfs(G, w);
            }
        }
    }

    @Override
    public boolean connected(int v, int w) {
        return id[v] == id[w];
    }

    @Override
    public int id(int v) {
        return id[v];
    }

    @Override
    public int count() {
        return count;
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        CC.main(args, DepthFirstCC.class);
    }
}
