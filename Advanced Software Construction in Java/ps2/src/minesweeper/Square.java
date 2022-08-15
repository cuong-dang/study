package minesweeper;

/** Square in Minesweeper board */
public class Square {
    public enum State { UNTOUCHED, DUG, FLAGGED, EXPLODED }

    private State state;
    private final boolean hasBomb;

    /* Specs */
    // Abstraction function
    //   represent a square in Minesweeper board
    //   could contain bomb
    // Rep invariant
    //   state is not null
    // Rep exposure
    //   all fields are private
    //   state and hasBomb are immutable types
    // Thread safety
    //   client's responsibility

    /**
     * Construct a square.
     * @param hasBomb whether the square contains a bomb
     */
    public Square(boolean hasBomb) {
        this.state = State.UNTOUCHED;
        this.hasBomb = hasBomb;
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

    private void checkRep() {
        assert state != null;
    }
}
