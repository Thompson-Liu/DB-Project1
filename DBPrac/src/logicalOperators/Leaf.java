package logicalOperators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Operators.PhysicalPlanBuilder;
import utils.LogicalPlanWriter;

public class Leaf extends LogicalOperator {

	private String tableName;
	private String tableAlias;

	public Leaf(String name, String alias) {
		tableName= name;
		tableAlias= alias;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public List<LogicalOperator> getChildren() {
		return new ArrayList<LogicalOperator>();
	}

	public String getAlias() {
		return tableAlias;
	}

	@Override
	public void accept(PhysicalPlanBuilder planBuilder) {
		planBuilder.visit(this);
	}

	@Override
	public void accept(LogicalPlanWriter lpw) {
		try {
			lpw.visit(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
