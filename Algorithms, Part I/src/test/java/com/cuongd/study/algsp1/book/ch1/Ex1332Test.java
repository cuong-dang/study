package com.cuongd.study.algsp1.book.ch1;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class Ex1332Test {
    private Ex1332<Integer> steque;

    @Before
    public void setup() {
        steque = new Ex1332<>();
    }

    @Test
    public void testConstructor() {
        assertTrue(steque.isEmpty());
        assertEquals(0, steque.size());
    }

    @Test
    public void testPush() {
        steque.push(1);
        assertFalse(steque.isEmpty());
        assertEquals(1, steque.size());
        assertEquals(1, (int) steque.pop());
        assertTrue(steque.isEmpty());
    }

    @Test
    public void testPopPushOnly() {
        steque.push(1); steque.push(2); steque.push(3);
        assertEquals(3, (int) steque.pop());
        assertEquals(2, (int) steque.pop());
        assertEquals(1, (int) steque.pop());
    }

    @Test
    public void testPopEnqueueOnly() {
        steque.enqueue(1); steque.enqueue(2); steque.enqueue(3);
        assertEquals(1, (int) steque.pop());
        assertEquals(2, (int) steque.pop());
        assertEquals(3, (int) steque.pop());
        assertTrue(steque.isEmpty());
    }

    @Test
    public void testPopMixed() {
        steque.push(1); steque.push(2);
        steque.enqueue(3);
        steque.enqueue(4);
        steque.enqueue(5);
        assertEquals(3, (int) steque.pop());
        assertEquals(4, (int) steque.pop());
        assertEquals(5, (int) steque.pop());
        assertEquals(2, (int) steque.pop());
        assertEquals(1, (int) steque.pop());
    }

    @Test
    public void testPopMixed2() {
        steque.push(1); steque.enqueue(2);
        steque.push(3);
        steque.enqueue(4);
        steque.push(5);
        assertEquals(5, (int) steque.pop());
        assertEquals(4, (int) steque.pop());
        assertEquals(3, (int) steque.pop());
        assertEquals(2, (int) steque.pop());
        assertEquals(1, (int) steque.pop());
    }

    @Test
    public void testPopMixed3() {
        steque.enqueue(1); steque.enqueue(2);
        steque.push(3);
        steque.push(4);
        steque.push(5);
        assertEquals(5, (int) steque.pop());
        assertEquals(4, (int) steque.pop());
        assertEquals(3, (int) steque.pop());
        assertEquals(1, (int) steque.pop());
        assertEquals(2, (int) steque.pop());
    }
}
