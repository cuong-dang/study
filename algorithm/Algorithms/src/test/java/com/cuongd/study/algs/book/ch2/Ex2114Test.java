package com.cuongd.study.algs.book.ch2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex2114Test {
    @Test
    public void testCommonCase() {
        String[] a = new String[]{"E", "X", "A", "M", "P", "L", "E"};
        Ex2114.sort(a);
        assertEquals(new String[]{"A", "E", "E", "L", "M", "P", "X"}, a);
    }

    @Test
    public void testCommonCase2() {
        String[] a = new String[]{"S", "O", "R", "T", "E", "X", "A", "M", "P", "L", "E"};
        Ex2114.sort(a);
        assertEquals(new String[]{"A", "E", "E", "L", "M", "O", "P", "R", "S", "T", "X"}, a);
    }

    @Test
    public void testMinAtLast() {
        String[] a = new String[]{"E", "X", "E", "M", "P", "L", "A"};
        Ex2114.sort(a);
        assertEquals(new String[]{"A", "E", "E", "L", "M", "P", "X"}, a);
    }
}
