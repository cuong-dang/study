package com.cuongd.study.algs.book.ch1;

import edu.princeton.cs.algs4.Draw;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/** Amortized costs plots */
public class Ex1516 extends UnionFindQuickUnion {
    private static final int PLOT_WIDTH = 768;
    private static final int PLOT_HEIGHT = 256;
    private static final double PEN_RADIUS = 0.01;
    private static final int POINT_PADDING = 1;

    public int accessCount;

    public Ex1516(int N) {
        super(N);
        accessCount = 0;
    }

    @Override
    public int find(int p) {
        while (id[p] != p) {
            p = id[p];
            accessCount += 2;
        }
        accessCount++;
        return p;
    }

    @Override
    public void union(int p, int q) {
        int i = find(p);
        int j = find(q);

        if (i == j) {
            return;
        }
        id[i] = j;
        accessCount++;
        count--;
    }

    public static void main(String[] args) {
        In in = new In("./src/main/resources/ch1/mediumUF.txt");
        Draw draw = new Draw();
        int N = in.readInt(), totalCount = 0, x = POINT_PADDING;
        Ex1516 uf = new Ex1516(N);

        draw.setCanvasSize(PLOT_WIDTH, PLOT_HEIGHT);
        draw.setPenRadius(PEN_RADIUS);
        while (!in.isEmpty()) {
            int p = in.readInt();
            int q = in.readInt();
            if (uf.connected(p, q)) {
                continue;
            }
            uf.accessCount = 0;
            uf.union(p, q);
            totalCount += uf.accessCount;
            draw.setPenColor(Draw.GRAY);
            draw.point((double) x / PLOT_WIDTH, uf.accessCount/(double) PLOT_HEIGHT);
            draw.setPenColor(Draw.BOOK_RED);
            draw.point((double) x / PLOT_WIDTH, ((double) totalCount / x) / PLOT_HEIGHT);
            x += POINT_PADDING;
            StdOut.println(p + " " + q);
        }
        StdOut.println(uf.count() + " components");
        in.close();
    }
}
