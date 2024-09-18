package com.cuongd.study.algs.coursera.wk7;

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    private Picture pic;
    private Double[][] energy;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();

        pic = picture;
        energy = new Double[pic.height()][pic.width()];
    }

    // current picture
    public Picture picture() { return pic; }

    // width of current picture
    public int width() { return pic.width(); }

    // height of current picture
    public int height() { return pic.height(); }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new IllegalArgumentException();
        }

        if (energy[y][x] != null) return energy[y][x];
        if (x - 1 < 0 || x + 1 >= width()) return 1000;
        if (y - 1 < 0 || y + 1 >= height()) return 1000;
        double deltaX = delta(pic.get(x-1, y), pic.get(x+1, y));
        double deltaY = delta(pic.get(x, y-1), pic.get(x, y+1));
        double e = Math.sqrt(deltaX + deltaY);
        energy[y][x] = e;
        return e;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // transpose
        Picture oldPic = pic;
        Double[][] oldEnergy = energy;
        transpose();
        // calculate
        int[] result = findVerticalSeam();
        // restore
        pic = oldPic;
        energy = oldEnergy;
        return result;
    }

    private void transpose() {
        Picture oldPic = pic;
        pic = new Picture(oldPic.height(), oldPic.width());
        for (int i = 0; i < pic.height(); i++) {
            for (int j = 0; j < pic.width(); j++) {
                pic.set(j, i, oldPic.get(i, j));
            }
        }
        energy = new Double[pic.height()][pic.width()];
    }

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
        for (int h = 0; h < height()-1; h++) {
            for (int w = 0; w < width(); w++) {
                if (h == 0) spe[h][w] = energy(w, h);
                for (int d = -1; d <= 1; d++) {
                    if (w+d < 0 || w+d >= width()) continue;
                    double e = spe[h][w] + energy(w+d, h+1);
                    if (spe[h+1][w+d] > e) {
                        spe[h+1][w+d] = e;
                        sp[h+1][w+d] = w;
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
    public void removeHorizontalSeam(int[] seam) {
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != height()) throw new IllegalArgumentException();
        if (width() <= 1) throw new IllegalArgumentException();

        Picture newPic = new Picture(pic.width() - 1, pic.height());
        for (int h = 0; h < pic.height(); h++) {
            int d = 0;
            for (int w = 0; w < pic.width(); w++) {
                if (seam[h] < 0 || seam[h] > width()) {
                    throw new IllegalArgumentException();
                }
                if (h != 0 && Math.abs(seam[h] - seam[h-1]) > 1) {
                    throw new IllegalArgumentException();
                }

                if (w == seam[h]) {
                    d = -1;
                    continue;
                }
                newPic.set(w+d, h, pic.get(w, h));
            }
        }
        pic = newPic;
        energy = new Double[pic.height()][pic.width()];
    }

    private double delta(Color a, Color b) {
        return sq(a.getRed() - b.getRed()) +
                sq(a.getBlue() - b.getBlue()) +
                sq(a.getGreen() - b.getGreen());
    }

    private double sq(double x) { return Math.pow(x, 2); }
}
