package com.cuongd.study.alspe.c4;

import com.cuongd.study.alspe.DiGraph;
import com.cuongd.study.alspe.JohnsonAPSP;

import java.io.IOException;

class PA1 {
    public static void main(String[] args) throws IOException {
        System.out.println("G1: " + shortest(DiGraph.fromFile("data/g1.txt")));
        System.out.println("G2: " + shortest(DiGraph.fromFile("data/g2.txt")));
        System.out.println("G3: " + shortest(DiGraph.fromFile("data/g3.txt")));
    }

    private static double shortest(DiGraph G) {
        double ans = Double.POSITIVE_INFINITY;
        JohnsonAPSP j = new JohnsonAPSP(G);
        if (j.hasNegativeCycles()) return ans;
        for (int v = 0; v < G.V - 1; v++) {
            for (int w = v + 1; w < G.V; w++) {
                ans = Math.min(j.dist(v, w), ans);
            }
        }
        return ans;
    }
}
