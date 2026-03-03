package com.cuongd.study.algs.book.ch4;

public class Ex4224 {
    private boolean hasHamiltonianPath;

    public Ex4224(Digraph G) {
        Topological t = new Topological(G);
        hasHamiltonianPath = true;
        Integer curr, prev = null;
        for (int v : t.order()) {
            curr = v;
            if (prev != null && !G.hasEdge(prev, curr)) {
                hasHamiltonianPath = false;
                break;
            }
            prev = curr;
        }
    }

    public boolean hasHamiltonianPath() {
        return hasHamiltonianPath;
    }
}
