package com.cuongd.study.algsp1.book.ch2;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/** Dequeue sort */
public class Ex2114 {
    public static void sort(String[] a) {
        Deque<String> dq = new ArrayDeque<>(Arrays.asList(a));
        int sortedCount = 0, isMinCanaryCount = 0;
        String minCanary = "";
        while (sortedCount < dq.size() - 1) {
            sortedCount = 0;
            String last = dq.removeLast(), nextToLast = dq.removeLast();
            if (last.compareTo(nextToLast) < 0) {
                if (minCanary.isEmpty() && ++isMinCanaryCount == a.length - 1) {
                    /* Handle case where the min element is at last. */
                    minCanary = last;
                    dq.addFirst(last);
                    dq.addLast(nextToLast);
                } else {
                    dq.addLast(last);
                    dq.addFirst(nextToLast);
                }
            } else {
                if (minCanary.isEmpty())
                    minCanary = nextToLast;
                dq.addLast(nextToLast);
                dq.addLast(last);
                while (!(last = dq.removeLast()).equals(minCanary) &&
                        last.compareTo(dq.peekLast()) >= 0) {
                    ++sortedCount;
                    dq.addFirst(last);
                }
                if (last.equals(minCanary)) {
                    ++sortedCount;
                    dq.addFirst(last);
                }
                else
                    dq.addLast(last);
                if (sortedCount == a.length - 1)
                    break;
            }
        }
        for (int i = 0; i < a.length; i++)
            a[i] = dq.removeFirst();
    }
}
