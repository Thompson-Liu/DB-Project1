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
	private HashMap<List<String>, Integer[]> attrsInBB;

	public SelectLogOp(String tableName, String alias, Expression expression, HashMap<List<String>, Integer[]> selectAttr) {
		this.tableName= tableName;
		this.alias= alias;
		exp= expression;
		
		attrsInBB = new HashMap<List<String>, Integer[]>();
		for (List<String> attribute: selectAttr.keySet()) {
			attrsInBB.put(attribute, selectAttr.get(attribute));
		}
	}

	public Expression getSelectExpr() {
		return exp;
	}
	
	public HashMap<List<String>, Integer[]> getAttrributes() {
		return attrsInBB;
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
