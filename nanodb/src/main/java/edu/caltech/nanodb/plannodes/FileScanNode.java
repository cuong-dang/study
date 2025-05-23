package edu.caltech.nanodb.plannodes;


import edu.caltech.nanodb.expressions.Expression;
import edu.caltech.nanodb.expressions.OrderByExpression;
import edu.caltech.nanodb.indexes.IndexInfo;
import edu.caltech.nanodb.queryeval.*;
import edu.caltech.nanodb.relations.TableInfo;
import edu.caltech.nanodb.storage.FilePointer;
import edu.caltech.nanodb.storage.TupleFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


/**
 * <p>
 * A select plan-node that scans a tuple file, checking the optional predicate
 * against each tuple in the file.  Note that there are no optimizations used
 * if the tuple file is a sequential tuple file or a hashed tuple file.
 * </p>
 * <p>
 * This plan node can also be used with indexes, when a "file-scan" is to be
 * performed over all of the index's tuples, in whatever order the index will
 * produce the tuples.  If the planner wishes to take advantage of an index's
 * ability to look up tuples based on various values, the {@link IndexScanNode}
 * should be used instead.
 * </p>
 */
public class FileScanNode extends SelectNode {

    /**
     * A logging object for reporting anything interesting that happens.
     */
    private static Logger logger = LogManager.getLogger(FileScanNode.class);


    /**
     * The table-info for the table being scanned, or {@code null} if the node
     * is performing a scan over an index.
     */
    private TableInfo tableInfo;


    /**
     * The index-info for the index being scanned, or {@code null} if the node
     * is performing a scan over a table.
     */
    private IndexInfo indexInfo;


    /**
     * The table to select from if this node is a leaf.
     */
    private TupleFile tupleFile;


    /**
     * This field allows the file-scan node to mark a particular tuple in the
     * tuple-stream and then rewind to that point in the tuple-stream.
     */
    private FilePointer markedTuple;


    private boolean jumpToMarkedTuple;


    /**
     * Construct a file scan node that traverses a table file.
     *
     * @param tableInfo the information about the table being scanned
     * @param predicate an optional predicate for selection, or {@code null}
     *                  if all rows in the table should be included in the output
     */
    public FileScanNode(TableInfo tableInfo, Expression predicate) {
        super(predicate);

        if (tableInfo == null)
            throw new IllegalArgumentException("tableInfo cannot be null");

        this.tableInfo = tableInfo;
        tupleFile = tableInfo.getTupleFile();
    }


    /**
     * Construct a file scan node that traverses an index file.
     *
     * @param indexInfo the information about the index being scanned
     * @param predicate an optional predicate for selection, or {@code null}
     *                  if all rows in the index should be included in the output
     */
    public FileScanNode(IndexInfo indexInfo, Expression predicate) {
        super(predicate);

        if (indexInfo == null)
            throw new IllegalArgumentException("indexInfo cannot be null");

        this.indexInfo = indexInfo;
        tupleFile = indexInfo.getTupleFile();
    }


