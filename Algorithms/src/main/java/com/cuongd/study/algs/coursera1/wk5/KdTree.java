package com.cuongd.study.algs.coursera1.wk5;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class KdTree {
    private Node root;

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size(root);
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        root = put(root, p, true);
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return get(root, p, true) != null;
    }

    public void draw() {
        draw(root, true, 1, true);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        List<Point2D> rv = new ArrayList<>();
        range(rv, rect, root, true, 0, 1, 0, 1);
        return rv;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (root == null) {
            return null;
        }
        return nearest(p, root, root.p, p.distanceSquaredTo(root.p),
                true, 0, 1, 0, 1);
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
        else {
            if (p.equals(h.p)) {
                return h;
            }
            h.right = put(h.right, p, !comparingX);
        }

        h.n = size(h.left) + size(h.right) + 1;
        return h;
    }

    private Node get(Node x, Point2D p, boolean comparingX) {
        if (x == null) return null;
        int cmp = cmp(p, x.p, comparingX);
        if (cmp < 0) return get(x.left, p, !comparingX);
        else {
            if (p.equals(x.p)) {
                return x;
            }
            return get(x.right, p, !comparingX);
        }
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

    private void range(List<Point2D> rv, RectHV rect, Node x, boolean isVert,
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
        if (x == null) {
            return currNearest;
        }
        double thisDist = p.distanceSquaredTo(x.p);
        if (thisDist < currDist) {
            currNearest = x.p;
            currDist = thisDist;
        }
        if (x.left == null && x.right == null) {
            return currNearest;
        }
        List<RectHV> leftRightRects = leftRightRects(x, isVert,
                xMin, xMax, yMin, yMax);
        RectHV leftRect = leftRightRects.get(0);
        RectHV rightRect = leftRightRects.get(1);
        Node tryFirst, tryLater;
        RectHV tryFirstRect, tryLaterRect;
        if ((isVert && x.p.x() > p.x()) || (!isVert && x.p.y() > p.y())) {
            tryFirst = x.left;
            tryLater = x.right;
            tryFirstRect = leftRect;
            tryLaterRect = rightRect;
        } else {
            tryFirst = x.right;
            tryLater = x.left;
            tryFirstRect = rightRect;
            tryLaterRect = leftRect;
        }
        if (tryFirst == null || tryLater == null) {
            Node oneTry = tryFirst == null ? tryLater : tryFirst;
            RectHV oneTryRect = tryFirst == null ? tryLaterRect : tryFirstRect;
            if (currDist <= oneTryRect.distanceSquaredTo(p)) {
                return currNearest;
            }
            Point2D newNearest = nearest(p, oneTry, oneTry.p, currDist, !isVert,
                    oneTryRect.xmin(), oneTryRect.xmax(),
                    oneTryRect.ymin(), oneTryRect.ymax());
            return p.distanceSquaredTo(newNearest) < currDist ?
                    newNearest : currNearest;
        }
        Point2D newNearest = nearest(p, tryFirst, currNearest, currDist,
                !isVert, tryFirstRect.xmin(), tryFirstRect.xmax(),
                tryFirstRect.ymin(), tryFirstRect.ymax());
        double newNearestDist = p.distanceSquaredTo(newNearest);
        if (newNearestDist < currDist) {
            currNearest = newNearest;
            currDist = newNearestDist;
        }
        if (currDist <= tryLaterRect.distanceSquaredTo(p)) {
            return newNearest;
        }
        Point2D newNearest2 = nearest(p, tryLater, currNearest, currDist,
                !isVert, tryLaterRect.xmin(), tryLaterRect.xmax(),
                tryLaterRect.ymin(), tryLaterRect.ymax());
        double newNearest2Dist = p.distanceSquaredTo(newNearest2);
        return newNearest2Dist < currDist ? newNearest2 : currNearest;
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
            leftYMin = yMin;
            rightYMin = yMin;
            leftYMax = yMax;
            rightYMax = yMax;
        } else {
            leftYMin = yMin;
            leftYMax = x.p.y();
            rightYMin = x.p.y();
            rightYMax = yMax;
            leftXMin = xMin;
            rightXMin = xMin;
            leftXMax = xMax;
            rightXMax = xMax;
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
        StdDraw.setPenRadius(0.005);
        Point2D a, b, c, d, e, f, g, h, i, j;
        /* Test set 1 */
        KdTree t = new KdTree();
        a = new Point2D(0.7, 0.2);
        b = new Point2D(0.5, 0.4);
        c = new Point2D(0.2, 0.3);
        d = new Point2D(0.4, 0.7);
        e = new Point2D(0.9, 0.6);
        t.insert(a);
        t.insert(b);
        t.insert(c);
        t.insert(d);
        t.insert(e);
        t.draw();
        RectHV rect = new RectHV(0.1, 0.1, 0.6, 0.8);
        List<Point2D> expected = new ArrayList<>();
        expected.add(b);
        expected.add(c);
        expected.add(d);
        assert (t.range(rect).equals(expected));
        assert (t.nearest(new Point2D(0.1, 0.1)).equals(c));
        StdDraw.clear();

        /* Test set 2 */
        t = new KdTree();
        a = new Point2D(0.5, 0.5);
        b = new Point2D(0.25, 0.625);
        c = new Point2D(0.5, 0.375);
        t.insert(a);
        t.insert(b);
        t.insert(c);
        t.insert(a);
        t.draw();
        assert (t.size() == 3);
        assert (t.contains(a));
        assert (!t.contains(new Point2D(0.2, 0.625)));
        StdDraw.clear();

        /* Test set 2 */
        t = new KdTree();
        a = new Point2D(0.375, 0.1875);
        b = new Point2D(0.25, 1);
        c = new Point2D(1.0, 0.9375);
        d = new Point2D(0.1875, 0.125);
        e = new Point2D(0.8125, 0.625);
        f = new Point2D(0.5, 0.875);
        g = new Point2D(0.4375, 0.5);
        h = new Point2D(0.125, 0.4375);
        i = new Point2D(0.9375, 0.8125);
        j = new Point2D(0.75, 0.25);
        t.insert(a);
        t.insert(b);
        t.insert(c);
        t.insert(d);
        t.insert(e);
        t.insert(f);
        t.insert(g);
        t.insert(h);
        t.insert(i);
        t.insert(j);
        t.draw();
        (new Point2D(0.5625, 0.0625)).draw();
        assert (t.nearest(new Point2D(0.5625, 0.0625)).equals(a));
    }
}
