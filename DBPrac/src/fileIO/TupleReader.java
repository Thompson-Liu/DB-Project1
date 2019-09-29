package fileIO;

import java.util.ArrayList;

import dataStructure.Tuple;

interface TupleReader {

	public ArrayList<Tuple> readData();

	public void close();

	public void reset();

}
