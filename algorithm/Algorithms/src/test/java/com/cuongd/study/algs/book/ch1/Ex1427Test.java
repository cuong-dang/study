package com.cuongd.study.algs.book.ch1;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class Ex1427Test {
    private Ex1427<Integer> q;

    @Before
    public void setup() {
        q = new Ex1427<>();
    }

    @Test
    public void testEnqueue() {
        assertTrue(q.isEmpty());
        q.enqueue(1);
        assertFalse(q.isEmpty());
    }

    @Test
    public void testMixedOps() {
        q.enqueue(1);
        assertEquals(1, (int) q.dequeue());
        q.enqueue(1);
        q.enqueue(2);
        assertEquals(1, (int) q.dequeue());
        assertEquals(2, (int) q.dequeue());
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        assertEquals(1, (int) q.dequeue());
        q.enqueue(4);
        assertEquals(2, (int) q.dequeue());
        assertEquals(3, (int) q.dequeue());
        q.enqueue(5);
        q.enqueue(6);
        assertEquals(4, (int) q.dequeue());
        assertEquals(5, (int) q.dequeue());
        assertEquals(6, (int) q.dequeue());
    }
}
