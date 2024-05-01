package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Ex4123 {
    public static void main(String[] args) {
        SymbolGraph sg = new SymbolGraph("data/movies.txt", "/");
        Graph G = sg.G();
        BreadthFirstPaths bfs = new BreadthFirstPaths(G, sg.index("Bacon, Kevin"));
        Pattern p = Pattern.compile("\\(\\d{4}\\)", Pattern.CASE_INSENSITIVE);
        Map<Integer, Integer> count = new HashMap<>();

        for (int v = 0; v < G.V(); v++) {
            String name = sg.name(v);
            if (p.matcher(name).find()) {
                continue;
            }
            int d = bfs.distTo(sg.index(name));
            if (d != -1) {
                d = d / 2;
            }
            int n = count.getOrDefault(d, 0);
            count.put(d, n + 1);
        }
        for (int i = 0; i < 10; i++) {
            if (!count.containsKey(i)) {
                continue;
            }
            StdOut.printf("%d: ", i);
            for (int j = 0; j < count.get(i); j++) {
                StdOut.print("|");
            }
            StdOut.println();
        }
    }
}
