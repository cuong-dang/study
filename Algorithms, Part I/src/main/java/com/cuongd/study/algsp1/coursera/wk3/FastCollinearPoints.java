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
        for (Point point : points)
            if (point == null)
                throw new IllegalArgumentException();
        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j)
                if (points[i].compareTo(points[j]) == 0)
                    throw new IllegalArgumentException();
        }

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
            if (otherPoints.length < 3)
                break;
            Arrays.sort(otherPoints, points[i].slopeOrder());

            /* examine collinear points */
            Point[] collinear = new Point[n];
            int numCollinear = 0;
            collinear[numCollinear++] = otherPoints[0];
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
                /* to not have duplicates, only take if the pivot point is min */
                processCandidate(points[i], collinear, numCollinear);
                numCollinear = 0;
                collinear[numCollinear++] = otherPoints[curr];
            }
            // last
            if (numCollinear >= 3)
                processCandidate(points[i], collinear, numCollinear);
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

    private void processCandidate(Point pivot, Point[] collinear, int n) {
        boolean pivotIsMin = true;
        for (int j = 0; j < n; j++)
            if (pivot.compareTo(collinear[j]) > 0) {
                pivotIsMin = false;
                break;
            }
        if (pivotIsMin) {
            Arrays.sort(collinear, 0, n);
            segments[numberOfSegments++] = new LineSegment(pivot, collinear[n-1]);
        }
    }
}
