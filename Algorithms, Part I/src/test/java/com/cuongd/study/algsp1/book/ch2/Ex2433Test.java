package com.cuongd.study.algsp1.book.ch2;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class Ex2433Test {
    private Ex2433<Integer> ipq;

    @Before
    public void init() {
        ipq = new Ex2433<>(10);
    }

    @Test
    public void testInsert() {
        ipq.insert(1, 0);
        assertTrue(ipq.contains(1));
        assertEquals(0, (int) ipq.keyOf(1));
        assertEquals(1, ipq.minIndex());
        assertEquals(0, (int) ipq.minKey());

        ipq.insert(2, 3);
        assertTrue(ipq.contains(2));
        assertEquals(3, (int) ipq.keyOf(2));
        assertEquals(1, ipq.minIndex());
        assertEquals(0, (int) ipq.minKey());

        ipq.insert(3, -1);
        assertEquals(3, ipq.minIndex());
        assertEquals(-1, (int) ipq.minKey());
    }

    @Test
    public void testChangeKey() {
        ipq.insert(1, 0);
        ipq.changeKey(1, 2);
        assertTrue(ipq.contains(1));
        assertEquals(2, (int) ipq.keyOf(1));
        assertEquals(1, ipq.minIndex());
        assertEquals(2, (int) ipq.minKey());

        ipq.insert(3, 4);
        ipq.changeKey(3, 0);
        assertEquals(3, ipq.minIndex());
        assertEquals(0, (int) ipq.minKey());
    }

    @Test
    public void testDelete() {
        ipq.insert(1, 0);
        ipq.delete(1);
        assertFalse(ipq.contains(1));

        ipq.insert(1, 0);
        assertTrue(ipq.contains(1));
        ipq.insert(2, 1);
        assertEquals(1, ipq.minIndex());
        assertEquals(0, (int) ipq.minKey());

        ipq.delete(1);
        assertEquals(2, ipq.minIndex());
        assertEquals(1, (int) ipq.minKey());
    }

    @Test
    public void testDelMin() {
        ipq.insert(5, 6);
        ipq.insert(4, 8);
        assertEquals(5, ipq.minIndex());
        assertEquals(6, (int) ipq.minKey());
        assertEquals(6, (int) ipq.delMin());
        assertFalse(ipq.contains(5));
        assertEquals(4, ipq.minIndex());
        assertEquals(8, (int) ipq.minKey());
        assertEquals(8, (int) ipq.delMin());

        ipq.insert(0, 0);
        ipq.insert(1, 1);
        ipq.insert(2, 2);
        ipq.changeKey(0, 3);
        assertEquals(1, (int) ipq.delMin());
        assertEquals(2, (int) ipq.delMin());
        assertEquals(3, (int) ipq.delMin());

        ipq.insert(0, 0);
        ipq.insert(1, 1);
        ipq.insert(2, 2);
        ipq.delete(0);
        assertEquals(1, (int) ipq.delMin());
        assertEquals(2, (int) ipq.delMin());
    }
}
