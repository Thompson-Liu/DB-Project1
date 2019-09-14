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

public class JoinOperator extends SelectOperator {

	private DataTable outerTable;
	private DataTable joinResultTable;
	private ArrayList<Table> joinTables;
	private ArrayList<String> leftTables;	//deep left join: outer table, composed of joined result sofar
	private String rightTable; 				//inner table to be joined
	private Expression joinExp;
	private Catalog catalog;

	public JoinOperator(DataTable outerTable, Expression expression,ArrayList<String> leftTables, String rightTable) {
		super(rightTable,expression);
		joinExp = expression;
		catalog = Catalog.getInstance();
		this.leftTables = leftTables;
		this.rightTable = rightTable;
	}
	
	//  could use scan / or could also use right table directly as input
	public Tuple getNextTuple(Tuple left) {
		Tuple next;
		Tuple right;
		while((right=super.getNextTuple())!=null) {
			EvaluateWhere evawhere = new EvaluateWhere(left,right,leftTables, rightTable);
			if((next=evawhere.evaluate(joinExp))!=null) {
				return next;
			}
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
	
//	controller connect to join 
//	Table fromLeft = (Table) select.getFromItem();
//	if(fromLeft!=null && select.getJoins()!=null){
//		SelectOperator selectLeft = new SelectOperator(fromLeft.getName(),select.getWhere());
//		DataTable left = selectLeft.dump();
//		ArrayList<String> leftTableNames = new ArrayList<String>();
//		leftTableNames.add(left.getTableName());
//		for (Iterator joinsIt = select.getJoins().iterator(); joinsIt.hasNext();) {
//			Join right = (Join) joinsIt.next();
//			// to produced after WHERE result
//			JoinOperator join = new JoinOperator(left,select.getWhere(),leftTableNames,rig);
//			left = join.dump();
//		}
//		return left;
//	}

}
