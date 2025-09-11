package com.cuongd.study.alspe;

public class JohnsonAPSP {
    private final Dijkstra[] dss;
    private final BellmanFord bf;

    public JohnsonAPSP(DiGraph G) {
        dss = new Dijkstra[G.V];
        // add new vertex
        DiGraph GP = new DiGraph(G.V + 1);
        for (int v = 0; v < G.V; v++) {
            for (DiGraph.DiEdge e : G.adj(v)) {
                GP.addEdge(e);
            }
            GP.addEdge(new DiGraph.DiEdge(G.V, v));
        }
        // run Bellman-Ford
        bf = new BellmanFord(GP, G.V);
        if (bf.hasNegativeCycles()) return;
        // adjust edge weights
        DiGraph GPP = new DiGraph(G.V);
        for (int v = 0; v < GPP.V; v++) {
            for (DiGraph.DiEdge e : G.adj(v)) {
                GPP.addEdge(new DiGraph.DiEdge(e.from, e.to, e.weight + bf.dist(e.from) - bf.dist(e.to)));
            }
        }
        // run Dijkstra
        for (int v = 0; v < G.V; v++) {
            dss[v] = new Dijkstra(GPP, v);
        }
    }

    public double dist(int v, int w) {
        if (bf.hasNegativeCycles()) {
            throw new IllegalStateException();
        }
        return dss[v].distTo(w) - bf.dist(v) + bf.dist(w);
    }

    public static void main(String[] args) {
        DiGraph G = new DiGraph(5);
        G.addEdge(new DiGraph.DiEdge(0, 1, 2));
        G.addEdge(new DiGraph.DiEdge(0, 2, 4));
        G.addEdge(new DiGraph.DiEdge(1, 2, -1));
        G.addEdge(new DiGraph.DiEdge(1, 3, 2));
        G.addEdge(new DiGraph.DiEdge(2, 4, 4));
        G.addEdge(new DiGraph.DiEdge(3, 4, 2));
        JohnsonAPSP j = new JohnsonAPSP(G);
        assert Double.compare(j.dist(0, 0), 0) == 0;
        assert Double.compare(j.dist(0, 1), 2) == 0;
        assert Double.compare(j.dist(0, 2), 1) == 0;
        assert Double.compare(j.dist(0, 3), 4) == 0;
        assert Double.compare(j.dist(0, 4), 5) == 0;
    }
}
