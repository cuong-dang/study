package com.cuongd.study.algsp1.book.ch2;

import java.lang.reflect.InvocationTargetException;

public class MergeSortTopDown extends SortCommon {
    public static void merge(Comparable[] a, int lo, int mid, int hi, Comparable[] aux) {
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; ++k)
            aux[k] = a[k];
        for (int k = lo; k <= hi; ++k) {
            if (i > mid) a[k] = aux[j++];
            else if (j > hi) a[k] = aux[i++];
            else if (less(aux[i], aux[j])) a[k] = aux[i++];
            else a[k] = aux[j++];
        }
    }

    @Override
    public void sort(Comparable[] a) {
        Comparable[] aux = new Comparable[a.length];
        sort(a, 0, a.length - 1, aux);
    }

    private static void sort(Comparable[] a, int lo, int hi, Comparable[] aux) {
        if (lo >= hi) return;
        int mid = lo + (hi - lo) / 2;
        sort(a, lo, mid, aux);
        sort(a, mid+1, hi, aux);
        merge(a, lo, mid, hi, aux);
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        run(MergeSortTopDown.class);
    }
}
