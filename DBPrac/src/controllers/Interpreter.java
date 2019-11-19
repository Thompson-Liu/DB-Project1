package controllers;

import fileIO.BinaryTupleWriter;
import utils.CatalogGenerator;
import utils.DataGenerator;
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
		String inputDir = fileParser.getDir();
		String outputDir = fileParser.getDir();
		String tempDir = fileParser.getDir();

		//		int buildIndex = fileParser.getFlag();
		//		int evalQuery = fileParser.getFlag();

		// Generate the catalog of data relations' directory and schema
		CatalogGenerator catalogGen = new CatalogGenerator(inputDir + "/db");
		catalogGen.createCatalog();

		// build random testing data
		//		DataGenerator dataGen = new DataGenerator(3, inputDir, 3, 4, 100000);

		// If buildIndex = 1, build the index
		IndexBuilder indexBuilder = new IndexBuilder(inputDir + "/db");
		//		if (buildIndex == 1) {
		indexBuilder.buildIndices();
		//		}

		// If evalQuery = 1, evaluate the qeury
		//		if (evalQuery == 1) {
		indexBuilder.readIndices();
		QueryEvaluator queryEval = new QueryEvaluator(inputDir + "/queries.sql", 
				outputDir, inputDir, tempDir);
		queryEval.evaluateQuery();
		//		}
	}
}