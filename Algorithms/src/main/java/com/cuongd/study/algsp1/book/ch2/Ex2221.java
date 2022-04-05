package com.cuongd.study.algsp1.book.ch2;

import edu.princeton.cs.algs4.Merge;

/** Triplicates */
public class Ex2221 {
    public static String[] triplicates(String[] a, String[] b, String[] c, int n) {
        String[] r = new String[n];
        int ai = 0, bi = 0, ci = 0, ri = 0;

        Merge.sort(a);
        Merge.sort(b);
        Merge.sort(c);

        while (ai < a.length && bi < b.length && ci < c.length) {
            if (a[ai].equals(b[bi]) && a[ai].equals(c[ci])) {
                r[ri++] = a[ai];
                ++ai;
                ++bi;
                ++ci;
            }
            else if (a[ai].compareTo(b[bi]) <= 0 && a[ai].compareTo(c[ci]) <= 0)
                ++ai;
            else if (b[bi].compareTo(a[ai]) <= 0 && b[bi].compareTo(c[ci]) <= 0)
                ++bi;
            else if (c[ci].compareTo(a[ai]) <= 0 && c[ci].compareTo(b[bi]) <= 0)
                ++ci;
        }
        return r;
    }
}
