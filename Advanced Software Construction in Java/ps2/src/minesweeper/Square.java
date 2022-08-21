package minesweeper;

/** Square in Minesweeper board */
public class Square {
    public enum State { UNTOUCHED, DUG, FLAGGED, EXPLODED }

    private State state;
    private final boolean hasBomb;
    private int numSurroundingBombs;

    /* Specs */
    // Abstraction function
    //   represent a square in Minesweeper board
    //   could contain bomb
    // Rep invariant
    //   state is not null
    //   0 <= numSurroundingBombs <= 8
    // Rep exposure
    //   all fields are private
    //   state, hasBomb, numSurroundingBombs are immutable types
    // Thread safety
    //   client's responsibility

    /**
     * Construct a square.
     * @param hasBomb whether the square contains a bomb
     * @param numSurroundingBombs number of bombs in surrounding squares
     * @throws IllegalArgumentException if invalid number of surrounding bombs
     */
    public Square(boolean hasBomb, int numSurroundingBombs) {
        checkNumSurroundingBombs(hasBomb, numSurroundingBombs);
        this.state = State.UNTOUCHED;
        this.hasBomb = hasBomb;
        this.numSurroundingBombs = numSurroundingBombs;
        checkRep();
    }

    /** @return state. */
    public State state() {
        return state;
    }

    /** @return whether square contains bomb. */
    public boolean hasBomb() {
        return hasBomb;
    }

    /** @return number of surrounding bombs. */
    public int numSurroundingBombs() {
        return numSurroundingBombs;
    }

    /**
     * Perform dig operation.
     * @return true if original state is UNTOUCHED, false otherwise.
     */
    public boolean dig() {
        if (!hasBomb) {
            return doOp(State.UNTOUCHED, State.DUG);
        } else {
            return doOp(State.UNTOUCHED, State.EXPLODED);
        }
    }

    /**
     * Perform flag operation.
     * @return true if original state is UNTOUCHED, false otherwise.
     */
    public boolean flag() {
        return doOp(State.UNTOUCHED, State.FLAGGED);
    }

    /**
     * Perform dig operation.
     * @return true if original state is FLAGGED, false otherwise.
     */
    public boolean deflag() {
        return doOp(State.FLAGGED, State.UNTOUCHED);
    }

    /**
     * Perform op helper.
     * @param legalState state square must be in to perform the top
     * @param newState state after the op
     * @return true if state was in legaState, false otherwise
     */
    private boolean doOp(State legalState, State newState) {
        boolean result;
        if (state == legalState) {
            state = newState;
            result = true;
        } else {
            result = false;
        }
        checkRep();
        return result;
    }

    /**
     * Set the number of surrounding bombs.
     * @param numSurroundingBombs number of surrounding bombs
     * @throws IllegalStateException if invalid
     */
    public void setNumSurroundingBombs(int numSurroundingBombs) {
        checkNumSurroundingBombs(hasBomb, numSurroundingBombs);
        this.numSurroundingBombs = numSurroundingBombs;
    }

    /**
     * Check if the number of surrounding bombs are valid.
     *   0 <= numSurroundingBombs <= 8 ||
     *   hasBomb && numSurroundingBombs = -1
     * @param hasBomb whether the square contains a bomb
     * @param numSurroundingBombs number of surrounding bombs
     * @throws IllegalStateException if invalid
     */
    private void checkNumSurroundingBombs(boolean hasBomb, int numSurroundingBombs) {
        if (hasBomb && numSurroundingBombs != -1) {
            throw new IllegalArgumentException(
                    String.format("Illegal number of surrounding bombs for bomb square: %d", numSurroundingBombs)
            );
        }
        if (!hasBomb && (numSurroundingBombs < 0 || numSurroundingBombs > 8)) {
            throw new IllegalArgumentException(
                    String.format("Illegal number of surrounding bombs for non-bomb square: %d", numSurroundingBombs)
            );
        }
    }

    private void checkRep() {
        assert state != null;
        assert (hasBomb && numSurroundingBombs == -1) ||
                (!hasBomb && 0 <= numSurroundingBombs  && numSurroundingBombs <= 8);
    }

    @Override
    public String toString() {
        if (state == State.UNTOUCHED) {
            return "-";
        } else if (state == State.FLAGGED) {
            return "F";
        } else if (state == State.DUG && numSurroundingBombs == 0) {
            return " ";
        } else if (state == State.DUG && numSurroundingBombs != 0) {
            return String.format("%d", numSurroundingBombs);
        } else if (state == State.EXPLODED) {
            return "X";
        }
        throw new RuntimeException(String.format("Unknown square state %s", state));
    }
}
