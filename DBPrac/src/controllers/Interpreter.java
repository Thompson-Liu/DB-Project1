package controllers;

import java.io.FileReader;
import java.util.ArrayList;

import dataStructure.Catalog;
import dataStructure.DataTable;
import dataStructure.Tuple;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import operator.ScanOperator;
import operator.SelectOperator;
import parser.EvaluateExpression;

public class Interpreter {

	private static final String queriesFile= "queries.sql";

	public static void main(String[] args) {
		try {
			CCJSqlParser parser= new CCJSqlParser(new FileReader(queriesFile));
			Statement statement;
			while ((statement= parser.Statement()) != null) {
				System.out.println("Read statement: " + statement);
				Select select= (Select) statement;
				System.out.println("Select body is " + select.getSelectBody());
				SelectBody selectBody= select.getSelectBody();
				PlainSelect plainSelect= (PlainSelect) selectBody;
				Table fileName= (Table) plainSelect.getFromItem();
				String tableName= fileName.getName();

				Catalog cat= Catalog.getInstance();
				cat.addDir(tableName, "samples/input/db/data/Boats");
				cat.addDir("Sailors", "samples/input/db/data/Sailors");
				cat.addDir("Reserves", "samples/input/db/data/Reserves");
				ArrayList<String> schem= new ArrayList<String>();
				schem.add("D");
				schem.add("E");
				schem.add("F");
				cat.addSchema(tableName, schem);

				SelectOperator selectOperator= new SelectOperator(tableName,plainSelect.getWhere());
				EvaluateExpression expressionVisitor= new EvaluateExpression(tableName,plainSelect.getWhere());
				System.out.println("plain select is " + plainSelect.toString());
				
//				Tuple rst= expressionVisitor.evaluate(selectOperator.getNextTuple());
//				if (rst != null) {
//					System.out.println(rst.printData());
//				}
				System.out.println("there");
				DataTable result = selectOperator.dump();
				result.printTable();
				//					System.out.println("select items are" + plainSelect.getSelectItems());
				//					System.out.println("from items are" + plainSelect.getFromItem());
				//					System.out.println("remaining from items" + plainSelect.getJoins());
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}

	}

}


