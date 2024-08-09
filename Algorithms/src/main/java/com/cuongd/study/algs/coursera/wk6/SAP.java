package com.cuongd.study.algs.coursera.wk6;

import edu.princeton.cs.algs4.Digraph;

import java.util.HashSet;
import java.util.Set;

public class SAP {
    private final Digraph G;

    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        this.G = new Digraph(G);
    }

    public int length(int v, int w) {
        BFSWalk bfsV = new BFSWalk(G, v);
        BFSWalk bfsW = new BFSWalk(G, w);

        while (!bfsV.done() || !bfsW.done()) {
            Set<Integer> landedOnV = new HashSet<>();
            Set<Integer> landedOnW = new HashSet<>();
            if (!bfsV.done()) {
                bfsV.step(landedOnV);
                int ans = check(landedOnV, bfsV, bfsW);
                if (ans != -1) {
                    return ans;
                }
            }
            if (!bfsW.done()) {
                bfsW.step(landedOnW);
                int ans = check(landedOnW, bfsW, bfsV);
                if (ans != -1) {
                    return ans;
                }
            }
        }
        return -1;
    }

    private int check(Set<Integer> landed, BFSWalk bfsV, BFSWalk bfsW) {
        for (int v : landed) {
            if (bfsW.distTo(v) != -1) {
                return bfsV.distTo(v) + bfsW.distTo(v);
            }
        }
        return -1;
    }
}
