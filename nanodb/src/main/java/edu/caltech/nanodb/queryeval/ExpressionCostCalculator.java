package edu.caltech.nanodb.queryeval;

import edu.caltech.nanodb.expressions.Expression;
import edu.caltech.nanodb.expressions.ExpressionProcessor;
import edu.caltech.nanodb.expressions.SubqueryOperator;
import edu.caltech.nanodb.plannodes.PlanNode;

public class ExpressionCostCalculator implements ExpressionProcessor {
    private float cost;

    public ExpressionCostCalculator() {
    }

    @Override
    public void enter(Expression node) {
        if (node instanceof SubqueryOperator) {
            PlanNode sp = ((SubqueryOperator) node).getSubqueryPlan();
            assert sp.getCost() != null;
            cost += sp.getCost().cpuCost;
        } else {
            cost++;
        }
    }

    @Override
    public Expression leave(Expression node) {
        return node;
    }

    public float getCost() {
        return cost;
    }
}
