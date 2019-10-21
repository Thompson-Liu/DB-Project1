/** in-memory join :   fetched all the data from left and right operators 
 *  and join them in memory
 */
package physicalOperator;

import java.util.ArrayList;
import java.util.HashMap;
import dataStructure.Tuple;
import fileIO.*;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateWhere;

/** the class for the join operator */
public class JoinOperator extends Operator {

	private ArrayList<String> schema;
	private String tableName;
	private Operator leftOperator;
	private Operator rightOperator;
	private Expression joinExp;
	private static boolean resetFlag= true;
	private Tuple left;
	private HashMap<String, String> tableAlias;

	/** Constructor to create JOIN operator
	 * 
	 * @param LeftOperator the left operator of join
	 * @param RightOperator the right operator of join
	 * @param expression the where expression to select tuple
	 * @param tableAlias hashmap of <tablename,alias> */
	public JoinOperator(Operator LeftOperator, Operator RightOperator,
		Expression expression, HashMap<String, String> tableAlias) {
		joinExp= expression;
		
		schema = (ArrayList<String>) LeftOperator.schema().clone();
		schema.addAll(RightOperator.schema());
	
		tableName = LeftOperator.getTableName() + " " + RightOperator.getTableName();
		leftOperator= LeftOperator;
		rightOperator= RightOperator;
		this.tableAlias= tableAlias;
	}

	/** reset both left operator and right operator to start from beginning */
	@Override
	public void reset() {
		leftOperator.reset();
		rightOperator.reset();
	}

	/** @return the table name from where the operator reads the data */
	@Override
	public String getTableName() {
		return tableName;
	}

	/** @return the schema of the data table that is read by the operator */
	@Override
	// return the schema of the current table
	public ArrayList<String> schema() {
		return schema;
	}

	@Override
	public Tuple getNextTuple() {
		Tuple next= null;
		boolean flag= true;
		Tuple right;
		EvaluateWhere evawhere= new EvaluateWhere(joinExp, leftOperator.schema(), rightOperator.schema(), tableAlias);
		
		while (flag) {
			if (resetFlag) {
				while ((left= leftOperator.getNextTuple()) != null) {
					while ((right= rightOperator.getNextTuple()) != null) {
						if ((next= evawhere.evaluate(left, right)) != null) {
							resetFlag= false;
							return next;
						}
					}
					rightOperator.reset();
				}
				flag= false;
			} else {
				while ((right= rightOperator.getNextTuple()) != null) {
					if ((next= evawhere.evaluate(left, right)) != null) {
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
		Tuple t;
		while((t = getNextTuple()) != null) {
			writer.addNextTuple(t);
		}
		writer.dump();
		writer.close();
	}
}
