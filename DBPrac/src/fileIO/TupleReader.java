package fileIO;

import java.util.ArrayList;

import dataStructure.Tuple;

public interface TupleReader {
	
	public Tuple readNextTuple();
	
//	public ArrayList<Tuple> readNextPage();

	public void close();

	public void reset();
	
//	/**
//	 * delete this file of tupleReader
//	 */
//	public void deleteFile();

}
