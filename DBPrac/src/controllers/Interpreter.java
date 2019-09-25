package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import dataStructure.Catalog;
import net.sf.jsqlparser.parser.CCJSqlParser;
<<<<<<< HEAD
=======
import net.sf.jsqlparser.parser.ParseException;
>>>>>>> 7af36e51c6650ec7e4789223a7e00c307619de17
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import operator.Operator;

public class Interpreter {

<<<<<<< HEAD
	private static final String queriesFile= "queries.sql";
	private static final String dataDir= "samples/input/dbTest/";
	private HashMap<String, String> aliasMap;

=======
>>>>>>> 7af36e51c6650ec7e4789223a7e00c307619de17
	public static void main(String[] args) {
		String queriesFile = args[0] + "/queries.sql";
		String dataDir = args[0] + "/db";
		String outputDir = args[1];
		int queryCounter = 1;

		try {
			CCJSqlParser parser= new CCJSqlParser(new FileReader(new File(queriesFile)));
			Statement statement;
			while ((statement= parser.Statement()) != null) {
<<<<<<< HEAD
//				System.out.println("Read statement: " + statement);
				Select select= (Select) statement;

				System.out.println("Select body is " + select.getSelectBody());
				SelectBody selectBody= select.getSelectBody();
				PlainSelect plainSelect= (PlainSelect) selectBody;
=======
				try {
					Select select= (Select) statement;

					//别忘了comment掉system.out.print
					System.out.println("Select body is " + select.getSelectBody());
					SelectBody selectBody= select.getSelectBody();
					PlainSelect plainSelect= (PlainSelect) selectBody;
					Catalog cat= createCatalog(dataDir);

					OperatorFactory opfactory= new OperatorFactory();
					Operator op= opfactory.generateQueryPlan(plainSelect);
>>>>>>> 7af36e51c6650ec7e4789223a7e00c307619de17

					File file = new File(outputDir + "/query" + Integer.toString(queryCounter)); 
					PrintStream ps = new PrintStream(new FileOutputStream(file));
					op.dump(System.out, true);

					queryCounter++;
				}
				catch (Exception e) {
					System.err.println("Exception occurred during executing the query number " + Integer.toString(queryCounter));
					queryCounter++;
								e.printStackTrace();
				}
			}
		}
		catch(FileNotFoundException fileNotFound) {
			System.err.println("The query file directory does not exist");
			System.err.println(queriesFile);
		}
		catch (ParseException parseException) {
			System.err.println("Exception occured during parsing");
		}
	}

	/** Construct catalog from directory
	 * 
	 * @param directory  directory to find the schema file
	 * @return   the schema
	 */
	private static Catalog createCatalog(String directory) {
		Catalog cat= Catalog.getInstance();
		try {
			FileReader schemafw= new FileReader(directory + "/schema.txt");
			BufferedReader readSchema= new BufferedReader(schemafw);
			String line;
			while ((line= readSchema.readLine()) != null) {
				String[] schemaLine= line.trim().split("\\s+");
				String tableName= schemaLine[0];
				cat.addDir(tableName, directory + "/data/" + tableName);
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
