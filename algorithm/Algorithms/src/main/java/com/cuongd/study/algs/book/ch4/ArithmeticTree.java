package com.cuongd.study.algs.book.ch4;

import com.cuongd.study.algs.common.Either;

public class ArithmeticTree {
    private final Either<Integer, Operation> value;
    private ArithmeticTree left;
    private ArithmeticTree right;

    public ArithmeticTree(Either<Integer, Operation> value) {
        this.value = value;
    }

    public void setLeft(ArithmeticTree left) {
        this.left = left;
    }

    public void setRight(ArithmeticTree right) {
        this.right = right;
    }

    public int evaluate() {
        if (value.isLeft()) return value.left();
        switch (value.right()) {
            case ADD:
                return left.evaluate() + right.evaluate();
            case SUBTRACT:
                return left.evaluate() - right.evaluate();
            case MULTIPLE:
                return left.evaluate() * right.evaluate();
            case DIVIDE:
                return left.evaluate() / right.evaluate();
            default:
                throw new RuntimeException();
        }
    }

    public enum Operation {ADD, SUBTRACT, MULTIPLE, DIVIDE}
}
