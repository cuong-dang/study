package com.cuongd.study.algs.coursera1.wk3;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FastCollinearPointsTest {
    @Test
    public void testVerticalPoints() {
        Point p1 = new Point(1, 0);
        Point p2 = new Point(1, 1);
        Point p3 = new Point(1, 2);
        Point p4 = new Point(1, 3);

        FastCollinearPoints fast = new FastCollinearPoints(new Point[]{p1, p2, p3, p4});
        assertEquals(1, fast.numberOfSegments());
    }
}
