package com.cuongd.study.algsp1.book.ch1;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyLinkedListTest {
    private MyLinkedList<Integer> list;

    @Before
    public void setup() {
        list = new MyLinkedList<>();
    }

    @Test
    public void testConstructor() {
        assertTrue(list.isEmpty());
    }

    @Test
    public void testAdd() {
        list.add(1);
        assertFalse(list.isEmpty());
        assertEquals(1, (int) list.first.value);
        list.add(2);
        assertEquals(2, (int) list.first.value);
        assertEquals(1, (int) list.first.next.value);
        assertNull(list.first.next.next);
    }
}
