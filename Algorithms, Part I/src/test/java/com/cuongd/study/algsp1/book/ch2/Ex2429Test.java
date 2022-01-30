package com.cuongd.study.algsp1.book.ch2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex2429Test {
    @Test
    public void test1() {
        Ex2429<Integer> pq = new Ex2429<>();
        pq.insert(1);
        assertEquals(1, (int) pq.max());
        assertEquals(1, (int) pq.min());
        assertEquals(1, (int) pq.delMax());
        pq.insert(1);
        assertEquals(1, (int) pq.delMin());
    }

    @Test
    public void test2() {
        Ex2429<Integer> pq = new Ex2429<>();
        pq.insert(1);
        pq.insert(2);
        pq.insert(3);
        assertEquals(3, (int) pq.max());
        assertEquals(1, (int) pq.min());
        assertEquals(3, (int) pq.delMax());
        assertEquals(2, (int) pq.delMax());
        assertEquals(1, (int) pq.delMax());
    }

    @Test
    public void test3() {
        Ex2429<Integer> pq = new Ex2429<>();
        pq.insert(1);
        pq.insert(2);
        pq.insert(3);
        assertEquals(3, (int) pq.delMax());
        assertEquals(1, (int) pq.delMin());
        assertEquals(2, (int) pq.max());
        assertEquals(2, (int) pq.min());
        assertEquals(2, (int) pq.delMin());
    }
}
