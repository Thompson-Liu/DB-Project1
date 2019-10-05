package logicalOperators;

import java.util.HashMap;

import net.sf.jsqlparser.expression.Expression;

public class JoinLogOp extends LogicalOperator{
	
	private Expression joinExp;
	private LogicalOperator left;
	private LogicalOperator right;
	private HashMap<String, String> tableAlias;
	
	
	public JoinLogOp(LogicalOperator leftOp, LogicalOperator rightOp, 
			Expression expr, HashMap<String, String> alias) {
		
		joinExp = expr;
		left = leftOp;
		right = rightOp;
		tableAlias = alias;
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
	
	public HashMap<String, String> getAlias() {
		return tableAlias;
	}
	
	public Expression getJoinExpression() {
		return joinExp;
	}
}
