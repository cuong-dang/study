package com.cuongd.study.algs.book.ch3;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;

import java.util.ArrayList;
import java.util.List;

import static edu.princeton.cs.algs4.StdOut.printf;
import static edu.princeton.cs.algs4.StdOut.println;

/**
 * Inverted concordance
 */
public class Ex3521 {
    public static void main(String[] args) {
        LinearProbingST<String, List<String>> st = new LinearProbingST<>();
        In in = new In(args[0]);
        int lineNo = 0;
        while (!in.isEmpty()) {
            lineNo++;
            String line = in.readLine();
            int wordNo = 0;
            for (String word : line.split(" ")) {
                if (word.isEmpty()) {
                    continue;
                }
                wordNo++;
                if (!st.contains(word)) {
                    st.put(word, new ArrayList<>());
                }
                st.get(word).add(String.format("%s:%s", lineNo, wordNo));
            }
        }
        LinearProbingST<Integer, MinPQ<Pair<Integer, String>>> inverted = new LinearProbingST<>();
        for (String word : st.keys()) {
            List<String> poss = st.get(word);
            for (String pos : poss) {
                int linePos = Integer.parseInt(pos.split(":")[0]);
                int wordPos = Integer.parseInt(pos.split(":")[1]);
                if (!inverted.contains(linePos)) {
                    inverted.put(linePos, new MinPQ<>());
                }
                inverted.get(linePos).insert(new Pair<>(wordPos, word));
            }
        }
        for (int i = 1; i <= lineNo; i++) {
            MinPQ<Pair<Integer, String>> words = inverted.get(i);
            while (words != null && !words.isEmpty()) {
                printf("%s ", words.min().getSecond());
                words.delMin();
            }
            println();
        }
    }

    private static class Pair<F extends Comparable<F>, S> implements Comparable<Pair<F, S>> {
        private final F first;
        private final S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        public F getFirst() {
            return first;
        }

        public S getSecond() {
            return second;
        }

        @Override
        public int compareTo(Pair<F, S> o) {
            return first.compareTo(o.first);
        }
    }
}
