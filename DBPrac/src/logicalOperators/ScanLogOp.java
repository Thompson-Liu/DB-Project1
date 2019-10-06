package logicalOperators;

import Operators.PhysicalPlanBuilder;

public class ScanLogOp extends LogicalOperator {
	
	private String tableName;
	private String aliasName;
	
	public ScanLogOp(String table, String alias) {
		tableName = table;
		aliasName = alias;
	}
	
	public LogicalOperator[] getChildren() {
		return null;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public String getAliasName() {
		return aliasName;
	}
	
	public void accept(PhysicalPlanBuilder planBuilder) {
		planBuilder.visit(this);
	}

}
