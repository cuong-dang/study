package com.cuongd.study.algsp1.book.ch1;

/**
 * Local minimum of a matrix
 *
 * I could not solve this problem but finally understood how it is solved through this SO's
 * answer: https://stackoverflow.com/a/24461101.
 */
public class Ex1419 {
    public static Index findLocalMin(int[][] a) {
        SubMatrix subMatrix = new SubMatrix(new Index(0, 0), new Index(a.length, a[0].length));

        while (true) {
            Index minRowCol = findMinRowCol(a, subMatrix);
            if (isLocalMin(a, minRowCol)) {
                return minRowCol;
            }
            Index nextRowCol = findNextSmallerRowCol(a, minRowCol);
            if (isLocalMin(a, nextRowCol)) {
                return nextRowCol;
            }
            subMatrix.enclose(nextRowCol);
        }
    }

    private static Index findMinRowCol(int[][] a, SubMatrix m) {
        Index rv = new Index(m.midRow, m.topLeft.j);
        int min = a[rv.i][rv.j];

        for (int j = m.topLeft.j + 1; j < m.botRight.j; j++) {
            if (a[m.midRow][j] < min) {
                min = a[m.midRow][j];
                rv.i = m.midRow;
                rv.j = j;
            }
        }
        for (int i = m.topLeft.i; i < m.botRight.i; i++) {
            if (a[i][m.midCol] < min) {
                min = a[i][m.midCol];
                rv.i = i;
                rv.j = m.midCol;
            }
        }
        return rv;
    }

    private static boolean isLocalMin(int[][] a, Index index) {
        int v = a[index.i][index.j], i = index.i, j = index.j;

        /* Four corners */
        if (i == 0 && j == 0) {
            return v < a[i][i+1] && v < a[j+1][j];
        } else if (i == 0 && j == a[0].length - 1) {
            return v < a[i][j-1] && v < a[i+1][j];
        } else if (i == a.length - 1 && j == 0) {
            return v < a[i][j+1] && v < a[i-1][j];
        } else if (i == a.length - 1 && j == a[0].length - 1) {
            return v < a[i][j-1] && v < a[i-1][j];
        /* Four walls */
        } else if (i == 0) {
            return v < a[i][j-1] && v < a[i][j+1] && v < a[i+1][j];
        } else if (i == a.length - 1) {
            return v < a[i][j-1] && v < a[i][j+1] && v < a[i-1][j];
        } else if (j == 0) {
            return v < a[i-1][j] && v < a[i][j+1] && v < a[i+1][j];
        } else if (j == a[0].length - 1) {
            return v < a[i-1][j] && v < a[i][j-1] && v < a[i+1][j];
        /* Middle */
        } else {
            return v < a[i-1][j] && v < a[i][j-1] && v < a[i][j+1] && v < a[i+1][j];
        }
    }

    private static Index findNextSmallerRowCol(int[][] a, Index mrc) {
        Index up = new Index(mrc.i - 1, mrc.j);
        Index down = new Index(mrc.i + 1, mrc.j);
        Index left = new Index(mrc.i, mrc.j - 1);
        Index right = new Index(mrc.i, mrc.j + 1);
        int v = a[mrc.i][mrc.j];

        if (existsAndIsSmaller(a, up, v)) return up;
        if (existsAndIsSmaller(a, down, v)) return down;
        if (existsAndIsSmaller(a, left, v)) return left;
        return right;
    }

    private static boolean existsAndIsSmaller(int[][] a, Index index, int v) {
        return index.i >= 0 && index.i < a.length && index.j >= 0 && index.j < a[0].length &&
                a[index.i][index.j] < v;
    }

    private static class SubMatrix {
        Index topLeft;
        Index botRight;
        int midRow;
        int midCol;

        public SubMatrix(Index topLeft, Index botRight) {
            this.topLeft = topLeft;
            this.botRight = botRight;
            setMidRowCol();
        }

        public void enclose(Index rc) {
            if (rc.i < midRow && rc.j < midCol) {
                botRight = new Index(midRow + 1, midCol + 1);
            } else if (rc.i < midRow && rc.j > midCol) {
                topLeft = new Index(topLeft.i, midCol);
                botRight = new Index(midRow + 1, botRight.j);
            } else if (rc.i > midRow && rc.j < midCol) {
                topLeft = new Index(midRow, topLeft.j);
                botRight = new Index(botRight.i, midCol + 1);
            } else {
                topLeft = new Index(midRow, midCol);
            }
            setMidRowCol();
        }

        private void setMidRowCol() {
            midRow = (topLeft.i + botRight.i) / 2;
            midCol = (topLeft.j + botRight.j) / 2;
        }

        @Override
        public String toString() {
            return String.format("{tl: %s, br: %s, mr: %d, mc: %d",
                    topLeft, botRight, midRow, midCol);
        }
    }

    static class Index {
        int i;
        int j;

        public Index(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return String.format("[%d,%d]", i, j);
        }
    }
}
