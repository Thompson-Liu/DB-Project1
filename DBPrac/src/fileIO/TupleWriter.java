package fileIO;

import java.util.ArrayList;

import dataStructure.Tuple;

public interface TupleWriter {

	
	public void write(ArrayList<Tuple> data);
	
	/**
	 *  only add tuple to the buffer, may not writing it out if buffer is not full
	 *  if want to make sure write out, should call dump()
	 * @param tup tuple to add
	 */
	public void addNextTuple(Tuple tup);
		
	public void reset();
	
	public void close();
	
	// make sure to write every thing from buffer to file (including the left pageData)
	public void dump();
	
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
