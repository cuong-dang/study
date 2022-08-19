/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.Files.readAllLines;

/** Thread-safe Minesweeper board */
public class Board {
    public enum OpResult { OK, ILLEGAL_MOVE, BOOM, GAME_OVER }
    public enum State { ACTIVE, FROZEN }

    private final Square[][] board;
    private State state;
    private final int dim;
    private int numBombsInitial;
    private int numBombsUnflagged;
    private int numFlags;

    /* Specs */
    // Abstraction function
    //   represents a Minesweeper board
    //   includes game state
    // Rep invariant
    //   0 <= numBombsUnflagged <= numBombsInitial
    //   numFlags >= 0
    //   if numBombsUnflagged == 0, state == FROZEN
    //   else state == ACTIVE
    // Rep exposure
    //   all fields are private
    //   exposed fields have immutable types
    // Thread safety
    //   coarse-grained where mutators and observers are synchronized

    /**
     * Construct a Minesweeper board.
     * @param file file path to board representation
     * @throws IOException if fail to open file
     */
    public Board(String file) throws IOException {
        List<String> lines = readAllLines(Paths.get(file), StandardCharsets.UTF_8);
        dim = lines.size();
        String[][] boardStr = new String[dim][dim];
        /* Fill board's string representation */
        int i = 0;
        for (String line : lines) {
            String[] squaresStr = line.split(" ");
            int j = 0;
            for (String squareStr : squaresStr) {
                boardStr[i][j++] = squareStr;
            }
            ++i;
        }
        /* Fill board */
        board = new Square[dim][dim];
        numBombsInitial = 0;
        for (i = 0; i < dim; ++i) {
            for (int j = 0; j < dim; ++j) {
                /* Calculate surrounding bombs */
                int numSurroundingBombs = 0;
                for (int k = -1; k < 2; ++k) {
                    if (isInBoard(i + k)) {
                        for (int h = -1; h < 2; ++h) {
                            if (isInBoard(j + h)) {
                                if ((k != 0 || h != 0) && boardStr[i+k][j+h].equals("X")) {
                                    ++numSurroundingBombs;
                                }
                            }
                        }
                    }
                }
                if (boardStr[i][j].equals("X")) {
                    board[i][j] = new Square(true, -1);
                    ++numBombsInitial;
                } else {
                    board[i][j] = new Square(false, numSurroundingBombs);
                }
            }
        }
        state = State.ACTIVE;
        numBombsUnflagged = numBombsInitial;
        numFlags = 0;
        checkRep();
    }

    /* Operations */
    /**
     * Dig a square.
     * @param x x coordinate
     * @param y y coordinate
     * @return an OpResult
     */
    public synchronized OpResult dig(int x, int y) {
        OpResult opResult = doOp(() -> board[x][y].dig(), x, y);
        if (opResult == OpResult.OK && board[x][y].state() == Square.State.EXPLODED) {
            --numBombsUnflagged;
            if (numBombsUnflagged == 0) {
                state = State.FROZEN;
            }
            opResult = OpResult.BOOM;
        }
        checkRep();
        return opResult;
    }

    /**
     * Flag a square.
     * @param x x coordinate
     * @param y y coordinate
     * @return an OpResult
     */
    public synchronized OpResult flag(int x, int y) {
        OpResult opResult = doOp(() -> board[x][y].flag(), x, y);
        if (opResult == OpResult.OK) {
            ++numFlags;
        }
        if (board[x][y].hasBomb()) {
            --numBombsUnflagged;
            if (numBombsUnflagged == 0) {
                state = State.FROZEN;
            }
        }
        checkRep();
        return opResult;
    }

    /**
     * Deflag a square.
     * @param x x coordinate
     * @param y y coordinate
     * @return an OpResult
     */
    public synchronized OpResult deflag(int x, int y) {
        OpResult opResult = doOp(() -> board[x][y].deflag(), x, y);
        if (opResult == OpResult.OK) {
            --numFlags;
        }
        if (board[x][y].hasBomb()) {
            ++numBombsUnflagged;
        }
        checkRep();
        return opResult;
    }

    /* Observers */
    public synchronized State state() {
        return state;
    }

    public synchronized int dim() {
        return dim;
    }

    public synchronized int numBombsInitial() {
        return numBombsInitial;
    }

    public synchronized int numBombsUnflagged() {
        return numBombsUnflagged;
    }

    public synchronized int numFlags() {
        return numFlags;
    }

    public synchronized Square.State squareState(int x, int y) {
        return board[x][y].state();
    }

    public synchronized int numSurroundingBombs(int x, int y) {
        return board[x][y].numSurroundingBombs();
    }

    @Override
    public synchronized String toString() {
        StringBuilder boardSb = new StringBuilder();
        for (Square[] squares : board) {
            StringBuilder lineSb = new StringBuilder();
            for (Square sq : squares) {
                lineSb.append(String.format("%s ", sq));
            }
            lineSb.replace(squares.length*2 - 1, squares.length*2, String.format("%n"));
            boardSb.append(lineSb);
        }
        return boardSb.toString();
    }

    private boolean isInBoard(int i) {
        return i >= 0 && i < dim;
    }

    private boolean isInBoard(int x, int y) {
        return isInBoard(x) && isInBoard(y);
    }

    private OpResult doOp(SquareOp op, int x, int y) {
        if (state == State.FROZEN) {
            return OpResult.GAME_OVER;
        }
        if (!isInBoard(x, y)) {
            return OpResult.ILLEGAL_MOVE;
        }
        boolean opOk = op.doOp();
        if (!opOk) {
            return OpResult.ILLEGAL_MOVE;
        }
        return OpResult.OK;
    }

    private void checkRep() {
        assert 0 <= numBombsUnflagged && numBombsUnflagged <= numBombsInitial;
        assert numFlags >= 0;
        if (numBombsUnflagged == 0) {
            assert state == State.FROZEN;
        } else {
            assert state == State.ACTIVE;
        }
    }
}
