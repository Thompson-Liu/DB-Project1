package logicalOperators;

import java.util.ArrayList;
import java.util.List;

import Operators.PhysicalPlanBuilder;

public class Leaf extends LogicalOperator {
	
	private String tableName;
	private String tableAlias;
	
	public Leaf(String name, String alias) {
		tableName = name;
		tableAlias = alias;
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
}
