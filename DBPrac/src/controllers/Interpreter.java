package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Operators.LogicalOperatorFactory;
import Operators.PhysicalPlanBuilder;
import dataStructure.Catalog;
import fileIO.*;
import fileIO.Logger;
import logicalOperators.LogicalOperator;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import physicalOperator.Operator;

/** The top level class of our code, which read inputs queries, tables and produce output data */
public class Interpreter {

	public static void main(String[] args) {
		String queriesFile= args[0] + "/queries.sql";
		String dataDir= args[0] + "/db";
		String outputDir= args[1];
		int queryCounter= 1;

		try {
			CCJSqlParser parser= new CCJSqlParser(new FileReader(new File(queriesFile)));
			Statement statement;
			while ((statement= parser.Statement()) != null) {
				try {
					Select select= (Select) statement;
					SelectBody selectBody= select.getSelectBody();
					PlainSelect plainSelect= (PlainSelect) selectBody;
					Catalog cat= createCatalog(dataDir);

					LogicalOperatorFactory logOpFactory= new LogicalOperatorFactory();
					LogicalOperator logOp= logOpFactory.generateQueryPlan(plainSelect);

					PhysicalPlanBuilder planBuilder= new PhysicalPlanBuilder();
					Operator op= planBuilder.generatePlan(logOp);

//					ReadableTupleWriter writer= new ReadableTupleWriter(
//							outputDir + "/query" + Integer.toString(queryCounter));
					BinaryTupleWriter writer= new BinaryTupleWriter(
						outputDir + "/query" + Integer.toString(queryCounter));
					long time1 = System.currentTimeMillis();
					op.dump(writer);
					
					// Test logger
					Logger log= Logger.getInstance();
					try {
						log.dumpMessage("Running queries..."+Integer.toString(queryCounter));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					long time2 = System.currentTimeMillis();
					long diffTime = time2-time1;
					try {
						log.dumpMessage("\n"+ "Execution time : "+Long.toString(diffTime));
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					queryCounter++ ;
				} catch (Exception e) {
					System.err.println(
						"Exception occurred during executing the query number " + Integer.toString(queryCounter));
					queryCounter++ ;
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException fileNotFound) {
			System.err.println("The query file directory does not exist");
			System.err.println(queriesFile);
		} catch (ParseException parseException) {
			System.err.println("Exception occured during parsing");
		}
	}

	/** Construct catalog from directory
	 * 
	 * @param directory directory to find the schema file
	 * @return the schema */
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
