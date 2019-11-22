package physicalOperator;

import java.io.IOException;
import java.util.ArrayList;

import dataStructure.Tuple;
import fileIO.TupleWriter;
import net.sf.jsqlparser.expression.Expression;
import parser.EvaluateWhere;
import utils.PhysicalPlanWriter;

/** the class for the select operator */
public class SelectOperator extends Operator {

	private Expression exp;
	private Operator childOp;
	private String tableName;
	private ArrayList<String> schema;

	/** A constructor that instantiates a SelectOperator
	 * 
	 * @param expression The expression that will be used to select tuple
	 * @param op The child operrator that is asscoaited with SelectOperator
	 * @param tableAlias The alias of the tableTable that is being selected by SelectOperator */
	public SelectOperator(Expression expression, Operator op) {
		exp= expression;
		childOp= op;
		this.schema= op.schema();
		tableName= childOp.getTableName();
	}

	/** @return Returns the next tuple read from the data */
	@Override
	public Tuple getNextTuple() {
		Tuple next;
		EvaluateWhere exprVisitor= new EvaluateWhere(exp, new ArrayList<String>(), childOp.schema());
		while ((next= childOp.getNextTuple()) != null) {
			if ((next= exprVisitor.evaluate(null, next)) != null) { 
				return next; }
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
		Tuple t;
		while ((t= getNextTuple()) != null) {
			writer.addNextTuple(t);
		}
		writer.dump();
		writer.close();
	}

	/** @return the schema of the data table that is read by the operator */
	@Override
	public ArrayList<String> schema() {
		return this.schema;
	}

	/** @return the table name from where the operator reads the data */
	@Override
	public String getTableName() {
		return this.tableName;
	}

	public Operator getChild() {
		return childOp;
	}

	public Expression getExpression() {
		return exp;
	}

	@Override
	public void accept(PhysicalPlanWriter ppw) {
		try {
			ppw.visit(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
