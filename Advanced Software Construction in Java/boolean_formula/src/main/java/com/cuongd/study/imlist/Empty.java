package com.cuongd.study.imlist;

public class Empty<E> implements ImList<E> {
    @Override
    public ImList<E> cons(E e) {
        return new Cons<>(e, this);
    }

    @Override
    public E first() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImList<E> rest() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
