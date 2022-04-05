package com.cuongd.study.algsp1.book.ch2;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class Ex2130 extends SortCommon {
    private static final int N = 1000000;
    private static final int T_UPPER_BOUND = 32768;

    @Override
    public void sort(Comparable[] a) {}

    private static void sort(Comparable[] a, int t) {
        int n = a.length;
        int h = t;
        while (h < n) h *= t;
        while (h >= 1) {
            for (int i = h; i < n; i++) {
                for (int j = i; j >= h && less(a[j], a[j-h]); j -= h) {
                    exchange(a, j, j - h);
                }
            }
            h /= t;
        }
    }

    public static void main(String[] args) {
        Double[] a = new Double[N];
        for (int i = 0; i < N; i++)
            a[i] = StdRandom.uniform();
        for (int t = 2; t <= T_UPPER_BOUND; t *= 2) {
            Stopwatch timer = new Stopwatch();
            sort(a, t);
            StdOut.printf("t = %d takes %.1f ms\n", t, timer.elapsedTime() * 1000);
            assert isSorted(a);
        }
    }
}
