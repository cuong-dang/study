package com.cuongd.study.alspe.c3.pa2;

import com.cuongd.study.alspe.Graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class P1 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("data/c3pa2p1.txt"));
        Graph G = new Graph(Integer.parseInt(lines.get(0)));
        for (int i = 1; i < lines.size(); i++) {
            String[] split = lines.get(i).split("\\s+");
            int v = Integer.parseInt(split[0]) - 1, w = Integer.parseInt(split[1]) - 1, c = Integer.parseInt(split[2]);
            G.addEdge(new Graph.Edge(v, w, c));
        }

        Clustering c = new Clustering(G, 4);
        System.out.println("Min spacing: " + c.minSpacing());
    }
}
