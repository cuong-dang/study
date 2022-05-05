package com.cuongd.study.algsp1.book.ch3;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex3225Test {
    @Test
    public void test1() {
        //noinspection unchecked
        Ex3225<Integer, Integer> st = new Ex3225<Integer, Integer>(new Ex3225.Item[]{
                new Ex3225.Item<>(0, 0),
                new Ex3225.Item<>(1, 0),
                new Ex3225.Item<>(2, 0),
                new Ex3225.Item<>(3, 0),
                new Ex3225.Item<>(4, 0),
        });
        assertEquals(0, (int) st.root.left.key);
        assertEquals(1, (int) st.root.left.right.key);
        assertEquals(2, (int) st.root.key);
        assertEquals(3, (int) st.root.right.key);
        assertEquals(4, (int) st.root.right.right.key);
    }

    @Test
    public void test2() {
        //noinspection unchecked
        Ex3225<Integer, Integer> st = new Ex3225<Integer, Integer>(new Ex3225.Item[]{
                new Ex3225.Item<>(0, 0),
                new Ex3225.Item<>(1, 0),
                new Ex3225.Item<>(2, 0),
                new Ex3225.Item<>(3, 0),
                new Ex3225.Item<>(4, 0),
                new Ex3225.Item<>(5, 0),
        });
        assertEquals(0, (int) st.root.left.key);
        assertEquals(1, (int) st.root.left.right.key);
        assertEquals(2, (int) st.root.key);
        assertEquals(3, (int) st.root.right.left.key);
        assertEquals(4, (int) st.root.right.key);
        assertEquals(5, (int) st.root.right.right.key);
    }

    @Test
    public void test3() {
        //noinspection unchecked
        Ex3225<Integer, Integer> st = new Ex3225<Integer, Integer>(new Ex3225.Item[]{
                new Ex3225.Item<>(0, 0),
                new Ex3225.Item<>(1, 0),
                new Ex3225.Item<>(2, 0),
                new Ex3225.Item<>(3, 0),
                new Ex3225.Item<>(4, 0),
                new Ex3225.Item<>(5, 0),
                new Ex3225.Item<>(6, 0),
                new Ex3225.Item<>(7, 0),
                new Ex3225.Item<>(8, 0),
                new Ex3225.Item<>(9, 0),
        });
        assertEquals(0, (int) st.root.left.left.key);
        assertEquals(1, (int) st.root.left.key);
        assertEquals(2, (int) st.root.left.right.key);
        assertEquals(3, (int) st.root.left.right.right.key);
        assertEquals(4, (int) st.root.key);
        assertEquals(5, (int) st.root.right.left.key);
        assertEquals(6, (int) st.root.right.left.right.key);
        assertEquals(7, (int) st.root.right.key);
        assertEquals(8, (int) st.root.right.right.key);
        assertEquals(9, (int) st.root.right.right.right.key);
    }
}
