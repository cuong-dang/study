package com.cuongd.study.algs.book.ch4;

import com.cuongd.study.algs.common.Either;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArithmeticTreeTest {
    @Test
    public void test() {
        ArithmeticTree one = new ArithmeticTree(Either.left(1));
        assertEquals(1, one.evaluate());
        ArithmeticTree add = new ArithmeticTree(Either.right(ArithmeticTree.Operation.ADD));
        ArithmeticTree two = new ArithmeticTree(Either.left(2));
        add.setLeft(one);
        add.setRight(two);
        assertEquals(3, add.evaluate());
    }
}
