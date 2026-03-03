package com.cuongd.study.algs.book.ch3;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Ex3419Test {
    @Test
    public void testSeparateChaining() {
        SeparateChainingHashST<Character, Integer> st = new SeparateChainingHashST<>();
        st.put('a', 0);
        st.put('b', 0);
        st.put('c', 0);
        Set<Character> keys = st.keys();
        assertEquals(3, keys.size());
        assertTrue(keys.contains('a'));
        assertTrue(keys.contains('b'));
        assertTrue(keys.contains('c'));
    }

    @Test
    public void testLinearProbing() {
        LinearProbingST<Character, Integer> st = new LinearProbingST<>();
        st.put('a', 0);
        st.put('b', 0);
        st.put('c', 0);
        Set<Character> keys = st.keys();
        assertEquals(3, keys.size());
        assertTrue(keys.contains('a'));
        assertTrue(keys.contains('b'));
        assertTrue(keys.contains('c'));
    }
}
