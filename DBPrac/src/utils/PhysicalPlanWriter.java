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

		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visit(BNLJ op) throws IOException {
		String exprStr = "";
		if (op.getExpression() != null) {
			exprStr = op.getExpression().toString();
		}
		
		writer.write(new String(new char[level]).replace("\0", "-") + "BNLJ[" + exprStr + "]\n");
		level++;
		int prevLevel= level;
		op.getOuterOperator().accept(this);
		level= prevLevel;
		op.getInnerOperator().accept(this);
		level= prevLevel;
	}

	public void visit(DuplicateEliminationOperator op) throws IOException {
		writer.write("DupElim\n");
		level++;
		op.getChild().accept(this);
	}

	public void visit(ExternalSortOperator op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "ExternalSort[" +
				String.join(", ", op.getColList()) + "]\n");
		level++;
		op.getChild().accept(this);
	}

	public void visit(IndexScanOperator op) throws IOException {
		String hi = "null";
		if (op.getHigh() != Integer.MAX_VALUE) {
			hi = op.getHigh() + "";
		}
		String lo = "null";
		if (op.getLow() != Integer.MIN_VALUE) {
			lo = op.getLow() + "";
		}
		writer.write(new String(new char[level]).replace("\0", "-") + "IndexScan[" +
				op.getOriginalTableName() + "," + op.getCol().split("\\.")[1] + "," 
				+ lo + "," + hi + "]\n");
	}

	// No need to implement this
	public void visit(JoinOperator op) {

	}

	public void visit(ProjectOperator op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "Project[" +
				String.join(", ", op.getSelectCols()) + "]\n");
		level++;
		op.getChild().accept(this);
	}

	public void visit(ScanOperator op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "TableScan[" +
				op.getOriginlTableName() + "]\n");
	}

	public void visit(SelectOperator op) throws IOException {
		String exprStr = "";
		if (op.getExpression() != null) {
			exprStr = op.getExpression().toString();
		}
		
		writer.write(new String(new char[level]).replace("\0", "-") + "Select[" +
				exprStr + "]\n");
		level++;
		int prevLevel= level;
		op.getChild().accept(this);
		level= prevLevel;
	}

	public void visit(SMJ op) throws IOException {
		String exprStr = "";
		if (op.getJoinExpression() != null) {
			exprStr = op.getJoinExpression().toString();
		}
		
		writer.write(new String(new char[level]).replace("\0", "-") + "SMJ[" +
				exprStr + "]\n");
		level++;
		int prevLevel= level;
		op.getLeftChild().accept(this);
		level= prevLevel;
		op.getRightChild().accept(this);
		level= prevLevel;
	}

	public void visit(SortOperator op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "InMemorySort[" +
				String.join(", ", op.getColList()) + "]\n");
		level++;
		op.getChild().accept(this);
	}
}
