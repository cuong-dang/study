package com.cuongd.study.algsp1.book.ch1;

import org.junit.Before;
import org.junit.Test;

import static com.cuongd.study.algsp1.book.ch1.Ex1331.*;
import static org.junit.Assert.assertEquals;

public class Ex1331Test {
    private Ex1331 list;

    @Before
    public void setup() {
        list = new Ex1331();
    }

    @Test
    public void testInsertFirst() {
        list.insertFirst(3);
        list.insertFirst(2);
        list.insertFirst(1);
        assertEquals("<1,2,3>", list.forwardString());
        assertEquals("<3,2,1>", list.backwardString());
    }

    @Test
    public void testInsertLast() {
        list.insertLast(1);
        list.insertLast(2);
        list.insertLast(3);
        assertEquals("<1,2,3>", list.forwardString());
        assertEquals("<3,2,1>", list.backwardString());
    }

    @Test
    public void testRemoveFirst() {
        list.insertLast(1);
        list.insertLast(2);
        list.insertLast(3);
        list.insertLast(4);
        list.removeFirst();
        assertEquals("<2,3,4>", list.forwardString());
        assertEquals("<4,3,2>", list.backwardString());
        list.removeFirst();
        assertEquals("<3,4>", list.forwardString());
        assertEquals("<4,3>", list.backwardString());
    }

    @Test
    public void testRemoveLast() {
        list.insertLast(1);
        list.insertLast(2);
        list.insertLast(3);
        list.insertLast(4);
        list.removeLast();
        assertEquals("<1,2,3>", list.forwardString());
        assertEquals("<3,2,1>", list.backwardString());
        list.removeLast();
        assertEquals("<1,2>", list.forwardString());
        assertEquals("<2,1>", list.backwardString());
    }

    @Test
    public void testInsertBefore() {
        DoubleNode node;

        list.insertLast(1);
        node = list.insertLast(3);
        list.insertLast(4);
        list.insertLast(5);
        insertBefore(node, 2);
        assertEquals("<1,2,3,4,5>", list.forwardString());
        assertEquals("<5,4,3,2,1>", list.backwardString());
    }

    @Test
    public void testInsertAfter() {
        DoubleNode node;

        list.insertLast(1);
        node = list.insertLast(2);
        list.insertLast(4);
        list.insertLast(5);
        insertAfter(node, 3);
        assertEquals("<1,2,3,4,5>", list.forwardString());
        assertEquals("<5,4,3,2,1>", list.backwardString());
    }

    @Test
    public void testRemove() {
        DoubleNode node, node2;

        list.insertLast(1);
        node = list.insertLast(2);
        node2 = list.insertLast(3);
        list.insertLast(4);
        remove(node);
        assertEquals("<1,3,4>", list.forwardString());
        assertEquals("<4,3,1>", list.backwardString());
        remove(node2);
        assertEquals("<1,4>", list.forwardString());
        assertEquals("<4,1>", list.backwardString());
    }
}
