package com.cuongd.study.algs.book.ch1;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cuongd.study.algs.book.ch1.Ex1412.findIntersects;
import static org.junit.Assert.assertEquals;

public class Ex1412Test {
    @Test
    public void testBothEmpty() {
        assertEquals(new ArrayList<>(),
                findIntersects(new int[]{}, new int[]{}));
    }

    @Test
    public void testOneEmpty() {
        assertEquals(new ArrayList<>(),
                findIntersects(new int[]{1, 2, 3}, new int[]{}));
    }

    @Test
    public void testNoIntersects() {
        assertEquals(new ArrayList<>(),
                findIntersects(new int[]{1, 2, 3}, new int[]{4, 5, 6}));
    }

    @Test
    public void testOneIntersect() {
        assertEquals(List.of(1),
                findIntersects(new int[]{1, 2, 3}, new int[]{1, 4, 5}));
    }

    @Test
    public void testOneIntersectDifferentPos() {
        assertEquals(List.of(4),
                findIntersects(new int[]{1, 4, 5}, new int[]{2, 4, 6}));
    }

    @Test
    public void testMultipleIntersects() {
        assertEquals(Arrays.asList(1, 5, 7, 9),
                findIntersects(new int[]{1, 3, 5, 7, 9},
                        new int[]{1, 2, 4, 5, 7, 8, 9}));
    }

    @Test
    public void testMultipleIntersectsDuplicates() {
        assertEquals(Arrays.asList(1, 5, 7, 9),
                findIntersects(new int[]{1, 1, 3, 5, 7, 7, 7, 9},
                        new int[]{1, 2, 2, 4, 5, 5, 7, 8, 9, 9, 9}));
    }
}
