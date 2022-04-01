package com.cuongd.study.algsp1.book.ch2;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

import java.lang.reflect.InvocationTargetException;

public abstract class SortCommon {
    protected static final String IN_PATH = "./src/main/resources/ch2/tiny.txt";

    abstract public void sort(Comparable[] a);

    protected static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    protected static void exchange(Comparable[] a, int i, int j) {
        Comparable t = a[i]; a[i] = a[j]; a[j] = t;
    }

    private static void show(Comparable[] a) {
        for (Comparable comparable : a) {
            StdOut.print(comparable + " ");
        }
        StdOut.println();
    }

    public static boolean isSorted(Comparable[] a) {
        for (int i = 1; i < a.length; i++) {
            if (less(a[i], a[i-1])) {
                return false;
            }
        }
        return true;
    }

    protected static <T extends SortCommon> void run(Class<T> sortClass) throws
            NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        String[] a = new In(IN_PATH).readAllStrings();
        SortCommon sorter = sortClass.getDeclaredConstructor().newInstance();
        sorter.sort(a);
        assert isSorted(a);
        show(a);
    }

    protected static void sortCompare(
            Class<? extends SortCommon> sortClass1, Class<? extends SortCommon> sortClass2,
            int n, int t) throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        double t1 = totalTimeSort(sortClass1, n, t);
        double t2 = totalTimeSort(sortClass2, n, t);
        StdOut.printf("%s is %.1f times faster than %s\n",
                sortClass1.getName(), t1/t2, sortClass2.getName());
    }

    private static double totalTimeSort(Class<? extends SortCommon> sortClass, int n, int t) throws
            NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        double total = 0;
        Double[] a = new Double[n];
        for (int i = 0; i < t; i++) {
            for (int j = 0; j < n; j++)
                a[j] = StdRandom.uniform();
            total += timeSort(sortClass, a);
        }
        return total;
    }

    private static <T extends SortCommon> double timeSort(Class<T> sortClass, Double[] a)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        Stopwatch timer = new Stopwatch();
        SortCommon sorter = sortClass.getDeclaredConstructor().newInstance();
        sorter.sort(a);
        return timer.elapsedTime();
    }
}
