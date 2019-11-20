package utils;

import java.io.BufferedWriter;
import java.io.IOException;

import physicalOperator.BNLJ;
import physicalOperator.DuplicateEliminationOperator;
import physicalOperator.ExternalSortOperator;
import physicalOperator.IndexScanOperator;
import physicalOperator.JoinOperator;
import physicalOperator.ProjectOperator;
import physicalOperator.SMJ;
import physicalOperator.ScanOperator;
import physicalOperator.SelectOperator;
import physicalOperator.SortOperator;

public class PhysicalPlanWriter {
	private int level;
	private BufferedWriter writer;

	public PhysicalPlanWriter(BufferedWriter bw) {
		writer= bw;
	}

	public void visit(BNLJ op) {

	}

	public void visit(DuplicateEliminationOperator op) throws IOException {

	}

	public void visit(ExternalSortOperator op) {

	}

	public void visit(IndexScanOperator op) {

	}

	public void visit(JoinOperator op) {

	}

	public void visit(ProjectOperator op) {

	}

	public void visit(ScanOperator op) {

	}

	public void visit(SelectOperator op) {

	}

	public void visit(SMJ op) {

	}

	public void visit(SortOperator op) {

	}

}
