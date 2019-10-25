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
import fileIO.BinaryTupleWriter;
import fileIO.ReadableTupleWriter;
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

	/**
	 * The driver function that will execute the data base management system, it can choose 
	 * beteen binary tuple writer and readable tuple writer, and also can generate 
	 * test data.
	 */
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
					createCatalog(dataDir);

					LogicalOperatorFactory logOpFactory= new LogicalOperatorFactory();
					LogicalOperator logOp= logOpFactory.generateQueryPlan(plainSelect);

					// need to pass in the name of the config file path
					PhysicalPlanBuilder planBuilder= new PhysicalPlanBuilder(args[0] + "/plan_builder_config.txt",
						args[2]);
					Operator op= planBuilder.generatePlan(logOp);
//
//					ReadableTupleWriter writer= new ReadableTupleWriter(
//						outputDir + "/query" + Integer.toString(queryCounter));
					BinaryTupleWriter writer= new BinaryTupleWriter(
						outputDir + "/query" + Integer.toString(queryCounter));

					long time1= System.currentTimeMillis();
					op.dump(writer);

					long time2= System.currentTimeMillis();
					long diffTime= time2 - time1;
//					System.out.println(diffTime);
					queryCounter++;
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
