package com.cuongd.study.algsp1.book.ch1;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex1344Test {
    private Ex1344 buffer;

    @Before
    public void setup() {
        buffer = new Ex1344();
        buffer.insert('a');
        buffer.insert('b');
        buffer.insert('c');
    }

    @Test
    public void testInsertGetDeleteSize() {
        assertEquals(3, buffer.size());
        assertEquals('c', buffer.get());
        buffer.delete();
        assertEquals('b', buffer.get());
        assertEquals(2, buffer.size());
        buffer.delete();
        assertEquals('a', buffer.get());
        assertEquals(1, buffer.size());
        buffer.delete();
        assertEquals(0, buffer.size());
    }

    @Test
    public void testMove() {
        buffer.left(1);
        assertEquals('b', buffer.get());
        buffer.insert('d');
        assertEquals(4, buffer.size());
        assertEquals('d', buffer.get());
        buffer.right(1);
        assertEquals('c', buffer.get());
        buffer.left(1);
        assertEquals('d', buffer.get());
        buffer.left(1);
        assertEquals('b', buffer.get());
        buffer.left(1);
        assertEquals('a', buffer.get());
        buffer.right(2);
        buffer.delete();
        assertEquals(3, buffer.size());
        assertEquals('b', buffer.get());
    }
}
