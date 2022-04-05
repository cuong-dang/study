package com.cuongd.study.algsp1.book.ch2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex243Test {
    @Test
    public void testUnorderedArray() {
        testCommon(1);
    }

    @Test
    public void testOrderedArray() {
        testCommon(2);
    }

    public void testCommon(int dataType) {
        Ex243<Integer> pq = new Ex243<>(dataType);
        pq.insert(1);
        pq.insert(7);
        assertEquals(7, (int) pq.removeMax());
        assertEquals(1, (int) pq.removeMax());
        pq.insert(4);
        pq.insert(6);
        pq.insert(8);
        assertEquals(8, (int) pq.removeMax());
        assertEquals(6, (int) pq.removeMax());
        pq.insert(0);
        assertEquals(4, (int) pq.removeMax());
        pq.insert(5);
        pq.insert(2);
        assertEquals(5, (int) pq.removeMax());
        assertEquals(2, (int) pq.removeMax());
        assertEquals(0, (int) pq.removeMax());
        pq.insert(3);
        assertEquals(3, (int) pq.removeMax());
        pq.insert(9);
        assertEquals(9, (int) pq.removeMax());
    }
}
