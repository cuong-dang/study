package com.cuongd.study.algs.book.ch2;

import java.lang.reflect.InvocationTargetException;

public class SelectionSort extends SortCommon {
    @Override
    public void sort(Comparable[] a) {
        for (int i = 0; i < a.length; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++) {
                if (less(a[j], a[min])) {
                    min = j;
                }
            }
            exchange(a, i, min);
        }
    }

    public static void main(String[] args) throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        run(SelectionSort.class);
    }
}
