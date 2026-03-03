package com.cuongd.study.algs.book.ch3;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ex3429Test {
    @Test
    public void test() {
        SeparateChainingHashST<Character, Integer> st = new SeparateChainingHashST<>();
        st.put2('a', 0);
        st.put2('b', 0);
        st.put2('c', 0);
        st.delete2('a');
        assertEquals(2, st.n());
        st.delete2('d');
        assertEquals(2, st.n());
        st.delete2('b');
        assertEquals(1, st.n());
        st.delete2('c');
        assertEquals(0, st.n());
    }
}
