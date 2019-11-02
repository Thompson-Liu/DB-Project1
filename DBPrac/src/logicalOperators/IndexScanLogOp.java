package logicalOperators;

import java.io.IOException;

import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.expression.Expression;
import parser.IndexConditionSeperator;

public class IndexScanLogOp extends LogicalOperator {

	private String tableName;
	private String aliasName;
	private String colName;
	private int lowKey;
	private int highKey;
	private boolean isClustered;
	private String indexDir;
	

	public IndexScanLogOp(String table, String alias, String col, int lowKey,int highKey, boolean isClustered, String indexDir) {
		tableName= table;
		aliasName= alias;
		colName = col;
		this.lowKey=lowKey;
		this.highKey=highKey;
		this.isClustered=isClustered;
		this.indexDir=indexDir;
	}

	@Override
	public LogicalOperator[] getChildren() {
		return null;
	}

	public String getTableName() {
		return tableName;
	}

	public String getAliasName() {
		return aliasName;
	}
	
	public String getColName() {
		return colName;
	}
	
	public int getHighKey() {
		return this.highKey;
	}
	
	public int getLowKey() {
		return this.lowKey;
	}

	@Override
	public void accept(PhysicalPlanBuilder planBuilder) {
		try {
			planBuilder.visit(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
