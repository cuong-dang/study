package com.cuongd.study.algsp1.coursera.wk3;

import java.util.Arrays;

public class FastCollinearPoints {
    private int numberOfSegments;
    private final LineSegment[] segments;

    public FastCollinearPoints(Point[] points) {
        /* corner cases */
        if (points == null)
            throw new IllegalArgumentException();
        int n = points.length;
        for (int i = 0; i < n; ++i)
            for (int j = i+1; j < n; ++j)
                if (points[i] == null || points[j] == null ||
                        points[i].compareTo(points[j]) == 0)
                    throw new IllegalArgumentException();

        /* main */
        numberOfSegments = 0;
        segments = new LineSegment[points.length * points.length];
        for (int i = 0; i < n; ++i) {
            /* sort other points by slope to current point */
            Point[] otherPoints = new Point[n-1];
            for (int j = 0, k = 0; j < n; ++j) {
                if (j == i)
                    continue;
                otherPoints[k++] = points[j];
            }
            Arrays.sort(otherPoints, points[i].slopeOrder());

            /* examine collinear points */
            Point[] collinear = new Point[n];
            collinear[0] = otherPoints[0];
            int numCollinear = 1;
            for (int curr = 1, prev = 0; curr < n-1; ++curr, ++prev) {
                double currSlope = points[i].slopeTo(otherPoints[curr]),
                        prevSlope = points[i].slopeTo(otherPoints[prev]);
                if (currSlope == prevSlope) {
                    collinear[numCollinear++] = otherPoints[curr];
                    continue;
                }
                if (numCollinear < 3) {
                    numCollinear = 0;
                    collinear[numCollinear++] = otherPoints[curr];
                    continue;
                }
                collinear[numCollinear++] = points[i];
                Arrays.sort(collinear, 0, numCollinear);
                segments[numberOfSegments++] = new LineSegment(collinear[0],
                        collinear[numCollinear-1]);
            }
        }
    }

    public int numberOfSegments() {
        return numberOfSegments;
    }

    public LineSegment[] segments() {
        LineSegment[] r = new LineSegment[numberOfSegments];
        System.arraycopy(segments, 0, r, 0, numberOfSegments);
        return r;
    }
}
