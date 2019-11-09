package logicalOperators;

import java.util.HashMap;

import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.expression.Expression;

public class JoinLogOp extends LogicalOperator{
	
	private Expression joinExp;
	private LogicalOperator left;
	private LogicalOperator right;	
	
	public JoinLogOp(LogicalOperator leftOp, LogicalOperator rightOp, Expression expr) {
		joinExp = expr;
		left = leftOp;
		right = rightOp;
	}
	
	public LogicalOperator[] getChildren() {
		return new LogicalOperator[] { left, right };
	}
	
	public LogicalOperator getLeftChild() {
		return left;
	}

	public LogicalOperator getRightChild() {
		return right;
	}
	
	public Expression getJoinExpression() {
		return joinExp;
	}
	
	public void accept(PhysicalPlanBuilder planBuilder) {
		planBuilder.visit(this);
	}
}
