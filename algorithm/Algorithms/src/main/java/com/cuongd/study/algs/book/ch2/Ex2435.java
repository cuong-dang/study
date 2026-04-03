package com.cuongd.study.algs.book.ch2;

public class Ex2435 {
    private final Node[] a;
    public final int N;
    public int T;

    public Ex2435(int[] p) {
        N = p.length;
        a = new Node[p.length + 1];
        T = 0;
        for (int i = p.length - 1; i >= 0; i--) {
            int ai = i + 1;
            a[ai] = new Node(
                    p[i], ai * 2 > N ?
                    p[i] :
                    (ai * 2 == N ? p[i] + a[ai*2].w : p[i] + a[ai*2].w + a[ai*2 + 1].w)
            );
            T += a[ai].v;
        }
        show(a);
    }

    public int random() {
        int t = (int) (Math.random() * T);
        for (int i = 1; i < a.length; ) {
            if (t < a[i].v)
                return i - 1;
            t -= a[i].v;
            if (t < a[i*2].w) {
                i *= 2;
            } else {
                t -= a[i*2].w;
                i = i*2 + 1;
            }
        }
        throw new IllegalStateException();
    }

    public void changeKey(int i, int v) {
        i += 1;
        int d = v - a[i].v;
        a[i].v = v;
        while (i >= 1) {
            a[i].w += d;
            i /= 2;
        }
    }

    private void show(Node[] a){
        for (int i = 1; i < a.length; i++) {
            System.out.printf("%d(%d)\n", a[i].v, a[i].w);
        }
    }

    private static class Node {
        int v;
        int w;

        public Node(int v, int w) {
            this.v = v;
            this.w = w;
        }
    }

    public static void main(String[] args) {
        int R = 1000000;
        int[] p = new int[]{6, 3, 9, 5, 2, 1, 7, 4, 8};
        Ex2435 a = new Ex2435(p);
        int[] count = new int[a.N];
        for (int i = 0; i < R; i++) {
            count[a.random()]++;
        }
        for (int i = 0; i < a.N; i++) {
            System.out.printf("%d: %.2f ~ %.2f\n", i, (double) count[i] / R, (double) p[i]/a.T);
        }

        a.changeKey(0, 7);
        a.changeKey(6, 6);
        a.changeKey(1, 9);
        a.changeKey(2, 3);
        a.changeKey(3, 8);
        a.changeKey(8, 5);

        p = new int[]{7, 9, 3, 8, 2, 1, 6, 4, 5};
        count = new int[a.N];
        for (int i = 0; i < R; i++) {
            count[a.random()]++;
        }
        for (int i = 0; i < a.N; i++) {
            System.out.printf("%d: %.2f ~ %.2f\n", i, (double) count[i] / R, (double) p[i]/a.T);
        }
    }
}
