package com.cuongd.study.algs.book.ch2;

import java.lang.reflect.InvocationTargetException;

public class InsertionSort extends SortCommon {
    @Override
    public void sort(Comparable[] a) {
        for (int i = 1; i < a.length; i++) {
            for (int j = i; j > 0 && less(a[j], a[j-1]); j--) {
                exchange(a, j, j - 1);
            }
        }
    }

    public static void main(String[] args) throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        run(InsertionSort.class);
    }
}
