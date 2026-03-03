package com.cuongd.study.algs.book.ch2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex2430Test {
    @Test
    public void testInserts() {
        Ex2430 medPq = new Ex2430();
        medPq.insert(8);
        assertEquals(8, medPq.median(), 0.000001);
        medPq.insert(4);
        assertEquals(6, medPq.median(), 0.000001);
        medPq.insert(2);
        assertEquals(4, medPq.median(), 0.000001);
        medPq.insert(1);
        assertEquals(3, medPq.median(), 0.000001);
        medPq.insert(3);
        assertEquals(3, medPq.median(), 0.000001);
        medPq.insert(7);
        assertEquals(3.5, medPq.median(), 0.000001);
        medPq.insert(9);
        assertEquals(4, medPq.median(), 0.000001);
        medPq.insert(0);
        assertEquals(3.5, medPq.median(), 0.000001);
        medPq.insert(6);
        assertEquals(4, medPq.median(), 0.000001);
        medPq.insert(5);
        assertEquals(4.5, medPq.median(), 0.000001);
    }

    @Test
    public void testDelMedians() {
        Ex2430 medPq = new Ex2430();
        medPq.insert(5);
        medPq.insert(3);
        medPq.delMedian();
        assertEquals(3, medPq.median(), 0.000001);
        medPq.insert(4);
        medPq.insert(8);
        assertEquals(4, medPq.median(), 0.000001);
        medPq.delMedian();
        assertEquals(5.5, medPq.median(), 0.000001);
    }
}
