/** in-memory join :   fetched all the data from left and right operators 
 *  and join them in memory
 */
package physicalOperator;

import java.util.ArrayList;
import java.util.List;
import dataStructure.Tuple;
import fileIO.TupleWriter;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateWhere;
import utils.PhysicalPlanWriter;

/** the class for the join operator */
public class JoinOperator extends Operator {

	private ArrayList<String> schema;
	private String tableName;
	private Operator leftOperator;
	private Operator rightOperator;
	private Expression joinExp;
	private boolean resetFlag= true;
	private Tuple left;
	private EvaluateWhere evawhere;

	/** Constructor to create JOIN operator
	 * 
	 * @param LeftOperator the left operator of join
	 * @param RightOperator the right operator of join
	 * @param expression the where expression to select tuple */
	public JoinOperator(Operator LeftOperator, Operator RightOperator, Expression expression, List<String> joinOrder) {
		joinExp= expression;
		leftOperator= LeftOperator;
		rightOperator= RightOperator;
		
		// construct the new schema, enforcing join order
		List<String> outerSchema = leftOperator.schema();
		schema = new ArrayList<String>();		
		int tableIndex = joinOrder.indexOf(rightOperator.getTableName());
		int tupleIndex = outerSchema.size();
		int counter = 0;
		String strCounter = "";
		if (tableIndex == joinOrder.size() - 1) {
			schema.addAll(outerSchema);
			schema.addAll(rightOperator.schema());
		} else {
			for (int i = 0; i < outerSchema.size(); ++i) {
				String curName = outerSchema.get(i).split("\\.")[0];
				if (!curName.equals(strCounter)) {
					if (counter == tableIndex) {
						tupleIndex = (i--);
						schema.addAll(rightOperator.schema());
						counter++;
						continue;
					}
					counter++;
					strCounter = curName;
				}
			    schema.add(outerSchema.get(i));
			}
		}

		tableName = "";
		for (int i = 0; i < joinOrder.size() - 1; ++i) {
			tableName += (joinOrder.get(i) + ",");
		}
		tableName += (joinOrder.get(joinOrder.size() - 1));
		
		evawhere = new EvaluateWhere(joinExp, leftOperator.schema(), rightOperator.schema(), tupleIndex);
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
					if ((next= evawhere.evaluate(left, right)) != null) { return next; }
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
		while ((t= getNextTuple()) != null) {
//			System.out.println(t.printData());
			writer.addNextTuple(t);
		}
		writer.dump();
		writer.close();
	}

	@Override
	public void accept(PhysicalPlanWriter ppw) {
	}
}
