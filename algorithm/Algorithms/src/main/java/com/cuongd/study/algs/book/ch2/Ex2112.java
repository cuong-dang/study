package com.cuongd.study.algs.book.ch2;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Ex2112 extends SortCommon {
    @Override
    public void sort(Comparable[] a) {
        int n = a.length;
        int h = 1;
        while (h < n/3) h = 3*h + 1;
        while (h >= 1) {
            for (int i = h; i < n; i++) {
                int numCompares = 0;
                for (int j = i; j >= h; j -= h) {
                    numCompares++;
                    if (less(a[j], a[j-h])) {
                        exchange(a, j, j - h);
                    } else {
                        break;
                    }
                }
                StdOut.println((double)numCompares / a.length);
            }
            h /= 3;
        }
    }

    public static void main(String[] args) {
        int n = 10;
        Ex2112 sorter = new Ex2112();
        while (true) {
            Double[] a = new Double[n];
            for (int i = 0; i < n; i++) {
                a[i] = StdRandom.uniform();
            }
            sorter.sort(a);
            n *= 10;
        }
    }
}
