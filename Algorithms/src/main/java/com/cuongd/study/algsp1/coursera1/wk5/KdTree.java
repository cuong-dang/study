package com.cuongd.study.algsp1.coursera1.wk5;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.SET;

public class KdTree {
    private final SET<Point2DC> st;
    private boolean isRed;

    public KdTree() {
        st = new SET<>();
        isRed
    }

    public boolean isEmpty() {
        return st.isEmpty();
    }

    public int size() {
        return st.size();
    }

    void insert()

    private static final class Point2DC implements Comparable<Point2DC> {
        private final Point2D p;
        private final Color c;

        public Point2DC(Point2D p, Color c) {
            this.p = p;
            this.c = c;
        }

        @Override
        public int compareTo(Point2DC that) {
            if (this.c == Color.RED) {
                return Double.compare(this.p.x(), that.p.x());
            }
            return Double.compare(this.p.y(), that.p.y());
        }
    }

    public enum Color {
        RED, BLUE
    }
}
