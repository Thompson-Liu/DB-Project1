package controllers;

import java.io.FileReader;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
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
//				System.out.println("select items are" + plainSelect.getSelectItems());
//				System.out.println("from items are" + plainSelect.getFromItem());
//				System.out.println("remaining from items" + plainSelect.getJoins());
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}

		EvaluateExpression expressionVisitor= new EvaluateExpression();

	}

}
