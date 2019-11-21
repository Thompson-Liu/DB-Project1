package dataStructure;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * When calling Index Builder, it will 
 *
 */
public class TableStats {

	private String tableName;
	private int tableTuples;
	private String ClusteredIndex;
	private HashMap<String, String> indexDir;
	private ArrayList<String> Indexes;  // all index including the clustered
	private HashMap<String,int[]> colStats;   // statistic of column range
	private HashMap<String,Integer> indexLeaves;     // num of leave nodes

	public TableStats(String tableName) {
		this.tableName=tableName;
		this.Indexes = new ArrayList<String>();
		this.colStats = new HashMap<String,int[]>();
		this.indexLeaves = new HashMap<String,Integer>();
		this.indexDir= new HashMap<String, String>();
	}

	public void setTuples(int tableTuples) {
		this.tableTuples=tableTuples;
	}

	public void setIndexDir(String column, String dir) {
		this.indexDir.put(column, dir);
	}

	public int getTuples() {
		return this.tableTuples;
	}

	public void setIndexLeaves(String column,int num) {
		this.indexLeaves.put(column,num);
	}

	public int getNumLeaves(String column) {
		// Initialize reader, as in BinaryTupleReader
		String indexFile =this.indexDir.get(column);
		FileInputStream fin;
		try {
			fin = new FileInputStream(indexFile);
			FileChannel fc= fin.getChannel();
			ByteBuffer buffer= ByteBuffer.allocate(4096);
			fc.read(buffer);
			int rootInd= buffer.getInt(0); // first int on the header page is the address of the root
			int numLeaves= buffer.getInt(4); // # of leaves in the tree
			return numLeaves;
		} catch (Exception e) {
			System.err.println("cannot find index file");
			e.printStackTrace();
		}
		System.err.println("not finding the index file at function in [TableStats]");
		return 1;
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
