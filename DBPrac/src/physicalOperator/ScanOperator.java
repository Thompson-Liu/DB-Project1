package physicalOperator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import dataStructure.Catalog;
import dataStructure.DataTable;
import dataStructure.Tuple;

/** the class for the scan operator that scans and reads input data tables. */
public class ScanOperator extends Operator {

	// Check if this will be inherited by the children class
	private BufferedReader br;
	private DataTable data;
	private String tableName;  // if there is Alias then use Alias, otherwise use TableName
	private String dirName;   // only the name of the table,
						      // to get the directory and schema from the catalog

	/** @param tableName,hasAlias hasAlias is true if the tableName contains alias e.g. hasAlias if
	 * tableName= "Sailors AS S"
	 * @param hasAlias */
	public ScanOperator(String tableName, String aliasName) {
		this.tableName= tableName;
		this.dirName= tableName;
		Catalog catalog= Catalog.getInstance();
		if (aliasName != "") {
			dirName= tableName.replace("AS " + aliasName, "").trim();
			// if there's alias, always using alias name as the index for columns
			tableName= aliasName;
		}
		String dir= catalog.getDir(dirName);
		ArrayList<String> schema= catalog.getSchema(dirName);
		ArrayList<String> newSchema= (ArrayList<String>) schema.clone();
		for (int i= 0; i < schema.size(); i++ ) {
			newSchema.set(i, tableName + "." + schema.get(i));
		}
		data= new DataTable(tableName, newSchema);

		File file= new File(dir);
		try {
			br= new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.err.println("Data directory " + dir + " is not found");
		}
	}

	@Override
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

	/** reset read stream to re-read the data */
	@Override
	public void reset() {
		Catalog catalog= Catalog.getInstance();
		String dir= catalog.getDir(dirName);
		ArrayList<String> schema= catalog.getSchema(dirName);
		ArrayList<String> newSchema= (ArrayList<String>) schema.clone();
		for (int i= 0; i < schema.size(); i++ ) {
			newSchema.set(i, tableName + "." + schema.get(i));
		}
		data= new DataTable(tableName, newSchema);

		File file= new File(dir);
		try {
			br= new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.err.println("Data directory " + dir + " is not found");
		}
	}

	@Override
	public void dump(PrintStream ps, boolean print) {
		Tuple next;
		while ((next= getNextTuple()) != null) {
		}
		if (print) {
			data.printTable(ps);
		}
	}

	@Override
	public ArrayList<String> schema() {
		return data.getSchema();
	}

	@Override
	public String getTableName() {
		return data.getTableName();
	}

	@Override
	public DataTable getData() {
		dump(System.out, false);
		return data;
	}
}