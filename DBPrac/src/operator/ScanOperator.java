package operator;

import dataStructure.Tuple;
import dataStructure.Catalog;
import dataStructure.DataTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ScanOperator extends Operator {

	private DataTable data;
	private BufferedReader br;
	
	public ScanOperator(String name) {
		data = new DataTable(name);
		
		Catalog catalog = Catalog.getInstance();
		String dir = catalog.getDir(name);
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
			Tuple tuple = new Tuple(read);
			data.addData(tuple.getData());
			return tuple;
		}
	}
	
	public void reset() {
		try {
			br.reset();
		} catch (IOException e) {
			System.err.println("Reset to the previous mark failed");
		}
	}
	
	public DataTable dump() {
		return data;
	}
}
