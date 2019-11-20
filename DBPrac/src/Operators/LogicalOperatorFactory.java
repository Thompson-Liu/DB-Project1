package Operators;

import java.util.ArrayList;
import java.util.HashMap;
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
		
		String aliasName = "";
		String fromLeft = plainSelect.getFromItem().toString();
		Join firstJoin = new Join();
		firstJoin.setRightItem(plainSelect.getFromItem());
		
		List<Join> joinList = new ArrayList<Join>();
		joinList.add(firstJoin);
		
		LogicalOperator intOp;
		if (plainSelect.getJoins() != null) {
			joinList.addAll(plainSelect.getJoins());
			intOp = pushSelect(plainSelect, joinList);
		} else {
			if(plainSelect.getFromItem().getAlias()!=null) {
				aliasName = plainSelect.getFromItem().getAlias().toString();
				String tempTable = fromLeft.replace("AS " + aliasName, "").trim();
				intOp = (plainSelect.getWhere() != null) ? 
						new SelectLogOp(tempTable, aliasName, plainSelect.getWhere(), new HashMap<List<String>, Integer[]>())
						: new Leaf(tempTable, aliasName);
			} else {
				intOp = (plainSelect.getWhere() != null) ? 
						new SelectLogOp(fromLeft, "", plainSelect.getWhere(), new HashMap<List<String>, Integer[]>()) 
						: new Leaf(fromLeft, "");
			}
		}

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
		return intOp;
	}

	private LogicalOperator pushSelect(PlainSelect plainSelect, List<Join> joinList) {
		// TODO Auto-generated method stub
		UnionFindGenerator ufGen = new UnionFindGenerator(plainSelect.getWhere());
		UnionFind uf = ufGen.getUnionFind();
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
			Expression unusedSelExpr = ufGen.getResidualSelect();
			UnusedSelectVisitor visitor;
			Catalog cat = Catalog.getInstance();
			
			// If there is a bluebox containing the table, push down the selection
			HashMap<List<String>, Integer[]> selectAttr;
			if (tempAlias.equals("")) {
				selectAttr = uf.findSelect(tempTable);
				visitor = new UnusedSelectVisitor(tempTable, cat.getSchema(tempTable), unusedSelExpr);
			} else {
				selectAttr = uf.findSelect(tempAlias);
				visitor = new UnusedSelectVisitor(tempAlias, cat.getSchema(tempTable), unusedSelExpr);
			}
			
			// if the residual selection also matches the tableName, push down too
			Expression expr = visitor.getTableExpr();
			if(selectAttr.isEmpty() && expr == null) {
				select = new Leaf(tempTable, tempAlias);
			} else {
				Expression exprInBox = buildExpression(selectAttr);
				expr = (exprInBox == null) ? expr : new AndExpression(exprInBox, expr);
				select = new SelectLogOp(tempTable, tempAlias, expr, selectAttr);
			}
			joinChildren.add(select);
		}
		JoinLogOp join = new JoinLogOp(joinChildren, ufGen.getResidualJoin());
		return join;
	}
	
	private Expression buildExpression(HashMap<List<String>, Integer[]> attrs) {
		// If the hashMap is empty, all attributes are resolved, return a null Expression
		if (attrs.isEmpty()) {
			return null;
		}

		List<Expression> intermediate = new ArrayList<Expression>();
		for (List<String> attribute: attrs.keySet()) {
			// Initialize a null value to start the rescurssion 
			intermediate.add(buildCol(new NullValue(), attribute, attrs.get(attribute)));
		}

		if (intermediate.isEmpty()) {
			return null;
		} else if (intermediate.size() == 1) {
			return intermediate.get(0);
		}

		Expression result = new AndExpression(intermediate.remove(0), intermediate.remove(1));
		return buildExpressionHelper(result, intermediate); 
	}

	private Expression buildExpressionHelper(Expression intermediate, List<Expression> expr) {
		if (expr.size() == 0) {
			return intermediate;
		}
		return buildExpressionHelper(new AndExpression(intermediate, expr.remove(0)), expr);
	}

	private Expression buildCol(Expression intermediate, List<String> attrs, Integer[] stats) {
		//  Construct a left Column object
		String left = attrs.remove(0);
		String leftTableName = left.split("\\.")[0];
		String leftTableCol = left.split("\\.")[1];

		Column leftCol = new Column();
		leftCol.setColumnName(leftTableCol);
		Table leftTable = new Table();
		leftTable.setName(leftTableName);
		leftCol.setTable(leftTable);

		// if there's only one attrs left, construct a attr op val
		if (attrs.size() == 0) {
			if (stats[0] == null && stats[1] == null) {
				// This case shouldn't happen
				assert(! (intermediate instanceof NullValue));
				return intermediate;
			} 

			if (stats[0] == null && stats[1] != null) {
				MinorThanEquals minorEqual = new MinorThanEquals();
				minorEqual.setLeftExpression(leftCol);
				minorEqual.setRightExpression(new LongValue(stats[1].toString()));
				if (intermediate instanceof NullValue) {
					return minorEqual;
				}
				return new AndExpression(intermediate, minorEqual);
			}

			if (stats[0] != null && stats[1] == null) {
				GreaterThanEquals greaterEqual = new GreaterThanEquals();
				greaterEqual.setLeftExpression(leftCol);
				greaterEqual.setRightExpression(new LongValue(stats[0].toString()));
				if (intermediate instanceof NullValue) {
					return greaterEqual;
				}
				return new AndExpression(intermediate, greaterEqual);
			}

			GreaterThanEquals greaterEqual = new GreaterThanEquals();
			greaterEqual.setLeftExpression(leftCol);
			greaterEqual.setRightExpression(new LongValue(stats[0].toString()));

			MinorThanEquals minorEqual = new MinorThanEquals();
			minorEqual.setLeftExpression(leftCol);
			minorEqual.setRightExpression(new LongValue(stats[1].toString()));
			if (intermediate instanceof NullValue) {
				return new AndExpression(minorEqual, greaterEqual);
			}
			return new AndExpression(new AndExpression(intermediate, greaterEqual), minorEqual);
		}

		// Otherwise, construct the right Column object
		String right = attrs.get(0);
		String rightTableName = right.split("\\.")[0];
		String rightTableCol = right.split("\\.")[1];

		Column rightCol = new Column();
		rightCol.setColumnName(rightTableCol);
		Table rightTable = new Table();
		rightTable.setName(rightTableName);
		rightCol.setTable(rightTable);

		// Construct the equality between the two column
		EqualsTo colEqual = new EqualsTo();
		colEqual.setLeftExpression(leftCol);
		colEqual.setRightExpression(rightCol);

		if (intermediate instanceof NullValue) {
			return buildCol(colEqual, attrs, stats);
		}
		return buildCol(new AndExpression(intermediate, colEqual), attrs, stats);
	}
}
