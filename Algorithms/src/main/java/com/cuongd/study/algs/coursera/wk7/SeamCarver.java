package com.cuongd.study.algs.coursera.wk7;

import edu.princeton.cs.algs4.Picture;

import java.awt.*;

public class SeamCarver {
    private Picture pic;
    private Double[][] energy;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        pic = picture;
        energy = new Double[pic.width()][pic.height()];
    }

    // current picture
    public Picture picture() { return pic; }

    // width of current picture
    public int width() { return pic.width(); }

    // height of current picture
    public int height() { return pic.height(); }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (energy[x][y] != null) return energy[x][y];
        if (x - 1 < 0 || x + 1 >= width()) return 1000;
        if (y - 1 < 0 || y + 1 >= height()) return 1000;
        double deltaX = delta(pic.get(x-1, y), pic.get(x+1, y));
        double deltaY = delta(pic.get(x, y-1), pic.get(x, y+1));
        double e = Math.sqrt(deltaX + deltaY);
        energy[x][y] = e;
        return e;
    }

    // sequence of indices for horizontal seam
//    public int[] findHorizontalSeam()

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] spe = new double[height()][width()];
        int[][] sp = new int[height()][width()];
        // init
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                spe[y][x] = Double.POSITIVE_INFINITY;
            }
        }
        // solve matrix
        for (int y = 0; y < height() - 1; y++) {
            for (int x = 0; x < width(); x++) {
                if (y == 0) spe[y][x] = energy(x, y);
                for (int d = -1; d <= 1; d++) {
                    if (x+d < 0 || x+d >= width()) continue;
                    double e = spe[y][x] + energy(x+d, y+1);
                    if (spe[y+1][x+d] > spe[y][x] + e) {
                        spe[y+1][x+d] = spe[y][x] + e;
                        sp[y+1][x+d] = x;
                    }
                }
            }
        }
        // find min
        int minX = 0;
        for (int x = 1; x < width(); x++) {
            if (spe[height()-1][x] < spe[height()-1][minX]) {
                minX = x;
            }
        }
        // build result
        int[] result = new int[height()];
        result[result.length - 1] = minX;
        for (int y = height() - 1; y >= 1; y--) {
            result[y - 1] = sp[y][minX];
            minX = sp[y][minX];
        }
        return result;
    }

    // remove horizontal seam from current picture
//    public void removeHorizontalSeam(int[] seam)

    // remove vertical seam from current picture
//    public void removeVerticalSeam(int[] seam)

    private double delta(Color a, Color b) {
        return sq(a.getRed() - b.getRed()) + sq(a.getBlue() - b.getBlue()) + sq(a.getGreen() - b.getGreen());
    }

    private double sq(double x) { return Math.pow(x, 2); }

    //  unit testing (optional)
//    public static void main(String[] args)
}
