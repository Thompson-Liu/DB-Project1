package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dataStructure.Catalog;

public class QueryCost {

	private String tableName;
	private int numTuples;
	private ArrayList<String> schema; 
	private HashMap<String,Integer[]> colRange; 
	private String clusteredIndex;
	private ArrayList<String> unclusteredIndexes;

	/**
	 * 
	 * @param tableName
	 * @param dbDir     directory of the file /input/db
	 */
	public QueryCost(String tableName, String dbDir) {
		this.tableName = tableName;
		Catalog catalog = Catalog.getInstance();
		schema = catalog.getSchema(tableName);
		clusteredIndex = catalog.getClusteredIndex(tableName);
		unclusteredIndexes = catalog.getUnclusteredIndexes(tableName);
		try {
			BufferedReader stats= new BufferedReader(new FileReader(dbDir+"/stats.txt"));
			String nextLine;
			while ((nextLine= stats.readLine()) != null && nextLine !="") {
				String[] tableStats = nextLine.split(" ");
				if(tableStats[0]==tableName) {
					this.numTuples = Integer.parseInt(tableStats[1]);
					for(int i=2;i<tableStats.length;i=i+3) {
						Integer[] range = {Integer.parseInt(tableStats[i+1]),Integer.parseInt(tableStats[i+2])};
						colRange.put(tableStats[i],range);
					}
					return;
				}
			}
		} catch (IOException e) {
			System.err.println("Unable to find or read the stats file ! ");
			e.printStackTrace();
		}

	}

	public Integer ScanCost(String col,boolean isFullScan,Integer low, Integer high) {
		// how to get the number of leave nodes ??
		int numPages= (int) Math.ceil(numTuples*4.0*schema.size()/4096);
		int numLeaves;
		int level=0;   // number of levels for the tree
		int maxVal = Math.min(high,colRange.get(col)[1]);
		int minVal = Math.max(low, colRange.get(col)[0]);
		double r = 1.0*(maxVal-minVal)/numTuples;        //reduction factor
		int totalPageIO = numPages;
		if(isFullScan) {
			totalPageIO= numPages;
		}
		else if(col==clusteredIndex) {
			totalPageIO= (int) (level+r*numPages);
		}else if(this.unclusteredIndexes.contains(col)) {
			totalPageIO = (int) (level + numLeaves*r+numTuples*r);
		}
			return totalPageIO;
	}
}
