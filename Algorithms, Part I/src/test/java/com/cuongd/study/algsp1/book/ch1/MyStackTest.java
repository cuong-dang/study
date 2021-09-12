package com.cuongd.study.algsp1.book.ch1;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MyStackTest {
    MyStack<Integer> s;

    @Before
    public void setup() {
        s = new MyStack<>();
    }

    @Test
    public void testConstructor() {
        assertTrue(s.isEmpty());
    }

    @Test
    public void testPushAndPopAndResize() {
        s.push(1);
        assertEquals(1, (int) s.pop());
        assertTrue(s.isEmpty());

        s.push(1); s.push(2); s.push(3); s.push(4);
        assertEquals(4, s.size());
        assertEquals(4, (int) s.pop());
        assertEquals(3, (int) s.pop());
        assertEquals(2, (int) s.pop());
        assertEquals(1, (int) s.pop());
        assertTrue(s.isEmpty());
    }

    @Test
    public void testReverse() {
        s.push(1);
        s.reverse();
        assertEquals(1, (int) s.pop());
        s.push(1); s.push(2); s.push(3);
        s.reverse();
        assertEquals(1, (int) s.pop());
        assertEquals(2, (int) s.pop());
        assertEquals(3, (int) s.pop());
    }

    /* Regression tests */
    @Test
    public void testResizeToZero() {
        /* The following sequence failed if internal array is resized to length
         * 0. Following comments describe what happened before the fix.
         */
        s.push(1); // a.length == 2
        s.pop(); // a.length resize to 1
        s.push(1);
        s.pop(); // a.length resize to 0
        s.push(1); // ArrayIndexOutOfBoundsException thrown
    }

    @Test
    public void testIterator() {
        int[] expected = new int[]{4, 3, 2, 1};
        int i = 0;

        s.push(1); s.push(2); s.push(3); s.push(4);
        for (int e : s)
            assertEquals(expected[i++], e);
    }

    @Test
    public void testToString() {
        s.push(1);
        assertEquals("<1>", s.toString());
        s.push(2);
        assertEquals("<1,2>", s.toString());
    }
}
