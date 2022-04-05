package com.cuongd.study.algsp1.book.ch2;

import java.lang.reflect.InvocationTargetException;

/** Insertion sort with sentinel */
public class Ex2124 extends SortCommon {
    @Override
    public void sort(Comparable[] a) {
        int min, i;
        for (i = 0, min = 0; i < a.length; i++)
            if (less(a[i], a[min])) min = i;
        exchange(a, 0, min);
        for (i = 1; i < a.length; i++) {
            for (int j = i; less(a[j], a[j-1]); j--) {
                exchange(a, j, j - 1);
            }
        }
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        sortCompare(Ex2124.class, InsertionSort.class, 1000, 100);
    }
}
