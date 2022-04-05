package com.cuongd.study.algsp1.book.ch2;

import java.lang.reflect.InvocationTargetException;

public class HeapSort extends SortCommon {
    @Override
    public void sort(Comparable[] a) {
        Comparable[] b = new Comparable[a.length + 1];
        System.arraycopy(a, 0, b, 1, a.length);

        int N = b.length - 1;
        for (int k = N/2; k >= 1; --k)
            sink(b, k, N);
        while (N > 1) {
            exchange(b, 1, N--);
            sink(b, 1, N);
        }

        System.arraycopy(b, 1, a, 0, a.length);
    }

    private void sink(Comparable[] a, int k, int N) {
        while (2 * k <= N) {
            int j = 2 * k;
            if (j < N && less(a[j], a[j+1]))
                ++j;
            if (less(a[j], a[k]))
                break;
            exchange(a, j, k);
            k = j;
        }
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        run(HeapSort.class);
    }
}
