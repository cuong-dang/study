package edu.caltech.nanodb.queryast;


import edu.caltech.nanodb.expressions.ColumnName;
import edu.caltech.nanodb.expressions.Expression;
import edu.caltech.nanodb.expressions.OrderByExpression;
import edu.caltech.nanodb.queryeval.InvalidSQLException;
import edu.caltech.nanodb.relations.ColumnInfo;
import edu.caltech.nanodb.relations.Schema;
import edu.caltech.nanodb.relations.SchemaNameException;
import edu.caltech.nanodb.storage.TableManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


/**
 * This class represents a single <tt>SELECT ...</tt> statement or clause.
 * <tt>SELECT</tt> statements can appear as clauses within other expressions,
 * so the class is written to be used easily within other classes.
 */
public class SelectClause {

    /** A logging object for reporting anything interesting that happens. **/
    private static Logger logger = LogManager.getLogger(SelectClause.class);


    /**
     * The parent of this select-clause, if it is a nested subquery;
     * {@code null} otherwise.
     */
    private SelectClause parentSelect;


    /**
     * This flag indicates whether the <tt>SELECT</tt> expression should
     * generate duplicate rows, or whether it should simply produce distinct
     * or unique rows.
     */
    private boolean distinct = false;


    /**
     * The specification of values to be produced by the <tt>SELECT</tt>
     * clause.  These expressions comprise the Generalized Project operation
     * portion of the command.
     */
    private List<SelectValue> selectValues = new ArrayList<>();


    /**
     * This field holds a hierarchy of one or more base and derived relations
     * that produce the rows considered by this <tt>SELECT</tt> clause.  If
     * the <tt>SELECT</tt> expression has no <tt>FROM</tt> clause, this field
     * will be <tt>null</tt>.
     */
    private FromClause fromClause = null;


    /**
     * If a <tt>WHERE</tt> expression is specified, this field will refer to
     * the expression to be evaluated.
     */
    private Expression whereExpr = null;


    /**
     * This collection holds zero or more entries specifying <tt>GROUP BY</tt>
     * values.  If the <tt>SELECT</tt> expression has no <tt>GROUP BY</tt>
     * clause, this collection will be empty.
     */
    private List<Expression> groupByExprs = new ArrayList<>();


    /**
     * If a <tt>HAVING</tt> expression is specified, this field will refer to
     * the expression to be evaluated.
     */
    private Expression havingExpr = null;


    /**
     * This collection holds zero or more entries specifying <tt>ORDER BY</tt>
     * values.  If the <tt>SELECT</tt> expression has no <tt>ORDER BY</tt>
     * clause, this collection will be empty.
     */
    private List<OrderByExpression> orderByExprs = new ArrayList<>();


    /**
     * The maximum number of rows that may be returned by the <tt>SELECT</tt>
     * clause.  The default value of 0 means "no limit".
     */
    private int limit = 0;


    /**
     * The offset of the first row to return from the <tt>SELECT</tt> clause.
     * Earlier rows will be computed but not returned.  The default value of 0
     * means "start with the first row."
     */
    private int offset = 0;


    /**
     * When preparing SQL commands for execution, this value is filled in with
     * the schema that this <tt>SELECT</tt> clause produces.
     */
    private Schema resultSchema = null;


    /**
     * When preparing SQL commands for execution, this value is filled in with
     * the schema that this query's <tt>FROM</tt> clause produces.
     */
    private Schema fromSchema = null;


    /**
     * If this query is correlated with one or more enclosing queries, this
     * will contain column names, and the corresponding references to the
     * enclosing queries.
     *
     * @design (Donnie) We need to know both the column names and the
     *         corresponding parent-queries that will generate those values,
     *         so that we can set up the execution plan properly.
     */
    private HashMap<ColumnName, SelectClause> correlatedWith = new HashMap<>();


