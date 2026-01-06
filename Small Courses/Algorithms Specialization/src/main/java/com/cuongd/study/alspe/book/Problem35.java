package com.cuongd.study.alspe.book;

import java.util.ArrayList;
import java.util.List;

public class Problem35 {
    private static List<Integer> solve(int[] x) {
        return solve(x, 0, x.length - 1);
    }

    private static List<Integer> solve(int[] x, int lo, int hi) {
        if (lo == hi) return List.of(x[lo]);
        int mid = (lo + hi) / 2;
        List<Integer> x1 = solve(x, lo, mid), x2 = solve(x, mid + 1, hi);
        List<Integer> res1 = new ArrayList<>(), res2 = new ArrayList<>();
        for (int i = 0; i < x1.size(); i++) {
            res1.add(x1.get(i) + x2.get(i));
            res2.add(x1.get(i) - x2.get(i));
        }
        res1.addAll(res2);
        return res1;
    }

    public static void main(String[] args) {
        assert solve(new int[]{1}).equals(List.of(1));
        assert solve(new int[]{1, 2}).equals(List.of(3, -1));
        assert solve(new int[]{2, 1, 3, 4}).equals(List.of(10, 0, -4, 2));
    }
}
