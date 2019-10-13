package physicalOperator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructure.DataTable;
import dataStructure.Tuple;
import fileIO.*;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateWhere;

/** the class for the select operator */
public class SelectOperator extends Operator {

	private Expression exp;
	private Operator childOp;
	private DataTable data;
	private HashMap<String, String> tableAlias;

	/** A constructor that instantiates a SelectOperator
	 * 
	 * @param expression The expression that will be used to select tuple
	 * @param op The child operrator that is asscoaited with SelectOperator
	 * @param tableAlias The alias of the tableTable that is being selected by SelectOperator */
	public SelectOperator(Expression expression, Operator op, HashMap<String, String> tableAlias) {
		exp= expression;
		childOp= op;
		data= new DataTable(op.getTableName(), op.schema());
		this.tableAlias= tableAlias;
	}

	/** @return Returns the next tuple read from the data */
	@Override
	public Tuple getNextTuple() {
		Tuple next;
		EvaluateWhere exprVisitor= new EvaluateWhere(exp, new ArrayList<String>(),
			childOp.schema(), tableAlias);
		while ((next= childOp.getNextTuple()) != null) {
			if ((next= exprVisitor.evaluate(null, next)) != null) {
				data.addData(next);

				return next;
			}
		}
		
		return null;
	}

	/** reset read stream to re-read the data */
	@Override
	public void reset() {
		childOp.reset();
	}

	/** Prints the data read by operator to the PrintStream [ps]
	 * 
	 * @param ps The print stream that the output will be printed to
	 * @param print boolean decides whether the data will actually be printed */
	@Override
	public void dump(TupleWriter writer) {
		writer.addTable(getData().toArrayList());
		writer.dump();
		writer.close();
	}

	/** @return the schema of the data table that is read by the operator */
	@Override
	public ArrayList<String> schema() {
		return data.getSchema();
	}

	/** @return the table name from where the operator reads the data */
	@Override
	public String getTableName() {
		return data.getTableName();
	}

	/** @return the data read by the operator in DataTable data structure */
	@Override
	public DataTable getData() {
		Tuple t;
		while ((t = getNextTuple()) != null) {
		}
		reset();
		return data;
	}
}
