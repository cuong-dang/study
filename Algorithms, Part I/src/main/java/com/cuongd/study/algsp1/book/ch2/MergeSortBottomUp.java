package com.cuongd.study.algsp1.book.ch2;

import java.lang.reflect.InvocationTargetException;

public class MergeSortBottomUp extends MergeSortTopDown {
    @Override
    public void sort(Comparable[] a) {
        int n = a.length;
        Comparable[] aux = new Comparable[n];
        for (int sz = 1; sz < n; sz = 2*sz)
            for (int lo = 0; lo < n - sz; lo += 2*sz) {
                int mid = lo + sz - 1;
                merge(a, lo, mid, Math.min(lo + 2*sz - 1, n - 1), aux);
            }
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        run(MergeSortBottomUp.class);
    }
}
