package logicalOperators;

import java.io.IOException;

import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.expression.Expression;

public class IndexScanLogOp extends LogicalOperator {

	private String tableName;
	private String aliasName;

	public IndexScanLogOp(String table, String alias, Expression expression) {
		tableName= table;
		aliasName= alias;
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
