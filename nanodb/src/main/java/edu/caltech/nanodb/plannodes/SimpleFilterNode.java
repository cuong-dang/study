package edu.caltech.nanodb.plannodes;


import edu.caltech.nanodb.expressions.Expression;
import edu.caltech.nanodb.expressions.OrderByExpression;
import edu.caltech.nanodb.queryeval.*;

import java.util.ArrayList;
import java.util.List;


/**
 * This select plan node implements a simple filter of a subplan based on a
 * predicate.
 */
public class SimpleFilterNode extends SelectNode {

    public SimpleFilterNode(PlanNode child, Expression predicate) {
        super(child, predicate);
    }


    /**
     * Returns true if the passed-in object is a <tt>SimpleFilterNode</tt> with
     * the same predicate and child sub-expression.
     *
     * @param obj the object to check for equality
     * @return true if the passed-in object is equal to this object; false
     * otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleFilterNode) {
            SimpleFilterNode other = (SimpleFilterNode) obj;
            return leftChild.equals(other.leftChild) &&
                    predicate.equals(other.predicate);
        }
        return false;
    }


    /**
     * Computes the hashcode of a PlanNode.  This method is used to see if two
     * plan nodes CAN be equal.
     **/
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (predicate != null ? predicate.hashCode() : 0);
        hash = 31 * hash + leftChild.hashCode();
        return hash;
    }


    /**
     * Creates a copy of this simple filter node node and its subtree.  This
     * method is used by {@link PlanNode#duplicate} to copy a plan tree.
     */
    @Override
    protected PlanNode clone() throws CloneNotSupportedException {
        SimpleFilterNode node = (SimpleFilterNode) super.clone();

        // Copy the subtree.
        node.leftChild = leftChild.duplicate();

        return node;
    }


    @Override
    public String toString() {
        return "SimpleFilter[pred:  " + predicate.toString() + "]";
    }


    /**
     * This node's results are sorted if its subplan produces sorted results.
     */
    public List<OrderByExpression> resultsOrderedBy() {
        return leftChild.resultsOrderedBy();
    }


    /**
     * This node supports marking if its subplan supports marking.
     */
    public boolean supportsMarking() {
        return leftChild.supportsMarking();
    }


    /**
     * The simple filter node doesn't require any marking from either child.
     */
    public boolean requiresLeftMarking() {
        return false;
    }


    /**
     * The simple filter node doesn't require any marking from either child.
     */
    public boolean requiresRightMarking() {
        return false;
    }


    // Inherit javadocs from base class.
    public void prepare() {
        // Need to prepare the left child-node before we can do our own work.
        leftChild.prepare();

        // Grab the schema and stats from the left child.
        schema = leftChild.getSchema();
        ArrayList<ColumnStats> childStats = leftChild.getStats();

        cost = new PlanCost(
                leftChild.cost.numTuples *
                        SelectivityEstimator.estimateSelectivity(predicate, schema, childStats),
                leftChild.cost.tupleSize,
                leftChild.cost.cpuCost + leftChild.cost.numTuples,
                leftChild.cost.numBlockIOs,
                leftChild.cost.numLargeSeeks
        );
        ExpressionCostCalculator ecc = new ExpressionCostCalculator();
        predicate.traverse(ecc);
        cost.cpuCost += leftChild.cost.numTuples * ecc.getCost();

        stats = StatisticsUpdater.updateStats(predicate, schema, childStats);
    }


    public void initialize() {
        super.initialize();

        leftChild.initialize();
    }


    public void cleanUp() {
        leftChild.cleanUp();
    }


    protected void advanceCurrentTuple() {
        currentTuple = leftChild.getNextTuple();
    }


    /**
     * The simple filter node relies on marking/reset support in its subplan.
     */
    public void markCurrentPosition() {
        leftChild.markCurrentPosition();
    }


    /**
     * The simple filter node relies on marking/reset support in its subplan.
     */
    public void resetToLastMark() {
        leftChild.resetToLastMark();
    }
}
