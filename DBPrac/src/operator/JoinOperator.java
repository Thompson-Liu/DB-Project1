/**
 * Takes in a joined left table and right table to scan
 */
package operator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;

import dataStructure.Catalog;
import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.EvaluateWhere;

public class JoinOperator extends Operator {

	private Operator leftOperator;
	private Operator rightOperator;
	private Expression joinExp;

	public JoinOperator(Operator LeftOperator, Operator RightOperator, Expression expression) {
		this.joinExp=expression;
		this.leftOperator = LeftOperator;
		this.rightOperator = RightOperator;
	}
	
	//  could use scan / or could also use right table directly as input
	public Tuple getNextTuple(DataTable leftTable, DataTable rightTable) {
		Tuple next;
		Tuple left;
		Tuple right;
		EvaluateWhere evawhere = new EvaluateWhere(joinExp );
		while((left=leftOperator.getNextTuple())!=null) {
			while((right=rightOperator.getNextTuple())!=null) {
				if((next=evawhere.evaluate(left,right,leftTable.getSchema(), rightTable.getSchema()))!=null) {
					return next;
				}
			}
			rightOperator.reset();
		}
		return null;
	}
		
	public DataTable dump(DataTable leftTable, DataTable rightTable) {
		ArrayList<String> ret= (ArrayList<String>) leftTable.getSchema().clone();
		ret.addAll(rightTable.getSchema());
		DataTable result = new DataTable("Output", ret);
		Tuple tup = new Tuple();
		while ((tup = getNextTuple(leftTable, rightTable)) != null) {
			result.addData(tup.getTuple());
		}
		
		return result;
	}

}
