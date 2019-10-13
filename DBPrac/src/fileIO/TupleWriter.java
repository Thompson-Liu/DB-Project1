package fileIO;

import java.util.ArrayList;

import dataStructure.Tuple;

public interface TupleWriter {

	public void writeTable(ArrayList<Tuple> data);
		
	public void dump();
	public void close();
	
}
