package com.cuongd.study.algs.book.ch1;

import org.junit.Test;

import static com.cuongd.study.algs.book.ch1.Ex1410.binSearch;
import static org.junit.Assert.assertEquals;

public class Ex1410Test {
    @Test
    public void testOneItem() {
        assertEquals(0, binSearch(new int[]{0}, 0));
    }

    @Test
    public void testTwoItems() {
        assertEquals(0, binSearch(new int[]{0, 1}, 0));
        assertEquals(1, binSearch(new int[]{0, 1}, 1));
        assertEquals(0, binSearch(new int[]{0, 0}, 0));
    }

    @Test
    public void testMoreThanTwoItems() {
        assertEquals(0, binSearch(new int[]{0, 1, 2, 3, 4, 5}, 0));
        assertEquals(1, binSearch(new int[]{0, 1, 2, 3, 4, 5}, 1));
        assertEquals(2, binSearch(new int[]{0, 1, 2, 3, 4, 5}, 2));
        assertEquals(3, binSearch(new int[]{0, 1, 2, 3, 4, 5}, 3));
        assertEquals(4, binSearch(new int[]{0, 1, 2, 3, 4, 5}, 4));
        assertEquals(5, binSearch(new int[]{0, 1, 2, 3, 4, 5}, 5));
    }

    @Test
    public void testMoreThanTwoItemsWithDuplicates() {
        assertEquals(0, binSearch(new int[]{0, 0, 0, 1, 1, 2, 3, 3, 3}, 0));
        assertEquals(3, binSearch(new int[]{0, 0, 0, 1, 1, 2, 3, 3, 3}, 1));
        assertEquals(5, binSearch(new int[]{0, 0, 0, 1, 1, 2, 3, 3, 3}, 2));
        assertEquals(6, binSearch(new int[]{0, 0, 0, 1, 1, 2, 3, 3, 3}, 3));
    }
}
