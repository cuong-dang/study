package com.cuongd.study.algs.book.ch4;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.lang.reflect.InvocationTargetException;

public abstract class CC {
    public abstract boolean connected(int v, int w);

    public abstract int count();

    public abstract int id(int v);

    public static void main(String[] args, Class<? extends CC> C) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Graph G = new Graph(new In(args[0]));
        CC cc = C.getConstructor(new Class[]{Graph.class}).newInstance(G);

        int M = cc.count();
        StdOut.println(M + " components");

        Bag<Integer>[] components = (Bag<Integer>[]) new Bag[M];
        for (int i = 0; i < M; i++) {
            components[i] = new Bag<>();
        }
        for (int v = 0; v < G.V(); v++) {
            components[cc.id(v)].add(v);
        }
        for (int i = 0; i < M; i++) {
            for (int v : components[i]) {
                StdOut.print(v + " ");
            }
            StdOut.println();
        }
    }
}