    /**
     * Returns true if the passed-in object is a <tt>FileScanNode</tt> with
     * the same predicate and table.
     *
     * @param obj the object to check for equality
     * @return true if the passed-in object is equal to this object; false
     * otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileScanNode) {
            FileScanNode other = (FileScanNode) obj;
            // We don't include the table-info or the index-info since each
            // table or index is in its own tuple file.
            return tupleFile.equals(other.tupleFile) &&
                    predicate.equals(other.predicate);
        }

        return false;
    }


    /**
     * Computes the hashcode of a PlanNode.  This method is used to see if two
     * plan nodes CAN be equal.
     **/
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (predicate != null ? predicate.hashCode() : 0);
        // We don't include the table-info or the index-info since each table
        // or index is in its own tuple file.
        hash = 31 * hash + tupleFile.hashCode();
        return hash;
    }


    /**
     * Creates a copy of this simple filter node node and its subtree.  This
     * method is used by {@link PlanNode#duplicate} to copy a plan tree.
     */
    @Override
    protected PlanNode clone() throws CloneNotSupportedException {
        FileScanNode node = (FileScanNode) super.clone();

        // TODO:  Should we clone these?
        node.tableInfo = tableInfo;
        node.indexInfo = indexInfo;

        // The tuple file doesn't need to be copied since it's immutable.
        node.tupleFile = tupleFile;

        return node;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("FileScan[");
        if (tableInfo != null) {
            buf.append("table:  ").append(tableInfo.getTableName());
        } else if (indexInfo != null) {
            buf.append("index:  ").append(indexInfo.getTableName());
            buf.append('.').append(indexInfo.getIndexName());
        } else {
            throw new IllegalStateException("Both tableInfo and indexInfo " +
                    "are null!");
        }

        if (predicate != null)
            buf.append(", pred:  ").append(predicate.toString());

        buf.append("]");

        return buf.toString();
    }


    /**
     * Currently we will always say that the file-scan node produces unsorted
     * results.  In actuality, a file scan's results will be sorted if the table
     * file uses a sequential format, but currently we don't have any sequential
     * file formats.
     */
    public List<OrderByExpression> resultsOrderedBy() {
        return null;
    }


    /**
     * This node supports marking.
     */
    public boolean supportsMarking() {
        return true;
    }


    /**
     * This node has no children so of course it doesn't require marking.
     */
    public boolean requiresLeftMarking() {
        return false;
    }


    /**
     * This node has no children so of course it doesn't require marking.
     */
    public boolean requiresRightMarking() {
        return false;
    }


    protected void prepareSchema() {
        // Grab the schema from the table.
        schema = tupleFile.getSchema();
    }


    // Inherit javadocs from base class.
    public void prepare() {
        // Grab the schema and statistics from the table file.

        schema = tupleFile.getSchema();
        TableStats tableStats = tupleFile.getStats();

        cost = new PlanCost(
                tableStats.numTuples,
                tableStats.avgTupleSize,
                tableStats.numTuples,
                tableStats.numDataPages,
                1);
        if (predicate != null) {
            cost.numTuples *= SelectivityEstimator.estimateSelectivity(predicate, schema,
                    tableStats.getAllColumnStats());
            ExpressionCostCalculator ecc = new ExpressionCostCalculator();
            predicate.traverse(ecc);
            cost.cpuCost += tableStats.numTuples * ecc.getCost();
        }
        stats = StatisticsUpdater.updateStats(predicate, schema, tableStats.getAllColumnStats());
    }


    public void initialize() {
        super.initialize();

        // Reset our marking state.
        markedTuple = null;
        jumpToMarkedTuple = false;
    }


    public void cleanUp() {
        // Nothing to do!
    }


    /**
     * Advances the current tuple forward for a file scan. Grabs the first
     * tuple if current is null.  Otherwise gets the next tuple.
     */
    protected void advanceCurrentTuple() {

        if (jumpToMarkedTuple) {
            logger.debug("Resuming at previously marked tuple.");
            currentTuple = tupleFile.getTuple(markedTuple);
            jumpToMarkedTuple = false;

            return;
        }

        if (currentTuple == null)   // Get the first tuple.
            currentTuple = tupleFile.getFirstTuple();
        else                        // Get the next tuple.
            currentTuple = tupleFile.getNextTuple(currentTuple);
    }


    public void markCurrentPosition() {
        if (currentTuple == null)
            throw new IllegalStateException("There is no current tuple!");

        logger.debug("Marking current position in tuple-stream.");
        markedTuple = currentTuple.getExternalReference();
    }


    public void resetToLastMark() {
        if (markedTuple == null)
            throw new IllegalStateException("There is no last-marked tuple!");

        logger.debug("Resetting to previously marked position in tuple-stream.");
        jumpToMarkedTuple = true;
    }
}
