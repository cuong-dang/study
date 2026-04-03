package com.cuongd.study.algs.book.ch1;

import org.junit.Test;

import static com.cuongd.study.algs.book.ch1.Ex1420.bitonicContains;
import static com.cuongd.study.algs.book.ch1.Ex1420.findBitonicSeparator;
import static org.junit.Assert.*;

public class Ex1420Test {
    @Test
    public void testFindBitonicSeparator() {
        assertEquals(2, findBitonicSeparator(new int[]{0, 1, 4, 3, 2}));
        assertEquals(3, findBitonicSeparator(new int[]{0, 1, 2, 4, 3}));
    }

    @Test
    public void testBitonicContains() {
        assertTrue(bitonicContains(new int[]{0, 1, 4, 3, 2}, 0));
        assertTrue(bitonicContains(new int[]{0, 1, 4, 3, 2}, 1));
        assertTrue(bitonicContains(new int[]{0, 1, 4, 3, 2}, 4));
        assertTrue(bitonicContains(new int[]{0, 1, 4, 3, 2}, 3));
        assertTrue(bitonicContains(new int[]{0, 1, 4, 3, 2}, 2));
        assertFalse(bitonicContains(new int[]{0, 1, 4, 3, 2}, 5));
    }

    @Test
    public void testBitonicContains2() {
        assertTrue(bitonicContains(new int[]{0, 1, 2, 4, 3}, 0));
        assertTrue(bitonicContains(new int[]{0, 1, 2, 4, 3}, 1));
        assertTrue(bitonicContains(new int[]{0, 1, 2, 4, 3}, 4));
        assertTrue(bitonicContains(new int[]{0, 1, 4, 3, 2}, 3));
        assertTrue(bitonicContains(new int[]{0, 1, 2, 4, 3}, 2));
        assertFalse(bitonicContains(new int[]{0, 1, 2, 4, 3}, 5));
    }
}
