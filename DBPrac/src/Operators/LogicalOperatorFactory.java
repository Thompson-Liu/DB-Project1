package Operators;

import java.util.ArrayList;
import java.util.List;
import dataStructure.BlueBox;
import dataStructure.Catalog;
import dataStructure.UnionFind;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import parser.UnionFindGenerator;
import parser.UnusedSelectVisitor;
import logicalOperators.DuplicateEliminationLogOp;
import logicalOperators.JoinLogOp;
import logicalOperators.Leaf;
import logicalOperators.LogicalOperator;
import logicalOperators.ProjectLogOp;
import logicalOperators.SelectLogOp;
import logicalOperators.SortLogOp;

/** produce the operator tree structure
 * takes in the plainselect
 *
 */
public class LogicalOperatorFactory {

	/** Generate query plan of operator tree based on the plainSelect clause
	 * perform select first, then join, the project. If the query contains an Order By, sort operator 
	 * will be the root, followed by the rest of the plan. If the query contains Distinct, it will be 
	 * the root.   
	 * 
	 * @param plainSelect
	 * @return operator
	 */
	public LogicalOperator generateQueryPlan(PlainSelect plainSelect) {
		Join firstJoin = new Join();
		firstJoin.setRightItem(plainSelect.getFromItem());

		List<Join> joinList = new ArrayList<Join>();
		joinList.add(firstJoin);

		LogicalOperator intOp;
		if (plainSelect.getJoins() != null) {
			joinList.addAll(plainSelect.getJoins());
		} 
		intOp = pushSelect(plainSelect, joinList);

		// check select clause
		List<SelectItem> selectItems= plainSelect.getSelectItems();
		if (!(selectItems.get(0) instanceof AllColumns)) {
			intOp= new ProjectLogOp(intOp, selectItems);
		}
		Distinct d= plainSelect.getDistinct();
		List<OrderByElement> tmpList= plainSelect.getOrderByElements();
		if (tmpList != null) {
			List<String> orderByList= new ArrayList<String>(tmpList.size());
			for (OrderByElement x : tmpList) {
				String[] nameCol = x.toString().split("\\.");
				String full = nameCol[0] + "." + nameCol[1];
				orderByList.add(full);  
			}
			intOp= new SortLogOp(intOp, orderByList);
			return (d == null) ? intOp : new DuplicateEliminationLogOp((SortLogOp) intOp);
		}

		if (d != null) {
			intOp= new SortLogOp(intOp, null);
			return new DuplicateEliminationLogOp((SortLogOp) intOp);

		}
		return intOp;
	}

	/**
	 * The logic to push down the select conditions
	 * 
	 * @param plainSelect    the query statement
	 * @param joinList       the list of join items 
	 * @return			     a logical operator after pushing down availale conditions
	 */
	private LogicalOperator pushSelect(PlainSelect plainSelect, List<Join> joinList) {
		// TODO Auto-generated method stub
		UnionFindGenerator ufGen = new UnionFindGenerator(plainSelect.getWhere());
		UnionFind uf = ufGen.getUnionFind();
		Expression unusedSelExpr = ufGen.getResidualSelect();

		ArrayList<LogicalOperator> joinChildren = new ArrayList<LogicalOperator>();
		for(int i = 0; i < joinList.size(); ++i) {
			Join joinRel = joinList.get(i);

			String tempAlias = "";
			String tempTable = joinRel.getRightItem().toString();
			if(joinRel.getRightItem().getAlias()!=null) {
				tempAlias = joinRel.getRightItem().getAlias().toString();
				tempTable = tempTable.replace("AS " + tempAlias, "").trim();
			}

			LogicalOperator select;
			UnusedSelectVisitor visitor;
			Catalog cat = Catalog.getInstance();

			// If there is a bluebox containing the table, push down the selection
			UnionFind selectAttrs;
			if (tempAlias.equals("")) {
				selectAttrs = uf.findSelect(tempTable);
				visitor = new UnusedSelectVisitor(tempTable, cat.getSchema(tempTable), unusedSelExpr);
			} else {
				selectAttrs = uf.findSelect(tempAlias);
				visitor = new UnusedSelectVisitor(tempAlias, cat.getSchema(tempTable), unusedSelExpr);
			}

			// if the residual selection also matches the tableName, push down too
			Expression expr;
			Expression unusedExpr = visitor.getTableExpr();
			if(selectAttrs.isEmpty() && unusedExpr == null) {
				select = new Leaf(tempTable, tempAlias);
			} else {
				// generate a copy of the attributes list
				UnionFind ufCopy = new UnionFind(selectAttrs);

				Expression exprInBox = ufCopy.buildExpression();
				if (unusedExpr == null) {
					expr = exprInBox;
				} else {
					expr = (exprInBox == null) ? unusedExpr : new AndExpression(exprInBox, unusedExpr);
				}
				select = new SelectLogOp(tempTable, tempAlias, expr, unusedExpr, selectAttrs.getBlueBoxes(), new Leaf(tempTable, tempAlias));
			}
			joinChildren.add(select);
		}
		
		if (joinChildren.size() == 1) {
			return joinChildren.get(0);
		}
		
		JoinLogOp join = new JoinLogOp(joinChildren, ufGen.getResidualJoin(), uf);
		return join;
	}
}
