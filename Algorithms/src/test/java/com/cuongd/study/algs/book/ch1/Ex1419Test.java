package com.cuongd.study.algs.book.ch1;

import org.junit.Test;

import static com.cuongd.study.algs.book.ch1.Ex1419.Index;
import static com.cuongd.study.algs.book.ch1.Ex1419.findLocalMin;
import static org.junit.Assert.assertEquals;

public class Ex1419Test {
    @Test
    public void testN2() {
        Index v = findLocalMin(new int[][]{
                new int[]{1, 2},
                new int[]{3, 4},
        });
        assertEquals(0, v.i);
        assertEquals(0, v.j);
    }

    @Test
    public void testN31() {
        Index v = findLocalMin(new int[][]{
                new int[]{1, 9, 5},
                new int[]{8, 7, 4},
                new int[]{2, 3, 6},
        });
        assertEquals(2, v.i);
        assertEquals(0, v.j);
    }

    @Test
    public void testN32() {
        Index v = findLocalMin(new int[][]{
                new int[]{1, 8, 9},
                new int[]{7, 4, 6},
                new int[]{3, 2, 5},
        });
        assertEquals(2, v.i);
        assertEquals(1, v.j);
    }

    @Test
    public void testN5() {
        Index v = findLocalMin(new int[][]{
                new int[]{ 7, 13, 16,  9, 18},
                new int[]{22, 20,  4, 11, 14},
                new int[]{25,  8, 15,  2,  6},
                new int[]{24,  1, 10, 21, 19},
                new int[]{ 5, 12, 23,  3, 17},
        });
        assertEquals(2, v.i);
        assertEquals(3, v.j);
    }

    @Test
    public void testN7() {
        Index v = findLocalMin(new int[][]{
                new int[]{11, 25, 40, 39, 18, 46,  4},
                new int[]{ 5,  6, 27, 29, 48, 28,  1},
                new int[]{13, 45, 37, 33,  3,  2, 35},
                new int[]{43, 31, 49, 19, 22, 20,  7},
                new int[]{12,  8, 14, 24, 34, 38, 17},
                new int[]{32, 44, 47, 41, 21, 23, 42},
                new int[]{10, 30, 36, 26, 15,  9, 16},
        });
        assertEquals(3, v.i);
        assertEquals(6, v.j);
    }
}
