package controllers;

import java.io.IOException;
import utils.CatalogGenerator;
import utils.IndexBuilder;
import utils.InputFileParser;
import utils.QueryEvaluator;

/** The top level class of our code, which read inputs queries, tables and produce output data */
public class Interpreter {

	/** The driver function that will execute the data base management system, it can choose beteen
	 * binary tuple writer and readable tuple writer, and also can generate test data. */
	public static void main(String[] args) {

		// Parse the input file
		InputFileParser fileParser = new InputFileParser(args[0]);
		boolean optimize = false;
//		String inputDir = fileParser.getDir();
//		String outputDir = fileParser.getDir();
//		String tempDir = fileParser.getDir();
		
		String current = "";
		try {
			current = new java.io.File( "." ).getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String inputDir = current + "/samples/input";
		String outputDir = current + "/samples/output";
		String tempDir = current + "/samples/tempdir";

		// Generate the catalog of data relations' directory and schema
		long time1= System.currentTimeMillis();
		CatalogGenerator catalogGen = new CatalogGenerator(inputDir + "/db");
		catalogGen.createCatalog();
		long time2= System.currentTimeMillis();
		long diffTime= time2 - time1;
		System.out.println("time requried to generate the catalog: " + diffTime);
		
		time1 = System.currentTimeMillis();
		IndexBuilder indexBuilder = new IndexBuilder(inputDir + "/db");
		if (!optimize) {
			// build the index
			indexBuilder.buildIndices_opt(optimize, "");
		}
		indexBuilder.readIndices();
		time2 = System.currentTimeMillis();
		diffTime = time2 - time1;
		System.out.println("time requried to generate indexes is: " + diffTime);
		
		QueryEvaluator queryEval = new QueryEvaluator(inputDir + "/queries.sql", 
				outputDir, inputDir, tempDir);
		queryEval.evaluateQuery();
	}
}