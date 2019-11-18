package Operators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataStructure.Catalog;
import dataStructure.UnionFind;
import logicalOperators.DuplicateEliminationLogOp;
import logicalOperators.JoinLogOp;
import logicalOperators.Leaf;
import logicalOperators.LogicalOperator;
import logicalOperators.ProjectLogOp;
import logicalOperators.SelectLogOp;
import logicalOperators.SortLogOp;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import parser.IndexConditionSeperator;
import parser.JoinTableVisitor;
import parser.UnionFindGenerator;
import physicalOperator.BNLJ;
import physicalOperator.DuplicateEliminationOperator;
import physicalOperator.ExternalSortOperator;
import physicalOperator.IndexScanOperator;
import physicalOperator.JoinOperator;
import physicalOperator.Operator;
import physicalOperator.ProjectOperator;
import physicalOperator.SMJ;
import physicalOperator.ScanOperator;
import physicalOperator.SelectOperator;
import physicalOperator.SortOperator;

public class PhysicalPlanBuilder {

	static Operator immOp;
	private String tempDir;
	private String indexDir;

	public PhysicalPlanBuilder(String tempPath, String indexPath) {
		tempDir= tempPath;
		indexDir= indexPath;
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
	
	private Expression buildJoinExpr(Expression unused, List<ArrayList<String>> condFromBox) {
		if (condFromBox.isEmpty()) {
			return unused;
		} 
	
		Expression expr = null;
		for (List<String> attrs: condFromBox) {
			assert(attrs.size() == 2);
			
			Column leftCol = new Column();
			leftCol.setColumnName(attrs.get(0));
			Table leftTable = new Table();
			leftTable.setName(attrs.get(0).split("\\.")[0]);
			leftCol.setTable(leftTable);
			
			Column rightCol = new Column();
			rightCol.setColumnName(attrs.get(1));
			Table rightTable = new Table();
			rightTable.setName(attrs.get(1).split("\\.")[0]);
			rightCol.setTable(rightTable);

			EqualsTo equalsTo = new EqualsTo();
			equalsTo.setLeftExpression(leftCol);
			equalsTo.setRightExpression(rightCol);
			
			expr = (expr != null) ? new AndExpression(expr, equalsTo) : equalsTo; 
		}
		expr = (unused == null) ? expr : new AndExpression(expr, unused);
		return expr;
	}

	public Operator generatePlan(LogicalOperator lop) {
		lop.accept(this);
		return immOp;
	}

	public void visit(Leaf leaf) {
		immOp = new ScanOperator(leaf.getTableName(), leaf.getAlias());
	}

	public void visit(SelectLogOp selectLop) {
		HashMap<List<String>, Integer[]> attributes = selectLop.getAttrributes();
		SelectCost costCalc = new SelectCost();
		String[] selectPlan = costCalc.selectScan(selectLop.getTableName(), selectLop.getAlias(), attributes);

		// If the first element of the array is "full", generate a full scan
		if (selectPlan[0].equals("full")) {
			immOp = new ScanOperator(selectLop.getTableName(), selectLop.getAlias());

			Expression unused = selectLop.getUnusedExpr();
			if (attributes.isEmpty()) {
				// It must have some expression, otherwise, should generate a leaf
				assert(unused != null);
				immOp = new SelectOperator(unused, immOp);
				return;
			}
			Expression expr = buildExpression(attributes);
			expr = (unused == null) ? expr : new AndExpression(expr, unused);
			immOp = new SelectOperator(unused, immOp);
			return;
		}

		assert(selectPlan[0].equals("index"));
		int low;
		int high;
		
		EqualsTo removed = null;
		for (List<String> attr: attributes.keySet()) {
			// check if the original hashmap is modified correctly
			if (attr.contains(selectPlan[1])) {
				low = (attributes.get(attr)[0] != null) ? attributes.get(attr)[0] : Integer.MIN_VALUE;
				high = (attributes.get(attr)[1] != null) ? attributes.get(attr)[1] : Integer.MAX_VALUE;
				
				// Removed the indexed column from the hashmap
				Integer[] removedBound = attributes.get(attr);
				attributes.remove(attr);
				attr.remove(selectPlan[1]);
				attributes.put(attr, removedBound);
				
				
				// Example: [S.A = S.B AND S.A < 3] -> [S.B] if indexing on column A, then the expression built 
				// becomes [S.B < 3], want to restore [S.A = S.B]
				if (attr.size() > 0 && high != low) {
					Column removedColExpr = new Column();
					removedColExpr.setColumnName(selectPlan[1].split("\\.")[1]);
					Table removedTable = new Table();
					removedTable.setName(selectPlan[1].split("\\.")[0]);
					removedColExpr.setTable(removedTable);
					
					Column nextColExpr = new Column();
					nextColExpr.setColumnName(attr.get(0).split("\\.")[1]);
					Table nextTable = new Table();
					nextTable.setName(attr.get(0).split("\\.")[0]);
					nextColExpr.setTable(nextTable);
					
					removed = new EqualsTo();
					removed.setLeftExpression(removedColExpr);
					removed.setRightExpression(nextColExpr);
				}
			}
		}
		String tableIndexDir = indexDir + "/" + selectLop.getTableName() + "." + selectPlan[1].split("\\.")[1];
		boolean clustered = selectPlan[2].equals("clustered");
		immOp = new IndexScanOperator(selectLop.getTableName(), selectLop.getAlias(), selectPlan[1], 
				tableIndexDir, clustered, low, high);

		Expression unused = selectLop.getUnusedExpr();
		// If all select expressions are resolve, which means one indexScan is enough 
		if (attributes.isEmpty() && unused == null) {
			return;
		}

		if (attributes.isEmpty()) {
			// It must have some expression, otherwise, should generate a leaf
			assert(unused != null);
			immOp = new SelectOperator(unused, immOp);
			return;
		}
		Expression expr = buildExpression(attributes);
		expr = (unused == null) ? expr : new AndExpression(expr, unused);
		expr = (removed == null) ? expr : new AndExpression(removed, expr);
		immOp = new SelectOperator(expr, immOp);
		return;
	}

	public void visit(ProjectLogOp projectLop) {
		projectLop.getChildren().get(0).accept(this);
		immOp= new ProjectOperator(immOp, projectLop.getItems());
	}

	public void visit(SortLogOp sortLop) {
		sortLop.getChildren().get(0).accept(this);
		immOp= new ExternalSortOperator(immOp, sortLop.getColumns(), 10, tempDir, "sort");
	}

	public void visit(DuplicateEliminationLogOp dupElimLogOp) {
		dupElimLogOp.getChildren().get(0).accept(this);
		if (immOp instanceof SortOperator) {
			immOp= new DuplicateEliminationOperator((SortOperator) immOp);
		} else {
			immOp= new DuplicateEliminationOperator((ExternalSortOperator) immOp);
		}
	}

	/**
	 * 
	 * FROM S, R, B WHERE S.A = R.B will generate BNLJ -> SMJ
	 * 
	 * @param joinLogOp
	 */
	public void visit(JoinLogOp joinLogOp) {
		UnionFindGenerator ufGen = new UnionFindGenerator(joinLogOp.getJoinExpression());
		UnionFind uf = ufGen.getUnionFind();
		List<LogicalOperator> joinChildren = joinLogOp.getChildren();
		JoinOptimizer joinOptimizer = new JoinOptimizer(joinChildren, uf);
		List<LogicalOperator> joinOrder = joinOptimizer.findOptimalJoinOrder();
		
		
		// Build the Join tree
		joinOrder.get(0).accept(this);;
		Operator leftChildOp = immOp;
		joinOrder.get(1).accept(this);;
		Operator rightChildOp = immOp;
		
		List<String> joinedTable = new ArrayList<String>();
		joinedTable.add(leftChildOp.getTableName());
		Expression joinCondition = buildJoinExpr(joinLogOp.getJoinExpression(), uf.findJoinInBox(joinedTable, rightChildOp.getTableName()));
		JoinTableVisitor joinVisitor = new JoinTableVisitor(joinCondition, joinedTable, rightChildOp.getTableName());
		
		
		// choose between SMJ and BNLJ
		List<LogicalOperator> joinOrderCopy = new ArrayList<LogicalOperator>(joinOrder);
		if(joinVisitor.allEquality() && checkSMJ(joinVisitor.getRelevant(), joinOrderCopy)) {
			immOp = new SMJ(10, leftChildOp, rightChildOp, joinVisitor.getRelevant(), tempDir, true);
		} else {
			immOp = new BNLJ(10, leftChildOp, rightChildOp, joinVisitor.getRelevant());
		}
		
		// reset to loop through the remaining tables
		Expression restExpr = buildJoinExpr(joinVisitor.getResidual(), uf.findJoinInBox(joinedTable, rightChildOp.getTableName()));
		joinedTable.add(rightChildOp.getTableName());
		
		for(int i = 2; i < joinOrder.size(); ++i) {
			joinOrder.get(i).accept(this);
			Operator rightJoin = immOp;
			
			joinVisitor = new JoinTableVisitor(restExpr, joinedTable, rightJoin.getTableName());
			
			// choose between SMJ and BNLJ
			joinOrderCopy = new ArrayList<LogicalOperator>(joinOrder);
			if(joinVisitor.allEquality() && checkSMJ(joinVisitor.getRelevant(), joinOrder)) {
				immOp = new SMJ(10, leftChildOp, rightChildOp, joinVisitor.getRelevant(), tempDir, true);
			} else {
				immOp = new BNLJ(10, leftChildOp, rightChildOp, joinVisitor.getRelevant());
			}			
			
			restExpr = buildJoinExpr(joinVisitor.getResidual(), uf.findJoinInBox(joinedTable, rightChildOp.getTableName()));
			joinedTable.add(rightChildOp.getTableName());
		}
	}
	
	/**
	 * 
	 * Example: FROM S, R, B WHERE S.A = R.B AND S.C = B.G is valid
	 * 
	 * @param joinExpr
	 * @param joinOrder
	 * @return
	 */
	private boolean checkSMJ(Expression joinExpr, List<LogicalOperator> joinOrder) {
		if (! (joinExpr instanceof AndExpression)) {
			assert(joinExpr instanceof BinaryExpression);
			if (joinOrder.size() != 2) {
				return false;
			} 

			Column left = (Column) ((BinaryExpression) joinExpr).getLeftExpression();
			Column right = (Column) ((BinaryExpression) joinExpr).getRightExpression();
			
			String leftTableName = left.getTable().getName();
			String rightTableName = right.getTable().getName();
			String leftJoinName = joinOrder.get(0).getTableName();
			String rightJoinName = joinOrder.get(1).getTableName();
			
			return ((leftJoinName.equals(leftTableName) && rightJoinName.equals(rightTableName))
				|| (leftJoinName.equals(rightTableName) && rightJoinName.equals(leftTableName)));
		} 
		
		Expression rightExpr = ((AndExpression) joinExpr).getRightExpression();
		Column left = (Column) ((BinaryExpression) rightExpr).getLeftExpression();
		Column right = (Column) ((BinaryExpression) rightExpr).getRightExpression();
		String leftExprName = left.getTable().getName();
		String rightExprName = right.getTable().getName();
		String rightJoinName = joinOrder.remove(joinOrder.size() - 1).getTableName();
		
		return checkSMJ(((AndExpression) joinExpr).getLeftExpression(), joinOrder) && 
				(leftExprName.equals(rightJoinName) || rightExprName.equals(rightJoinName));
	}
}
