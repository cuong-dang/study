package com.cuongd.study.algs.book.ch2;

import com.cuongd.study.algs.book.ch1.MyQueue;

public class Ex2214 extends SortCommon {
    public static MyQueue<Comparable> mergeQueues(MyQueue<Comparable> q1, MyQueue<Comparable> q2) {
        MyQueue<Comparable> result = new MyQueue<>();
        Comparable i1 = null, i2 = null;
        while (true) {
            if (q1.isEmpty() && i1 == null) {
                if (i2 != null) result.enqueue(i2);
                while (!q2.isEmpty()) result.enqueue(q2.dequeue());
                break;
            }
            else if (q2.isEmpty() && i2 == null) {
                if (i1 != null) result.enqueue(i1);
                while (!q1.isEmpty()) result.enqueue(q1.dequeue());
                break;
            }
            else {
                if (i1 == null) i1 = q1.dequeue();
                if (i2 == null) i2 = q2.dequeue();
                if (less(i1, i2)) {
                    result.enqueue(i1);
                    i1 = null;
                } else {
                    result.enqueue(i2);
                    i2 = null;
                }
            }
        }
        return result;
    }

    @Override
    public void sort(Comparable[] a) { }
}