    /**
     * Mark the select clause's results as being distinct or not distinct.
     * This corresponds to whether the SQL command is
     * "<tt>SELECT [ALL] ...</tt>" or "<tt>SELECT DISTINCT ...</tt>".
     *
     * @param distinct If true, specifies that the results of this select
     *        clause are distinct.  If false, the results of the clause are
     *        not distinct.
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }


    /**
     * Returns true if the select clause's results are to be distinct, or
     * false if the clause's results are not distinct.
     */
    public boolean isDistinct() {
        return distinct;
    }


    /**
     * Adds a specification to the set of values produced by this
     * <tt>SELECT</tt> clause.  This method is called by the parser as a
     * <tt>SELECT</tt> command (or subquery) is being parsed.
     */
    public void addSelectValue(SelectValue selectValue) {
        if (selectValue == null)
            throw new NullPointerException();

        selectValues.add(selectValue);
    }


    /**
     * Retrieves the select values for this select clause.
     *
     * @return the select values
     */
    public List<SelectValue> getSelectValues() {
        return selectValues;
    }


    public boolean isTrivialProject() {
        if (selectValues.size() == 1) {
            SelectValue selVal = selectValues.get(0);
            return selVal.isWildcard() && !selVal.getWildcard().isTableSpecified();
        }
        return false;
    }


    /**
     * Returns the parent of this query if it is a subquery; {@code null}
     * otherwise.
     *
     * @return the parent of this query if it is a subquery; {@code null}
     *         otherwise.
     */
    public SelectClause getParentSelect() {
        return parentSelect;
    }


    /**
     * Sets the hierarchy of base and derived relations that produce the rows
     * considered by this <tt>SELECT</tt> clause.
     */
    public void setFromClause(FromClause fromClause) {
        this.fromClause = fromClause;
    }


    /**
     * Retrieves the from clause for this select clause.  This can be
     * {@code null} if the query doesn't specify a FROM clause.
     *
     * @return the from clause
     */
    public FromClause getFromClause() {
        return fromClause;
    }


    /**
     * Returns the schema of the data produced by the FROM clause of this
     * query.  If the query has no FROM clause, this will be an empty schema.
     *
     * @return the schema produced by the FROM clause of this query, or an
     *         empty schema if the query has no FROM clause.
     */
    public Schema getFromSchema() {
        return fromSchema;
    }


    /**
     * Sets the expression for the <tt>WHERE</tt> clause.  A {@code null}
     * value indicates that the <tt>SELECT</tt> clause has no <tt>WHERE</tt>
     * condition.
     */
    public void setWhereExpr(Expression whereExpr) {
        this.whereExpr = whereExpr;
    }


    /**
     * Retrieves the where clause from this from clause.
     *
     * @return the where exprssion
     */
    public Expression getWhereExpr() {
        return whereExpr;
    }


    public void addGroupByExpr(Expression groupExpr) {
        groupByExprs.add(groupExpr);
    }


    public List<Expression> getGroupByExprs() {
        return groupByExprs;
    }


    public void setHavingExpr(Expression havingExpr) {
        this.havingExpr = havingExpr;
    }


    public Expression getHavingExpr() {
        return havingExpr;
    }


    public void addOrderByExpr(OrderByExpression orderByExpr) {
        orderByExprs.add(orderByExpr);
    }


    public List<OrderByExpression> getOrderByExprs() {
        return orderByExprs;
    }


    /**
     * If a <tt>LIMIT</tt> clause is specified, this method returns the
     * specified limit; otherwise, the default of 0 is returned.
     *
     * @return the offset specified in the SQL
     */
    public int getLimit() {
        return limit;
    }


    /**
     * Set the upper limit of how many rows should be produced by this query.
     * A value of 0 means "unlimited."  Negative values are disallowed and
     * will cause an exception to be thrown.
     *
     * @param limit a positive number specifying the maximum number of tuples
     *        to produce, or 0 to specify "unlimited."
     */
    public void setLimit(int limit) {
        if (limit < 0) {
            throw new InvalidSQLException("Limit must be at least 0 (got " +
                limit + ")");
        }

        this.limit = limit;
    }


