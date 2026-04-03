package com.cuongd.study.alspe.c3.pa1;

import com.cuongd.study.alspe.Graph;
import com.cuongd.study.alspe.PrimMST;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class P3 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("data/c3pa1p3.txt"));
        int V = Integer.parseInt(lines.get(0).split("\\s+")[0]);
        Graph G = new Graph(V);
        for (int i = 1; i < lines.size(); i++) {
            String[] fields = lines.get(i).split("\\s+");
            int v = Integer.parseInt(fields[0]) - 1, w = Integer.parseInt(fields[1]) - 1, weight = Integer.parseInt(fields[2]);
            G.addEdge(new Graph.Edge(v, w, weight));
        }
        List<Graph.Edge> mst = new PrimMST(G).mst();
        double totalWeight = 0;
        for (Graph.Edge e : mst) {
            totalWeight += e.weight;
        }
        System.out.println("Ans: " + totalWeight);
    }
}
