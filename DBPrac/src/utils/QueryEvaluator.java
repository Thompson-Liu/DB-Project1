package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import Operators.LogicalOperatorFactory;
import Operators.PhysicalPlanBuilder;
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

public class QueryEvaluator {
	private String queryDir;
	private String outputDir;
	private String inputDir;
	private String tempDir;
	
	private int queryCounter;
	
	public QueryEvaluator(String queriesFile, String outDir, String inDir, String tmpDir) {
		queryDir = queriesFile;
		outputDir = outDir;
		inputDir = inDir;
		tempDir = tmpDir;
		queryCounter = 1;
	}
	
	public void evaluateQuery() {
		try {
			CCJSqlParser parser= new CCJSqlParser(new FileReader(new File(queryDir)));
			Statement statement;
			while ((statement= parser.Statement()) != null) {
				try {
					System.out.println("Running query" + queryCounter);
					Select select= (Select) statement;
					SelectBody selectBody= select.getSelectBody();
					PlainSelect plainSelect= (PlainSelect) selectBody;

					long time1 = System.currentTimeMillis();
					LogicalOperatorFactory logOpFactory= new LogicalOperatorFactory();
					LogicalOperator logOp= logOpFactory.generateQueryPlan(plainSelect);
					long time2= System.currentTimeMillis();
					long diffTime= time2 - time1;
					System.out.println("Total time requried to generate the logical plan: " + diffTime);

					// Output logical plan tree
					BufferedWriter planLWriter = new BufferedWriter(new FileWriter(outputDir + "/query" + queryCounter + "_logicalplan"));
					LogicalPlanWriter logPlanWriter = new LogicalPlanWriter(planLWriter, logOp);
					
					// need to pass in the name of the config file path
					time1 = System.currentTimeMillis();
					PhysicalPlanBuilder planBuilder= new PhysicalPlanBuilder(tempDir, inputDir + "/db/indexes");
					Operator op= planBuilder.generatePlan(logOp);
					time2 = System.currentTimeMillis();
					diffTime = time2 - time1;
					System.out.println("Total time requried to generate the physcial plan: " + diffTime);

					BufferedWriter planPWriter = new BufferedWriter(new FileWriter(outputDir + "/query" + queryCounter + "_physicalplan"));
					PhysicalPlanWriter physicalPlanWriter = new PhysicalPlanWriter(planPWriter, op);

					ReadableTupleWriter writer= new ReadableTupleWriter(
						outputDir + "/query" + Integer.toString(queryCounter));
//					BinaryTupleWriter writer= new BinaryTupleWriter(
//						outputDir + "/query" + Integer.toString(queryCounter));

					time1= System.currentTimeMillis();
					op.dump(writer);

					time2 = System.currentTimeMillis();
					diffTime = time2 - time1;
					System.out.println("time requried to run the query is: " + diffTime);
					queryCounter++ ;
				} catch (Exception e) {
					System.err.println(
						"Exception occurred during executing the query number " + Integer.toString(queryCounter));
					queryCounter++ ;
					e.printStackTrace();
				}
				System.out.println("\n");
			}
		} catch (FileNotFoundException fileNotFound) {
			System.err.println("The query file directory does not exist");
			System.err.println(queryDir);
		} catch (ParseException parseException) {
			parseException.printStackTrace();
			System.err.println("Exception occured during parsing");
		}
	}
}
