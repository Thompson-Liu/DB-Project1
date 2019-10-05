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

	public PhysicalPlanBuilder() {
		// TODO Auto-generated constructor stub
	}

	public static Operator generatePlan(LogicalOperator lop) {
		if (lop == null) { return null; }
		if (lop instanceof ScanLogOp) {
			ScanOperator root= new ScanOperator(((ScanLogOp) lop).getTableName(), ((ScanLogOp) lop).getAliasName());
		} else if (lop instanceof SelectLogOp) {
			SelectOperator root= new SelectOperator(((SelectLogOp) lop).getSelectExpr(),
				generatePlan(((SelectLogOp) lop).getChildren()[0]), ((SelectLogOp) lop).getAlias());
		} else if (lop instanceof ProjectLogOp) {
			ProjectOperator root= new ProjectOperator(generatePlan(((ProjectLogOp) lop).getChildren()[0]),
				((ProjectLogOp) lop).getItems(), ((ProjectLogOp) lop).getAlias());
		} else if (lop instanceof JoinLogOp) {
			JoinOperator root= new JoinOperator(generatePlan(((JoinLogOp) lop).getLeftChild()),
				generatePlan(((JoinLogOp) lop).getRightChild()), ((JoinLogOp) lop).getJoinExpression(),
				((JoinLogOp) lop).getAlias());
		} else if (lop instanceof SortLogOp) {
			SortOperator root= new SortOperator(generatePlan(((SortLogOp) lop).getChildren()[0]),
				((SortLogOp) lop).getColumns());
		} else if (lop instanceof DuplicateEliminationLogOp) {
			DuplicateEliminationOperator root= new DuplicateEliminationOperator(
				(SortOperator) generatePlan(((DuplicateEliminationLogOp) lop).getChidren()[0]));
		}
		for (LogicalOperator child : lop.getChildren()) {

		}
		return null;

	}

	public Operator visit(ScanLogOp scanLop) {
		return new ScanOperator(scanLop.getTableName(), scanLop.getAliasName());
	}

	public Operator visit(SelectLogOp selectLop) {
		return new SelectOperator(selectLop.getSelectExpr(), visit(selectLop.getChildren()[0]), selectLop.getAlias());
	}

//	public Operator visit(ProjectLogOp projectLop) {
//		return new ProjectOperator();
//	}
//
//	public Operator visit (SortLogOp sortLop) {
//		return new 
//	}
//
//	public Operator visit(DuplicateEliminationLogOp dupElimLogOp) {
//
//	}

}
