package com.cuongd.study.alspe.c2;

import com.cuongd.study.alspe.DiGraph;
import com.cuongd.study.alspe.Dijkstra;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class PA2 {
    public static void main(String[] args) throws IOException {
        DiGraph G = new DiGraph(200);
        List<String> lines = Files.readAllLines(Path.of("data/pa6.txt"));
        for (String line : lines) {
            String[] vx = line.split("\\s+");
            int v = Integer.parseInt(vx[0]) - 1;
            for (int i = 1; i < vx.length; i++) {
                String[] wx = vx[i].split(",");
                int w = Integer.parseInt(wx[0]) - 1;
                int weight = Integer.parseInt(wx[1]);
                G.addEdge(new DiGraph.DiEdge(v, w, weight));
            }
        }
        Dijkstra sp = new Dijkstra(G, 0);
        int[] ws = new int[]{7 - 1, 37 - 1, 59 - 1, 82 - 1, 99 - 1, 115 - 1, 133 - 1, 165 - 1, 188 - 1, 197 - 1};
        for (int w : ws) {
            System.out.print((int) sp.distTo(w) + ",");
        }
        System.out.println();
    }
}
