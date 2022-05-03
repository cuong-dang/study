package com.cuongd.study.algsp1.book.ch3;

import org.junit.Test;

public class Ex3221Test {
    @Test
    public void test1() {
        Ex3221<String, Integer> st = new Ex3221<>();
        st.put("E", 0); st.put("A", 0); st.put("S", 0); st.put("Y", 0); st.put("Q", 0);
        st.put("U", 0); st.put("T", 0); st.put("I", 0); st.put("O", 0); st.put("N", 0);
        int N = 1_000_000;
        for (int i = 0; i < N; i++) {
            String k = st.randomKey();
            st.put(k, st.get(k) + 1);
        }
        Iterable<String> keys = st.keys();
        for (String k : keys) {
            System.out.printf("%s: %d\n", k, st.get(k));
        }
    }
}
