package com.cuongd.study.algsp1.book.ch1;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Ex1339Test {
    private Ex1339<Integer> ring;

    @Before
    public void setup() {
        ring = new Ex1339<>();
    }

    @Test
    public void testPutGetStraight() {
        ring.put(1);
        ring.put(2);
        ring.put(3);
        ring.put(4);
        assertEquals(4, ring.size());
        assertEquals(1, (int) ring.get());
        assertEquals(2, (int) ring.get());
        assertEquals(3, (int) ring.get());
        assertEquals(4, (int) ring.get());
        assertTrue(ring.isEmpty());
    }

    @Test
    public void testPutGetMixed() {
        ring.put(1);
        assertEquals(1, (int) ring.get());
        ring.put(2);
        ring.put(3);
        assertEquals(2, (int) ring.get());
        assertEquals(3, (int) ring.get());
    }

    @Test
    public void testPutGetWrapAround() {
        ring.put(1);
        ring.put(2);
        ring.put(3);
        ring.put(4);
        assertEquals(1, (int) ring.get());
        assertEquals(2, (int) ring.get());
        ring.put(5);
        ring.put(6);
        assertEquals(3, (int) ring.get());
        assertEquals(4, (int) ring.get());
        assertEquals(5, (int) ring.get());
        ring.put(7);
        ring.put(8);
        ring.put(9);
        assertEquals(6, (int) ring.get());
        assertEquals(7, (int) ring.get());
        assertEquals(8, (int) ring.get());
        assertEquals(9, (int) ring.get());
    }
}
