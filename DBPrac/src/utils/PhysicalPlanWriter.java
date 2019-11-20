package utils;

import java.io.BufferedWriter;
import java.io.IOException;

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

public class PhysicalPlanWriter {
	private int level;
	private BufferedWriter writer;

	public PhysicalPlanWriter(BufferedWriter bw, Operator op) {
		writer= bw;
		op.accept(this);
	}

	public void visit(BNLJ op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "BNLJ[" +
			op.getExpression().toString() + "]\n");
		level++ ;
		int prevLevel= level;
		op.getOuterOperator().accept(this);
		level= prevLevel;
		op.getInnerOperator().accept(this);
		level= prevLevel;
	}

	public void visit(DuplicateEliminationOperator op) throws IOException {
		writer.write("DupElim");
		level++ ;
		op.getChild().accept(this);
	}

	public void visit(ExternalSortOperator op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "ExternalSort[" +
			String.join(", ", op.getColList()) + "]\n");
		level++ ;
		op.getChild().accept(this);
	}

	public void visit(IndexScanOperator op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "IndexScan[" +
			op.getTableName() + ", " + op.getCol() + ", " + op.getLow() + ", " + op.getHigh() + "]\n");
	}

	// No need to implement this
	public void visit(JoinOperator op) {
	}

	public void visit(ProjectOperator op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "Project[" +
			String.join(", ", op.getSelectCols()) + "]\n");
		level++ ;
		op.getChild().accept(this);
	}

	public void visit(ScanOperator op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "TableScan[" +
			op.getTableName() + "]\n");
	}

	public void visit(SelectOperator op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "Select[" +
			op.getExpression() + "]\n");
		level++ ;
		int prevLevel= level;
		op.getChild().accept(this);
		level= prevLevel;
	}

	public void visit(SMJ op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "SMJ[" +
			op.getJoinExpression().toString() + "]\n");
		level++ ;
		int prevLevel= level;
		op.getLeftChild().accept(this);
		level= prevLevel;
		op.getRightChild().accept(this);
		level= prevLevel;
	}

	public void visit(SortOperator op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "InMemorySort[" +
			String.join(", ", op.getColList()) + "]\n");
		level++ ;
		op.getChild().accept(this);
	}
}
