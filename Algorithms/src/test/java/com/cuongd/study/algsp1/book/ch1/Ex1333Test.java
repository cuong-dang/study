package com.cuongd.study.algsp1.book.ch1;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Ex1333Test {
    private Ex1333<Integer> deque;

    @Before
    public void setup() {
        deque = new Ex1333<>();
    }

    @Test
    public void testConstructor() {
        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
    }

    @Test
    public void testPushLeftNoResize() {
        deque.pushLeft(1);
        deque.pushLeft(2);
        assertEquals(2, (int) deque.popLeft());
        assertEquals(1, (int) deque.popLeft());
    }

    @Test
    public void testPushRightNoResize() {
        deque.pushRight(1);
        deque.pushRight(2);
        assertEquals(2, (int) deque.popRight());
        assertEquals(1, (int) deque.popRight());
    }

    @Test
    public void testPushLeftResize() {
        deque.pushLeft(1);
        deque.pushLeft(2);
        deque.pushLeft(3);
        deque.pushLeft(4);
        assertEquals(4, (int) deque.popLeft());
        assertEquals(3, (int) deque.popLeft());
        assertEquals(2, (int) deque.popLeft());
        assertEquals(1, (int) deque.popLeft());
    }

    @Test
    public void testPushRightResize() {
        deque.pushRight(1);
        deque.pushRight(2);
        deque.pushRight(3);
        deque.pushRight(4);
        assertEquals(4, (int) deque.popRight());
        assertEquals(3, (int) deque.popRight());
        assertEquals(2, (int) deque.popRight());
        assertEquals(1, (int) deque.popRight());
    }

    @Test
    public void testPushMixed() {
        deque.pushLeft(1);
        deque.pushRight(2);
        deque.pushRight(3);
        deque.pushLeft(4);
        deque.pushLeft(5);
        deque.pushLeft(6);
        deque.pushRight(7);
        assertEquals(6, (int) deque.popLeft());
        assertEquals(5, (int) deque.popLeft());
        assertEquals(4, (int) deque.popLeft());
        assertEquals(1, (int) deque.popLeft());
        assertEquals(7, (int) deque.popRight());
        assertEquals(3, (int) deque.popRight());
        assertEquals(2, (int) deque.popRight());
    }

    @Test
    public void testPopLeftCrossOver() {
        deque.pushLeft(1);
        deque.pushLeft(2);
        deque.pushRight(3);
        deque.pushRight(4);
        deque.pushRight(5);
        assertEquals(2, (int) deque.popLeft());
        assertEquals(1, (int) deque.popLeft());
        assertEquals(3, (int) deque.popLeft());
        assertEquals(4, (int) deque.popLeft());
        assertEquals(5, (int) deque.popLeft());
    }

    @Test
    public void testPopRightCrossOver() {
        deque.pushLeft(1);
        deque.pushLeft(2);
        deque.pushRight(3);
        deque.pushRight(4);
        deque.pushRight(5);
        assertEquals(5, (int) deque.popRight());
        assertEquals(4, (int) deque.popRight());
        assertEquals(3, (int) deque.popRight());
        assertEquals(1, (int) deque.popRight());
        assertEquals(2, (int) deque.popRight());
    }
}
