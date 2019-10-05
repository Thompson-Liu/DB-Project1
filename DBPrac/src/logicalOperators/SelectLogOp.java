package logicalOperators;

import java.util.HashMap;
import net.sf.jsqlparser.expression.Expression;
import physicalOperator.Operator;

public class SelectLogOp extends LogicalOperator {
	
	private Expression exp;
	private HashMap<String, String> tableAlias;
	private LogicalOperator childOp;

	public SelectLogOp(Expression expression, LogicalOperator op, HashMap<String, String> alias) {
		exp = expression;
		childOp = op;
		tableAlias = alias;
	}
	
	public LogicalOperator[] getChildren() {
		return new LogicalOperator[] { childOp };
	}
	
	public Expression getSelectExpr() {
		return exp;
	}
	
	public HashMap<String, String> getAlias() {
		return tableAlias;
	}
}
