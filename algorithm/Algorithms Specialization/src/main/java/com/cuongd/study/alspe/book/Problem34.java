package com.cuongd.study.alspe.book;

class Problem34 {
    private static boolean solve(int[] a) {
        if (a.length == 0) return false;
        return solve(a, 0, a.length - 1);
    }

    private static boolean solve(int[] a, int lo, int hi) {
        if (lo == hi) return a[lo] == lo;
        int mid = (lo + hi) / 2;
        if (a[mid] == mid) return true;
        if (a[mid] > mid) {
            return solve(a, lo, mid - 1);
        } else {
            return solve(a, mid + 1, hi);
        }
    }

    public static void main(String[] args) {
        assert solve(new int[]{0});
        assert !solve(new int[]{1, 2, 3});
        assert solve(new int[]{-1, 0, 2});
        assert solve(new int[]{-2, -1, 2, 3, 4, 5, 6, 7, 8});
    }
}
