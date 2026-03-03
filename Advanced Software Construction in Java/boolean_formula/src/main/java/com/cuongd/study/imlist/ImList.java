package com.cuongd.study.imlist;

public interface ImList<E> {
    static <E> ImList<E> empty() {
        return new Empty<>();
    }

    ImList<E> cons(E e);
    E first();
    ImList<E> rest();
    boolean isEmpty();
}
