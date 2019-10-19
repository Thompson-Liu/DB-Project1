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
	
//	/**
//	 * delete this file of tupleReader
//	 */
//	public void deleteFile();

}
