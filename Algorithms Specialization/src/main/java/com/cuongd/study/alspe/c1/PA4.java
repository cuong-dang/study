package com.cuongd.study.alspe.c1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class PA4 {
    private static int randomContraction(Graph G) {
        for (int i = 0; i < G.V - 2; i++) {
            Edge e = G.randomEdge();
            G.merge(e.v, e.w);
        }
        for (int v = 0; v < G.V; v++) {
            if (G.adj[v] != null) {
                return G.adj[v].size();
            }
        }
        throw new AssertionError();
    }

    private static class Graph {
        private final List<Edge>[] adj;
        public final int V;

        public Graph(int V) {
            this.V = V;
            adj = (List<Edge>[]) new List[V];
            for (int v = 0; v < V; v++) {
                adj[v] = new ArrayList<>();
            }
        }

        public Graph(String filePath) throws IOException {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            this.V = lines.size();
            adj = (List<Edge>[]) new List[V];
            for (int v = 0; v < V; v++) {
                adj[v] = new ArrayList<>();
            }
            for (String line : lines) {
                String[] vx = line.split("\\s+");
                int v = Integer.parseInt(vx[0]) - 1;
                for (int i = 1; i < vx.length; i++) {
                    int w = Integer.parseInt(vx[i]) - 1;
                    addEdge(v, w, false);
                }
            }
        }

        public void addEdge(int v, int w, boolean bidirectional) {
            Edge e = new Edge(v, w);
            adj[v].add(e);
            if (bidirectional)
                adj[w].add(e);
        }

        public void merge(int v, int w) {
            for (Edge e : adj[w]) {
                int other = e.other(w);
                if (other != v) {
                    addEdge(v, other, true);
                    adj[other].remove(new Edge(other, w));
                }
            }
            while (adj[v].remove(new Edge(v, w)))
                ;
            adj[w] = null;
        }

        public Edge randomEdge() {
            List<Edge> edgeList = edgeList();
            return edgeList.get(new Random().nextInt(edgeList.size()));
        }

        private List<Edge> edgeList() {
            List<Edge> edgeList = new ArrayList<>();
            for (int v = 0; v < V; v++) {
                if (adj[v] == null) continue;
                for (Edge e : adj[v]) {
                    if (v < e.other(v)) edgeList.add(e);
                }
            }
            return edgeList;
        }
    }

    private static class Edge {
        public final int v;
        public final int w;

        public Edge(int v, int w) {
            this.v = v;
            this.w = w;
        }

        public int other(int vertex) {
            if (vertex == v) return w;
            if (vertex == w) return v;
            throw new IllegalArgumentException();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Edge)) return false;
            Edge edge = (Edge) o;
            return (v == edge.v && w == edge.w) || (v == edge.w && w == edge.v);
        }

        @Override
        public int hashCode() {
            return Objects.hash(v, w);
        }
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Graph G = mkGraph();
        G.merge(0, 1);
        assert G.adj[0].size() == 3;
        assert G.adj[1] == null;
        assert G.adj[2].size() == 3;
        assert G.adj[3].size() == 2;
        assert G.edgeList().size() == 4;
        G.merge(0, 2);
        assert G.adj[0].size() == 2;
        assert G.adj[2] == null;
        assert G.adj[3].size() == 2;
        assert G.edgeList().size() == 2;

        G = mkGraph();
        G.merge(0, 1);
        G.merge(2, 3);
        assert G.adj[0].size() == 3;
        assert G.adj[2].size() == 3;
        assert G.adj[3] == null;
        assert G.edgeList().size() == 3;

        G = mkGraph();
        G.merge(0, 3);
        assert G.adj[0].size() == 4;
        assert G.adj[1].size() == 3;
        assert G.adj[2].size() == 3;
        assert G.adj[3] == null;
        assert G.edgeList().size() == 5;
        G.merge(0, 1);
        assert G.adj[0].size() == 3;
        assert G.adj[1] == null;
        assert G.adj[2].size() == 3;
        assert G.edgeList().size() == 3;

        G = mkGraph();
        int actual = randomContraction(G);
        assert actual == 2 || actual == 3;

        int numThreads = 96;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            futures.add(executor.submit(() -> {
                Graph GG = new Graph("data/pa4.txt");
                return randomContraction(GG);
            }));
        }
        int min = Integer.MAX_VALUE;
        for (Future<Integer> future : futures) {
            min = Math.min(min, future.get());
        }
        executor.shutdown();
        System.out.println(min);
    }

    private static Graph mkGraph() {
        Graph G = new Graph(4);
        G.addEdge(0, 1, true);
        G.addEdge(0, 2, true);
        G.addEdge(1, 2, true);
        G.addEdge(1, 3, true);
        G.addEdge(2, 3, true);
        return G;
    }
}
