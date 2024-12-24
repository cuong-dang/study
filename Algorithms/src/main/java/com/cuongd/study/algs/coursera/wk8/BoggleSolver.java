package com.cuongd.study.algs.coursera.wk8;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TrieSET;

public class BoggleSolver {
    private final BoggleTrie dict;
    private boolean[][] marked;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dict = new BoggleTrie();
        for (String word : dictionary) {
            dict.add(word);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        TrieSET result = new TrieSET();
        marked = new boolean[board.rows()][board.cols()];
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                walk(board, row, col, "", result, null);
            }
        }
        return result;
    }

    private void walk(BoggleBoard board, int row, int col, String prefix,
                      TrieSET result, BoggleTrie.Node fromNode) {
        char c = board.getLetter(row, col);
        String s = prefix + c;
        if (c == 'Q') {
            s += 'U';
        }
        marked[row][col] = true;
        BoggleTrie.Node prefixNode = dict.searchPrefix(s, fromNode);
        if (prefixNode != null) {
            if (s.length() >= 3 && dict.contains(s)) {
                result.add(s);
            }
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int nRow = row+i, nCol = col+j;
                    if (nRow >= 0 && nRow < board.rows() &&
                            nCol >= 0 && nCol < board.cols() &&
                            !marked[nRow][nCol]) {
                        walk(board, nRow, nCol, s, result, prefixNode);
                    }
                }
            }
        }
        marked[row][col] = false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word.length() < 3 || !dict.contains(word)) return 0;
        switch (word.length()) {
            case 3: case 4: return 1;
            case 5: return 2;
            case 6: return 3;
            case 7: return 5;
            default: return 11;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
