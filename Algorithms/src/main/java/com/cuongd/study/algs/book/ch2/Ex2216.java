package com.cuongd.study.algs.book.ch2;

import com.cuongd.study.algs.book.ch1.MyQueue;

import java.lang.reflect.InvocationTargetException;

/** Natural mergesort */
public class Ex2216 extends MergeSortBottomUp {
    @Override
    public void sort(Comparable[] a) {
        int n = a.length, lo = 0, mid, hi;
        MyQueue<Integer> q = new MyQueue<>();

        Comparable[] aux = new Comparable[n];
        for (int i = 0; i < n; ) {
            int nextBound = findNextOrderBoundary(a, i);
            q.enqueue(nextBound);
            i = nextBound + 1;
        }
        while (true) {
            mid = q.dequeue();
            if (q.isEmpty()) break;
            if (mid == n - 1) {
                q.enqueue(mid);
                lo = 0;
                continue;
            }
            hi = q.dequeue();
            merge(a, lo, mid, hi, aux);
            q.enqueue(hi);
            lo = hi == n - 1 ? 0 : hi + 1;
        }
    }

    private static int findNextOrderBoundary(Comparable[] a, int start) {
        int end = start + 1;
        while (start != a.length - 1 && less(a[start], a[end])) {
            ++start;
            ++end;
        }
        return start;
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        run(Ex2216.class);
        sortCompare(MergeSortTopDown.class, Ex2216.class, 10000, 4);
    }
}
