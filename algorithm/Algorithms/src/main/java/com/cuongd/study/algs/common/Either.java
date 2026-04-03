package com.cuongd.study.algs.common;

public class Either<L, R> {
    private final L left;
    private final R right;

    public static <L, R> Either<L, R> left(L left) {
        return new Either<>(left, null);
    }

    public static <L, R> Either<L, R> right(R right) {
        return new Either<>(null, right);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }
}
