package com.cuongd.study.alspe;

import java.util.Arrays;

import static com.cuongd.study.alspe.Partition.partition;

class DSelect {
    public static int select(int[] a, int k) {
        return select(a, k, 0, a.length - 1);
    }

    private static int select(int[] a, int k, int lo, int hi) {
        int n = hi - lo + 1;
        if (n == 1) return a[lo];
        int[] c = new int[Math.max(1, n / 5)];
        for (int i = 0; i < c.length; i++) {
            c[i] = median(a, lo + i * 5, Math.min(lo + i * 5 + 5 - 1, hi));
        }

        int p = select(c, n / 10);

        int pi = -1;
        for (int i = lo; i <= hi; i++) {
            if (a[i] == p) {
                pi = i;
                break;
            }
        }

        int j = partition(a, lo, hi, pi);
        int order = j - lo;
        if (order == k) return p;
        if (order > k) return select(a, k, lo, j - 1);
        return select(a, k - order - 1, j + 1, hi);
    }

    private static int median(int[] a, int lo, int hi) {
        int[] aux = new int[5];
        Arrays.fill(aux, Integer.MAX_VALUE);
        System.arraycopy(a, lo, aux, 0, hi - lo + 1);
        Arrays.sort(aux);
        return aux[(hi - lo) / 2];
    }

    public static void main(String[] args) {
        assert select(new int[]{1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 0) == 0;
        assert select(new int[]{1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 1) == 1;
        assert select(new int[]{1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 2) == 2;
        assert select(new int[]{1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 3) == 3;
        assert select(new int[]{1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 4) == 4;
        assert select(new int[]{1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 5) == 5;
        assert select(new int[]{1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 6) == 6;
        assert select(new int[]{1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 7) == 7;
        assert select(new int[]{1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 8) == 8;
        assert select(new int[]{1, 0, 2, 3, 5, 4, 6, 8, 9, 7}, 9) == 9;
    }
}
