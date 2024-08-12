package edu.caltech.nanodb.plannodes;


import edu.caltech.nanodb.expressions.*;
import edu.caltech.nanodb.relations.Tuple;


/**
 * PlanNode representing the <tt>WHERE</tt> clause in a <tt>SELECT</tt>
 * operation.  This is the relational algebra Select operator.
 */
public abstract class SelectNode extends PlanNode {

    /**
     * Predicate used for selection.
     */
    public Expression predicate;


    /**
     * The current tuple that the node is selecting.
     */
    protected Tuple currentTuple;


    /**
     * True if we have finished scanning or pulling tuples from children.
     */
    private boolean done;


    /**
     * Constructs a SelectNode that scans a file for tuples.
     *
     * @param predicate the selection criterion
     */
    protected SelectNode(Expression predicate) {
        super();
        this.predicate = predicate;
    }


    protected SelectNode(PlanNode leftChild, Expression predicate) {
        super(leftChild);
        this.predicate = predicate;
    }


    /**
     * Creates a copy of this select node and its subtree.  This method is used
     * by {@link PlanNode#duplicate} to copy a plan tree.
     */
    @Override
    protected PlanNode clone() throws CloneNotSupportedException {
        SelectNode node = (SelectNode) super.clone();

        // Copy the predicate.
        if (predicate != null)
            node.predicate = predicate.duplicate();
        else
            node.predicate = null;

        return node;
    }


    /**
     * Do initialization for the select operation. Resets state variables.
     */
    @Override
    public void initialize() {
        super.initialize();

        currentTuple = null;
        done = false;
    }


    /**
     * Gets the next tuple selected by the predicate.
     *
     * @return the tuple to be passed up to the next node.
     * @throws java.lang.IllegalStateException if this is a scanning node
     *                                         with no algorithm or a filtering node with no child, or if
     *                                         the leftChild threw an IllegalStateException.
     */
    public Tuple getNextTuple() {

        // If this node is finished finding tuples, return null until it is
        // re-initialized.
        if (done)
            return null;

        // Continue to advance the current tuple until it is selected by the
        // predicate.
        while (true) {
            advanceCurrentTuple();

            // If the last tuple in the file (or chain of nodes) did not
            // satisfy the predicate, then the selection process is over,
            // so set the done flag and break out of the loop.
            if (currentTuple == null) {
                done = true;
                break;
            }

            // If we found a tuple that satisfies the predicate, break out of
            // the loop!
            if (isTupleSelected(currentTuple))
                break;
            currentTuple.unpin();
        }

        // The current tuple now satisfies the predicate, so return it.
        // Short-circuit optimization in case of PK = value. We only support one-column PKs.
        if (schema.getPrimaryKey() != null && schema.getPrimaryKey().size() == 1 &&
                predicate instanceof CompareOperator) {
            CompareOperator comp = (CompareOperator) predicate;
            comp.normalize();
            if (comp.getType() == CompareOperator.Type.EQUALS &&
                    comp.getLeftExpression() instanceof ColumnValue &&
                    comp.getRightExpression() instanceof LiteralValue) {
                ColumnName pkName = schema.getColumnInfo(schema.getPrimaryKey().getCol(0)).getColumnName();
                ColumnName colName = ((ColumnValue) comp.getLeftExpression()).getColumnName();
                if (colName.equals(pkName)) {
                    done = true;
                }
            }
        }
        return currentTuple;
    }


    /**
     * Helper function that advances the current tuple reference in the node.
     */
    protected abstract void advanceCurrentTuple();


    protected boolean isTupleSelected(Tuple tuple) {
        // If the predicate was not set, return true.
        if (predicate == null)
            return true;

        // Set up the environment and then evaluate the predicate!

        environment.clear();
        environment.addTuple(schema, tuple);
        return predicate.evaluatePredicate(environment);
    }
}