    /**
     * If an <tt>OFFSET</tt> clause is specified, this method returns the
     * specified offset; otherwise, the default of 0 is returned.
     *
     * @return the offset specified in the SQL
     */
    public int getOffset() {
        return offset;
    }


    /**
     * Set the starting offset for the rows that should be produced by this
     * query.  A value of 0 means "start with the first row" (in other words,
     * "no offset").  Negative values are disallowed and will cause an
     * exception to be thrown.
     *
     * @param offset a positive number specifying the number of tuples to skip
     *        during query evaluation, or 0 to specify "start at the
     *        beginning."
     */
    public void setOffset(int offset) {
        if (offset < 0) {
            throw new InvalidSQLException("Offset must be at least 0 (got " +
                offset + ")");
        }

        this.offset = offset;
    }


    /**
     * Returns true if the select clause is correlated with some enclosing
     * query, or false otherwise.
     *
     * @return true if the select clause is correlated with some enclosing
     *         query, or false otherwise.
     */
    public boolean isCorrelated() {
        return !correlatedWith.isEmpty();
    }


    public Set<ColumnName> getCorrelatedColumns() {
        return correlatedWith.keySet();
    }


    /**
     * This method computes the resulting schema from this query, and in the
     * process it performs various semantic checks as well.
     *
     * @param tableManager the table manager to use for retrieving schema info
     *
     * @param parentSelect the enclosing SELECT query if this is a nested
     *        subquery, or {@code null} if this is a top-level query
     *
     * @return the schema of this select clause's result
     *
     * @throws SchemaNameException if the select clause contains some kind of
     *         semantic error involving schemas that are referenced
     */
    public Schema computeSchema(TableManager tableManager,
        SelectClause parentSelect) throws SchemaNameException {

        this.parentSelect = parentSelect;

        // Compute the schema of the FROM clause first.  We assume that no
        // subqueries in the FROM clause are correlated with this or enclosing
        // queries.
        if (fromClause != null) {
            fromSchema = fromClause.computeSchema(tableManager);
        }
        else {
            // No FROM clause - no FROM schema...
            fromSchema = new Schema();
        }

        // This helper-object is used for handling subqueries in the SELECT,
        // WHERE and HAVING clauses.  These nested queries need to have their
        // schemas computed, and possible correlated evaluation identified.
        SubquerySchemaComputer subquerySchemaComputer =
            new SubquerySchemaComputer(this, tableManager);

        // Make sure that all expressions in this SELECT clause reference
        // known and non-ambiguous names from the FROM clause.

        // SELECT values:  SELECT a, b + c, tbl.* ...
        List<ColumnInfo> resultColumnInfos = new ArrayList<>();
        Set<String> fromTables = fromSchema.getTableNames();

        for (SelectValue selVal : selectValues) {
            if (selVal.isWildcard()) {
                // Make sure that if a table name is specified, that the table
                // name is in the query's FROM-clause schema.
                ColumnName colName = selVal.getWildcard();
                if (colName.isTableSpecified()) {
                    if (!fromTables.contains(colName.getTableName())) {
                        throw new SchemaNameException(String.format(
                            "SELECT-value %s specifies an unrecognized " +
                            "table name.", colName));
                    }
                }
            }
            else {
                // An expression that is not a wildcard.  It could contain
                // column references that need to be resolved.  Also, it could
                // contain a scalar subquery, particularly one that requires
                // correlated evaluation.
                Expression expr = selVal.getExpression();

                // Get the list of column-values, and resolve each one.
                // Note that this will not descend into subqueries.
                resolveExpressionRefs("SELECT-value", expr, fromSchema,
                    /* checkParentQueries */ true);

                // If the expression contains any subqueries, resolve any
                // column-references that reference this or enclosing queries.
                expr.traverse(subquerySchemaComputer);
            }

            // Update the result-schema with this select-value's column-info(s).
            resultColumnInfos.addAll(selVal.getColumnInfos(fromSchema, resultSchema));
        }

        // Construct a resultSchema which is the "summation" of all
        // SelectValues' columnInfos.
        resultSchema = new Schema(resultColumnInfos);

        logger.debug("Query schema:  " + resultSchema);
        logger.debug("FROM-clause schema:  " + fromSchema);

        // WHERE clause:
        if (whereExpr != null) {
            resolveExpressionRefs("WHERE clause", whereExpr, fromSchema,
                /* checkParentQueries */ true);

            // Simplify the expression
            whereExpr = whereExpr.simplify();

            // Compute the schemas of subqueries embedded in the WHERE clause.
            whereExpr.traverse(subquerySchemaComputer);
        }

        // GROUP BY clauses:
        for (Expression expr : groupByExprs) {
            // GROUP BY expressions aren't allowed to have subqueries.
            resolveExpressionRefs("GROUP BY clause", expr, fromSchema,
                /* checkParentQueries */ false);
        }

        // HAVING clause:
        if (havingExpr != null) {
            // The HAVING clause can reference values produced by grouping and
            // aggregation.  However, according to the SQL standard, it won't
            // see any values except from the FROM-clause's schema.
            resolveExpressionRefs("HAVING clause", havingExpr, fromSchema,
                /* checkParentQueries */ true);

            // Simplify the expression
            havingExpr = havingExpr.simplify();

            // Compute the schemas of subqueries embedded in the HAVING clause
            havingExpr.traverse(subquerySchemaComputer);
        }

        // ORDER BY clauses:  These are computed from the result of the
        // SELECT clause, not the result of the FROM clause.
        for (OrderByExpression expr : orderByExprs) {
            // ORDER BY expressions aren't allowed to have subqueries.
            resolveExpressionRefs("ORDER BY clause", expr.getExpression(),
                resultSchema, /* checkParentQueries */ false);
        }

        // All done!  Return the computed schema.
        return resultSchema;
    }


