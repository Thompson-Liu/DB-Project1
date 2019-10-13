package Operators;

import logicalOperators.DuplicateEliminationLogOp;
import logicalOperators.JoinLogOp;
import logicalOperators.LogicalOperator;
import logicalOperators.ProjectLogOp;
import logicalOperators.ScanLogOp;
import logicalOperators.SelectLogOp;
import logicalOperators.SortLogOp;
import physicalOperator.DuplicateEliminationOperator;
import physicalOperator.JoinOperator;
import physicalOperator.Operator;
import physicalOperator.ProjectOperator;
import physicalOperator.ScanOperator;
import physicalOperator.SelectOperator;
import physicalOperator.SortOperator;

public class PhysicalPlanBuilder {

	static Operator immOp;
	
	public PhysicalPlanBuilder() {

	}
	
	public Operator generatePlan(LogicalOperator lop) {
		lop.accept(this);
		return immOp;
	}

	public void visit(ScanLogOp scanLop) {
		immOp = new ScanOperator(scanLop.getTableName(), scanLop.getAliasName());
	}

	public void visit(SelectLogOp selectLop) {
		selectLop.getChildren()[0].accept(this);
		immOp = new SelectOperator(selectLop.getSelectExpr(), immOp, selectLop.getAlias());
	}

	public void visit(ProjectLogOp projectLop) {
		projectLop.getChildren()[0].accept(this);
		immOp = new ProjectOperator(immOp, projectLop.getItems(), projectLop.getAlias());
	}

	public void visit (SortLogOp sortLop) {
		sortLop.getChildren()[0].accept(this);
		immOp = new SortOperator(immOp, sortLop.getColumns());
	}

	public void visit(DuplicateEliminationLogOp dupElimLogOp) {
		dupElimLogOp.getChidren()[0].accept(this);
		immOp = new DuplicateEliminationOperator((SortOperator)immOp);
	}

	public void visit(JoinLogOp joinLogOp) {
		joinLogOp.getChildren()[0].accept(this);
		Operator leftChildOp = immOp;
		
		joinLogOp.getChildren()[1].accept(this);
		Operator rightChildOp = immOp;
		
		immOp = new JoinOperator(leftChildOp, rightChildOp, joinLogOp.getJoinExpression(),
				joinLogOp.getAlias());
	}

}
