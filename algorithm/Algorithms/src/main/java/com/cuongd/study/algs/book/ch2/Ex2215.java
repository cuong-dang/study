package com.cuongd.study.algs.book.ch2;

import com.cuongd.study.algs.book.ch1.MyQueue;

/** Bottom-up queue mergesort */
public class Ex2215 {
    public static MyQueue<Comparable> buqMergeSort(Comparable[] a) {
        MyQueue<MyQueue<Comparable>> qoq = new MyQueue<>();
        for (int i = 0; i < a.length; ++i) {
            MyQueue<Comparable> q = new MyQueue<>();
            q.enqueue(a[i]);
            qoq.enqueue(q);
        }
        while (qoq.size() != 1) {
            MyQueue<Comparable> q1 = qoq.dequeue(), q2 = qoq.dequeue();
            qoq.enqueue(Ex2214.mergeQueues(q1, q2));
        }
        return qoq.dequeue();
    }
}
