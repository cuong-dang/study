package com.cuongd.study.algsp1.coursera.wk3;

import java.util.Arrays;
import java.util.Comparator;

public class BruteCollinearPoints {
    private int numberOfSegments;
    private final LineSegment[] segments;

    public BruteCollinearPoints(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException();
        int n = points.length;
        for (int i = 0; i < n; ++i)
            for (int j = i+1; j < n; ++j)
                if (points[i] == null || points[j] == null ||
                        points[i].compareTo(points[j]) == 0)
                    throw new IllegalArgumentException();

        numberOfSegments = 0;
        segments = new LineSegment[points.length * points.length];

        for (int i1 = 0; i1 < n; ++i1)
            for (int i2 = i1+1; i2 < n; ++i2)
                for (int i3 = i2+1; i3 < n; ++i3)
                    for (int i4 = i3+1; i4 < n; ++i4) {
                        double slopeP1P2 = points[i1].slopeTo(points[i2]),
                                slopeP1P3 = points[i1].slopeTo(points[i3]);
                        if (slopeP1P2 != slopeP1P3)
                            continue;
                        double slopeP1P4 = points[i1].slopeTo(points[i4]);
                        if (slopeP1P2 != slopeP1P4)
                            continue;
                        Point[] forSort =
                                new Point[]{points[i1], points[i2], points[i3], points[i4]};
                        Arrays.sort(forSort, new PointComparator());
                        segments[numberOfSegments++] = new LineSegment(forSort[0], forSort[3]);
                    }
    }

    public int numberOfSegments() {
        return numberOfSegments;
    }

    public LineSegment[] segments() {
        return segments;
    }

    private static class PointComparator implements Comparator<Point> {
        @Override
        public int compare(Point p1, Point p2) {
            return p1.compareTo(p2);
        }
    }
}
