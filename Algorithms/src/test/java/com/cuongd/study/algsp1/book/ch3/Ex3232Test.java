package com.cuongd.study.algsp1.book.ch3;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Ex3232Test {
    Ex3232<Integer, Integer> bst;

    @Before
    public void init() {
        bst = new Ex3232<>();
    }

    @Test
    public void testOneNode() {
        bst.put(0, 1);
        assertTrue(bst.checkCount());
        bst.root.n = 0;
        assertFalse(bst.checkCount());
    }

    @Test
    public void testTwoNodes() {
        bst.put(0, 1); bst.put(2, 3);
        assertTrue(bst.checkCount());
        bst.root.n = 1;
        assertFalse(bst.checkCount());
    }

    @Test
    public void testCommonCase() {
        bst.put(5, 5); bst.put(4, 4); bst.put(2, 2); bst.put(3, 3);
        assertTrue(bst.checkCount());
        bst.root.n = 3;
        assertFalse(bst.checkCount());
    }
}
