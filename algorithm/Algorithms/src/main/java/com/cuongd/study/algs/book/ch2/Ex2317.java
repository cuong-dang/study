package com.cuongd.study.algs.book.ch2;

import edu.princeton.cs.algs4.StdRandom;

import java.lang.reflect.InvocationTargetException;

public class Ex2317 extends SortCommon {
    @Override
    public void sort(Comparable[] a) {
        StdRandom.shuffle(a);
        int maxi = 0;
        for (int i = 1; i < a.length; i++)
            if (less(a[maxi], a[i]))
                maxi = i;
        exchange(a, maxi, a.length - 1);
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
            while (less(a[++i], key))
                ;
            while (less(key, a[--j]))
                ;
            if (i >= j) break;
            exchange(a, i, j);
        }
        exchange(a, lo, j);
        return j;
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        sortCompare(Ex2317.class, QuickSort.class, 100000, 100);
    }
}
