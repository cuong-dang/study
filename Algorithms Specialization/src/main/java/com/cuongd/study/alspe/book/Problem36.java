package com.cuongd.study.alspe.book;

import java.util.List;

public class Problem36 {
    private static List<Integer> solve(List<List<Integer>> m) {
        return solve(m, 0, 0, m.size());
    }

    private static List<Integer> solve(List<List<Integer>> m, int x, int y, int n) {
        if (n == 1) return List.of(x, y);

        List<Integer> p0 = solve(m, x, y, n / 2);
        if (!onBorder(p0.get(0), p0.get(1), m, x, y, n / 2, 0)) return p0;
        List<Integer> p1 = solve(m, x, y + n / 2, n / 2);
        if (!onBorder(p1.get(0), p1.get(1), m, x, y + n / 2, n / 2, 1)) return p1;
        List<Integer> p2 = solve(m, x + n / 2, y, n / 2);
        if (!onBorder(p2.get(0), p2.get(1), m, x + n / 2, y, n / 2, 2)) return p2;
        List<Integer> p3 = solve(m, x + n / 2, y + n / 2, n / 2);
        if (!onBorder(p3.get(0), p3.get(1), m, x + n / 2, y + n / 2, n / 2, 3)) return p3;

        if (isLocalMin(p0.get(0), p0.get(1), m, x, y, n)) return p0;
        if (isLocalMin(p1.get(0), p1.get(1), m, x, y, n)) return p1;
        if (isLocalMin(p2.get(0), p2.get(1), m, x, y, n)) return p2;
        if (isLocalMin(p3.get(0), p3.get(1), m, x, y, n)) return p3;
        throw new AssertionError();
    }

    private static boolean onBorder(int px, int py, List<List<Integer>> m, int mx, int my, int n, int pos) {
        switch (pos) {
            case 0:
                return (px == mx + n - 1 || py == my + n - 1);
            case 1:
                return (px == mx + n - 1 || py == my);
            case 2:
                return (px == mx || py == my + n - 1);
            case 3:
                return (px == mx || py == my);
            default:
                throw new AssertionError();
        }
    }

    private static boolean isLocalMin(int px, int py, List<List<Integer>> m, int mx, int my, int n) {
        int p = m.get(px).get(py);
        // up
        if (px > mx && m.get(px - 1).get(py) < p) return false;
        // down
        if (px < mx + n - 1 && m.get(px + 1).get(py) < p) return false;
        // left
        if (py > my && m.get(px).get(py - 1) < p) return false;
        // right
        if (py < my + n - 1 && m.get(px).get(py + 1) < p) return false;
        return true;
    }

    public static void main(String[] args) {
        assert solve(List.of(List.of(1))).equals(List.of(0, 0));
        assert solve(List.of(List.of(3, 2), List.of(1, 4))).equals(List.of(0, 1));
        assert solve(List.of(
                List.of(1, 2, 3, 4),
                List.of(5, 6, 7, 8),
                List.of(9, 10, 11, 12),
                List.of(13, 14, 15, 16)))
                .equals(List.of(0, 0));
        assert solve(List.of(
                List.of(3, 2, 5, 6),
                List.of(4, 1, 7, 8),
                List.of(9, 10, 13, 14),
                List.of(11, 12, 15, 16)))
                .equals(List.of(1, 1));
    }
}
