package com.cuongd.study.algsp1.book.ch3;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Ex3230Test {
    private List<Ex3230.Ex3230Node<Integer, Integer>> nodes;

    @Before
    public void init() {
        nodes = new ArrayList<>();
    }

    @Test
    public void testOneNode() {
        nodes.add(new Ex3230.Ex3230Node<>(5, 0));
        Ex3230<Integer, Integer> tree = new Ex3230<>(nodes);
        assertEquals(1, tree.root.n);
        assertEquals(5, (int) tree.root.key);
        assertEquals(0, (int) tree.root.val);
        assertNull(tree.root.left);
        assertNull(tree.root.right);
    }

    @Test
    public void testTwoNodes() {
        nodes.add(new Ex3230.Ex3230Node<>(5, 0));
        nodes.add(new Ex3230.Ex3230Node<>(4, 1));
        Ex3230<Integer, Integer> tree = new Ex3230<>(nodes);
        assertEquals(2, tree.root.n);
        assertEquals(5, (int) tree.root.key);
        assertEquals(0, (int) tree.root.val);
        assertEquals(4, (int) tree.root.left.key);
        assertEquals(1, (int) tree.root.left.val);
        assertNull(tree.root.right);
    }

    @Test
    public void testAllLeft() {
        nodes.add(new Ex3230.Ex3230Node<>(5, 0));
        nodes.add(new Ex3230.Ex3230Node<>(4, 1));
        nodes.add(new Ex3230.Ex3230Node<>(2, 2));
        Ex3230<Integer, Integer> tree = new Ex3230<>(nodes);
        assertEquals(3, tree.root.n);
        assertEquals(5, (int) tree.root.key);
        assertEquals(0, (int) tree.root.val);
        assertEquals(4, (int) tree.root.left.key);
        assertEquals(1, (int) tree.root.left.val);
        assertEquals(2, (int) tree.root.left.left.key);
        assertEquals(2, (int) tree.root.left.left.val);
        assertNull(tree.root.right);
    }

    @Test
    public void testCommonCase() {
        nodes.add(new Ex3230.Ex3230Node<>(5, 0));
        nodes.add(new Ex3230.Ex3230Node<>(4, 1));
        nodes.add(new Ex3230.Ex3230Node<>(2, 2));
        nodes.add(new Ex3230.Ex3230Node<>(3, 3));
        nodes.add(new Ex3230.Ex3230Node<>(7, 4));
        nodes.add(new Ex3230.Ex3230Node<>(6, 5));
        nodes.add(new Ex3230.Ex3230Node<>(8, 6));
        Ex3230<Integer, Integer> tree = new Ex3230<>(nodes);
        assertEquals(7, tree.root.n);
        assertEquals(5, (int) tree.root.key);
        assertEquals(0, (int) tree.root.val);
        assertEquals(4, (int) tree.root.left.key);
        assertEquals(1, (int) tree.root.left.val);
        assertEquals(2, (int) tree.root.left.left.key);
        assertEquals(2, (int) tree.root.left.left.val);
        assertEquals(3, (int) tree.root.left.left.right.key);
        assertEquals(3, (int) tree.root.left.left.right.val);
        assertEquals(7, (int) tree.root.right.key);
        assertEquals(4, (int) tree.root.right.val);
        assertEquals(6, (int) tree.root.right.left.key);
        assertEquals(5, (int) tree.root.right.left.val);
        assertEquals(8, (int) tree.root.right.right.key);
        assertEquals(6, (int) tree.root.right.right.val);
    }
}
