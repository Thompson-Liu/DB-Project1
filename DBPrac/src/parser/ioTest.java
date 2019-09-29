package parser;

import fileIO.BinaryTupleReader;

public class ioTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BinaryTupleReader tr= new BinaryTupleReader(
			"/Users/ziweigu/Desktop/DB-Project1/DBPrac/samples/input/db/data/Boats");
		System.out.println(tr.readData());

	}

}
