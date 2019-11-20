package logicalOperators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.expression.Expression;
import utils.LogicalPlanWriter;

public class SelectLogOp extends LogicalOperator {

	private String tableName;
	private String alias;
	private Expression exp;
	private Expression unusedExpr;
	private HashMap<List<String>, Integer[]> attributes;

	public SelectLogOp(String tableName, String alias, Expression expression) {
		this.tableName= tableName;
		this.alias= alias;
		exp= expression;
		attributes= new HashMap<List<String>, Integer[]>();
	}

	public SelectLogOp(String tableName, String alias,
		HashMap<List<String>, Integer[]> attrs, Expression expression) {
		this.tableName= tableName;
		this.alias= alias;
		unusedExpr= expression;

		// check if a hashmap is copied correctly
		attributes= new HashMap<List<String>, Integer[]>();
		for (List<String> attribute : attrs.keySet()) {
			attributes.put(attribute, attrs.get(attribute));
		}
	}

	public Expression getSelectExpr() {
		return exp;
	}

	public Expression getUnusedExpr() {
		return unusedExpr;
	}

	public HashMap<List<String>, Integer[]> getAttrributes() {
		return attributes;
	}

	@Override
	public List<LogicalOperator> getChildren() {
		return new ArrayList<LogicalOperator>();
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
