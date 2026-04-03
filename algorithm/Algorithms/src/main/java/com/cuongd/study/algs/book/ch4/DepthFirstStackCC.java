package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Stack;

public class DepthFirstStackCC extends CC {
    private boolean[] marked;
    private int[] id;
    private int count;

    public DepthFirstStackCC(Graph G) {
        marked = new boolean[G.V()];
        id = new int[G.V()];
        for (int s = 0; s < G.V(); s++) {
            if (!marked[s]) {
                Stack<Integer> stack = new Stack<>();
                stack.push(s);
                while (!stack.isEmpty()) {
                    int v = stack.pop();
                    marked[v] = true;
                    id[v] = count;
                    for (int w : G.adj(v)) {
                        if (!marked[w]) {
                            stack.push(w);
                        }
                    }
                }
                count++;
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
}