    /**
     * Returns the schema for this select clause, or {@code null} if the
     * {@link #computeSchema} method hasn't yet been called on this clause.
     *
     * @return the schema for this select clause
     */
    public Schema getSchema() {
        return resultSchema;
    }


    /**
     * This helper function goes through the expression and verifies that
     * every symbol-reference corresponds to an actual value produced by the
     * <tt>FROM</tt>-clause of the <tt>SELECT</tt> query.  Any column-names
     * that don't include a table-name are also updated to include the proper
     * table-name.
     * <p>
     * It's possible to have symbols that don't reference columns in the
     * <tt>FROM</tt> clause, if a query is correlated with an enclosing query.
     * In these cases, the unresolvable attributes are collected so they can
     * be resolved at the end of the process.
     *
     * @param desc A short string describing the context of the expression,
     *        since expressions can appear in the <tt>SELECT</tt> clause, the
     *        <tt>WHERE</tt> clause, the <tt>GROUP BY</tt> clause, etc.
     *
     * @param expr The expression that will be evaluated.
     *
     * @param s The schema against which the expression will be evaluated.
     *
     * @param checkParentQueries if this is true and the column-name can't be
     *        resolved against the current query, any parent queries will also
     *        be checked for the column-name.  This allows correlated queries
     *        to be resolved properly.
     *
     * @throws SchemaNameException if an expression-reference cannot be resolved
     *         against the specified schema, either because the named column
     *         or table doesn't appear in the schema, or if a column name is
     *         ambiguous.
     */
    private void resolveExpressionRefs(String desc, Expression expr, Schema s,
        boolean checkParentQueries) throws SchemaNameException {

        // Get the list of column-values in the expression, and resolve each one.
        // (This won't include subquery expressions, since they will reference
        // the subquery's schema.)
        ArrayList<ColumnName> exprColumns = new ArrayList<>();
        expr.getAllSymbols(exprColumns);

        for (ColumnName colName : exprColumns) {
            assert !colName.isColumnWildcard();
            resolveColumnRef(desc, expr, colName, s, checkParentQueries);
        }
    }


