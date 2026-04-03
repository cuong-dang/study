package com.cuongd.study.alspe.c2;

import com.cuongd.study.alspe.DiGraph;
import com.cuongd.study.alspe.SCC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PA1 {
    public static void main(String[] args) throws IOException {
        DiGraph G = new DiGraph(875714);
        List<String> lines = Files.readAllLines(Path.of("data/pa5.txt"));
        for (String line : lines) {
            String[] vx = line.split("\\s+");
            int v = Integer.parseInt(vx[0]) - 1;
            int w = Integer.parseInt(vx[1]) - 1;
            G.addEdge(new DiGraph.DiEdge(v, w));
        }
        int[] scc = new SCC(G).id();
        Map<Integer, Integer> count = new HashMap<>();
        for (int id : scc) {
            count.put(id, count.getOrDefault(id, 0) + 1);
        }
        List<Integer> ans = count.values().stream().sorted(Comparator.reverseOrder()).limit(5).collect(Collectors.toList());
        System.out.println(ans);
    }
}
