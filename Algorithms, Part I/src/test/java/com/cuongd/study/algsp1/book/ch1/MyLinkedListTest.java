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
        assertEquals(0, list.size());
    }

    @Test
    public void testAdd() {
        list.add(1);
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertEquals(1, (int) list.first.value);
        list.add(2);
        assertEquals(2, list.size());
        assertEquals(2, (int) list.first.value);
        assertEquals(1, (int) list.first.next.value);
        assertNull(list.first.next.next);
    }

    @Test
    public void testDelete0() {
        list.add(1); list.add(2); list.add(3); list.add(4);
        list.delete(0);
        assertEquals(3, list.size());
        assertEquals(3, (int) list.first.value);
    }

    @Test
    public void testDelete1() {
        list.add(1); list.add(2); list.add(3); list.add(4);
        list.delete(1);
        assertEquals(3, list.size());
        assertEquals(4, (int) list.first.value);
        assertEquals(2, (int) list.first.next.value);
    }

    @Test
    public void testDelete3() {
        list.add(1); list.add(2); list.add(3); list.add(4);
        list.delete(3);
        assertEquals(3, list.size());
        assertEquals(4, (int) list.first.value);
        assertEquals(3, (int) list.first.next.value);
        assertEquals(2, (int) list.first.next.next.value);
    }
}
