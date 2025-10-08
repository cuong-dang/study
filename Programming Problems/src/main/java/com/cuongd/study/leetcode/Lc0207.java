package com.cuongd.study.leetcode;

import java.util.ArrayList;
import java.util.List;

public class Lc0207 {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        Graph G = new Graph(numCourses);
        for (int[] prerequisite : prerequisites) {
            G.addEdge(prerequisite[0], prerequisite[1]);
        }
        return !new Cycle(G).hasCycle();
    }

    public static class Cycle {
        private boolean[] marked;
        private boolean[] onStack;
        private boolean hasCycle;

        public Cycle(Graph G) {
            marked = new boolean[G.V()];
            onStack = new boolean[G.V()];
            for (int v = 0; v < G.V(); v++) {
                if (!marked[v]) dfs(G, v);
            }
        }

        public boolean hasCycle() {
            return hasCycle;
        }

        private void dfs(Graph G, int v) {
            onStack[v] = true;
            marked[v] = true;
            for (int w : G.adj(v)) {
                if (hasCycle) return;
                if (!marked[w]) {
                    dfs(G, w);
                } else if (onStack[w]) {
                    hasCycle = true;
                }
            }
            onStack[v] = false;
        }
    }

    public static class Graph {
        private final int V;
        private final List<Integer>[] adj;

        @SuppressWarnings("unchecked")
        public Graph(int V) {
            this.V = V;
            adj = (List<Integer>[]) new List[V];
            for (int v = 0; v < V; v++) {
                adj[v] = new ArrayList<>();
            }
        }

        public void addEdge(int v, int w) {
            adj[v].add(w);
        }

        public List<Integer> adj(int v) {
            return adj[v];
        }

        public int V() {
            return V;
        }
    }
}
