package logicalOperators;

import java.util.HashMap;

import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.expression.Expression;
import physicalOperator.Operator;

public class SelectLogOp extends LogicalOperator {
	
	private Expression exp;
	private LogicalOperator childOp;

	public SelectLogOp(Expression expression, LogicalOperator op) {
		exp = expression;
		childOp = op;
	}
	
	public LogicalOperator[] getChildren() {
		return new LogicalOperator[] { childOp };
	}
	
	public Expression getSelectExpr() {
		return exp;
	}
	
	public void accept(PhysicalPlanBuilder planBuilder) {
		planBuilder.visit(this);
	}
}
