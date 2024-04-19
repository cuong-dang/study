package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class DegreesOfSeparation {
    public static void main(String[] args) {
        SymbolGraph sg = new SymbolGraph(args[0], args[1]);
        Graph G = sg.G();
        String source = args[2];

        if (!sg.contains(source)) {
            StdOut.printf("%s not in database.\n", source);
            return;
        }

        BreadthFirstPaths bfs = new BreadthFirstPaths(G, sg.index(source));

        while (!StdIn.isEmpty()) {
            String sink = StdIn.readLine();
            if (sg.contains(sink)) {
                int t = sg.index(sink);
                if (bfs.hasPathTo(t)) {
                    for (int v : bfs.pathTo(t)) {
                        StdOut.printf("  %s\n", sg.name(v));
                    }
                } else {
                    StdOut.println("Not connected.");
                }
            } else {
                StdOut.println("Not in database.");
            }
        }
    }
}
