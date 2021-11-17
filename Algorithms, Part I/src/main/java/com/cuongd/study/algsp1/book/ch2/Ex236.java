package com.cuongd.study.algsp1.book.ch2;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import static com.cuongd.study.algsp1.book.ch2.SortCommon.exchange;
import static com.cuongd.study.algsp1.book.ch2.SortCommon.less;

public class Ex236 {
    private static int c;

    private static void sort(Comparable[] a) {
        StdRandom.shuffle(a);
        sort(a, 0, a.length - 1);
    }

    private static void sort(Comparable[] a, int lo, int hi) {
        if (hi <= lo) return;
        int j = partition(a, lo, hi);
        sort(a, lo, j-1);
        sort(a, j+1, hi);
    }

    private static int partition(Comparable[] a, int lo, int hi) {
        Comparable key = a[lo];
        int i = lo, j = hi + 1;

        while (true) {
            while (less(a[++i], key)) { ++c; if (i == hi) break; }
            while (less(key, a[--j])) { ++c; if (j == lo) break; }
            if (i >= j) break;
            exchange(a, i, j);
        }
        exchange(a, lo, j);
        return j;
    }

    public static void main(String[] args) {
        int n;

        c = 0; n = 10000;
        Double[] a = new Double[n];
        for (int i = 0; i < n; i++) a[i] = StdRandom.uniform();
        sort(a);
        StdOut.printf("n = %d c = %d 2n*ln(n)= %.0f\n", n, c, 2*n*Math.log(n));
    }
}
