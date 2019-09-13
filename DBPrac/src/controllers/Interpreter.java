package controllers;

import java.io.FileReader;
import java.util.ArrayList;

import dataStructure.Catalog;
import dataStructure.Tuple;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import operator.ScanOperator;
import parser.EvaluateExpression;

public class Interpreter {

	private static final String queriesFile= "queries.sql";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
				cat.addDir(tableName, "/Users/ziweigu/Downloads/DB-Project1/DBPrac/db/data/Boats");
				ArrayList<String> schem= new ArrayList<String>();
				schem.add("D");
				schem.add("E");
				schem.add("F");
				cat.addSchema(tableName, schem);

				ScanOperator scanOperator= new ScanOperator(tableName);
				EvaluateExpression expressionVisitor= new EvaluateExpression(scanOperator.getNextTuple(),
					fileName.getName());
				System.out.println("plain select is " + plainSelect.toString());
				Tuple rst= expressionVisitor.evaluate(plainSelect);
				if (rst != null) {
					System.out.println(rst.printData());
				}
				System.out.println("there");
//				System.out.println("select items are" + plainSelect.getSelectItems());
//				System.out.println("from items are" + plainSelect.getFromItem());
//				System.out.println("remaining from items" + plainSelect.getJoins());
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}

	}

}
