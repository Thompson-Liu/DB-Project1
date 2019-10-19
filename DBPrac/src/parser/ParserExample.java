package parser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;

/** Example class for getting started with JSQLParser. Reads SQL statements from a file and prints
 * them to screen; then extracts SelectBody from each query and also prints it to screen.
 * 
 * @author Lucja Kot */
public class ParserExample {

	private static final String queriesFile= "/Users/yutingyang/Desktop/db_prac/DB-Project2/DBPrac/samples/input/queries.sql";

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
				Distinct astrick= plainSelect.getDistinct();
				System.out.println("select items are     :     "+plainSelect.getSelectItems().get(0).toString());

				System.out.println("join alias  are   ::: :"+plainSelect.getJoins() );
//				System.out.println("select items are" + astrick.toString());
//				System.out.println("***from items are  :   " + plainSelect.getFromItem().toString());
////				System.out.println("remaining from items" + plainSelect.getJoins());
////				Expression a = AdditiveExpression();
				System.out.println("-----where clause is  :  " + plainSelect.getWhere());
				EvaluateWhere eva = new EvaluateWhere(plainSelect.getWhere(),new ArrayList<String>(),new ArrayList<String>(),new HashMap<String,String>())  ;
				System.out.println("Class of where clause is  :   "+(plainSelect.getWhere() instanceof EqualsTo));
//				System.out.println("inside where is   :    " + plainSelect.getWhere().toString());
//				System.out.println("join list is :    " + plainSelect.getJoins());
//				System.out.println("join list next :    " + plainSelect.getJoins().get(0).toString());
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}
	}
}