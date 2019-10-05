package controllers;

import fileIO.BinaryTupleReader;
import fileIO.BinaryTupleWriter;

public class ioTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BinaryTupleReader tr= new BinaryTupleReader(
			"/Users/yutingyang/Desktop/db_prac/DB-Project2/DBPrac/samples/input/db/data/Boats");
		System.out.println(tr.readData());
//		System.out.println(tr.readData().get(2).printData());
//
//		BinaryTupleWriter tw= new BinaryTupleWriter("/Users/ziweigu/Desktop/DB-Project1/DBPrac/samples/output/result");
//		tw.writeTuple(tr.readData());

//		System.out.println(tr.readData().get(2).printData());
		BinaryTupleWriter tw= new BinaryTupleWriter("result");
		tw.writeTable(tr.readData());
		tw.dump();
	}

}
