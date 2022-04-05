package com.cuongd.study.algsp1.book.ch1;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Ex121 {
    public static void main(String[] args) {
        int n = StdIn.readInt();
        Point2D[] points = new Point2D[n];
        for (int i = 0; i < n; i++)
            points[i] = new Point2D(StdRandom.uniform(), StdRandom.uniform());
        StdOut.println(
                String.format("Min distance: %.2f", + minDistance(points)));
    }

    /**
     * Calculate the minimum distance between two points in an array of points.
     *
     * @param points array of Point2D points; must have more than one element
     * @return min distance between two points
     */
    public static double minDistance(Point2D[] points) {
        assert(points.length > 1);
        double dMin = Double.MAX_VALUE;

        for (int i = 1; i < points.length; i++)
            for (int j = 0; j < i; j++) {
                double dCurr = points[i].distanceTo(points[j]);
                if (dMin > dCurr)
                    dMin = dCurr;
            }
        return dMin;
    }
}
