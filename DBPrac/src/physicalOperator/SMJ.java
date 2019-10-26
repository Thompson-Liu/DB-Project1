package physicalOperator;

import java.util.ArrayList;
import java.util.HashMap;
import dataStructure.Tuple;
import fileIO.TupleWriter;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateJoin;

/** the class for implementing the Sort Merge Join algorithm */
public class SMJ extends Operator {
	private Operator leftOp;
	private Operator rightOp;
	private ArrayList<String> leftColList;
	private ArrayList<String> rightColList;
	private ExternalSortOperator leftExSortOp;
	private ExternalSortOperator rightExSortOp;
	private Tuple tr;
	private Tuple ts;
	private Tuple gs;
	private int ptr;     // store the number of lines of gs
	private int count;   // store the number of lines of ts
	private ArrayList<String> schema;

	private ArrayList<String> leftSchema;
	private ArrayList<String> rightSchema;

	/** @return true if the first k pairs of attributes in leftColList and rightColList are equal to
	 * each other for the two given tuples, and false otherwise. */
	private int ensureEqual(Tuple leftTup, Tuple rightTup) {

		int result = 0; 
		int count = 0;
		while (count < leftColList.size() && result == 0) {
			result = leftTup.getData(leftSchema.indexOf(leftColList.get(count))) - rightTup
					.getData(rightSchema.indexOf(rightColList.get(count)));
			count++;
		}
		return result;
	}

	/** @param bufferSize: the buffer size used in external sort
	 * @param left: the left child operator
	 * @param right: the right child operator
	 * @param joinExpr: the join evaluation to be evaluated
	 * @param alias: the mapping between table names and aliases
	 * @param dir: the prefix of the directory that stores the temporary files generated during external
	 * sort. */
	public SMJ(int bufferSize, Operator left, Operator right, Expression joinExpr, HashMap<String, String> alias,
			String dir) {
		EvaluateJoin evalJoin= new EvaluateJoin(joinExpr, left.getTableName(), right.getTableName(), alias);
		leftColList= evalJoin.getJoinAttributesLeft();
		rightColList= evalJoin.getJoinAttributesRight();
		leftOp= left;
		rightOp= right;
		leftExSortOp= new ExternalSortOperator(leftOp, leftColList, bufferSize, dir, leftOp.getTableName());
		rightExSortOp= new ExternalSortOperator(rightOp, rightColList, bufferSize, dir, "right");

		tr = leftExSortOp.getNextTuple();

		ts = rightExSortOp.getNextTuple();
		gs = null;
		ptr = 0;
		count = 0;
		this.schema= new ArrayList<String>(left.schema());
		this.schema.addAll(right.schema());

		leftSchema = leftExSortOp.schema();
		rightSchema = rightExSortOp.schema();
	}

	@Override
	public Tuple getNextTuple() {
		while (tr != null && ts != null) {
			if (gs == null) {
				while (ensureEqual(tr, ts) < 0) {
					tr = leftExSortOp.getNextTuple();
					if (tr == null) { 
						leftExSortOp.deleteFile();
						rightExSortOp.deleteFile();
						return null; 
					} 
				}

				while (ensureEqual(tr, ts) > 0) {
					ts = rightExSortOp.getNextTuple();
					if (ts == null) { 
						leftExSortOp.deleteFile();
						rightExSortOp.deleteFile();
						return null; 
					} 
					count++;
				}
				gs = new Tuple(ts.getTuple());
				ptr = count;
			}

			Tuple joinedTuple = null;
			if (ensureEqual(tr, ts) == 0) {
				joinedTuple = new Tuple();

				// Generate the combined tuple result
				for (int j= 0; j < leftOp.schema().size(); j++ ) {
					joinedTuple.addData(tr.getData(j));
				}
				for (int j= 0; j < rightOp.schema().size(); j++ ) {
					joinedTuple.addData(ts.getData(j));
				}

				ts = rightExSortOp.getNextTuple();
				count++;
				if (ts == null || ensureEqual(tr, ts) != 0) {
					tr = leftExSortOp.getNextTuple();
					gs = null;

					rightExSortOp.resetIndex(ptr);
					ts = rightExSortOp.getNextTuple();
					count = ptr;
				}
				return joinedTuple;
			} 
			rightExSortOp.resetIndex(ptr);
			ts = rightExSortOp.getNextTuple();
			count = ptr;

			tr = leftExSortOp.getNextTuple();
			gs = null;
		}
		leftExSortOp.deleteFile();
		rightExSortOp.deleteFile();
		return null;
	}

	@Override
	public void dump(TupleWriter writer) {
		Tuple t;
		while ((t= getNextTuple()) != null) {
			writer.addNextTuple(t);
		}
		writer.dump();
		writer.close();
	}

	@Override
	public void reset() {
		leftExSortOp.reset();
		rightExSortOp.reset();
		tr= leftExSortOp.getNextTuple();
		Tuple firstTuple= rightExSortOp.getNextTuple();
		ts= firstTuple;
		gs= firstTuple;
	}

	@Override
	public ArrayList<String> schema() {
		return this.schema;
	}

	@Override
	public String getTableName() {
		return leftOp.getTableName() + "," + rightOp.getTableName();
	}
}