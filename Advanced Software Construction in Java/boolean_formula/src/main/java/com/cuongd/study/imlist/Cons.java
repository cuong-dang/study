package com.cuongd.study.imlist;

public class Cons<E> implements ImList<E> {
    private final E first;
    ImList<E> rest;

    public Cons(E first, ImList<E> rest) {
        this.first = first;
        this.rest = rest;
    }

    @Override
    public ImList<E> cons(E e) {
        return new Cons<>(e, this);
    }

    @Override
    public E first() {
        return first;
    }

    @Override
    public ImList<E> rest() {
        return rest;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
