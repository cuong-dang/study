package com.cuongd.study.algs.book.ch3;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Ex343Test {
    @Test
    public void test() {
        Ex343<String, Integer> st = new Ex343<>(5);
        st.put("E", 0);
        st.put("A", 1);
        st.put("S", 2);
        st.put("Y", 3);
        st.put("Q", 4);
        st.put("U", 5);
        st.put("T", 6);
        st.put("I", 7);
        st.put("O", 8);
        st.put("N", 9);

        assertEquals(0, (int) st.get("E"));
        assertEquals(0, (int) st.numKeys("E"));
        assertEquals(1, (int) st.get("A"));
        assertEquals(1, (int) st.numKeys("A"));
        assertEquals(2, (int) st.get("S"));
        assertEquals(2, (int) st.numKeys("S"));
        assertEquals(3, (int) st.get("Y"));
        assertEquals(3, (int) st.numKeys("Y"));
        assertEquals(4, (int) st.get("Q"));
        assertEquals(4, (int) st.numKeys("Q"));
        assertEquals(5, (int) st.get("U"));
        assertEquals(5, (int) st.numKeys("U"));
        assertEquals(6, (int) st.get("T"));
        assertEquals(6, (int) st.numKeys("T"));
        assertEquals(7, (int) st.get("I"));
        assertEquals(7, (int) st.numKeys("I"));
        assertEquals(8, (int) st.get("O"));
        assertEquals(8, (int) st.numKeys("O"));
        assertEquals(9, (int) st.get("N"));
        assertEquals(9, (int) st.numKeys("N"));

        st.deleteGtNumKeys(4);

        assertEquals(0, (int) st.get("E"));
        assertEquals(1, (int) st.get("A"));
        assertEquals(2, (int) st.get("S"));
        assertEquals(3, (int) st.get("Y"));
        assertEquals(4, (int) st.get("Q"));
        assertNull(st.get("U"));
        assertNull(st.get("T"));
        assertNull(st.get("I"));
        assertNull(st.get("O"));
        assertNull(st.get("N"));
    }
}
