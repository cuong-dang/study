package com.cuongd.study.alspe.book;

import com.cuongd.study.alspe.DiGraph;
import com.cuongd.study.alspe.SCC;

class Problem812 {
    private final int N;
    private final DiGraph G;

    public Problem812(int N) {
        this.N = N;
        this.G = new DiGraph(2 * N);
    }

    public void addPosPos(int a, int b) {
        int aNeg = a + N, bNeg = b + N;
        G.addEdge(new DiGraph.DiEdge(aNeg, b));
        G.addEdge(new DiGraph.DiEdge(bNeg, a));
    }

    public void addPosNeg(int a, int b) {
        int aNeg = a + N, bNeg = b + N;
        G.addEdge(new DiGraph.DiEdge(aNeg, bNeg));
        G.addEdge(new DiGraph.DiEdge(b, a));
    }

    public void addNegNeg(int a, int b) {
        int aNeg = a + N, bNeg = b + N;
        G.addEdge(new DiGraph.DiEdge(a, bNeg));
        G.addEdge(new DiGraph.DiEdge(b, aNeg));
    }

    public boolean solvable() {
        SCC scc = new SCC(G);
        for (int i = 0; i < N; i++) {
            if (scc.id()[i] == scc.id()[i + N]) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Problem812 sat = new Problem812(3);
        sat.addPosPos(0, 1);
        sat.addPosNeg(2, 0);
        sat.addNegNeg(1, 2);
        assert sat.solvable();

        sat = new Problem812(1);
        sat.addPosPos(0, 0);
        sat.addNegNeg(0, 0);
        assert !sat.solvable();

        sat = new Problem812(2);
        sat.addPosPos(0, 0);
        sat.addPosNeg(1, 0);
        sat.addNegNeg(0, 1);
        assert !sat.solvable();
    }
}
