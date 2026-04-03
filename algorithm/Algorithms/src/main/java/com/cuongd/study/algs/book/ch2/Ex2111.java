package com.cuongd.study.algs.book.ch2;

import java.lang.reflect.InvocationTargetException;

public class Ex2111 extends SortCommon {
    private static final int HS = 1024;

    @Override
    public void sort(Comparable[] a) {
        int[] hs = new int[HS];
        for (int i = 0, h = 1; h < a.length; i++, h = h*3 + 1)
            hs[i] = h;
        for (int hi = hs.length - 1; hi >= 0; hi--)
            for (int i = hs[hi]; i < a.length; i++)
                for (int j = i; j >= hs[hi] && less(a[j], a[j-hs[hi]]); j -= hs[hi])
                    exchange(a, j, j-hs[hi]);
    }

    public static void main(String[] args) throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        run(Ex2111.class);
    }
}
