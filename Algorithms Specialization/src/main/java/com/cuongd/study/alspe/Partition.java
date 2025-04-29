package com.cuongd.study.alspe;

public class Partition {
    public static int partition(int[] a, int lo, int hi, int p) {
        swap(a, lo, p);
        int i = lo + 1;
        for (int j = lo + 1; j <= hi; j++) {
            if (a[j] > a[lo]) continue;
            swap(a, j, i++);
        }
        swap(a, lo, i - 1);
        return i - 1;
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
