package dataStructure;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  track the schema with the table directory and the column_num and column_name
 */
public class Catalog {
	
	private static Catalog dbCatalog = null;
	private HashMap<String, String> tableDir;
	private HashMap<String, ArrayList<String>> schemaList;
	private HashMap<String,String> sortedCol;     // the index that the table is sorted on
	private HashMap<String, Boolean> isClustered;
	
	/**
	 * The private Catalog object consturctor that will not be accessed from other
	 * classes to ensure the singleton design. 
	 */
	private Catalog() {
		tableDir = new HashMap<String, String>();
		schemaList = new HashMap<String, ArrayList<String>>();
		sortedCol = new HashMap<String,String>();
		isClustered = new HashMap<String, Boolean>() ;
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
	 * Add the schema of the data into the catalog
	 * 
	 * @param name    The name of the data
	 * @param schema  The schema of the data
	 */
	public void addSchema(String name, ArrayList<String> schema) {
		schemaList.put(name, schema);
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
	public void addIndex(String tableName, String sortedCol,boolean isClustered) {
		this.sortedCol.put(tableName, sortedCol);
		this.isClustered.put(tableName, isClustered);
	}
	
	/**
	 * 
	 * @param tableName the name of table
	 * @return col is sorted on on file
	 */
	public String getIndexCol(String tableName) {
		return sortedCol.get(tableName);
	}
	
	/**
	 * 
	 * @param tableName the name of table
	 * @return col is sorted on on file
	 */
	public Boolean getIsClustered(String tableName) {
		return isClustered.get(tableName);
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
