package com.cuongd.study.algs.coursera.wk8;

import edu.princeton.cs.algs4.TrieSET;

public class BoggleSolver {
    private TrieSET dict;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dict = new TrieSET();
        for (String word : dictionary) {
            dict.add(word);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        TrieSET result = new TrieSET();
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                StringBuilder collector = new StringBuilder();
                walk(board, row, col, result);
            }
        }
        return result;
    }

    private void walk(BoggleBoard board, int row, int col,
                      StringBuilder collector, TrieSET result) {
        collector.append()
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word)
}
