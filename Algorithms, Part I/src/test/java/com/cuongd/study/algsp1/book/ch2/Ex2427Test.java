package com.cuongd.study.algsp1.book.ch2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex2427Test {
    @Test
    public void testSimple() {
        MaxPQ<Integer> pq = new MaxPQ<>();
        pq.insert(2);
        assertEquals(2, (int) pq.min());
        pq.insert(1);
        assertEquals(1, (int) pq.min());
    }

    @Test
    public void testInsertOnly() {
        MaxPQ<Integer> pq = new MaxPQ<>();
        pq.insert(5);
        assertEquals(5, (int) pq.min());
        pq.insert(4);
        assertEquals(4, (int) pq.min());
        pq.insert(9);
        assertEquals(4, (int) pq.min());
        pq.insert(8);
        assertEquals(4, (int) pq.min());
        pq.insert(7);
        assertEquals(4, (int) pq.min());
        pq.insert(2);
        assertEquals(2, (int) pq.min());
        pq.insert(3);
        assertEquals(2, (int) pq.min());
        pq.insert(6);
        assertEquals(2, (int) pq.min());
        pq.insert(1);
        assertEquals(1, (int) pq.min());
        pq.insert(0);
        assertEquals(0, (int) pq.min());
    }

    @Test
    public void mixedOps() {
        MaxPQ<Integer> pq = new MaxPQ<>();
        pq.insert(8);
        pq.insert(4);
        assertEquals(4, (int) pq.min());
        pq.delMax();
        assertEquals(4, (int) pq.min());
        pq.delMax();
        pq.insert(3);
        assertEquals(3, (int) pq.min());
        pq.insert(1);
        assertEquals(1, (int) pq.min());
        pq.insert(0);
        assertEquals(0, (int) pq.min());
        pq.delMax();
        assertEquals(0, (int) pq.min());
        pq.delMax();
        assertEquals(0, (int) pq.min());
    }
}
