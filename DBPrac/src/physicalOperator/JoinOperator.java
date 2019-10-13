/**
 * Takes in a joined left table and right table to scan
 */
package physicalOperator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructure.DataTable;
import dataStructure.Tuple;
import fileIO.*;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateWhere;

/** the class for the join operator */
public class JoinOperator extends Operator {

	private DataTable currentTable;
	private Operator leftOperator;
	private Operator rightOperator;
	private Expression joinExp;
	private static boolean  resetFlag= true;
	private Tuple left;
	private HashMap<String, String> tableAlias;
	private int ptr;

	/** Constructor to create JOIN operator
	 * 
	 * @param LeftOperator the left operator of join
	 * @param RightOperator the right operator of join
	 * @param expression the where expression to select tuple
	 * @param tableAlias hashmap of <tablename,alias> */
	public JoinOperator(Operator LeftOperator, Operator RightOperator,
		Expression expression, HashMap<String, String> tableAlias) {
		joinExp= expression;
		ArrayList<String> cur= (ArrayList<String>) LeftOperator.schema().clone();
		cur.addAll(RightOperator.schema());
		currentTable= new DataTable(LeftOperator.getTableName() + " " + RightOperator.getTableName(), cur);
		leftOperator= LeftOperator;
		rightOperator= RightOperator;
		this.tableAlias= tableAlias;
		
		//build the table with the operator construction
		buildTable();
		
	}

	/** reset both left operator and right operator to start from beginning */
	@Override
	public void reset() {
		ptr=-1;
//		leftOperator.reset();
//		rightOperator.reset();
	}

	/** @return the table name from where the operator reads the data */
	@Override
	public String getTableName() {
		return currentTable.getTableName();
	}

	/** @return the schema of the data table that is read by the operator */
	@Override
	// return the schema of the current table
	public ArrayList<String> schema() {
		return currentTable.getSchema();
	}


	@Override
	public Tuple getNextTuple() {
		ptr+= 1;
		if (ptr < currentTable.cardinality()) return new Tuple(currentTable.getRow(ptr));
		return null;
//		Tuple next= null;
//		boolean flag= true;
//		Tuple right;
//		EvaluateWhere evawhere= new EvaluateWhere(joinExp, leftOperator.schema(),
//			rightOperator.schema(), tableAlias);
//		while (flag) {
//			if (resetFlag) {
//				while ((left= leftOperator.getNextTuple()) != null) {
//					while ((right= rightOperator.getNextTuple()) != null) {
//						
//						if ((next= evawhere.evaluate(left, right)) != null) {
////							System.out.println(next.printData());
////							System.out.println("left is   " + left.printData());
////							System.out.println("right is   " + right.printData());
//							currentTable.addData(next);
//							resetFlag= false;
//							return next;}}
//					rightOperator.reset();
//				}
//				flag= false;
//			} else {
//				while ((right= rightOperator.getNextTuple()) != null) {
//					if ((next= evawhere.evaluate(left, right)) != null) {
//						currentTable.addData(next);
//						return next;}}
//				rightOperator.reset();
//				resetFlag= true;}
//		}
//		return null;

	}
	
	
	/** for every left tuple loop through every right tuple, until finding a valid tuple or no more
	 * tuple to add reset to next left tuple when one tuple is done permutating right table
	 * 
	 * @return Returns the next tuple read from the data */
	private void buildTable() {
		Tuple t;
		while ((t = HelperBuildTuple()) != null) {
		}
	}
	public Tuple HelperBuildTuple() {
		Tuple next= null;
		boolean flag= true;
		Tuple right;
		EvaluateWhere evawhere= new EvaluateWhere(joinExp, leftOperator.schema(),
			rightOperator.schema(), tableAlias);
		while (flag) {
			if (resetFlag) {
				while ((left= leftOperator.getNextTuple()) != null) {
					while ((right= rightOperator.getNextTuple()) != null) {
						if ((next= evawhere.evaluate(left, right)) != null) {
							currentTable.addData(next);
							resetFlag= false;
//							System.out.println(next.printData());
							return next;
						}
					}
					rightOperator.reset();
				}
				flag= false;
			} else {
				while ((right= rightOperator.getNextTuple()) != null) {
					
					if ((next= evawhere.evaluate(left, right)) != null) {
						currentTable.addData(next);
//						System.out.println(next.printData());
						return next;
					}
				}
				rightOperator.reset();
				resetFlag= true;
			}
		}
		return null;
	}


	/** Prints the data read by operator to the PrintStream [ps]
	 * 
	 * @param ps The print stream that the output will be printed to
	 * @param print boolean decides whether the data will actually be printed */
	@Override
	public void dump(TupleWriter writer) {
		writer.writeTable(getData().toArrayList());
		writer.dump();
		writer.close();
	}

	/** @return the data read by the operator in DataTable data structure */
	@Override
	public DataTable getData() {
		Tuple t;
		while ((t = getNextTuple()) != null) {
			
		}
		
		reset();
		return currentTable;
	}
}
