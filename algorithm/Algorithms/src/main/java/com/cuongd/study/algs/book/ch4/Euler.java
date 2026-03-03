package com.cuongd.study.algs.book.ch4;

public class Euler {
    private int[] ins;
    private int[] outs;
    private boolean hasEulerianCycle;

    public Euler(Digraph G) {
        ins = new int[G.V()];
        outs = new int[G.V()];

        hasEulerianCycle = new DepthFirstCC(G.undirected()).count() == 1 && hasEqualInOutDegrees(G);
    }

    public boolean hasEulerianCycle() {
        return hasEulerianCycle;
    }

    private boolean hasEqualInOutDegrees(Digraph G) {
        for (int v = 0; v < G.V(); v++) {
            for (int w : G.adj(v)) {
                outs[v]++;
                ins[w]++;
            }
        }
        for (int v = 0; v < G.V(); v++) {
            if (ins[v] != outs[v]) {
                return false;
            }
        }
        return true;
    }
}
