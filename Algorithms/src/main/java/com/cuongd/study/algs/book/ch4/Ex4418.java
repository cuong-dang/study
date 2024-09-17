package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.*;

public class Ex4418 {
    public static void main(String[] args) {
        int N = StdIn.readInt(); StdIn.readLine();
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(2*N+2);
        int s = 2*N, t = 2*N+1;
        for (int i = 0; i < N; i++) {
            String[] a = StdIn.readLine().split("\\s+");
            double duration = Double.parseDouble(a[0]);
            G.addEdge(new DirectedEdge(s, i, 0));
            G.addEdge(new DirectedEdge(i, i+N, duration));
            G.addEdge(new DirectedEdge(i+N, t, 0));
            for (int j = 1; j < a.length; j++) {
                int successor = Integer.parseInt(a[j]);
                G.addEdge(new DirectedEdge(i+N, successor, 0.0));
            }
        }

        AcyclicLP lp = new AcyclicLP(G, s);
        StdOut.println("Critical path length: " + lp.distTo(t));
        for (int i = 0; i < 2*N; i++) {
            if (lp.distTo(i) == lp.distTo(t)) {
                for (DirectedEdge e : lp.pathTo(i)) {
                    if (e.weight() == 0) {
                        System.out.printf("%s ", e.to());
                    }
                }
                System.out.println();
            }
        }
    }
}
