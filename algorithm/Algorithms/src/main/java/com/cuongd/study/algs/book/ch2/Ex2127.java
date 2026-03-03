package com.cuongd.study.algs.book.ch2;

import java.lang.reflect.InvocationTargetException;

public class Ex2127 extends SortCommon {
    private static final int INIT_N = 128;
    private static final int MAX_N = 8192;

    @Override
    public void sort(Comparable[] a) {}

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        for (int i = INIT_N; i <= MAX_N; i *= 2)
            sortCompare(ShellSort.class, InsertionSort.class, i, 100);
    }
}
