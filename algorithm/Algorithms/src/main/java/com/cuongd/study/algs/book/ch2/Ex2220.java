package com.cuongd.study.algs.book.ch2;

import static com.cuongd.study.algs.book.ch2.SortCommon.less;

public class Ex2220 {
    public static void merge(Comparable[] a, int lo, int mid, int hi, int[] aux, int[] idx) {
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; ++k)
            aux[k] = idx[k];
        for (int k = lo; k <= hi; ++k) {
            if (i > mid) idx[k] = aux[j++];
            else if (j > hi) idx[k] = aux[i++];
            else if (less(a[aux[i]], a[aux[j]])) idx[k] = aux[i++];
            else idx[k] = aux[j++];
        }
    }

    public static int[] sort(Comparable[] a) {
        int[] aux = new int[a.length];
        int[] idx = new int[a.length];
        for (int i = 0; i < a.length; ++i)
            idx[i] = i;
        sort(a, 0, a.length - 1, aux, idx);
        return idx;
    }

    private static void sort(Comparable[] a, int lo, int hi, int[] aux, int[] idx) {
        if (lo >= hi) return;
        int mid = lo + (hi - lo) / 2;
        sort(a, lo, mid, aux, idx);
        sort(a, mid+1, hi, aux, idx);
        merge(a, lo, mid, hi, aux, idx);
    }
}
