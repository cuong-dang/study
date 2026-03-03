package com.cuongd.study.algs.book.ch3;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex3418Test {
    @Test
    public void test() {
        SeparateChainingHashST<Character, Integer> st = new SeparateChainingHashST<>(2, 1);
        assertEquals(0, st.n());
        assertEquals(2, st.m());
        st.put('a', 0);
        assertEquals(1, st.n());
        assertEquals(2, st.m());
        assertEquals(0, (int) st.get('a'));
        st.put('b', 1);
        assertEquals(2, st.n());
        assertEquals(2, st.m());
        assertEquals(0, (int) st.get('a'));
        assertEquals(1, (int) st.get('b'));

        st.put('c', 2);
        assertEquals(3, st.n());
        assertEquals(4, st.m());
        assertEquals(0, (int) st.get('a'));
        assertEquals(1, (int) st.get('b'));
        assertEquals(2, (int) st.get('c'));
    }
}
