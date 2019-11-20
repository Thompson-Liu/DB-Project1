package dataStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *  track the schema with the table directory and the column_num and column_name
 */
public class Catalog {

	private static Catalog dbCatalog = null;
	private HashMap<String, String> tableDir;
	private HashMap<String, ArrayList<String>> schemaList;
	private HashMap<String,TableStats> tableInfo;
	

	/**
	 * The private Catalog object consturctor that will not be accessed from other
	 * classes to ensure the singleton design. 
	 */
	private Catalog() {
		tableDir = new HashMap<String, String>();
		schemaList = new HashMap<String, ArrayList<String>>();
		tableInfo = new HashMap<String,TableStats>();
	}

	/**
	 * 
	 * @return An instance of Catalog object
	 */
	public static Catalog getInstance() {
		if (dbCatalog == null) {
			dbCatalog = new Catalog();
		}
		return dbCatalog;
	}

	/** 
	 * Get the directory of the datatable associated with the name 
	 * 
	 * @param name    The name of the table that is searched for 
	 * @return        the directory of the data
	 */
	public String getDir(String name) {
		return tableDir.get(name);
	}

	/**
	 * Add a data name and its associated directory into the catalog
	 * 
	 * @param name    the name of the data 
	 * @param dir	  the directory of the data where it's stored
	 */
	public void addDir(String name, String dir) {
		tableDir.put(name, dir);
	}

	/**
	 *  set the table with its total number of tuples
	 * @param tableName 
	 * @param totalTuples
	 */
	public void setTupleNums(String tableName, int totalTuples) {
		TableStats tab;
		if (this.tableInfo.containsKey(tableName)) {
			tab = this.tableInfo.get(tableName);
			tab.setTuples(totalTuples);
		}else {
			tab = new TableStats(tableName);
			tab.setTuples(totalTuples);
			this.tableInfo.put(tableName,tab);
		}
		
	}

	/**
	 * 
	 * @param tableName
	 * @return the total number of tuples in this table
	 */
	public int getTupleNums(String tableName) {
		return this.tableInfo.get(tableName).getTuples();
	}


	/**
	 * Add the schema of the data into the catalog
	 * 
	 * @param name    The name of the data
	 * @param schema  The schema of the data
	 */
	public void addSchema(String name, ArrayList<String> schema) {
		schemaList.put(name, schema);
	}

	/**
	 *  only add leaves num if the table Name has been stored in catalog
	 * @param tableName
	 * @param column
	 * @param num
	 */
	public void setLeavesNum(String tableName,String column,int num) {
		if(this.tableInfo.containsKey(tableName)) {
			this.tableInfo.get(tableName).setIndexLeaves(column, num);
		}
	}
	
	public int getLeavesNum(String tableName,String column) {
		return this.tableInfo.get(tableName).getNumLeaves(column);
	}
	/**
	 * Get the schema associated with the data
	 * 
	 * @param name    the name of the data that is being searched for
	 * @return        an arraylist of the schema associated with the data
	 */
	public ArrayList<String> getSchema(String name) {
		return schemaList.get(name);
	}

	/**
	 *  set the index column that the table is sorted on
	 * @param tableName  : name of table
	 * @param setSortCol : index on the file that the table sorted on
	 * @param isClustered : if the table is clustered
	 */
	public void addIndex(String tableName, String columnName,boolean isClustered) {
		TableStats tab;
		//added
		if(this.tableInfo.containsKey(tableName)) {
			tab=this.tableInfo.get(tableName);
			tab.addIndex(columnName, isClustered);
		}else {
			tab=new TableStats(tableName);
			tab.addIndex(columnName, isClustered);
			this.tableInfo.put(tableName, tab);
		}		
	}
	
	public void addIndexDir(String tableName, String columnName,String dir) {
		TableStats tab ;
		if(this.tableInfo.containsKey(tableName)) {
			tab=this.tableInfo.get(tableName);
			tab.setIndexDir(columnName,dir);
		}else {
			tab = new TableStats(tableName);
			tab.setIndexDir(columnName,dir);
			this.tableInfo.put(tableName, tab);
		}		
	}

	/**
	 * 
	 * @param tableName
	 * @return the single clustered index of this table
	 */
	public String getClusteredIndex(String tableName){
		return this.tableInfo.get(tableName).getClustered();
	}

	/**
	 * 
	 * @param tableName
	 * @return the set of all indexes(clustered+unclustered) of this table
	 */
	public ArrayList<String> getAllIndexes(String tableName) {
		return this.tableInfo.get(tableName).getAllIndex();
	}
	
	/**
	 * 
	 * @param tableName  the table of column stats
	 * @param columnName  name of column
	 * @param range     [low, high]
	 */ 
	public void addColRange(String tableName, String columnName, int[] range) {
		this.tableInfo.get(tableName).setColRange(columnName, range);
	}
	
	/**
	 * 
	 * @param tableName
	 * @param columnName
	 * @return the range [low, high] of the tableName of columnName
	 */
	public int[] getColRange(String tableName,String columnName) {
		if(this.tableInfo.containsKey(tableName)) {
			return this.tableInfo.get(tableName).getColRange(columnName);
		}
		return null;
	}

//	/**
//	 * 
//	 * @param tableName the name of table
//	 * @return col is sorted on on file
//	 */
//	public String getIndexCol(String tableName) {
//		return sortedCol.get(tableName);
//	}

	/**
	 * 
	 * @param tableName the name of table
	 * @return col is sorted on on file
	 */
	public Boolean getIsClustered(String tableName,String col) {
		return (this.tableInfo.get(tableName).getClustered().equals(col));
	}

	/** 
	 * Print the catalog: data, directory tuples and data, schema tuples for debugging, 
	 */
	public void printCatalog() {
		System.out.println("Tables directorys:");
		for(String table: tableDir.keySet()) {
			System.out.println(table+tableDir.get(table));
		}
	}
}
