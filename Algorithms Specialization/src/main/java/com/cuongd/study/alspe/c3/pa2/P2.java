package com.cuongd.study.alspe.c3.pa2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class P2 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("data/c3pa2p2.txt"));
        List<Integer>[] w = new List[24];
        for (int i = 0; i < 24; i++) {
            w[i] = new ArrayList<>();
        }
        for (int i = 1; i < lines.size(); i++) {
            int x = Integer.parseInt(lines.get(i).replaceAll("\\s+", ""), 2);
            int p = Integer.bitCount(x);
            w[p].add(x);
        }
        int ans = 0;
        for (int i = 0; i < w.length - 2; i++) {
            ans += checkSelf(w[i]);
            ans += check(w[i], w[i + 1]);
            ans += check(w[i], w[i + 2]);
        }
        System.out.println("Ans: " + ans);
    }

    private static int checkSelf(List<Integer> a) {
        int count = 0;
        for (int i = 0; i < a.size() - 1; i++) {
            for (int j = i + 1; j < a.size(); j++) {
                if (Integer.bitCount(a.get(i) ^ a.get(j)) < 3) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int check(List<Integer> xs, List<Integer> ys) {
        int count = 0;
        for (int x : xs) {
            for (int y : ys) {
                if (Integer.bitCount(x ^ y) < 3) {
                    count++;
                }
            }
        }
        return count;
    }
}
