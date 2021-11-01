package com.cuongd.study.algsp1.book.ch2;

import com.cuongd.study.algsp1.book.ch1.MyQueue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex2215Test {
    @Test
    public void testBuqMergeSort() {
        Integer[] a = {2, 5, 0, 7, 6, 3, 4, 9, 8, 1};
        MyQueue<Comparable> q = Ex2215.buqMergeSort(a);
        for (int i = 0; i < 10; ++i)
            assertEquals(i, q.dequeue());
    }
}
