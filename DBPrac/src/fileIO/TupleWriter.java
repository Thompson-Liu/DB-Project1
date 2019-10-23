package fileIO;

import java.util.ArrayList;

import dataStructure.Tuple;

public interface TupleWriter {

	/**
	 *  only write to buffer, need to call dump if want to write out the full buffer
	 * @param data  ArrayList of Tuple
	 */
	public void write(ArrayList<Tuple> data);
	
	/**
	 * 
	 * @return the file info of the binary tuple writer
	 */
	public String getInfo() ;
	
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
}
