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
	private Tuple tr;
	private Tuple ts;
	private Tuple gs;
	private int ptr;     // store the number of lines of gs
	private int count;   // store the number of lines of ts
	private ArrayList<String> schema;
	private boolean useExternal;
	
	private Operator leftSortOp;
	private Operator rightSortOp;
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
			String dir, boolean useExternal) {
		EvaluateJoin evalJoin= new EvaluateJoin(joinExpr, left.getTableName(), right.getTableName(), alias);
		leftColList= evalJoin.getJoinAttributesLeft();
		rightColList= evalJoin.getJoinAttributesRight();
		leftOp= left;
		rightOp= right;
		this.useExternal = useExternal;
		
		if (useExternal) {
			leftSortOp= new ExternalSortOperator(leftOp, leftColList, bufferSize, dir, leftOp.getTableName());
			rightSortOp= new ExternalSortOperator(rightOp, rightColList, bufferSize, dir, "right");
		} else {
			leftSortOp= new SortOperator(leftOp, leftColList);
			rightSortOp= new SortOperator(rightOp, rightColList);
		}
		
		tr = leftSortOp.getNextTuple();

		ts = rightSortOp.getNextTuple();
		gs = null;
		ptr = 0;
		count = 0;
		this.schema= new ArrayList<String>(left.schema());
		this.schema.addAll(right.schema());

		leftSchema = leftSortOp.schema();
		rightSchema = rightSortOp.schema();
	}

	@Override
	public Tuple getNextTuple() {
		while (tr != null && ts != null) {
			if (gs == null) {
				while (ensureEqual(tr, ts) < 0) {
					tr = leftSortOp.getNextTuple();
					if (tr == null && useExternal) { 
						( (ExternalSortOperator)leftSortOp ).deleteFile();
						( (ExternalSortOperator)rightSortOp ).deleteFile();
						return null; 
					} 
				}

				while (ensureEqual(tr, ts) > 0) {
					ts = rightSortOp.getNextTuple();
					if (ts == null && useExternal) { 
						( (ExternalSortOperator)leftSortOp ).deleteFile();
						( (ExternalSortOperator)rightSortOp ).deleteFile();
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

				ts = rightSortOp.getNextTuple();
				count++;
				if (ts == null || ensureEqual(tr, ts) != 0) {
					tr = leftSortOp.getNextTuple();
					gs = null;

					if(useExternal) {
						( (ExternalSortOperator)rightSortOp ).resetIndex(ptr);
					} else {
						( (SortOperator)rightSortOp ).resetIndex(ptr);
					}
					ts = rightSortOp.getNextTuple();
					count = ptr;
				}
				return joinedTuple;
			} 
			
			if(useExternal) {
				( (ExternalSortOperator)rightSortOp ).resetIndex(ptr);
			} else {
				( (SortOperator)rightSortOp ).resetIndex(ptr);
			}
			ts = rightSortOp.getNextTuple();
			count = ptr;

			tr = leftSortOp.getNextTuple();
			gs = null;
		}
		if(useExternal) {
			( (ExternalSortOperator)leftSortOp ).deleteFile();
			( (ExternalSortOperator)rightSortOp ).deleteFile();
		}
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
		leftSortOp.reset();
		rightSortOp.reset();
		tr= leftSortOp.getNextTuple();
		Tuple firstTuple= rightSortOp.getNextTuple();
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