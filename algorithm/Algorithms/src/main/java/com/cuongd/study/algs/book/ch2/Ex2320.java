package com.cuongd.study.algs.book.ch2;

import com.cuongd.study.algs.book.ch1.MyStack;
import edu.princeton.cs.algs4.StdRandom;

import java.lang.reflect.InvocationTargetException;

/* Nonrecursive quicksort */
public class Ex2320 extends SortCommon {
    @Override
    public void sort(Comparable[] a) {
        StdRandom.shuffle(a);
        MyStack<Integer> sortStack = new MyStack<>();
        sortStack.push(0);
        sortStack.push(a.length - 1);
        while (!sortStack.isEmpty()) {
            int hi = sortStack.pop(), lo = sortStack.pop();

            if (hi <= lo) continue;
            Comparable key = a[lo];
            int i = lo, j = hi + 1;
            while (true) {
                while (less(a[++i], key)) if (i == hi) break;
                while (less(key, a[--j])) if (j == lo) break;
                if (i >= j) break;
                exchange(a, i, j);
            }
            exchange(a, lo, j);

            int lo1, hi1, lo2, hi2;
            if (hi - (j+1) > (j-1) - lo) {
                lo1 = j+1;
                hi1 = hi;
                lo2 = lo;
                hi2 = j-1;
            } else {
                lo1 = lo;
                hi1 = j-1;
                lo2 = j+1;
                hi2 = hi;
            }
            sortStack.push(lo1);
            sortStack.push(hi1);
            sortStack.push(lo2);
            sortStack.push(hi2);
        }
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        run(QuickSort.class);
    }
}
