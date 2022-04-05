package com.cuongd.study.algsp1.book.ch1;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyQueueTest {
    private MyQueue<Integer> q;

    @Before
    public void setup() {
        q = new MyQueue<>();
    }

    @Test
    public void testConstructor() {
        assertTrue(q.isEmpty());
        assertEquals(0, q.size());
    }

    @Test
    public void testEnqueue() {
        q.enqueue(1);
        assertFalse(q.isEmpty());
        assertEquals(1, q.size());

        q.enqueue(2);
        assertEquals(2, q.size());
    }

    @Test
    public void testDequeue() {
        q.enqueue(1); q.enqueue(2);
        assertEquals(1, (int) q.dequeue());
        assertEquals(1, q.size());
        assertEquals(2, (int) q.dequeue());
        assertEquals(0, q.size());
        assertTrue(q.isEmpty());
    }

    @Test
    public void testResizeUp() {
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        q.enqueue(5);
        assertEquals(5, q.size());
    }

    @Test
    public void testResizeDown() {
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        assertEquals(1, (int) q.dequeue());
        assertEquals(2, (int) q.dequeue());
        assertEquals(3, (int) q.dequeue());
        assertEquals(4, (int) q.dequeue());
        assertTrue(q.isEmpty());
    }

    @Test
    public void testMoveToFront() {
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        q.dequeue();
        q.dequeue();
        q.enqueue(5);
        q.enqueue(6);
        assertEquals(3, (int) q.dequeue());
        assertEquals(4, (int) q.dequeue());
        assertEquals(5, (int) q.dequeue());
        assertEquals(6, (int) q.dequeue());
        assertTrue(q.isEmpty());
    }
}
