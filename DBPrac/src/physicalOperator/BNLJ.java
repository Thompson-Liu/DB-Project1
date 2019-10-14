package physicalOperator;

import java.util.ArrayList;
import java.util.HashMap;

import dataStructure.Buffer;
import dataStructure.DataTable;
import dataStructure.Tuple;
import fileIO.BinaryTupleReader;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateWhere;

public class BNLJ extends Operator {

	private BinaryTupleReader reader;
	private static Buffer buffer;
	private int numOuters;
	private Operator leftOp;
	private Operator rightOp;
	private Expression joinCond;
	private DataTable data;
	private HashMap<String, String> alias;
	private boolean bufState = true;
	private boolean tupState = true;
	private int bufTupState = 0;
	
	private Tuple innerTup;
	private EvaluateWhere eval;


	public BNLJ(int numPages, Operator left, Operator right, 
			Expression joinExp, HashMap<String, String> tableAlias) {
		
		// is there a way to know how many attributes?
		numOuters = numPages * 4096;
		buffer = new Buffer(numOuters);

		leftOp = left;
		rightOp = right;
		joinCond = joinExp;
		ArrayList<String> cur= new ArrayList<String>(leftOp.schema());
		cur.addAll(rightOp.schema());
		data = new DataTable(leftOp.getTableName() + " " + rightOp.getTableName(), cur);
		alias = tableAlias;
		
		eval = new EvaluateWhere(joinCond, leftOp.schema(), rightOp.schema(), alias);

	}

	private void populateBuffer() {
		buffer.clear();
		Tuple tup = leftOp.getNextTuple();
		while(!buffer.overflow() && tup != null) {
			buffer.addData(tup);
		}
	}

	@Override
	public Tuple getNextTuple() {
		Tuple next = null;
		Tuple outerTup;
		boolean flag = true;

		while (flag) {
			// if need to get another block, repopulate the buffer
			if (bufState)  {
				populateBuffer();
				
				// if the new buffer is empty, indicating that nothing left from outer, 
				// then return null
				if(buffer.empty()) {
					return null;
				}

				// if need to get another tuple from inner S
				if (tupState) {
					while ((innerTup = rightOp.getNextTuple()) != null) {
						while ((outerTup = buffer.getTuple(bufTupState++)) != null) {
							if ((next = eval.evaluate(outerTup, innerTup)) != null) {
								data.addData(next);
								tupState = false;
								bufState = false;
								return next;
							}
						} 
						bufTupState = 0;
					}
					rightOp.reset();
				} else {
					while ((outerTup = buffer.getTuple(bufTupState++)) != null) {
						if ((next = eval.evaluate(outerTup, innerTup)) != null) {
							data.addData(next);
							bufState = false;
							return next;
						}
					}
					bufTupState = 0;
					bufState = false;
					tupState = true;
				}
			} 
			
			else {
				if (tupState) {
					while ((innerTup = rightOp.getNextTuple()) != null) {
						while ((outerTup = buffer.getTuple(bufTupState++)) != null) {
							if ((next = eval.evaluate(outerTup, innerTup)) != null) {
								data.addData(next);
								tupState = false;
								return next;
							}
						} 
						bufTupState = 0;
					}
					rightOp.reset();
					bufState = true;
				} else {
					while ((outerTup = buffer.getTuple(bufTupState++)) != null) {
						if ((next = eval.evaluate(outerTup, innerTup)) != null) {
							data.addData(next);
							return next;
						}
					}
					bufTupState = 0;
					tupState = true;
				}
			}
		}
		return null;
	} 

	@Override
	public void reset() {
		bufState = true;
		tupState = true;
		bufTupState = 0;
		reader.reset();
		rightOp.reset();
	}

	@Override
	public ArrayList<String> schema() {
		return data.getSchema();
	}

	public String getTableName() {
		return data.getTableName();
	}

	public DataTable getData() {
		Tuple t;
		while ((t = getNextTuple()) != null) {

		}
		reset();
		return data;
	}
}
