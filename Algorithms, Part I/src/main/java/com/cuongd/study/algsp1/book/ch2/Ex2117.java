package com.cuongd.study.algsp1.book.ch2;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;

import static com.cuongd.study.algsp1.book.ch2.SortCommon.less;

public class Ex2117 {
    private static final int CANVAS_WIDTH = 1024;
    private static final int CANVAS_HEIGHT = 512;
    private static final int DRAW_PAUSE = 10;
    private static final double BAR_WIDTH = 0.007;
    private static final double BAR_HEIGHT_SCALE = 0.5;
    private static final double BAR_PADDING = BAR_WIDTH/10;
    private static final int N = 128;

    private static void sort(double[] a) {
        /* Shell short */
        show(a);
        int n = a.length;
        int h = 1;
        while (h < n/3) h = 3*h + 1;
        while (h >= 1) {
            for (int i = h; i < n; i++) {
                for (int j = i; j >= h && less(a[j], a[j-h]); j -= h) {
                    exchange(a, j, j - h);
                    show(a);
                }
            }
            h /= 3;
        }
    }

    private static void exchange(double[] a, int i, int j) {
        double t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    private static void show(double[] a) {
        StdDraw.clear();
        for (int i = 0; i < a.length; ++i)
            StdDraw.filledRectangle(
                    BAR_WIDTH/2 + i*(BAR_WIDTH + BAR_PADDING), BAR_HEIGHT_SCALE*a[i]/2,
                    BAR_WIDTH/2, BAR_HEIGHT_SCALE*a[i]);
        StdDraw.show();
        StdDraw.pause(DRAW_PAUSE);
    }

    public static void main(String[] args) {
        double[] a = new double[N];
        StdDraw.setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        StdDraw.setPenColor(StdDraw.DARK_GRAY);
        StdDraw.enableDoubleBuffering();
        for (int i = 0; i < N; ++i)
            a[i] = StdRandom.uniform();
        sort(a);
    }
}
