package com.cuongd.study.algs.coursera.wk8;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TrieSET;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                StringBuilder sb = new StringBuilder();
                Set<List<Integer>> walked = new HashSet<>();
                walk(board, row, col, sb, walked, result);
            }
        }
        return result;
    }

    private void walk(BoggleBoard board, int row, int col,
                      StringBuilder sb, Set<List<Integer>> walked,
                      TrieSET result) {
        char c = board.getLetter(row, col);
        sb.append(c);
        if (c == 'Q') {
            sb.append('U');
        }
        List<Integer> thisSquareRowCol = List.of(row, col);
        walked.add(thisSquareRowCol);
        if (hasKeysWithPrefix(sb.toString())) {
            if (sb.length() >= 3 &&
                    dict.contains(sb.toString())) {
                result.add(sb.toString());
            }
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int nRow = row+i, nCol = col+j;
                    if (nRow >= 0 && nRow < board.rows() &&
                            nCol >= 0 && nCol < board.cols() &&
                            !walked.contains(List.of(nRow, nCol))) {
                        walk(board, nRow, nCol, sb, walked, result);
                    }
                }
            }
        }
        walked.remove(thisSquareRowCol);
        sb.deleteCharAt(sb.length()-1);
        if (c == 'Q') {
            sb.deleteCharAt(sb.length()-1);
        }
    }

    private boolean hasKeysWithPrefix(String s) {
        for (String t : dict.keysWithPrefix(s)) {
            return true;
        }
        return false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
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
