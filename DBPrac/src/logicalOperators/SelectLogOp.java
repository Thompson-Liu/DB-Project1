package logicalOperators;

import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.expression.Expression;
import physicalOperator.Operator;

public class SelectLogOp extends LogicalOperator {
	
	private String tableName;
	private String alias;
	private Expression exp;
	private Integer lowKey;
	private Integer highKey;

	public SelectLogOp(String tableName, String alias, Expression expression) {
		this.tableName = tableName;
		this.alias = alias;
		exp = expression;
	}
	
	public SelectLogOp(String tableName, String alias, Integer low, Integer high) {
		this.tableName = tableName;
		this.alias = alias;
		lowKey = low;
		highKey = high;
	}
	
	public int getLowKey() {
		if(lowKey == null) {
			return Integer.MIN_VALUE; 
		} 
		return lowKey;
	}
	
	public int getHighKey() {
		if(highKey == null) {
			return Integer.MAX_VALUE; 
		} 
		return highKey;
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
