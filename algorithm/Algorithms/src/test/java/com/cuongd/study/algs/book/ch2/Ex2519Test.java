package com.cuongd.study.algs.book.ch2;

import org.junit.Test;

import static com.cuongd.study.algs.book.ch2.Ex2519.kendallTau;
import static org.junit.Assert.assertEquals;

public class Ex2519Test {
    @Test
    public void testKt1() {
        Integer[] a = new Integer[]{2, 0, 1, 3, 4, 5, 6};
        assertEquals(2, kendallTau(a));
    }

    @Test
    public void testKt2() {
        Integer[] a = new Integer[]{2, 3, 4, 0, 1, 5, 6};
        assertEquals(6, kendallTau(a));
    }

    @Test
    public void testKt3() {
        Integer[] a = new Integer[]{2, 4, 3, 0, 1, 5, 6};
        assertEquals(7, kendallTau(a));
    }

    @Test
    public void testKt4() {
        Integer[] a = new Integer[]{0, 1, 2, 5, 6, 4, 3};
        assertEquals(5, kendallTau(a));
    }
}
