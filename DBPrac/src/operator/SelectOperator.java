package operator;

import java.io.BufferedReader;
import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.EvaluateExpression;

public class SelectOperator extends ScanOperator {

	private String tableName;
	private Expression exp;
	
	public SelectOperator (String name, Expression expression) {
		super(name);
		tableName = name;
		exp = expression;
	}
	
	public Tuple getNextTuple(){
		Tuple next;
		while ((next = super.getNextTuple()) != null) {
			EvaluateExpression exprVisitor = new EvaluateExpression(next, tableName);
			if ((next = exprVisitor.evaluate(exp)) != null) {
				return next;
			} else {
				super.removeLastTuple();
			}
		}
		return null;
	}
	
	public void reset() {
		super.reset();
	}
	
	public DataTable dump() {
		return super.dump();
	}
}
