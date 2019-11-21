package logicalOperators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Operators.PhysicalPlanBuilder;
import dataStructure.BlueBox;
import dataStructure.UnionFind;
import net.sf.jsqlparser.expression.Expression;
import utils.LogicalPlanWriter;

public class JoinLogOp extends LogicalOperator {

	private Expression joinExp;
	private List<LogicalOperator> childOp;
	private UnionFind blueBoxList;

	public JoinLogOp(ArrayList<LogicalOperator> fromItems, Expression expr, UnionFind list) {
		joinExp= expr;

		childOp= new ArrayList<LogicalOperator>(fromItems.size());
		for (LogicalOperator logOp : fromItems) {
			childOp.add(logOp);
		}
		
		blueBoxList = list;
	}
	
	public UnionFind getUnionFind() {
		return blueBoxList;
	}

	@Override
	public List<LogicalOperator> getChildren() {
		return childOp;
	}

	public LogicalOperator getChild(int index) {
		return childOp.get(index);
	}

	public Expression getJoinExpression() {
		return joinExp;
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

	@Override
	public String getTableName() {
		return "";
	}
}
