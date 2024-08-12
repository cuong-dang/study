package edu.caltech.nanodb.queryeval;

import edu.caltech.nanodb.expressions.Environment;
import edu.caltech.nanodb.expressions.Expression;
import edu.caltech.nanodb.expressions.ExpressionProcessor;
import edu.caltech.nanodb.expressions.SubqueryOperator;
import edu.caltech.nanodb.plannodes.PlanNode;

public class ExpressionPlanner implements ExpressionProcessor {
    private final AbstractPlannerImpl planner;
    private boolean hasSeenSubqueries;
    private final Environment parentEnvironment;

    public ExpressionPlanner(AbstractPlannerImpl planner) {
        this.planner = planner;
        parentEnvironment = new Environment();
    }

    @Override
    public void enter(Expression e) {
    }

    @Override
    public Expression leave(Expression e) {
        if (e instanceof SubqueryOperator) {
            hasSeenSubqueries = true;
            SubqueryOperator sq = (SubqueryOperator) e;
            PlanNode plan = planner.makeUnpreparedPlan(sq.getSubquery());
            plan.addParentEnvironmentToPlanTree(parentEnvironment);
            plan.prepare();
            sq.setSubqueryPlan(plan);
        }
        return e;
    }

    public boolean hasSeenSubqueries() {
        return hasSeenSubqueries;
    }

    public Environment parentEnvironment() {
        return parentEnvironment;
    }
}
