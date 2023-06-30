package com.cuongd.study.algsp1.coursera1.wk5;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

import java.util.ArrayList;
import java.util.List;

public class PointSET {
    private final SET<Point2D> st;

    public PointSET() {
        st = new SET<>();
    }

    public boolean isEmpty() {
        return st.isEmpty();
    }

    public int size() {
        return st.size();
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        st.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return st.contains(p);
    }

    public void draw() {
        for (Point2D p : st) {
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        List<Point2D> rv = new ArrayList<>();
        for (Point2D p : st) {
            if (rect.contains(p)) {
                rv.add(p);
            }
        }
        return rv;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        Point2D rv = null;
        for (Point2D pp : st) {
            if (rv == null) {
                rv = pp;
            } else {
                if (pp.distanceTo(p) < rv.distanceTo(p)) {
                    rv = pp;
                }
            }
        }
        return rv;
    }

    public static void main(String[] args) {
        PointSET ps = new PointSET();
        ps.insert(new Point2D(0.5, 0.5));
        ps.insert(new Point2D(0.7, 0.3));
        ps.draw();
    }
}
