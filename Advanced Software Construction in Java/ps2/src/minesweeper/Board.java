/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

/** Thread-safe Minesweeper board */
public class Board {
    public enum OpResult { OK, ILLEGAL_STATE, BOOM, GAME_ENDED }
    public enum State { ACTIVE, FROZEN }

    private Square[][] board;
    private State state;
    private int numBombsInitial;
    private int numBombsRemaining;
    private int numFlags;

    public Board(String file) {
        throw new RuntimeException("Unimplemented");
    }

    public Board(int dim, int bombs) {
        throw new RuntimeException("Unimplemented");
    }

    /* Operations */

    public OpResult dig(int x, int y) {
        throw new RuntimeException("Unimplemented");
    }

    public OpResult flag(int x, int y) {
        throw new RuntimeException("Unimplemented");
    }

    public OpResult deflag(int x, int y) {
        throw new RuntimeException("Unimplemented");
    }

    /* Observers */

    public State state() {
        throw new RuntimeException("Unimplemented");
    }

    public int numBombsInitial() {
        throw new RuntimeException("Unimplemented");
    }

    public int numBombsRemaining() {
        throw new RuntimeException("Unimplemented");
    }

    public int numFlags() {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public String toString() {
        throw new RuntimeException("Unimplemented");
    }
}
