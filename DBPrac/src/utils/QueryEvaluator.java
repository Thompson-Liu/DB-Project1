package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import Operators.LogicalOperatorFactory;
import Operators.PhysicalPlanBuilder;
import fileIO.BinaryTupleWriter;
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
					Select select= (Select) statement;
					SelectBody selectBody= select.getSelectBody();
					PlainSelect plainSelect= (PlainSelect) selectBody;

					LogicalOperatorFactory logOpFactory= new LogicalOperatorFactory();
					LogicalOperator logOp= logOpFactory.generateQueryPlan(plainSelect);

					// need to pass in the name of the config file path
					PhysicalPlanBuilder planBuilder= new PhysicalPlanBuilder(inputDir + "/plan_builder_config.txt", tempDir, inputDir + "/db/indexes");
					Operator op= planBuilder.generatePlan(logOp);

//					ReadableTupleWriter writer= new ReadableTupleWriter(
//						outputDir + "/query" + Integer.toString(queryCounter));
					BinaryTupleWriter writer= new BinaryTupleWriter(
						outputDir + "/query" + Integer.toString(queryCounter));

					long time1= System.currentTimeMillis();
					op.dump(writer);

					long time2= System.currentTimeMillis();
					long diffTime= time2 - time1;
					System.out.println(diffTime);
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
			System.err.println(queryDir);
		} catch (ParseException parseException) {
			System.err.println("Exception occured during parsing");
		}
	}
}
