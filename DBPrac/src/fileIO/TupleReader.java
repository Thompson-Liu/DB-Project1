package fileIO;

import dataStructure.Tuple;

interface TupleReader {

	public Tuple readNextTuple(String name);

	public void close();

	public void reset();

//	public TupleReader() {
//		// TODO Auto-generated constructor stub
//	}

}
