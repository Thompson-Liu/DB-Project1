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
import parser.EvaluateExpression;
import parser.EvaluateWhere;

public class JoinOperator extends Operator {

	private DataTable joinResultTable;
	private Operator leftOperator;
	private Operator rightOperator;
	private DataTable leftTable;
	private DataTable rightTable;
//	private ArrayList<Table> joinTables;
//	private ArrayList<String> leftTables;	//deep left join: outer table, composed of joined result sofar
//	private String rightTable; 				//inner table to be joined
	private Expression joinExp;

	public JoinOperator(Operator LeftOperator, Operator RightOperator, Expression expression) {
		this.joinExp=expression;
		this.leftOperator = LeftOperator;
		this.rightOperator = RightOperator;
		this.leftTable = LeftOperator.dump();
		this.rightTable = RightOperator.dump();
//		super(rightTable,expression);
//		joinExp = expression;
//		catalog = Catalog.getInstance();
//		this.leftTables = leftTables;
//		this.rightTable = rightTable;
	}
	
	//  could use scan / or could also use right table directly as input
	public Tuple getNextTuple() {
		Tuple next;
		Tuple left;
		Tuple right;
		EvaluateWhere evawhere = new EvaluateWhere(joinExp );
		while((left=leftOperator.getNextTuple())!=null) {
			while((right=rightOperator.getNextTuple())!=null) {
				if((next=evawhere.evaluate(left,right,leftTable, rightTable))!=null) {
					return next;
				}
			}
			rightOperator.reset();
		}
		return null;
	}
	
	public DataTable dump() {
		
		DataTable data = new DataTable("output");
		for(int i=0; i<outerTable.cardinality();i++) {
			Tuple left= new Tuple(outerTable.getData(i));
			Tuple right;
			while((right= this.getNextTuple())!=null) {
				data.addData(right.getTuple());
			}
			super.reset();
		}
		return data;
	}

}
