package com.cuongd.study.algs.book.ch2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex2221Test {
    @Test
    public void testTriplicates() {
        String[] a = {"3", "9", "0", "6", "1", "2", "8"};
        String[] b = {"8", "6", "5", "0", "7", "9", "1"};
        String[] c = {"2", "3", "1", "8", "0", "9", "6"};
        String[] expected = {"0", "1", "6", "8", "9"}, r;

        r = Ex2221.triplicates(a, b, c, a.length);
        for (int i = 0; i < expected.length; ++i)
            assertEquals(expected[i], r[i]);
    }
}
