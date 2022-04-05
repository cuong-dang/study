package com.cuongd.study.algsp1.book.ch2;

import com.cuongd.study.algsp1.book.ch1.MyQueue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex2214Test {
    MyQueue<Comparable> q1 = new MyQueue<>();
    MyQueue<Comparable> q2 = new MyQueue<>();

    @Test
    public void testMergeQueues() {
        q1.enqueue(3); q1.enqueue(5); q1.enqueue(6); q1.enqueue(8); q1.enqueue(9);
        q2.enqueue(0); q2.enqueue(1); q2.enqueue(2); q2.enqueue(4); q2.enqueue(7);
        MyQueue<Comparable> result = Ex2214.mergeQueues(q1, q2);
        for (int i = 0; i < 10; ++i)
            assertEquals(i, result.dequeue());
    }
}
