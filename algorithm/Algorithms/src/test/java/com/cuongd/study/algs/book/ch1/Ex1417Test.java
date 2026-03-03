package com.cuongd.study.algs.book.ch1;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static com.cuongd.study.algs.book.ch1.Ex1417.findFarthestPair;
import static org.junit.Assert.assertEquals;

public class Ex1417Test {
    @Test
    public void testTwoElements() {
        assertEquals(new HashSet<>(Arrays.asList(1.0, 2.0)),
                findFarthestPair(new double[]{1, 2}));
    }

    @Test
    public void testThreeElements() {
        assertEquals(new HashSet<>(Arrays.asList(1.0, 3.0)),
                findFarthestPair(new double[]{1, 2, 3}));
        assertEquals(new HashSet<>(Arrays.asList(1.0, 3.0)),
                findFarthestPair(new double[]{1, 3, 2}));
        assertEquals(new HashSet<>(Arrays.asList(1.0, 3.0)),
                findFarthestPair(new double[]{2, 1, 3}));
        assertEquals(new HashSet<>(Arrays.asList(1.0, 3.0)),
                findFarthestPair(new double[]{2, 3, 1}));
        assertEquals(new HashSet<>(Arrays.asList(1.0, 3.0)),
                findFarthestPair(new double[]{3, 1, 2}));
        assertEquals(new HashSet<>(Arrays.asList(1.0, 3.0)),
                findFarthestPair(new double[]{3, 2, 1}));
    }

    @Test
    public void testMoreThanThreeElements() {
        assertEquals(new HashSet<>(Arrays.asList(1.0, 4.0)),
                findFarthestPair(new double[]{3, 4, 1, 2}));
        assertEquals(new HashSet<>(Arrays.asList(-1.0, -5.0)),
                findFarthestPair(new double[]{-2, -4, -1, -3,- 5}));
        assertEquals(new HashSet<>(Arrays.asList(-1.0, -6.0)),
                findFarthestPair(new double[]{-6, -3, -2, -4, -1, -5}));
        assertEquals(new HashSet<>(Arrays.asList(-3.0, 3.0)),
                findFarthestPair(new double[]{1, 0, -1, 2, 3, -3, -2}));
    }
}
