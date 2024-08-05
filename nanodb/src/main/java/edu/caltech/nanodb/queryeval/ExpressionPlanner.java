package edu.caltech.nanodb.queryeval;

import edu.caltech.nanodb.expressions.Expression;
import edu.caltech.nanodb.expressions.ExpressionProcessor;
import edu.caltech.nanodb.expressions.SubqueryOperator;
import edu.caltech.nanodb.plannodes.PlanNode;

public class ExpressionPlanner implements ExpressionProcessor {
    private final AbstractPlannerImpl planner;

    public ExpressionPlanner(AbstractPlannerImpl planner) {
        this.planner = planner;
    }

    @Override
    public void enter(Expression e) {
    }

    @Override
    public Expression leave(Expression e) {
        if (e instanceof SubqueryOperator) {
            SubqueryOperator sq = (SubqueryOperator) e;
            PlanNode plan = planner.makeUnpreparedPlan(sq.getSubquery());
            plan.prepare();
            sq.setSubqueryPlan(plan);
        }
        return e;
    }
}
