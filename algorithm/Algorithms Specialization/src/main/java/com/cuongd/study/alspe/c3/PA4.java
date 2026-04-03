package com.cuongd.study.alspe.c3;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.readAllLines;

class PA4 {
    public static void main(String[] args) throws IOException {
        List<String> lines = readAllLines(Path.of("data/c3pa4p2.txt"));
        int n = Integer.parseInt(lines.get(0).split("\\s+")[1]), c = Integer.parseInt(lines.get(0).split("\\s+")[0]);
        List<Integer>[] items = new List[n];
        for (int i = 1; i <= n; i++) {
            String[] split = lines.get(i).split("\\s+");
            items[i - 1] = List.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }
        System.out.println("Ans: " + new Knapsack(items, c).optimalSol);
    }

    private static class Knapsack {
        private final List<Integer>[] items;
        private final Map<List<Integer>, Integer> dp;
        public final int optimalSol;


        public Knapsack(List<Integer>[] items, int c) {
            int n = items.length;
            this.items = items;
            dp = new HashMap<>();
            optimalSol = solve(n, c);
        }

        private int solve(int i, int c) {
            if (i == 0) {
                return 0;
            }

            List<Integer> dpKey = List.of(i, c);
            if (dp.containsKey(dpKey)) return dp.get(dpKey);
            int w = items[i - 1].get(1), v = items[i - 1].get(0);
            if (w > c) {
                dp.put(dpKey, solve(i - 1, c));
            } else {
                dp.put(dpKey, Math.max(solve(i - 1, c), solve(i - 1, c - w) + v));
            }
            return dp.get(dpKey);
        }

        public static void main(String[] args) {
            List<Integer>[] items = new List[]{List.of(3, 4), List.of(2, 3), List.of(4, 2), List.of(4, 3)};
            assert new Knapsack(items, 6).optimalSol == 8;
        }
    }
}
