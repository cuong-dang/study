package com.cuongd.study.algsp1.book.ch2;

import static com.cuongd.study.algsp1.book.ch2.SortCommon.exchange;
import static com.cuongd.study.algsp1.book.ch2.SortCommon.less;
import static edu.princeton.cs.algs4.StdOut.print;
import static edu.princeton.cs.algs4.StdOut.println;

public class Ex235 {
    public static void sortTwoKeys(Comparable[] a) {
        Comparable k1 = a[0], k2 = null;
        int lo = -1, hi = a.length;
        for (int i = 1; i < a.length; ++i)
            if (less(k1, a[i])) {
                k2 = a[i];
                break;
            } else if (less(a[i], k1)) {
                k2 = k1;
                k1 = a[i];
                break;
            }
        while (true) {
            while (a[++lo] == k1) if (lo == hi) break;
            while (a[--hi] == k2) if (lo == hi) break;
            if (lo == hi) break;
            exchange(a, lo, hi);
        }
    }

    public static void main(String[] args) {
        Character a[] = {'a', 'a', 'b', 'a', 'b', 'a', 'b', 'b', 'b', 'a', 'a', 'b', 'a'};
        Character b[] = {'b', 'a', 'a', 'b', 'a', 'b', 'b', 'a', 'a'};

        sortTwoKeys(a);
        sortTwoKeys(b);
        for (int i = 0; i < a.length; i++) print(a[i] + " ");
        println();
        for (int i = 0; i < b.length; i++) print(b[i] + " ");
    }
}
