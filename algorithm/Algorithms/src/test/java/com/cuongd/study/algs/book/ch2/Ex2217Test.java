package com.cuongd.study.algs.book.ch2;

import com.cuongd.study.algs.book.ch1.MyQueue;
import org.junit.Before;
import org.junit.Test;

import static com.cuongd.study.algs.book.ch2.Ex2217.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Ex2217Test {
    IntNode n0 = new IntNode(), n1 = new IntNode(), n2 = new IntNode(), n3 = new IntNode(),
            n4 = new IntNode(), n5 = new IntNode(), n6 = new IntNode(), n7 = new IntNode(),
            n8 = new IntNode(), n9 = new IntNode();
    IntNode n10 = new IntNode(), n11 = new IntNode(), n12 = new IntNode(), n13 = new IntNode(),
            n14 = new IntNode(), n15 = new IntNode(), n16 = new IntNode(), n17 = new IntNode(),
            n18 = new IntNode(), n19 = new IntNode();

    @Before
    public void setUp() {
        /* 1 3 2 9 0 5 7 8 6 4 */
        n0.value = 0; n0.prev = n9; n0.next = n5;
        n1.value = 1; n1.prev = null; n1.next = n3;
        n2.value = 2; n2.prev = n3; n2.next = n9;
        n3.value = 3; n3.prev = n1; n3.next = n2;
        n4.value = 4; n4.prev = n6; n4.next = null;
        n5.value = 5; n5.prev = n0; n5.next = n7;
        n6.value = 6; n6.prev = n8; n6.next = n4;
        n7.value = 7; n7.prev = n5; n7.next = n8;
        n8.value = 8; n8.prev = n7; n8.next = n6;
        n9.value = 9; n9.prev = n2; n9.next = n0;
        /* 8 6 1 7 4 9 3 0 2 5 */
        n10.value = 0; n10.prev = n13; n10.next = n12;
        n11.value = 1; n11.prev = n16; n11.next = n17;
        n12.value = 2; n12.prev = n10; n12.next = n15;
        n13.value = 3; n13.prev = n19; n13.next = n10;
        n14.value = 4; n14.prev = n17; n14.next = n19;
        n15.value = 5; n15.prev = n12; n15.next = null;
        n16.value = 6; n16.prev = n18; n16.next = n11;
        n17.value = 7; n17.prev = n11; n17.next = n14;
        n18.value = 8; n18.prev = null; n18.next = n16;
        n19.value = 9; n19.prev = n14; n19.next = n13;
    }

    @Test
    public void testFindMergeBoundaries() {
        MyQueue<IntNode> b = findMergeBoundaries(n1);
        assertEquals(n3, b.dequeue());
        assertEquals(n9, b.dequeue());
        assertEquals(n8, b.dequeue());
        assertEquals(n6, b.dequeue());
    }

    @Test
    public void testMerge() {
        LoHi loHi = merge(n1, n3, n9);
        assertEquals(1, loHi.lo.value);
        assertNull(loHi.lo.prev);
        assertEquals(2, loHi.lo.next.value);
        assertEquals(1, loHi.lo.next.prev.value);
        assertEquals(3, loHi.lo.next.next.value);
        assertEquals(2, loHi.lo.next.next.prev.value);
        assertEquals(9, loHi.lo.next.next.next.value);
        assertEquals(3, loHi.lo.next.next.next.prev.value);
        assertEquals(0, loHi.lo.next.next.next.next.value);
        assertEquals(9, loHi.lo.next.next.next.next.prev.value);
    }

    @Test
    public void testSort() {
        int i = 0;

        sortLinkedList(n1);
        for (IntNode p = n0; p != null; p = p.next, ++i)
            assertEquals(i, p.value);
        i = 9;
        for (IntNode p = n9; p != null; p = p.prev, --i)
            assertEquals(i, p.value);
    }

    @Test
    public void testSort2() {
        int i = 0;

        sortLinkedList(n18);
        for (IntNode p = n10; p != null; p = p.next, ++i)
            assertEquals(i, p.value);
        i = 9;
        for (IntNode p = n19; p != null; p = p.prev, --i)
            assertEquals(i, p.value);
    }
}
