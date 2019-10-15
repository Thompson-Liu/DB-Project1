package fileIO;

import java.util.ArrayList;

import dataStructure.Tuple;

public interface TupleWriter {

	
	public void write(ArrayList<Tuple> data);
		
	public void reset();
	
	public void close();
	
//	/**
//	 * add table to the buffer
//	 * @param data arraylist of Tuple
//	 */
//	public void addTable(ArrayList<Tuple> data);
//		
//	/**
//	 *  add tuple to the table of current TupleWriter
//	 * @param tup
//	 */
//	public void addTuple(Tuple tup);
	
//	public void dump();

}
