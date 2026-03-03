package com.cuongd.study.algs.book.ch2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex2424Test {
    @Test
    public void testBasic() {
        Ex2424<Integer> pq = new Ex2424<>();
        pq.insert(1);
        assertEquals(1, (int) pq.delMax());
        pq.insert(2);
        pq.insert(3);
        assertEquals(3, (int) pq.delMax());
        assertEquals(2, (int) pq.delMax());
    }

    @Test
    public void testCommon() {
        Ex2424<Integer> pq = new Ex2424<>();
        pq.insert(4);
        pq.insert(3);
        pq.insert(6);
        pq.insert(7);
        pq.insert(5);
        assertEquals(7, (int) pq.delMax());
        assertEquals(6, (int) pq.delMax());
        assertEquals(5, (int) pq.delMax());
        pq.insert(2);
        pq.insert(9);
        assertEquals(9, (int) pq.delMax());
        assertEquals(4, (int) pq.delMax());
        pq.insert(0);
        pq.insert(1);
        pq.insert(8);
        assertEquals(8, (int) pq.delMax());
        assertEquals(3, (int) pq.delMax());
        assertEquals(2, (int) pq.delMax());
        assertEquals(1, (int) pq.delMax());
        assertEquals(0, (int) pq.delMax());
    }
}
