package com.cuongd.study.alspe.book;

class Problem33 {
    static int max(int[] a) {
        return max(a, 0, a.length-1);
    }

    static int max(int[] a, int lo, int hi) {
        if (lo == hi) return a[lo];
        int mid = (lo+hi)/2;
        // check left
        if (mid > lo && a[mid-1] < a[mid]) {
            lo = mid;
        }
        if (mid > lo && a[mid-1] > a[mid]) {
            hi = mid-1;
        }
        // check right
        if (mid < hi && a[mid] < a[mid+1]) {
            lo = mid+1;
        }
        if (mid < hi && a[mid] > a[mid+1]) {
            hi = mid;
        }
        return max(a, lo, hi);
    }

    public static void main(String[] args) {
        assert max(new int[]{1}) == 1;
        assert max(new int[]{1, 2}) == 2;
        assert max(new int[]{1, 2, 3}) == 3;
        assert max(new int[]{1, 2, 1}) == 2;
        assert max(new int[]{1, 2, 3, 2}) == 3;
        assert max(new int[]{1, 2, 3, 2, 1}) == 3;
        assert max(new int[]{3, 2, 1}) == 3;
        assert max(new int[]{2, 1}) == 2;
        assert max(new int[]{1, 2, 3, 4, 5, 4, 3, 2, 1}) == 5;
        assert max(new int[]{1, 2, 3, 4, 5, 6, 1}) == 6;
    }
}
