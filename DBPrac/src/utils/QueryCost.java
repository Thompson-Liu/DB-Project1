package utils;

public class QueryCost {

	String tableName;
	public QueryCost(String tableName) {
		this.tableName = tableName;
	}
	
	public Integer IndexScanCost() {
		return null;
	}
}
