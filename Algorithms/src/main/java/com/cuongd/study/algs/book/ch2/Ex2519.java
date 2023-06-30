package com.cuongd.study.algs.book.ch2;

import static com.cuongd.study.algs.book.ch2.SortCommon.less;

public class Ex2519 {
    public static int kendallTau(Comparable[] a) {
        Comparable[] aux = new Comparable[a.length];
        return sort(a, 0, a.length - 1, aux);
    }

    private static int merge(Comparable[] a, int lo, int mid, int hi, Comparable[] aux) {
        int kt = 0;
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; ++k)
            aux[k] = a[k];
        for (int k = lo; k <= hi; ++k) {
            if (i > mid) a[k] = aux[j++];
            else if (j > hi) a[k] = aux[i++];
            else if (less(aux[i], aux[j])) a[k] = aux[i++];
            else {
                kt = kt + (mid - i + 1);
                a[k] = aux[j++];
            }
        }
        return kt;
    }

    private static int sort(Comparable[] a, int lo, int hi, Comparable[] aux) {
        if (lo >= hi) return 0;
        int mid = lo + (hi - lo) / 2;
        int left = sort(a, lo, mid, aux);
        int right = sort(a, mid + 1, hi, aux);
        int kt = merge(a, lo, mid, hi, aux);
        return left + right + kt;
    }

    public static void main(String[] args) {
        Integer[] a = new Integer[]{2, 0, 1, 3, 4, 5, 6};
        System.out.println(kendallTau(a));
    }
}
