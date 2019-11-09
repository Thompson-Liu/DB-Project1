package logicalOperators;

import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.expression.Expression;
import physicalOperator.Operator;

public class SelectLogOp extends LogicalOperator {
	
	private String tableName;
	private String alias;
	private Expression exp;

	public SelectLogOp(String tableName, String alias, Expression expression) {
		this.tableName = tableName;
		this.alias = alias;
		exp = expression;
	}
	
	public Expression getSelectExpr() {
		return exp;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public String getAlias()  {
		return  alias;
	}
	
	public void accept(PhysicalPlanBuilder planBuilder) {
		planBuilder.visit(this);
	}
}
