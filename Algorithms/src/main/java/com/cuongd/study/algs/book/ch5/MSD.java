package com.cuongd.study.algs.book.ch5;

import edu.princeton.cs.algs4.Insertion;

import java.util.Comparator;

public class MSD {
    private static int R = 256; // radix
    private static int M = 15; // cutoff for small arrays
    private static String[] aux; // auxiliary array for distribution

    private static int charAt(String s, int d) {
        return d < s.length() ? s.charAt(d) : -1;
    }

    public static void sort(String[] a) {
        int N = a.length;
        aux = new String[N];
        sort(a, 0, N-1, 0);
    }

    private static void sort(String[] a, int lo, int hi, int d) {
        if (hi <= lo + M) {
            Insertion.sort(a, lo, hi,
                    Comparator.comparingInt(s -> charAt((String) s, d)));
            return;
        }
        int[] count = new int[R+2];
        for (int i = lo; i <= hi; i++) { // Compute frequency counts.
            count[charAt(a[i], d) + 2]++;
        }
        for (int r = 0; r < R+1; r++) { // Transform counts to indices.
            count[r+1] += count[r];
        }
        for (int i = lo; i <= hi; i++) { // Distribute.
            aux[count[charAt(a[i], d) + 1]++] = a[i];
        }
        for (int i = lo; i <= hi; i++) { // Copy back.
            a[i] = aux[i - lo];
        }
        for (int r = 0; r < R; r++) {
            sort(a, lo + count[r], lo + count[r+1] - 1, d+1);
        }
    }
}
