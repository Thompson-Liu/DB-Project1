package utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import dataStructure.BlueBox;
import logicalOperators.DuplicateEliminationLogOp;
import logicalOperators.JoinLogOp;
import logicalOperators.Leaf;
import logicalOperators.LogicalOperator;
import logicalOperators.ProjectLogOp;
import logicalOperators.SelectLogOp;
import logicalOperators.SortLogOp;
import parser.UnionFindGenerator;

public class LogicalPlanWriter {
	private int level;
	private BufferedWriter writer;
	private UnionFindGenerator ufg;

	public LogicalPlanWriter(BufferedWriter bw, UnionFindGenerator ufg) {
		writer= bw;
		this.ufg= ufg;
	}

	public void visit(DuplicateEliminationLogOp op) throws IOException {
		writer.write("DupElim");
		level++ ;
		List<LogicalOperator> children= op.getChildren();
		assert children.size() == 1;
		children.get(0).accept(this);
	}

	private String helper(Integer i) {
		if (i == null) return "null";
		return i + "";
	}

	public void visit(JoinLogOp op) throws IOException {
		// need residual join here
		writer.write(new String(new char[level]).replace("\0", "-") + "Join[" +
			ufg.getResidualJoin().toString() + "]\n");
		List<BlueBox> boxes= ufg.getUnionFind().getBlueBoxes();
		for (int i= 0; i < boxes.size(); i++ ) {
			writer.write(
				"[[" + String.join(", ", boxes.get(i).getAttr()) + "]], equals " + helper(boxes.get(i).getEqual()) +
					", min " + helper(boxes.get(i).getLower()) + ", max " + helper(boxes.get(i).getUpper()));
		}
		level++ ;
		List<LogicalOperator> children= op.getChildren();
		int prevLevel= level;
		for (LogicalOperator child : children) {
			level= prevLevel;
			child.accept(this);
		}

	}

	public void visit(Leaf op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "Leaf[" + op.getTableName() + "]\n");
	}

	public void visit(ProjectLogOp op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "Project[" +
			String.join(", ", op.getItems() + "") + "]\n"); // not sure what is selectItems toString()?
		level++ ;
		List<LogicalOperator> children= op.getChildren();
		assert children.size() == 1;
		children.get(0).accept(this);

	}

	public void visit(SelectLogOp op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "Select[" +
			op.getSelectExpr().toString() + "]\n");

	}

	public void visit(SortLogOp op) throws IOException {
		writer.write(new String(new char[level]).replace("\0", "-") + "Sort[" +
			String.join(", ", op.getColumns()) + "]\n");
		level++ ;
		List<LogicalOperator> children= op.getChildren();
		assert children.size() == 1;
		children.get(0).accept(this);
	}

}
