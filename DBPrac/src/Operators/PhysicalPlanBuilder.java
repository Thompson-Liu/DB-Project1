package Operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import dataStructure.BlueBox;
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
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import parser.IndexConditionSeperator;
import parser.JoinTableVisitor;
import parser.UnionFindGenerator;
import physicalOperator.BNLJ;
import physicalOperator.DuplicateEliminationOperator;
import physicalOperator.ExternalSortOperator;
import physicalOperator.IndexScanOperator;
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

	/**
	 * Build the join conditon from residual join and the bluebox
	 * 
	 * @param unused  	residual join condition that cannot fit in the bluebox
	 * @param condFromBox	 a list of list of equal column attributes that from the same bluebox 
	 * @return
	 */
	private Expression buildJoinExpr(Expression unused, List<ArrayList<String>> condFromBox) {
		if (condFromBox.isEmpty()) {
			return unused;
		} 

		Expression expr = null;
		for (List<String> attrs: condFromBox) {
			assert(attrs.size() == 2);

			Column leftCol = new Column();
			leftCol.setColumnName(attrs.get(0).split("\\.")[1]);
			Table leftTable = new Table();
			leftTable.setName(attrs.get(0).split("\\.")[0]);
			leftCol.setTable(leftTable);

			Column rightCol = new Column();
			rightCol.setColumnName(attrs.get(1).split("\\.")[1]);
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

	/**
	 * Generate the physcial operator from the logical Operator  
	 * 
	 * @param lop 	the logical operator built from logical operator factory
	 * @return
	 */
	public Operator generatePlan(LogicalOperator lop) {
		lop.accept(this);
		return immOp;
	}

	public void visit(Leaf leaf) {
		immOp = new ScanOperator(leaf.getTableName(), leaf.getAlias());
	}

	public void visit(SelectLogOp selectLop) {
		List<BlueBox> attributes = selectLop.getAttributes();
		SelectCost costCalc = new SelectCost();
		String[] selectPlan = costCalc.selectScan(selectLop.getTableName(), selectLop.getAlias(), attributes);

		// If the first element of the array is "full", generate a full scan
		if (selectPlan[0].equals("full")) {
			immOp = new ScanOperator(selectLop.getTableName(), selectLop.getAlias());
			immOp = new SelectOperator(selectLop.getSelectExpr(), immOp);
			return;
		}

		assert(selectPlan[0].equals("index"));
		Integer low = null;
		Integer high = null;

		EqualsTo removed = null;
		for (BlueBox attrBB: attributes) {
			List<String> attr = attrBB.getAttr();
			if (attr.contains(selectPlan[1])) {
				low = (attrBB.getLower() != null) ? attrBB.getLower() : Integer.MIN_VALUE;
				high = (attrBB.getUpper() != null) ? attrBB.getUpper() : Integer.MAX_VALUE;

				// Example: [S.A = S.B AND S.A < 3] -> [S.B] if indexing on column A, then the expression built 
				// becomes [S.B < 3], want to restore [S.A = S.B]
				if (attr.size() > 1 && high != low) {
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
				break;
			}
		}
		String tableIndexDir = indexDir + "/" + selectLop.getTableName() + "." + selectPlan[1].split("\\.")[1];
		boolean clustered = selectPlan[2].equals("clustered");
		try {
			String tableName = (selectLop.getAlias().equals("")) ? selectLop.getTableName() : selectLop.getAlias();
			IndexConditionSeperator indexSep= new IndexConditionSeperator(tableName, selectPlan[1].split("\\.")[1], selectLop.getSelectExpr());
			immOp = new IndexScanOperator(selectLop.getTableName(), selectLop.getAlias(), selectPlan[1], 
					tableIndexDir, clustered, low, high);
			if (indexSep.applyAll()) {
				return;
			}

			// Deal with the rest expressions with another select operator
			immOp = new SelectOperator(indexSep.getResidual(), immOp);
		} catch(Exception e) {
			System.err.println("Error when creating an indexScan Operator");
			e.printStackTrace();
		}				
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
		UnionFind uf = joinLogOp.getUnionFind();
		List<LogicalOperator> joinChildren = joinLogOp.getChildren();
		// Maintain the original Orderr
		List<String> oriJoinOrder = new ArrayList<String>();
		for (LogicalOperator logOp: joinChildren) {
			String name = logOp.getAlias().equals("") ? logOp.getTableName() : logOp.getAlias();
			oriJoinOrder.add(name);
		}

		JoinOptimizer joinOptimizer = new JoinOptimizer(joinChildren, uf);
		List<LogicalOperator> joinOrder = joinOptimizer.findOptimalJoinOrder();


		// Build the Join tree
		joinOrder.get(0).accept(this);
		Operator leftChildOp = immOp;
		joinOrder.get(1).accept(this);
		Operator rightChildOp = immOp;

		List<String> joinedTable = new ArrayList<String>();
		joinedTable.add(leftChildOp.getTableName());

		Expression joinCondition = buildJoinExpr(joinLogOp.getJoinExpression(), uf.findJoinInBox(joinedTable, rightChildOp.getTableName()));
		JoinTableVisitor joinVisitor = new JoinTableVisitor(joinCondition, joinedTable, rightChildOp.getTableName());

		Operator joinIntermediate;
		// choose between SMJ and BNLJ
		List<String> joinedTableCopy = new ArrayList<String>(joinedTable);
		joinedTableCopy.add(rightChildOp.getTableName());

		// restore the original join order
		List<String> tableOrder = new ArrayList<String>();
		List<Integer> tableOrderIndx = new ArrayList<Integer>();
		for (String tableName: joinedTableCopy) {
			tableOrderIndx.add(oriJoinOrder.indexOf(tableName));
		}
		tableOrderIndx.sort(null);
		for (Integer indx: tableOrderIndx) {
			tableOrder.add(oriJoinOrder.get(indx));
		}

		if(checkSMJ(joinVisitor.getRelevant(), joinedTable, rightChildOp.getTableName())) {
			joinIntermediate = new SMJ(10, leftChildOp, rightChildOp, joinVisitor.getRelevant(), tableOrder, tempDir, true);
		} else {
			joinIntermediate = new BNLJ(10, leftChildOp, rightChildOp, joinVisitor.getRelevant(), tableOrder);
		}

		// reset to loop through the remaining tables
		joinedTable.add(rightChildOp.getTableName());

		for(int i = 2; i < joinOrder.size(); ++i) {
			joinOrder.get(i).accept(this);
			Operator rightJoin = immOp;

			joinedTableCopy = new ArrayList<String>(joinedTable);
			joinedTableCopy.add(rightJoin.getTableName());

			// restore the original join order
			tableOrder = new ArrayList<String>();
			tableOrderIndx = new ArrayList<Integer>();
			for (String tableName: joinedTableCopy) {
				tableOrderIndx.add(oriJoinOrder.indexOf(tableName));
			}
			tableOrderIndx.sort(null);
			for (Integer indx: tableOrderIndx) {
				tableOrder.add(oriJoinOrder.get(indx));
			}

			Expression restExpr = buildJoinExpr(joinVisitor.getResidual(), uf.findJoinInBox(joinedTable, rightJoin.getTableName()));
			joinVisitor = new JoinTableVisitor(restExpr, joinedTable, rightJoin.getTableName());

			// choose between SMJ and BNLJ
			if(checkSMJ(joinVisitor.getRelevant(), joinedTable, rightJoin.getTableName())) {
				joinIntermediate = new SMJ(10, joinIntermediate, rightJoin, joinVisitor.getRelevant(), tableOrder, tempDir, true);
			} else {
				joinIntermediate = new BNLJ(10, joinIntermediate, rightJoin, joinVisitor.getRelevant(), tableOrder);
			}	
			joinedTable.add(rightJoin.getTableName());
		}
		immOp = joinIntermediate;
	}

	/**
	 * Check if SMJ is applicable
	 * Example: FROM S, R, B WHERE S.A = R.B AND S.C = B.G is valid
	 * 
	 * @param joinExpr         the expression between two  joined table
	 * @param joinedTable 	   a list of tableNames that are already joined together
	 * @param tableRight	   the table being joined together      
	 * @return
	 */
	private boolean checkSMJ(Expression joinExpr, List<String> joinedTable, String tableRight) {
		if (joinExpr == null) {
			return false;
		}

		if (joinExpr instanceof EqualsTo) {
			Column left = (Column) ((BinaryExpression) joinExpr).getLeftExpression();
			Column right = (Column) ((BinaryExpression) joinExpr).getRightExpression();

			String leftTableName = left.getTable().getName();
			String rightTableName = right.getTable().getName();

			return ((joinedTable.contains(leftTableName) && tableRight.equals(rightTableName))
					|| (tableRight.equals(leftTableName) && joinedTable.contains(rightTableName)));
		} 
		
		if (!(joinExpr instanceof AndExpression)) {
			return false;
		}
		
		Expression right = ((BinaryExpression) joinExpr).getRightExpression();
		Expression left = ((BinaryExpression) joinExpr).getLeftExpression();
		return checkSMJ(right, joinedTable, tableRight) && checkSMJ(left, joinedTable, tableRight);
	}
}