    /**
     * This helper function attempts to resolve a specific column-name against
     * a query's schema.

     * @param desc A short string describing the context of the expression,
     *        since expressions can appear in the <tt>SELECT</tt> clause, the
     *        <tt>WHERE</tt> clause, the <tt>GROUP BY</tt> clause, etc.
     *
     * @param expr The expression that will be evaluated.
     *
     * @param colName The column-name to resolve.
     *
     * @param s The schema against which the expression will be evaluated.
     *
     * @param checkParentQueries if this is true and the column-name can't be
     *        resolved against the current query, any parent queries will also
     *        be checked for the column-name.  This allows correlated queries
     *        to be resolved properly.
     *
     * @throws SchemaNameException if an expression-reference cannot be resolved
     *         against the specified schema, either because the named column
     *         or table doesn't appear in the schema, or if a column name is
     *         ambiguous.
     */
    private void resolveColumnRef(String desc, Expression expr,
        ColumnName colName, Schema s, boolean checkParentQueries)
        throws SchemaNameException {

        // This flag indicates whether we are referencing a column in a parent
        // query.
        boolean parentRef = false;

        // This is the query that we are examining.
        SelectClause clause = this;
        while (true) {
            // Attempt to resolve the column-name against the current schema's
            // columns.
            SortedMap<Integer, ColumnInfo> found = s.findColumns(colName);

            if (found.size() == 1) {
                // Found exactly one column that matches!
                if (!colName.isTableSpecified()) {
                    // Update the column-reference with the table name.
                    ColumnInfo colInfo = found.get(found.firstKey());
                    colName.setTableName(colInfo.getTableName());
                }

                if (parentRef) {
                    // This column-name references a column produced by a
                    // parent query, not this query.
                    assert clause != this;

                    // Sanity-check:  have we already seen this column name?
                    // If so, make sure that we find the same clause each time
                    if (correlatedWith.containsKey(colName) &&
                        correlatedWith.get(colName) != clause) {
                        throw new IllegalStateException(String.format(
                            "Column name %s is associated with two " +
                            "different queries", colName));
                    }
                    correlatedWith.put(colName, clause);
                }

                return;
            }

            if (found.size() > 1) {
                // In this case the column name is ambiguous - there may be
                // multiple columns in the schema with the same column name
                // but different table names.
                throw new SchemaNameException(String.format(
                    "%s %s contains an ambiguous column %s; found %s.",
                    desc, expr, colName, found.values()));
            }

            // We should only get to this point in the loop if we couldn't
            // find a column with the specified name.
            assert found.size() == 0;

            if (checkParentQueries && clause.parentSelect != null) {
                clause = clause.parentSelect;
                s = clause.getFromSchema();
                parentRef = true;
            }
            else {
                throw new SchemaNameException(String.format(
                    "%s %s references an unknown column %s.",
                    desc, expr, colName));
            }
        }
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("SelectClause[id=").append(System.identityHashCode(this)).append('\n');

        if (selectValues.size() > 0)
            buf.append("\tvalues=").append(selectValues).append('\n');

        if (fromClause != null)
            buf.append("\tfrom=").append(fromClause).append('\n');

        if (whereExpr != null)
            buf.append("\twhere=").append(whereExpr).append('\n');

        if (groupByExprs.size() > 0)
            buf.append("\tgroup_by=").append(groupByExprs).append('\n');

        if (havingExpr != null)
            buf.append("\thaving=").append(havingExpr).append('\n');

        if (orderByExprs.size() > 0)
            buf.append("\torder_by=").append(orderByExprs).append('\n');

        if (correlatedWith.size() > 0) {
            buf.append("\tcorrelatedWith=[");

            boolean first = true;
            for (Map.Entry e: correlatedWith.entrySet()) {
                if (first)
                    first = false;
                else
                    buf.append(',');

                buf.append(e.getKey()).append(':');
                buf.append(System.identityHashCode(e.getValue()));
            }
            buf.append("]\n");
        }

        buf.append(']');

        return buf.toString();
    }
}
