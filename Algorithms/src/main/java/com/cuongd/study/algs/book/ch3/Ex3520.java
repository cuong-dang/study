package com.cuongd.study.algs.book.ch3;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;

import java.util.ArrayList;
import java.util.List;

import static edu.princeton.cs.algs4.StdOut.printf;

public class Ex3520 {
    public static void main(String[] args) {
        LinearProbingST<String, List<String>> st = new LinearProbingST<>();
        In in = new In(args[0]);
        int lineNo = 0;
        while (!in.isEmpty()) {
            lineNo++;
            String line = in.readLine();
            int wordNo = 0;
            for (String word : line.split(" ")) {
                if (word.equals("")) {
                    continue;
                }
                wordNo++;
                if (!st.contains(word)) {
                    st.put(word, new ArrayList<>());
                }
                st.get(word).add(String.format("%s:%s", lineNo, wordNo));
            }
        }
        while (!StdIn.isEmpty()) {
            String query = StdIn.readString();
            if (st.contains(query)) {
                for (String pos : st.get(query)) {
                    printf("  %s\n", pos);
                }
            }
        }
    }
}
