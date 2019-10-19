package fileIO;

import java.util.ArrayList;

import dataStructure.Tuple;

public interface TupleReader {
	
	/**
	 * 
	 * @return the next tuple in file  (first check buffer, fetch new page if not)
	 */
	public Tuple readNextTuple();
	
	/**
	 * 
	 * @return the file directory + file name
	 */
	public String getFileInfo();
	
//	public ArrayList<Tuple> readNextPage();

	public void close();

	public void reset();
	
	/**
	 *  Reset to a pointer where the next tuple is the index-th row of the whole table
	 * @param index
	 */
	public void reset(int index);
	

}
