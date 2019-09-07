package parser;

import java.io.FileReader;
import expression.*;
import java.util.*;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;

public class Parser {
	
	// note: here in handout, we are assuming all statement are select,
	// but latter may change to add other statements.

	private static final String queriesFile= "queries.sql";

	public static void main(String[] args) {
		try {
			CCJSqlParser parser= new CCJSqlParser(new FileReader(queriesFile));
			Statement statement;
			while ((statement= parser.Statement()) != null) {
				System.out.println("Read statement: " + statement);
//				if(statement.isInstanceOf(Select)) {
				
//				}
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}
	}
	
	// Q1 check to see if we want it to be static
	private static void parse_select(Statement statement) {
		SelectInfo result = new SelectInfo();
		
		Select select= (Select) statement;
		System.out.println("Select body is " + select.getSelectBody());
		
		SelectBody selectBody = select.getSelectBody();
		PlainSelect plainSelect = (PlainSelect)selectBody;
		result.setSelectCol( plainSelect.getSelectItems() );
		System.out.println("select items are" +plainSelect.getSelectItems());
		
		Table item1 = (Table)plainSelect.getFromItem();
		String item1_name = item1.getName();
		ArrayList<String> from = new ArrayList<String>();
		from.add(item1_name);
		if( plainSelect.getJoins()!=null) {
			ArrayList<String> following_items = new ArrayList<String>( plainSelect.getJoins());
			from.addAll(following_items);
		}
		System.out.println("from items are" +plainSelect.getFromItem());
		System.out.println("remaining from items"+plainSelect.getJoins());
		
		// Where clause dealing
	}
}
