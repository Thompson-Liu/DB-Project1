package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import bpTree.BulkLoader;
import dataStructure.Catalog;
import fileIO.BinaryTupleReader;
import fileIO.BinaryTupleWriter;
import fileIO.TupleReader;
import fileIO.TupleWriter;

public class IndexBuilder {
	
	private BufferedReader reader;
	private String indexInfo; 
	private String indexes;
	private Catalog catalog = Catalog.getInstance();

	public IndexBuilder(String dbDir) {
		indexInfo = dbDir + "/index_info.txt";
		indexes = dbDir + "/indexes/";
	}
	
	private void buildIndex(int isClustered, int order, String attr, String tableName) {
		TupleReader tr = new BinaryTupleReader(catalog.getDir(tableName));
		TupleWriter tw = new BinaryTupleWriter(indexes + tableName + "." + attr);
		catalog.addIndexDir(tableName, attr, indexes + tableName + "." + attr);
		BulkLoader bulkloading = new BulkLoader(isClustered, order, tr, tw, attr, tableName);
		bulkloading.buildTree();
	}
	
	public void buildIndices() {
		try {
			
			reader = new BufferedReader(new FileReader(indexInfo));
		} catch (FileNotFoundException e) {
			System.err.println("Input file not found");
			e.printStackTrace();
		}
		
		String nextLine = null;
		try {
			while((nextLine = reader.readLine()) != null) {
				String components[] = nextLine.split(" ");
				String tableName = components[0];
				String columnName = components[1];
				int isClustered = Integer.parseInt(components[2]);
				int order = Integer.parseInt(components[3]);
				System.out.println(columnName);
				System.out.println(tableName);
				System.out.println(isClustered);
				// Build the index accordingly
				buildIndex(isClustered, order, columnName, tableName);
			}
		} catch (NumberFormatException e) {
			System.err.println("Error when converting integer to String");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error when reading from index file");
			e.printStackTrace();
		}
	}
	
	public void readIndices() {
		try {
			reader = new BufferedReader(new FileReader(indexInfo));
		} catch (FileNotFoundException e) {
			System.err.println("Input file not found");
			e.printStackTrace();
		}
		
		String nextLine = null;
		try {
			while((nextLine = reader.readLine()) != null) {
				String components[] = nextLine.split(" ");
				String tableName = components[0];
				String columnName = components[1];
				int isClustered = Integer.parseInt(components[2]);
				
				// update the catalog
				catalog.addIndex(tableName, columnName, isClustered == 1);
			}
		} catch (NumberFormatException e) {
			System.err.println("Error when converting integer to String");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error when reading from index file");
			e.printStackTrace();
		}
	}
}
