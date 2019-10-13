package fileIO;

import java.util.ArrayList;

import dataStructure.Tuple;

public interface TupleReader {

	public ArrayList<Tuple> readData(String fileName);

	public void close();

	public void reset();

}
