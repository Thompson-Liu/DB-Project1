package operator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import dataStructure.Catalog;
import dataStructure.DataTable;
import dataStructure.Tuple;

public class ScanOperator extends Operator {

	// Check if this will be inherited by the children class
	private BufferedReader br;
	private DataTable data;
	private String tableName;  //if there is Alias then use Alias, otherwise use TableName
	private String dirName;   // only the name of the table, 
	//to get the directory and schema from the catalog

	/**
	 * Constructor that instantiates a ScanOperator object
	 * 
	 * @param tableName         The name of the data read by the ScanOperator
	 * @param hasAlias		    hasAlias is true if the tableName contains alias
	 * 							e.g. hasAlias if tableName= "Sailors AS S"
	 */
	public ScanOperator(String tableName,String aliasName) {
		this.tableName= tableName;
		this.dirName=tableName;
		Catalog catalog= Catalog.getInstance();
		if(aliasName !="") {
			dirName = tableName.replace("AS "+aliasName,"").trim();
			// if there's alias, always using alias name as the index for columns
			tableName = aliasName;
		}
		String dir= catalog.getDir(dirName);
		ArrayList<String> schema = catalog.getSchema(dirName);
		ArrayList<String> newSchema = (ArrayList<String>) schema.clone();
		for(int i=0;i<schema.size();i++ ) {
			newSchema.set(i, tableName+"."+schema.get(i));
		}
		data= new DataTable(tableName,newSchema);

		File file= new File(dir);
		try {
			br= new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.err.println("Data directory " + dir + " is not found");
		}
	}

	/**
	 * @return Returns the next tuple read from the data
	 */
	public Tuple getNextTuple() {
		String read= null;
		try {
			read= br.readLine();
		} catch (IOException e) {
			System.err.println("An error occured during reading from file");
		}

		if (read == null) {
			return null;
		} else {
			Tuple nextTuple= new Tuple(read);
			data.addData(nextTuple);
			return (nextTuple);
		}
	}

	/**
	 * reset read stream to re-read the data
	 */
	public void reset() {
		Catalog catalog= Catalog.getInstance();
		String dir= catalog.getDir(dirName);
		ArrayList<String> schema = catalog.getSchema(dirName);
		ArrayList<String> newSchema = (ArrayList<String>) schema.clone();
		for(int i=0;i<schema.size();i++ ) {
			newSchema.set(i, tableName+"."+schema.get(i));
		}
		data= new DataTable(tableName, newSchema);

		File file= new File(dir);
		try {
			br= new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.err.println("Data directory " + dir + " is not found");
		}
	}

	/**
	 * Prints the data read by operator to the PrintStream [ps]
	 * 
	 * @param ps      The print stream that the output will be printed to
	 * @param print   boolean decides whether the data will actually be printed 
	 */
	public void dump(PrintStream ps, boolean print) {
		Tuple next;
		while ((next= getNextTuple()) != null) {}
		if (print) { data.printTable(ps); }
	}

	/**
	 * @return the schema of the data table that is read by the operator
	 */
	public ArrayList<String> schema() {
		return data.getSchema();
	}

	/**
	 * @return the table name from where the operator reads the data
	 */
	public String getTableName() {
		return data.getTableName();
	}

	/** 
	 * @return the data read by the operator in DataTable data structure
	 */
	public DataTable getData() {
		dump(System.out, false);
		return data;
	}
}
