package operator;

import java.io.BufferedReader;
import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.EvaluateExpression;

public class SelectOperator extends ScanOperator {

	private String tableName;
	private DataTable data;
	private Expression exp;
	private PlainSelect plainSelect;

	public SelectOperator(String name, PlainSelect ps) {
		super(name);
		tableName = name;
		plainSelect = ps;
	}

	public Tuple getNextTuple() {
		Tuple next;
		while ((next = this.getNextTuple()) != null) {
			EvaluateExpression exprVisitor = new EvaluateExpression(next, tableName);
			if ((next = exprVisitor.evaluate(plainSelect)) != null) {
				return next;
			} else {
				this.removeLastTuple();
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
