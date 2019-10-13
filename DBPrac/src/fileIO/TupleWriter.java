package fileIO;

import java.util.ArrayList;

import dataStructure.Tuple;

public interface TupleWriter {

	/**
	 * add table to the buffer
	 * @param data arraylist of Tuple
	 */
	public void addTable(ArrayList<Tuple> data);
		
//	/**
//	 * write tuple to the file
//	 * @param data arrayList of tuple
//	 */
//	public void writeTuple(ArrayList<Tuple> data);
	
	
	public void dump();
	public void close();
	
}
