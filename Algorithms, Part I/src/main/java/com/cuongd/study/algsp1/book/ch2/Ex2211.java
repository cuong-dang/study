package com.cuongd.study.algsp1.book.ch2;

import java.lang.reflect.InvocationTargetException;

/** Improvements */
public class Ex2211 extends MergeSortTopDown {
    private static final int SMALL_THRESHOLD = 16;

    public void merge(Comparable[] a, int lo, int mid, int hi, Comparable[] aux) {
        if (less(a[mid], a[mid+1])) return;
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
        if (a.length <= SMALL_THRESHOLD) {
            InsertionSort sorter = new InsertionSort();
            sorter.sort(a);
            return;
        }
        super.sort(a);
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        run(Ex2211.class);
    }
}
