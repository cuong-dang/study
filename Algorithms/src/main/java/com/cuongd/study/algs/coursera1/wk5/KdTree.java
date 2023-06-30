package com.cuongd.study.algs.coursera1.wk5;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class KdTree {
    private Node root;

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size(root);
    }

    public void insert(Point2D p) {
        root = put(root, p, true);
    }

    public boolean contains(Point2D p) {
        return get(root, p, true) != null;
    }

    public void draw() {
        draw(root, true, 1, true);
    }

    public Iterable<Point2D> range(RectHV rect) {
        Set<Point2D> rv = new HashSet<>();
        range(rv, rect, root, true, 0, 1, 0, 1);
        return rv;
    }

    public Point2D nearest(Point2D p) {
        if (root == null) {
            return null;
        }
        return nearest(p, root, root.p, p.distanceTo(root.p), true, 0, 1, 0, 1);
    }

    private int size(Node x) {
        if (x == null) {
            return 0;
        }
        return x.n;
    }

    private Node put(Node h, Point2D p, boolean comparingX) {
        if (h == null) return new Node(p, 1);

        int cmp = cmp(p, h.p, comparingX);
        if (cmp < 0) h.left = put(h.left, p, !comparingX);
        else if (cmp > 0) h.right = put(h.right, p, !comparingX);

        h.n = size(h.left) + size(h.right) + 1;
        return h;
    }

    private Node get(Node x, Point2D p, boolean comparingX) {
        if (x == null) return null;
        int cmp = cmp(p, x.p, comparingX);
        if (cmp < 0) return get(x.left, p, !comparingX);
        if (cmp > 0) return get(x.right, p, !comparingX);
        return x;
    }

    private void draw(Node x, boolean drawingVertical,
                      double boundary, boolean drawingTo) {
        double nextBoundary;
        if (x == null) {
            return;
        }
        if (drawingVertical) {
            StdDraw.setPenColor(Color.RED);
            if (drawingTo) {
                StdDraw.line(x.p.x(), 0, x.p.x(), boundary);
            } else {
                StdDraw.line(x.p.x(), boundary, x.p.x(), 1);
            }
            nextBoundary = x.p.x();
        } else {
            StdDraw.setPenColor(Color.BLUE);
            if (drawingTo) {
                StdDraw.line(0, x.p.y(), boundary, x.p.y());
            } else {
                StdDraw.line(boundary, x.p.y(), 1, x.p.y());
            }
            nextBoundary = x.p.y();
        }
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(4 * StdDraw.getPenRadius());
        x.p.draw();
        StdDraw.setPenRadius(StdDraw.getPenRadius() / 4);
        draw(x.left, !drawingVertical, nextBoundary, true);
        draw(x.right, !drawingVertical, nextBoundary, false);
    }

    private int cmp(Point2D x, Point2D y, boolean comparingX) {
        if (comparingX) {
            return Double.compare(x.x(), y.x());
        }
        return Double.compare(x.y(), y.y());
    }

    private void range(Set<Point2D> rv, RectHV rect, Node x, boolean isVert,
                       double xMin, double xMax, double yMin, double yMax) {
        if (x == null) {
            return;
        }
        if (rect.contains(x.p)) {
            rv.add(x.p);
        }
        List<RectHV> leftRightRects = leftRightRects(x, isVert,
                xMin, xMax, yMin, yMax);
        RectHV leftRect = leftRightRects.get(0);
        RectHV rightRect = leftRightRects.get(1);
        if (rect.intersects(leftRect)) {
            range(rv, rect, x.left, !isVert,
                    leftRect.xmin(), leftRect.xmax(),
                    leftRect.ymin(), leftRect.ymax());
        }
        if (rect.intersects(rightRect)) {
            range(rv, rect, x.right, !isVert,
                    rightRect.xmin(), rightRect.xmax(),
                    rightRect.ymin(), rightRect.ymax());
        }
    }

    private Point2D nearest(Point2D p, Node x, Point2D currNearest,
                            double currDist, boolean isVert, double xMin,
                            double xMax, double yMin, double yMax) {
        if (x == null || (x.left == null && x.right == null)) {
            return currNearest;
        }
        List<RectHV> leftRightRects = leftRightRects(x, isVert,
                xMin, xMax, yMin, yMax);
        RectHV leftRect = leftRightRects.get(0);
        RectHV rightRect = leftRightRects.get(1);
        Node tryFirst, tryLater;
        RectHV tryFirstRect, tryLaterRect;
        if (leftRect.contains(p)) {
            tryFirst = x.left;
            tryLater = x.right;
            tryFirstRect = leftRect;
            tryLaterRect = rightRect;
        } else {
            tryFirst = x.right;
            tryLater = x.left;
            tryFirstRect = leftRect;
            tryLaterRect = rightRect;
        }
        if (tryFirst == null || tryLater == null) {
            Node oneTry = tryFirst == null ? tryLater : tryFirst;
            RectHV oneTryRect = tryFirst == null ? tryLaterRect : tryFirstRect;
            if (currDist < oneTryRect.distanceTo(p)) {
                return currNearest;
            }
            Point2D newNearest = nearest(p, oneTry, oneTry.p,
                    p.distanceTo(oneTry.p), !isVert,
                    oneTryRect.xmin(), oneTryRect.xmax(),
                    oneTryRect.ymin(), oneTryRect.ymax());
            return p.distanceTo(newNearest) < currDist ?
                    newNearest : currNearest;
        }
        Point2D newNearest = nearest(p, tryFirst, tryFirst.p,
                p.distanceTo(tryFirst.p), !isVert,
                tryFirstRect.xmin(), tryFirstRect.xmax(),
                tryFirstRect.ymin(), tryFirstRect.ymax());
        double newNearestDist = p.distanceTo(newNearest);
        if (newNearestDist <= tryLaterRect.distanceTo(p)) {
            return newNearest;
        }
        Point2D newNearest2 = nearest(p, tryLater, tryLater.p,
                p.distanceTo(tryLater.p), !isVert,
                tryLaterRect.xmin(), tryLaterRect.xmax(),
                tryLaterRect.ymin(), tryLaterRect.ymax());
        double newNearest2Dist = p.distanceTo(newNearest2);
        Point2D finalNewNearest = newNearestDist < newNearest2Dist ?
                newNearest : newNearest2;
        double finalNewNearestDist = Math.min(newNearestDist, newNearest2Dist);
        return finalNewNearestDist < currDist ? finalNewNearest : currNearest;
    }

    private List<RectHV> leftRightRects(Node x, boolean isVert, double xMin,
                                        double xMax, double yMin, double yMax) {
        List<RectHV> rv = new ArrayList<>();
        double leftXMin, leftXMax, leftYMin, leftYMax;
        double rightXMin, rightXMax, rightYMin, rightYMax;
        if (isVert) {
            leftXMin = xMin;
            leftXMax = x.p.x();
            rightXMin = x.p.x();
            rightXMax = xMax;
            leftYMin = rightYMin = yMin;
            leftYMax = rightYMax = yMax;
        } else {
            leftYMin = yMin;
            leftYMax = x.p.y();
            rightYMin = x.p.y();
            rightYMax = yMax;
            leftXMin = rightXMin = xMin;
            leftXMax = rightXMax = xMax;
        }
        RectHV leftRect = new RectHV(leftXMin, leftYMin, leftXMax, leftYMax);
        RectHV rightRect = new RectHV(rightXMin, rightYMin,
                rightXMax, rightYMax);
        rv.add(leftRect);
        rv.add(rightRect);
        return rv;
    }

    private static class Node {
        private final Point2D p;
        private int n;
        private Node left, right;

        public Node(Point2D p, int n) {
            this.p = p;
            this.n = n;
        }
    }

    public static void main(String[] args) {
        Point2D a, b, c, d, e;
        a = new Point2D(0.7, 0.2);
        b = new Point2D(0.5, 0.4);
        c = new Point2D(0.2, 0.3);
        d = new Point2D(0.4, 0.7);
        e = new Point2D(0.9, 0.6);
        KdTree t = new KdTree();
        StdDraw.setPenRadius(0.005);
        t.insert(a);
        t.insert(b);
        t.insert(c);
        t.insert(d);
        t.insert(e);
        t.draw();

        RectHV rect = new RectHV(0.1, 0.1, 0.6, 0.8);
        Set<Point2D> expected = new HashSet<>();
        expected.add(b);
        expected.add(c);
        expected.add(d);
        assert (t.range(rect).equals(expected));

        assert (t.nearest(new Point2D(0.1, 0.1)).equals(c));
    }
}
