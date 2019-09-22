package controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructure.Catalog;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import operator.Operator;

public class Interpreter {

	private static final String queriesFile= "queries.sql";
	private static final String dataDir= "samples/input/dbTest/";
	private HashMap<String, String> aliasMap;

	public static void main(String[] args) {
		try {
			CCJSqlParser parser= new CCJSqlParser(new FileReader(queriesFile));
			Statement statement;
			while ((statement= parser.Statement()) != null) {
//				System.out.println("Read statement: " + statement);
				Select select= (Select) statement;
//				System.out.println("Select body is " + select.getSelectBody());
				SelectBody selectBody= select.getSelectBody();
				PlainSelect plainSelect= (PlainSelect) selectBody;

//				System.out.println("table name is " + fileName);
				Catalog cat= createCatalog(dataDir);
//				cat.printCatalog();

				OperatorFactory opfactory= new OperatorFactory();
				Operator op= opfactory.generateQueryPlan(plainSelect);
				op.dump(System.out, true);
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}

	}

	private static Catalog createCatalog(String directory) {
		Catalog cat= Catalog.getInstance();
		try {
			FileReader schemafw= new FileReader(dataDir + "schema.txt");
			BufferedReader readSchema= new BufferedReader(schemafw);
			String line;
			while ((line= readSchema.readLine()) != null) {
				String[] schemaLine= line.split(" ");
				String tableName= schemaLine[0];
				cat.addDir(tableName, dataDir + "data/" + tableName);
				ArrayList<String> schem= new ArrayList<String>();
				for (int i= 1; i < schemaLine.length; i++ ) {
					schem.add(schemaLine[i]);
				}
				cat.addSchema(tableName, schem);
			}
			readSchema.close();
		} catch (IOException e) {
			System.err.println("Exception unable to access the directory");
		}
		return cat;
	}
}
