package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.Map;

public class Ex4124 {
    public static void main(String[] args) {
        SymbolGraph sg = new SymbolGraph("data/movies.txt", "/");
        Graph G = sg.G();
        DepthFirstStackCC cc = new DepthFirstStackCC(G);
        Map<Integer, Integer> ccCount = new HashMap<>();

        StdOut.printf("Connected components: %d\n", cc.count());
        for (int v = 0; v < G.V(); v++) {
            int id = cc.id(v);
            ccCount.put(id, ccCount.containsKey(id) ? ccCount.get(id) + 1 : 1);
        }
        int numSmallComponents = 0, biggestComponentSize = 0, biggestComponentId = 0;
        for (int id : ccCount.keySet()) {
            int count = ccCount.get(id);
            if (count < 10) {
                numSmallComponents++;
            }
            if (count > biggestComponentSize) {
                biggestComponentSize = count;
                biggestComponentId = id;
            }
        }
        StdOut.printf("Number of small components: %d\n", numSmallComponents);
        StdOut.printf("Biggest component size: %d\n", biggestComponentSize);
        StdOut.printf("Biggest component id: %d\n", biggestComponentId);
    }
}
