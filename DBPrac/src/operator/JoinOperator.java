/**
 * Takes in a joined left table and right table to scan
 */
package operator;

import java.io.PrintStream;
import java.util.ArrayList;

import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateWhere;

public class JoinOperator extends Operator {

	private DataTable currentTable;
	private Operator leftOperator;
	private Operator rightOperator;
	private Expression joinExp;

	public JoinOperator(Operator LeftOperator, Operator RightOperator, Expression expression) {
		joinExp= expression;
		ArrayList<String> cur= (ArrayList<String>) LeftOperator.schema().clone();
		cur.addAll(RightOperator.schema());
		currentTable= new DataTable(LeftOperator.getTableName() + RightOperator.getTableName(), cur);
		leftOperator= LeftOperator;
		rightOperator= RightOperator;
	}

	@Override
	public void reset() {
		leftOperator.reset();
		rightOperator.reset();
	}

	@Override
	public String getTableName() {
		return currentTable.getTableName();
	}

	// return the schema of the current table
	public ArrayList<String> Schema() {
		return currentTable.getSchema();
	}

	// could use scan or could also use right table directly as input
	@Override
	public Tuple getNextTuple() {
		Tuple next;
		Tuple left;
		Tuple right;
		EvaluateWhere evawhere= new EvaluateWhere(joinExp);
		while ((left= leftOperator.getNextTuple()) != null) {
			while ((right= rightOperator.getNextTuple()) != null) {
				if ((next= evawhere.evaluate(left, right, leftOperator.schema(), rightOperator.schema())) != null) {
					currentTable.addData(next);
					return next;
				}
			}
			rightOperator.reset();
		}
		return null;
	}

	@Override
	public void dump(PrintStream ps) {
		currentTable.printTable(ps);
		;
	}

	@Override
	public DataTable getData() {
		return currentTable;
	}
}
