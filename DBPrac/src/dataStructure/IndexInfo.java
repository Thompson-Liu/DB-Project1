/**
 * class to store the index information of table
 */
package dataStructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IndexInfo {
	
	private int numTuples;
	private String clusteredIndex;
	private HashMap<String,List<Integer>>  indexStats;     // List<Integer>

	public IndexInfo() {
		clusteredIndex = null;
		indexStats = new HashMap<String,List<Integer>>();
	}
	
	public void setTupleNums(int num) {
		this.numTuples=num;
	}
	
	public int numTuples() {
		return this.numTuples;
	}
	
	public void addColInfo(String colName,int low,int high) {
		List<Integer> info = Arrays.asList(low,high);
		indexStats.put(colName, info);
	}
	
	public List<Integer> getColInfo(String colName) {
		return indexStats.get(colName);
	}
	
	public ArrayList<Integer> colRange(String col){
		return (ArrayList<Integer>) indexStats.get(col);
	}
	
	
	public void setClusteredIndex(String clusteredIndex) {
		this.clusteredIndex=clusteredIndex;
	}
	
	public String clusteredIndex() {
		return this.clusteredIndex;
	}
	
	public ArrayList<String> getallIndex() {
		ArrayList<String> availableIndex = new ArrayList<String>();
		for(String col:indexStats.keySet()) {
			availableIndex.add(col);
		}
		return availableIndex;
	}
}
