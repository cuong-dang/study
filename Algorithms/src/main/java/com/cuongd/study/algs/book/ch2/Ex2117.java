package com.cuongd.study.algs.book.ch2;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;

import static com.cuongd.study.algs.book.ch2.SortCommon.less;

public class Ex2117 {
    private static final int CANVAS_WIDTH = 1024;
    private static final int CANVAS_HEIGHT = 256;
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
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        drawBar(i, a[i]);
        drawBar(j, a[j]);
        StdDraw.show();
        StdDraw.pause(5*DRAW_PAUSE);
        StdDraw.setPenColor(StdDraw.DARK_GRAY);
        a[i] = a[j];
        a[j] = t;
    }

    private static void show(double[] a) {
        StdDraw.clear();
        for (int i = 0; i < a.length; ++i)
            drawBar(i, a[i]);
        StdDraw.show();
        StdDraw.pause(DRAW_PAUSE);
    }

    private static void drawBar(int i, double value) {
        StdDraw.filledRectangle(
                BAR_WIDTH/2 + i*(BAR_WIDTH + BAR_PADDING), BAR_HEIGHT_SCALE*value/2,
                BAR_WIDTH/2, BAR_HEIGHT_SCALE*value);
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
