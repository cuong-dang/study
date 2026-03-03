package com.cuongd.study.algs.book.ch3;

import static edu.princeton.cs.algs4.StdOut.printf;

public class Ex3524 {
    public static void main(String[] args) {
        BST<Integer, Integer> st = new BST<>();
        st.put(1643, 2033);
        st.put(5532, 7643);
        st.put(8999, 10332);
        st.put(5666653, 5669321);
        printf("9122: %d-%d\n", st.floor(9122), st.get(st.floor(9122)));
    }
}
