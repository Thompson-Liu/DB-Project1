package logicalOperators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Operators.PhysicalPlanBuilder;
import dataStructure.BlueBox;
import net.sf.jsqlparser.expression.Expression;
import utils.LogicalPlanWriter;

public class SelectLogOp extends LogicalOperator {

	private String tableName;
	private String alias;
	private Expression exp;
	private ArrayList<BlueBox> attributes;
	private LogicalOperator child;

	public SelectLogOp(String tableName, String alias, Expression expression, 
			List<BlueBox> bb, LogicalOperator leaf) {
		this.tableName= tableName;
		this.alias= alias;
		exp= expression;
		
		attributes = new ArrayList<BlueBox>();
		if (!bb.isEmpty()) {
			attributes.addAll(bb);
		}
		child = leaf;
	}

	public Expression getSelectExpr() {
		return exp;
	}
	
	public ArrayList<BlueBox> getAttributes() {
		return attributes;
	}

	@Override
	public List<LogicalOperator> getChildren() {
		List<LogicalOperator> children = new ArrayList<LogicalOperator>();
		children.add(child);
		return children;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	public String getAlias() {
		return alias;
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
