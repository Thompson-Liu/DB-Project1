package fileIO;

import java.util.ArrayList;

import dataStructure.Tuple;

interface TupleReader {

	public ArrayList<Tuple> readData(String fileName);

	public void close();

	public void reset();

}
