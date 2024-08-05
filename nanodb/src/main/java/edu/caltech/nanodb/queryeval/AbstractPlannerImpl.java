package edu.caltech.nanodb.queryeval;

import edu.caltech.nanodb.expressions.AggregateCollector;
import edu.caltech.nanodb.expressions.Expression;
import edu.caltech.nanodb.plannodes.*;
import edu.caltech.nanodb.queryast.FromClause;
import edu.caltech.nanodb.queryast.SelectClause;
import edu.caltech.nanodb.queryast.SelectValue;
import edu.caltech.nanodb.relations.TableInfo;
import edu.caltech.nanodb.storage.PageTuple;
import edu.caltech.nanodb.storage.StorageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class AbstractPlannerImpl implements Planner {
    /**
     * A logging object for reporting anything interesting that happens.
     */
    protected static Logger logger = LogManager.getLogger(SimplestPlanner.class);

    /**
     * The storage manager used during query planning.
     */
    protected StorageManager storageManager;

    /**
     * Sets the server to be used during query planning.
     */
    public void setStorageManager(StorageManager storageManager) {
        if (storageManager == null)
            throw new IllegalArgumentException("storageManager cannot be null");

        this.storageManager = storageManager;
    }

    /**
     * Returns the root of a plan tree suitable for executing the specified
     * query.
     *
     * @param selClause an object describing the query to be performed
     * @return a plan tree for executing the specified query
     */
    @Override
    public PlanNode makePlan(SelectClause selClause,
                             List<SelectClause> enclosingSelects) {
        PlanNode plan = makeUnpreparedPlan(selClause);
        plan.prepare();
        return plan;
    }

    protected PlanNode makeUnpreparedPlan(SelectClause selClause) {
        PlanNode plan;
        Expression predicate = selClause.getWhereExpr();

        checkInvalidAggregates(selClause, predicate);
        planSubqueries(selClause);

        /* FROM */
        if (selClause.getFromClause() == null) { // literals
            return new ProjectNode(selClause.getSelectValues());
        }
        plan = handleFromWhere(selClause.getFromClause(), predicate);
        /* GROUP BY & HAVING */
        plan = handleGroupByHaving(plan, selClause);
        /* ORDER BY */
        if (!selClause.getOrderByExprs().isEmpty()) {
            plan = new SortNode(plan, selClause.getOrderByExprs());
        }
        return new ProjectNode(plan, selClause.getSelectValues());
    }

    private void checkInvalidAggregates(SelectClause selClause, Expression predicate) {
        AggregateCollector scanner = new AggregateCollector(false);
        /* WHERE */
        if (predicate != null)
            predicate.traverse(scanner);
        /* ON */
        if (selClause.getFromClause() != null && selClause.getFromClause().isJoinExpr() &&
                selClause.getFromClause().getComputedJoinExpr() != null) {
            selClause.getFromClause().getComputedJoinExpr().traverse(scanner);
        }
        /* GROUP BY */
        for (Expression groupBy : selClause.getGroupByExprs()) {
            groupBy.traverse(scanner);
        }
    }

    private PlanNode handleGroupByHaving(PlanNode plan, SelectClause selClause) {
        AggregateCollector collector = new AggregateCollector(true);
        selClause.getSelectValues().stream()
                .filter(SelectValue::isExpression)
                .forEach(sv -> {
                    Expression e = sv.getExpression().traverse(collector);
                    sv.setExpression(e);
                });
        if (selClause.getHavingExpr() != null)
            selClause.getHavingExpr().traverse(collector);
        if (selClause.getGroupByExprs().isEmpty() && collector.getAggregates().isEmpty())
            return plan;
        PlanNode aggNode = new HashedGroupAggregateNode(plan, selClause.getGroupByExprs(),
                collector.getAggregates());
        if (selClause.getHavingExpr() != null) {
            aggNode = PlanUtils.addPredicateToPlan(aggNode, selClause.getHavingExpr());
        }
        return aggNode;
    }

    private void planSubqueries(SelectClause selClause) {
        ExpressionPlanner ep = new ExpressionPlanner(this);
        /* SELECT */
        selClause.getSelectValues().stream()
                .filter(SelectValue::isExpression)
                .forEach(sv -> sv.getExpression().traverse(ep));
        /* WHERE */
        if (selClause.getWhereExpr() != null)
            selClause.getWhereExpr().traverse(ep);
        /* HAVING */
        if (selClause.getHavingExpr() != null)
            selClause.getHavingExpr().traverse(ep);
    }

    /**
     * Constructs a simple select plan that reads directly from a table, with
     * an optional predicate for selecting rows.
     * <p>
     * While this method can be used for building up larger <tt>SELECT</tt>
     * queries, the returned plan is also suitable for use in <tt>UPDATE</tt>
     * and <tt>DELETE</tt> command evaluation.  In these cases, the plan must
     * only generate tuples of type {@link PageTuple},
     * so that the command can modify or delete the actual tuple in the file's
     * page data.
     *
     * @param tableName The name of the table that is being selected from.
     * @param predicate An optional selection predicate, or {@code null} if
     *                  no filtering is desired.
     * @return A new plan-node for evaluating the select operation.
     */
    public SelectNode makeSimpleSelect(String tableName, Expression predicate,
                                       List<SelectClause> enclosingSelects) {
        if (tableName == null)
            throw new IllegalArgumentException("tableName cannot be null");

        if (enclosingSelects != null) {
            // If there are enclosing selects, this subquery's predicate may
            // reference an outer query's value, but we don't detect that here.
            // Therefore we will probably fail with an unrecognized column
            // reference.
            logger.warn("Currently we are not clever enough to detect " +
                    "correlated subqueries, so expect things are about to break...");
        }

        // Open the table.
        TableInfo tableInfo =
                storageManager.getTableManager().openTable(tableName);

        // Make a SelectNode to read rows from the table, with the specified
        // predicate.
        SelectNode selectNode = new FileScanNode(tableInfo, predicate);
        selectNode.prepare();
        return selectNode;
    }

    abstract PlanNode handleFromWhere(FromClause fromClause, Expression predicate);
}
