package operator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;

import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import parser.EvaluateExpression;
import parser.EvaluateWhere;

public class joinOperator extends SelectOperator {
	
	private String tableName;
	private DataTable joinResultTable;
	private ArrayList<Table> joinTables;
	private Expression joinExp;
	
	public joinOperator(String tableName, Expression expression) {
		super(tableName, expression);
		joinExp = expression;
	}
	
	public DataTable getJoinTables(PlainSelect plainSelect) {
		Table left = (Table) plainSelect.getFromItem();
		for (Iterator joinsIt = plainSelect.getJoins().iterator(); joinsIt.hasNext();) {
			Join join = (Join) joinsIt.next();
			//to produced after WHERE result
			SelectOperator right = new SelectOperator(join.toString(),plainSelect.getWhere());
			joinResultTable = twoTableJoin(joinResultTable, right.dump(),plainSelect.getWhere());
		}
		return joinResultTable;
	}
	
	// Take in the WHERE preprocesed left table and fully join with the right table
	private DataTable twoTableJoin(DataTable left, DataTable right, Expression whereExp) {
		ArrayList<String> leftSchema = left.getSchema();
		ArrayList<String> rightSchema = right.getSchema();
		leftSchema.addAll(rightSchema);
		DataTable temp = new DataTable(tableName);
		for(int i=0; i<left.cardinality();i++) {
			for(int j=0; j<right.cardinality(); j++) {
				Tuple leftTuple = new Tuple(left.getData(i));
				Tuple rightTuple = new Tuple(right.getData(j));
				EvaluateWhere whereVisitor = new EvaluateWhere(leftTuple, rightTuple,left.getTableName(),right.getTableName());
				Tuple next = whereVisitor.evaluate(whereExp);
				if ( next != null) {
					temp.addTuple(next);
				} 
			}
		}
		return temp;
	}
	
	//store the result in a txt
	public void dump() {
//		try {
//		File fw = new File(tableName);
//		fw.createNewFile();
//		BufferedWriter WriterFileBuffer = new BufferedWriter(fw);
//		}
//		catch(IOException e) {
//			System.err.println("Marking stream returns an error");
//		}
	}
	
	
	
	

}
