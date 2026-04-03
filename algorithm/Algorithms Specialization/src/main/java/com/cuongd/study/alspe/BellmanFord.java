package com.cuongd.study.alspe;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.min;

public class BellmanFord {
    private boolean hasNegativeCycles;
    private final double[][] dp;

    public BellmanFord(DiGraph G, int s) {
        DiGraph GR = G.reverse();
        dp = new double[G.V + 1][G.V];
        dp[0][s] = 0.0;
        for (int i = 0; i < G.V; i++) {
            if (i != s) {
                dp[0][i] = Double.POSITIVE_INFINITY;
            }
        }
        boolean stable = true;
        for (int i = 1; i <= G.V; i++) {
            stable = true;
            for (int v = 0; v < G.V; v++) {
                List<Double> viaInbounds = viaInbounds(GR, dp[i - 1], v);
                dp[i][v] = viaInbounds.isEmpty() ? dp[i - 1][v] : Math.min(dp[i - 1][v], min(viaInbounds));
                if (dp[i][v] != dp[i - 1][v]) {
                    stable = false;
                }
            }
        }
        if (!stable) {
            hasNegativeCycles = true;
        }
    }

    public double dist(int w) {
        if (hasNegativeCycles) throw new IllegalStateException();
        return dp[dp.length - 1][w];
    }

    public boolean hasNegativeCycles() {
        return hasNegativeCycles;
    }

    private static List<Double> viaInbounds(DiGraph GR, double[] dp, int v) {
        List<Double> result = new ArrayList<>();
        for (DiGraph.DiEdge e : GR.adj(v)) {
            int w = e.other(v);
            result.add(dp[w] + e.weight);
        }
        return result;
    }

    public static void main(String[] args) {
        DiGraph G = new DiGraph(5);
        G.addEdge(new DiGraph.DiEdge(0, 1, 2));
        G.addEdge(new DiGraph.DiEdge(0, 2, 4));
        G.addEdge(new DiGraph.DiEdge(1, 2, -1));
        G.addEdge(new DiGraph.DiEdge(1, 3, 2));
        G.addEdge(new DiGraph.DiEdge(2, 4, 4));
        G.addEdge(new DiGraph.DiEdge(3, 4, 2));
        BellmanFord bf = new BellmanFord(G, 0);
        assert Double.compare(bf.dist(0), 0) == 0;
        assert Double.compare(bf.dist(1), 2) == 0;
        assert Double.compare(bf.dist(2), 1) == 0;
        assert Double.compare(bf.dist(3), 4) == 0;
        assert Double.compare(bf.dist(4), 5) == 0;
    }
}
