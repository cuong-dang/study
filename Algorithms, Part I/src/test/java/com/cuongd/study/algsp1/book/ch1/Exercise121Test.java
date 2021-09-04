package com.cuongd.study.algsp1.book.ch1;

import edu.princeton.cs.algs4.Point2D;
import org.junit.Test;

import static com.cuongd.study.algsp1.book.ch1.Exercise121.minDistance;
import static org.junit.Assert.assertEquals;

public class Exercise121Test {
    private static final Point2D P1 = new Point2D(1, 2);
    private static final Point2D P2 = new Point2D(4, 6);
    private static final Point2D P3 = new Point2D(1, 3);
    private static final Point2D P4 = new Point2D(3, 2);

    @Test
    public void testMinDistanceTwoDiffPoints() {
        assertEquals(5, (int) minDistance(new Point2D[]{P1, P2}));
    }

    @Test
    public void testMinDistanceTwoSamePoints() {
        assertEquals(0, (int) minDistance(new Point2D[]{P1, P1}));
    }

    @Test
    public void testMinDistanceCommonCase() {
        assertEquals(1,
                (int) minDistance(new Point2D[]{P1, P2, P3, P4}));
    }
}
