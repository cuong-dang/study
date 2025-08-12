package com.cuongd.study.alspe.c3.pa2;

import com.cuongd.study.alspe.UnionFind;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class P2 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("data/c3pa2p2.txt"));
        int n = lines.size() - 1;
        UnionFind uf = new UnionFind(n);
        List<List<Integer>>[] w = new List[Integer.parseInt(lines.get(0).split("\\s+")[1]) + 1];
        for (int i = 0; i < w.length; i++) {
            w[i] = new ArrayList<>();
        }
        for (int i = 1; i < lines.size(); i++) {
            int x = Integer.parseInt(lines.get(i).replaceAll("\\s+", ""), 2);
            int p = Integer.bitCount(x);
            w[p].add(List.of(i - 1, x));
        }
        for (int i = 0; i < w.length; i++) {
            checkSelf(uf, w[i]);
            if (i < w.length - 1) {
                check(uf, w[i], w[i + 1]);
            }
            if (i < w.length - 2) {
                check(uf, w[i], w[i + 2]);
            }
        }
        System.out.println("Ans: " + uf.numUnions());
    }

    private static void checkSelf(UnionFind uf, List<List<Integer>> a) {
        for (int i = 0; i < a.size() - 1; i++) {
            for (int j = i + 1; j < a.size(); j++) {
                if (Integer.bitCount(a.get(i).get(1) ^ a.get(j).get(1)) < 3) {
                    uf.union(a.get(i).get(0), a.get(j).get(0));
                }
            }
        }
    }

    private static void check(UnionFind uf, List<List<Integer>> xs, List<List<Integer>> ys) {
        for (List<Integer> x : xs) {
            for (List<Integer> y : ys) {
                if (Integer.bitCount(x.get(1) ^ y.get(1)) < 3) {
                    uf.union(x.get(0), y.get(0));
                }
            }
        }
    }
}
