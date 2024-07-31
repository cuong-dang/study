package edu.caltech.nanodb.plannodes;


import edu.caltech.nanodb.expressions.*;
import edu.caltech.nanodb.queryeval.ColumnStats;
import edu.caltech.nanodb.queryeval.PlanCost;
import edu.caltech.nanodb.relations.JoinType;
import edu.caltech.nanodb.relations.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


/**
 * This plan node implements a nested-loop join operation, which can support
 * arbitrary join conditions but is also the slowest join implementation.
 */
public class NestedLoopJoinNode extends ThetaJoinNode {
    /** A logging object for reporting anything interesting that happens. */
    private static Logger logger = LogManager.getLogger(NestedLoopJoinNode.class);


    /** Most recently retrieved tuple of the left relation. */
    private Tuple leftTuple;

    private boolean leftTupleMatched;

    /** Most recently retrieved tuple of the right relation. */
    private Tuple rightTuple;


    /** Set to true when we have exhausted all tuples from our subplans. */
    private boolean done;


    public NestedLoopJoinNode(PlanNode leftChild, PlanNode rightChild,
                              JoinType joinType, Expression predicate) {

        super(leftChild, rightChild, joinType, predicate);
        if (joinType == JoinType.RIGHT_OUTER) {
            swap();
            this.joinType = JoinType.LEFT_OUTER;
        }
    }


    /**
     * Checks if the argument is a plan node tree with the same structure, but not
     * necessarily the same references.
     *
     * @param obj the object to which we are comparing
     */
    @Override
    public boolean equals(Object obj) {

        if (obj instanceof NestedLoopJoinNode) {
            NestedLoopJoinNode other = (NestedLoopJoinNode) obj;

            return predicate.equals(other.predicate) &&
                leftChild.equals(other.leftChild) &&
                rightChild.equals(other.rightChild);
        }

        return false;
    }


    /** Computes the hash-code of the nested-loop plan node. */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (predicate != null ? predicate.hashCode() : 0);
        hash = 31 * hash + leftChild.hashCode();
        hash = 31 * hash + rightChild.hashCode();
        return hash;
    }


    /**
     * Returns a string representing this nested-loop join's vital information.
     *
     * @return a string representing this plan-node.
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("NestedLoop[");

        if (predicate != null)
            buf.append("pred:  ").append(predicate);
        else
            buf.append("no pred");

        if (schemaSwapped)
            buf.append(" (schema swapped)");

        buf.append(']');

        return buf.toString();
    }


    /**
     * Creates a copy of this plan node and its subtrees.
     */
    @Override
    protected PlanNode clone() throws CloneNotSupportedException {
        NestedLoopJoinNode node = (NestedLoopJoinNode) super.clone();

        // Clone the predicate.
        if (predicate != null)
            node.predicate = predicate.duplicate();
        else
            node.predicate = null;

        return node;
    }


    /**
     * Nested-loop joins can conceivably produce sorted results in situations
     * where the outer relation is ordered, but we will keep it simple and just
     * report that the results are not ordered.
     */
    @Override
    public List<OrderByExpression> resultsOrderedBy() {
        return null;
    }


    /** True if the node supports position marking. **/
    public boolean supportsMarking() {
        return leftChild.supportsMarking() && rightChild.supportsMarking();
    }


    /** True if the node requires that its left child supports marking. */
    public boolean requiresLeftMarking() {
        return false;
    }


    /** True if the node requires that its right child supports marking. */
    public boolean requiresRightMarking() {
        return false;
    }


    @Override
    public void prepare() {
        // Need to prepare the left and right child-nodes before we can do
        // our own work.
        leftChild.prepare();
        rightChild.prepare();

        // Use the parent class' helper-function to prepare the schema.
        prepareSchemaStats();

        PlanCost leftCost = leftChild.cost, rightCost = rightChild.cost;
        cost = new PlanCost(
                leftCost.numTuples * rightCost.numTuples,
                leftCost.tupleSize + rightCost.tupleSize,
                leftCost.cpuCost + rightCost.cpuCost + (leftCost.numTuples * rightCost.numTuples),
                leftCost.numBlockIOs + rightCost.numBlockIOs,
                leftCost.numLargeSeeks + rightCost.numLargeSeeks
        );
        if (!(predicate instanceof CompareOperator)) return;
        Expression leftCol = ((CompareOperator) predicate).getLeftExpression(),
                rightCol = ((CompareOperator) predicate).getRightExpression();
        if (!(leftCol instanceof ColumnValue || rightCol instanceof ColumnValue)) return;
        ColumnName leftColName = ((ColumnValue) leftCol).getColumnName(),
                rightColName = ((ColumnValue) rightCol).getColumnName();
        ColumnStats leftColStat = stats.get(schema.getColumnIndex(leftColName)),
                rightColStat = stats.get(schema.getColumnIndex(rightColName));
        cost.numTuples /= Math.max(leftColStat.getNumUniqueValues(), rightColStat.getNumUniqueValues());
}


    public void initialize() {
        super.initialize();

        done = false;
        leftTuple = null;
        rightTuple = null;
    }


    /**
     * Returns the next joined tuple that satisfies the join condition.
     *
     * @return the next joined tuple that satisfies the join condition.
     */
    public Tuple getNextTuple() {
        if (done)
            return null;

        while (getTuplesToJoin()) {
            if (rightTuple == null || canJoinTuples()) {
                leftTupleMatched = true;
                return joinTuples(leftTuple, rightTuple == null
                        ? TupleLiteral.ofSize(rightSchema.numColumns())
                        : rightTuple);
            }
        }

        return null;
    }


    /**
     * This helper function implements the logic that sets {@link #leftTuple}
     * and {@link #rightTuple} based on the nested-loops logic.
     *
     * @return {@code true} if another pair of tuples was found to join, or
     *         {@code false} if no more pairs of tuples are available to join.
     */
    private boolean getTuplesToJoin() {
        if (leftTuple == null) {
            leftTuple = leftChild.getNextTuple();
            leftTupleMatched = false;
            if (leftTuple == null) {
                done = true;
                return false;
            }
            rightChild.initialize();
        }
        rightTuple = rightChild.getNextTuple();
        if (rightTuple == null) {
            if (!leftTupleMatched && joinType == JoinType.LEFT_OUTER) {
                return true;
            }
            leftTuple = null;
            return getTuplesToJoin();
        }
        return true;
    }


    private boolean canJoinTuples() {
        // If the predicate was not set, we can always join them!
        if (predicate == null)
            return true;

        environment.clear();
        environment.addTuple(leftSchema, leftTuple);
        environment.addTuple(rightSchema, rightTuple);

        return predicate.evaluatePredicate(environment);
    }


    public void markCurrentPosition() {
        leftChild.markCurrentPosition();
        rightChild.markCurrentPosition();
    }


    public void resetToLastMark() throws IllegalStateException {
        leftChild.resetToLastMark();
        rightChild.resetToLastMark();

        // TODO:  Prepare to reevaluate the join operation for the tuples.
        //        (Just haven't gotten around to implementing this.)
    }


    public void cleanUp() {
        leftChild.cleanUp();
        rightChild.cleanUp();
    }
}
