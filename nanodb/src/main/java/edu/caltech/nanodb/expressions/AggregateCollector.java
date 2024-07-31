package edu.caltech.nanodb.expressions;

import edu.caltech.nanodb.functions.AggregateFunction;
import edu.caltech.nanodb.queryeval.InvalidSQLException;

import java.util.HashMap;
import java.util.Map;

public class AggregateCollector implements ExpressionProcessor {
    private final Map<String, FunctionCall> aggregates;
    private boolean allowingAggregates;

    public AggregateCollector(boolean allowingAggregates) {
        aggregates = new HashMap<>();
        this.allowingAggregates = allowingAggregates;
    }

    @Override
    public void enter(Expression e) {
        if (e instanceof FunctionCall) {
            FunctionCall fn = (FunctionCall) e;
            if (fn.getFunction() instanceof AggregateFunction)
                if (!allowingAggregates)
                    throw new InvalidSQLException("Aggregate found in invalid location: " + fn);
                else
                    allowingAggregates = false;
        }
    }

    @Override
    public Expression leave(Expression e) {
        if (e instanceof FunctionCall) {
            FunctionCall fn = (FunctionCall) e;
            if (fn.getFunction() instanceof AggregateFunction) {
                assert !allowingAggregates;
                allowingAggregates = true;
                aggregates.put(fn.toString(), fn);
                return new ColumnValue(new ColumnName(fn.toString()));
            }
        }
        return e;
    }

    public Map<String, FunctionCall> getAggregates() {
        return aggregates;
    }
}
