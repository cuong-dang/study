package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.lang.reflect.InvocationTargetException;

public abstract class Paths {
    abstract boolean hasPathTo(int v);

    abstract Iterable<Integer> pathTo(int v);

    public static void main(String[] args, Class<? extends Paths> C) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Graph G = new Graph(new In(args[0]));
        int s = Integer.parseInt(args[1]);
        Paths search = C.getConstructor(new Class[]{Graph.class, int.class}).newInstance(G, s);
        for (int v = 0; v < G.V(); v++) {
            StdOut.print(s + " to " + v + ": ");
            for (int x : search.pathTo(v)) {
                if (x == s) {
                    StdOut.print(x);
                } else {
                    StdOut.print("-" + x);
                }
            }
            StdOut.println();
        }
    }
}
