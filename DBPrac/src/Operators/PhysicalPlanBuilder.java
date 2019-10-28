package Operators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import logicalOperators.DuplicateEliminationLogOp;
import logicalOperators.JoinLogOp;
import logicalOperators.LogicalOperator;
import logicalOperators.ProjectLogOp;
import logicalOperators.ScanLogOp;
import logicalOperators.SelectLogOp;
import logicalOperators.SortLogOp;
import physicalOperator.BNLJ;
import physicalOperator.DuplicateEliminationOperator;
import physicalOperator.ExternalSortOperator;
import physicalOperator.JoinOperator;
import physicalOperator.Operator;
import physicalOperator.ProjectOperator;
import physicalOperator.SMJ;
import physicalOperator.ScanOperator;
import physicalOperator.SelectOperator;
import physicalOperator.SortOperator;

public class PhysicalPlanBuilder {

	static Operator immOp;
	private BufferedReader buffer;
	private int[] join;
	private int[] sort;
	String tempDir;

	public PhysicalPlanBuilder(String configPath, String tempPath) {
		try {
			buffer= new BufferedReader(new FileReader(configPath));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot locate the file" + configPath);
		}
		join= readConfig();
		sort= readConfig();
//		assert (sort[0] == 0 || (sort[0] == 1 && sort[1] >= 3));

		try {
			buffer.close();
		} catch (IOException e) {
			System.err.println("Error during closing the buffer.");
		}
		tempDir= tempPath;
	}

	private int[] readConfig() {
		String nextLine;
		int argList[]= null;
		try {
			nextLine= buffer.readLine();
			String[] args= nextLine.split(" ");
			if (args.length == 1) {
				argList= new int[] { 0 };
			} else {
				assert (args.length == 2);

				argList= new int[2];
				for (int i= 0; i < argList.length; i++ ) {
					int argInt= Integer.parseInt(args[i]);
					argList[i]= argInt;
				}
			}
		} catch (Exception e) {
			System.err.println("An error occured during reading from file");
			e.getStackTrace();
		}
		return argList;
	}

	public Operator generatePlan(LogicalOperator lop) {
		lop.accept(this);
		return immOp;
	}

	public void visit(ScanLogOp scanLop) {
		immOp= new ScanOperator(scanLop.getTableName(), scanLop.getAliasName());
	}

	public void visit(SelectLogOp selectLop) {
		selectLop.getChildren()[0].accept(this);
		immOp= new SelectOperator(selectLop.getSelectExpr(), immOp, selectLop.getAlias());
	}

	public void visit(ProjectLogOp projectLop) {
		projectLop.getChildren()[0].accept(this);
		immOp= new ProjectOperator(immOp, projectLop.getItems(), projectLop.getAlias());
	}

	public void visit(SortLogOp sortLop) {
		sortLop.getChildren()[0].accept(this);

		if (sort[0] == 0) {
			immOp= new SortOperator(immOp, sortLop.getColumns());
		} else {
			immOp= new ExternalSortOperator(immOp, sortLop.getColumns(), sort[1], tempDir, "sort");
		}
	}

	public void visit(DuplicateEliminationLogOp dupElimLogOp) {
		dupElimLogOp.getChidren()[0].accept(this);
		if (immOp instanceof SortOperator) {
			immOp= new DuplicateEliminationOperator((SortOperator)immOp);
		} else {
			immOp= new DuplicateEliminationOperator((ExternalSortOperator)immOp);
		}
//		immOp= new DuplicateEliminationOperator((SortOperator) immOp);
	}

	public void visit(JoinLogOp joinLogOp) {
		joinLogOp.getChildren()[0].accept(this);
		Operator leftChildOp= immOp;

		joinLogOp.getChildren()[1].accept(this);
		Operator rightChildOp= immOp;

		switch (join[0]) {
		case 0:
			immOp= new JoinOperator(leftChildOp, rightChildOp, joinLogOp.getJoinExpression(),
				joinLogOp.getAlias());
			break;
		case 1:
			immOp= new BNLJ(join[1], leftChildOp, rightChildOp, joinLogOp.getJoinExpression(),
				joinLogOp.getAlias());
			break;
		case 2:
			immOp= new SMJ(join[1], leftChildOp, rightChildOp, joinLogOp.getJoinExpression(),
				joinLogOp.getAlias(), tempDir);
			break;
		}

	}

}
