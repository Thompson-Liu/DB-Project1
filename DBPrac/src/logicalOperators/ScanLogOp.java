package logicalOperators;

import java.io.IOException;

import Operators.PhysicalPlanBuilder;

public class ScanLogOp extends LogicalOperator {

	private String tableName;
	private String alias;

	public ScanLogOp(String table, String aliasName) {
		tableName= table;
		alias = aliasName;
	}

	@Override
	public LogicalOperator[] getChildren() {
		return null;
	}

	public String getTableName() {
		return tableName;
	}
	
	public String getAliasName( ) {
		return alias;
	}

	@Override
	public void accept(PhysicalPlanBuilder planBuilder) {
		try {
			planBuilder.visit(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
