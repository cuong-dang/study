package com.cuongd.study.algsp1.book.ch2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex2220Test {
    @Test
    public void testIndexSort() {
        Integer[] a = {9, 2, 5, 7, 3, 6, 4, 0, 1, 8};
        int[] expected = {7, 8, 1, 4, 6, 2, 5, 3, 9, 0};
        int[] actual = Ex2220.sort(a);
        for (int i = 0; i < expected.length; ++i)
            assertEquals(expected[i], actual[i]);
    }
}
