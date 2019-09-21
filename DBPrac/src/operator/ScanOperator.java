package operator;

import dataStructure.Tuple;
import dataStructure.Catalog;
import dataStructure.DataTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class ScanOperator extends Operator {

	// Check if this will be inherited by the children class
	private BufferedReader br;
	private DataTable data;
	
	public ScanOperator(String tableName) {
		Catalog catalog = Catalog.getInstance();
		String dir = catalog.getDir(tableName);
		data = new DataTable(tableName, catalog.getSchema(tableName));
		
		File file = new File(dir);
		try {
			br = new BufferedReader(new FileReader(file));
		
			// Check this number limit
			br.mark(0);
		} catch (FileNotFoundException e) {
			System.err.println("Data directory " + dir + " is not found");
		} catch (IOException e) {
			System.err.println("Marking stream returns an error");
		}
	}
	
	@Override
	public Tuple getNextTuple(){
		String read = null;
		try {
			read = br.readLine();
		} catch (IOException e) {
			System.err.println("An error occured during reading from file");
		}
		
		if (read == null) {
			return null;
		} else {
			Tuple nextTuple = new Tuple(read);
			data.addData(nextTuple);
			return (nextTuple);
		}
	}
	
	@Override
	public void reset() {
		try {
			br.reset();
		} catch (IOException e) {
			System.err.println("Reset to the previous mark failed");
		}
	}
	
	@Override
	public void dump(PrintStream ps) {
		Tuple next;
		while ((next = getNextTuple()) != null) {
			
		}
		data.printTable(ps);
	}
	
	@Override
	public ArrayList<String> schema() {
		return data.getSchema();
	}
	
	@Override
	public String getTableName() {
		return data.getTableName();
	}
}
