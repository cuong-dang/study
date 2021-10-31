package com.cuongd.study.algsp1.book.ch2;

import java.lang.reflect.InvocationTargetException;

/** Faster merge */
public class Ex2210 extends MergeSortTopDown {
    @Override
    public void merge(Comparable[] a, int lo, int mid, int hi, Comparable[] aux) {
        int i = lo, j = hi;
        for (int k = lo; k <= mid; ++k)
            aux[k] = a[k];
        for (int k = hi; k > mid; --k)
            aux[mid+1 + (hi-k)] = a[k];
        for (int k = lo; k <= hi; ++k) {
            if (less(aux[i], aux[j])) a[k] = aux[i++];
            else a[k] = aux[j--];
        }
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        run(Ex2210.class);
    }
}
