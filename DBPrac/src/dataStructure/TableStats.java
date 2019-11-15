package dataStructure;

import java.util.ArrayList;
import java.util.HashMap;

public class TableStats {

	private String tableName;
	private String ClusteredIndex;
	private ArrayList<String> Indexes;  // all index including the clustered
	private int tableTuples;
	private HashMap<String,int[]> colStats;   // statistic of column range
	private HashMap<String,Integer> indexLeaves;   

	public TableStats(String tableName) {
		this.tableName=tableName;
		this.Indexes = new ArrayList<String>();
		this.colStats = new HashMap<String,int[]>();
		this.indexLeaves = new HashMap<String,Integer>();
	}
	
	public void setTuples(int tableTuples) {
		this.tableTuples=tableTuples;
	}
	
	public int getTuples() {
		return this.tableTuples;
	}
	
	public void setIndexLeaves(String column,int num) {
		this.indexLeaves.put(column,num);
	}
	
	public int getNumLeaves(String column) {
		return this.indexLeaves.get(column);
	}
	
	public void addIndex(String column,boolean isClustered) {
		if(isClustered) {
			this.ClusteredIndex=column;
		}
		this.Indexes.add(column);
	}
	
	public String getClustered() {
		return this.ClusteredIndex;
	}
	
	public ArrayList<String> getAllIndex(){
		return this.Indexes;
	}
	
	public void setColRange(String col,int[] range) {
		this.colStats.put(col,range);
	}
	
	public int[] getColRange(String col) {
		if(this.colStats.containsKey(col)) {
			return this.colStats.get(col);
		}
		return null;
	}
}
