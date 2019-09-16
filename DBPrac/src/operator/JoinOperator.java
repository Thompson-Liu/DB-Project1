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
	private DataTable leftTable;
	private DataTable rightTable;

	public JoinOperator(Operator LeftOperator, Operator RightOperator, Expression expression) {
		this.joinExp=expression;
		this.leftOperator = LeftOperator;
		this.rightOperator = RightOperator;
	}
	
	//  could use scan / or could also use right table directly as input
	public Tuple getNextTuple(String rightTableName) {
		Tuple next;
		Tuple left;
		Tuple right;
		EvaluateWhere evawhere = new EvaluateWhere(joinExp );
		while((left=leftOperator.getNextTuple(this.leftTable.getTableName()))!=null) {
			while((right=rightOperator.getNextTuple(rightTableName))!=null) {
				if((next=evawhere.evaluate(left,right,leftTable.getSchema(), this.rightTable.getSchema()))!=null) {
					return next;
				}
			}
			rightOperator.reset();
		}
		return null;
	}
		
	public DataTable dump(String leftTableName, String rightTableName) {
		leftTable = this.leftOperator.dump(leftTableName);
		rightTable = this.rightOperator.dump(rightTableName);
		ArrayList<String> ret= (ArrayList<String>) leftTable.getSchema().clone();
		ret.addAll(rightTable.getSchema());
		DataTable result = new DataTable("Output", ret);
		Tuple tup = new Tuple();
		while ((tup = getNextTuple(rightTableName)) != null) {
			result.addData(tup.getTuple());
		}
		
		return result;
	}

}
