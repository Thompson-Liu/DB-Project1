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
	
	/**
	 *  add another value to the buffer, should call dump() if want to make sure it
	 *  write out
	 * @param val
	 */
	public void addNextValue(int val);
		
	public void reset();
	
	/** reset the position to the page
	 *  @param page : the page number to reset back
	 */
	public void reset(int page);
	
	public void close();
	
	
	/**
	 *  make sure to write every thing from buffer to file (including the left pageData)
	 */
	public void dump();
}
