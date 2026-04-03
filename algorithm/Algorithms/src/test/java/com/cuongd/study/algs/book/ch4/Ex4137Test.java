package com.cuongd.study.algs.book.ch4;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Ex4137Test {
    @Test
    public void test() {
        Ex4137 eg = new Ex4137(16);

        assertEquals(0, eg.numPoints());
        eg.addPoint(0, 0);
        assertEquals(1, eg.numPoints());
        assertTrue(eg.hasPoint(0, 0));
        eg.addLine(0, 0, 1, 1);
        assertEquals(2, eg.numPoints());
        assertTrue(eg.hasPoint(1, 1));
        assertTrue(eg.hasLine(0, 0, 1, 1));
    }
}
