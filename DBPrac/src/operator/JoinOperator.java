/**
 * Takes in a joined left table and right table to scan
 */
package operator;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateWhere;

public class JoinOperator extends Operator {

	private DataTable currentTable;
	private Operator leftOperator;
	private Operator rightOperator;
	private Expression joinExp;
	private boolean resetFlag= true;
	private Tuple left;
	private HashMap<String,String> tableAlias;

	/** Constructor to create JOIN operator
	 * 
	 * @param LeftOperator  	the left operator of join
	 * @param RightOperator     the right operator of join
	 * @param expression  		the where expression to select tuple
	 * @param tableAlias  		hashmap of <tablename,alias>
	 */
	public JoinOperator(Operator LeftOperator, Operator RightOperator, 
			Expression expression,HashMap<String,String> tableAlias) {
		joinExp= expression;
		ArrayList<String> cur= (ArrayList<String>) LeftOperator.schema().clone();
		cur.addAll(RightOperator.schema());
		currentTable= new DataTable(LeftOperator.getTableName() + " " + RightOperator.getTableName(), cur);
		leftOperator= LeftOperator;
		rightOperator= RightOperator;
		this.tableAlias=tableAlias;
	}

	/**
	 * reset both left operator and right operator to start from beginning
	 */
	@Override
	public void reset() {
		leftOperator.reset();
		rightOperator.reset();
	}

	/**
	 * @return the table name from where the operator reads the data
	 */
	@Override
	public String getTableName() {
		return currentTable.getTableName();
	}

	/**
	 * @return the schema of the data table that is read by the operator
	 */
	@Override
	// return the schema of the current table
	public ArrayList<String> schema() {
		return currentTable.getSchema();
	}

	/** for every left tuple loop through every right tuple, until finding a valid tuple or no more tuple to add
	 * reset to next left tuple when one tuple is done permutating right table
	 * @return Returns the next tuple read from the data
	 */
	@Override
	public Tuple getNextTuple() {
		Tuple next= null;
		boolean flag= true;
		Tuple right;
		EvaluateWhere evawhere= new EvaluateWhere(joinExp, leftOperator.schema(),
			rightOperator.schema(),tableAlias);

		while (flag) {
			if (resetFlag) {
				while ((left= leftOperator.getNextTuple()) != null) {
					while ((right= rightOperator.getNextTuple()) != null) {
						if ((next= evawhere.evaluate(left, right)) != null) {
							currentTable.addData(next);
							resetFlag= false;
							return next;
						}
					}
				}
				flag= false;
			} else {
				while ((right= rightOperator.getNextTuple()) != null) {
					if ((next= evawhere.evaluate(left, right)) != null) {
						currentTable.addData(next);
						return next;
					}
				}
				rightOperator.reset();
				resetFlag= true;
			}
		}
		return null;

	}

	/**
	 * Prints the data read by operator to the PrintStream [ps]
	 * 
	 * @param ps      The print stream that the output will be printed to
	 * @param print   boolean decides whether the data will actually be printed 
	 */
	@Override
	public void dump(PrintStream ps, boolean print) {
		Tuple next;
		while ((next= getNextTuple()) != null) {
		}
		if (print) { currentTable.printTable(ps); }
	}

	/** 
	 * @return the data read by the operator in DataTable data structure
	 */
	@Override
	public DataTable getData() {
		dump(System.out, false);
		return currentTable;
	}
}
