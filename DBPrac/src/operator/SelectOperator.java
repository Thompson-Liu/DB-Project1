package operator;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.ArrayList;

import dataStructure.Catalog;
import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.EvaluateWhere;

public class SelectOperator extends Operator {

	private Expression exp;
	private Operator childOp;
	private DataTable data;
	
	public SelectOperator (Expression expression, Operator op) {
		exp = expression;
		childOp = op;
	}
	
	@Override
	public Tuple getNextTuple(){
		Tuple next;
		EvaluateWhere exprVisitor = new EvaluateWhere(exp);
		while ((next = childOp.getNextTuple()) != null) {
			if ((next = exprVisitor.evaluate(null, next, new ArrayList<String>(), childOp.schema())) != null) {
				data.addData(next);
				return next;
			} 
		}
		return null;
	}
	
	@Override
	public void reset() {
		childOp.reset();
	}
	
	@Override
	public void dump(PrintStream ps) {
		data.printTable(ps);
	}
	
	@Override
	public ArrayList<String> schema() {
		return data.getSchema();
	}
}
