package logicalOperators;

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

}
