package controllers;

import java.io.IOException;

import fileIO.Logger;
import fileIO.ReadableTupleReader;
import fileIO.ReadableTupleWriter;

public class ioTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		BinaryTupleReader tr= new BinaryTupleReader(
//				"/Users/yutingyang/Desktop/db_prac/DB-Project2/DBPrac/samples/input/db/data/Boats");
//		System.out.println(tr.readData());
		// System.out.println(tr.readData().get(2).printData());
		//
		// BinaryTupleWriter tw= new
		// BinaryTupleWriter("/Users/ziweigu/Desktop/DB-Project1/DBPrac/samples/output/result");
		// tw.writeTuple(tr.readData());

		// System.out.println(tr.readData().get(2).printData());
//		BinaryTupleWriter tw= new BinaryTupleWriter("result");
//		tw.writeTable(tr.readData());
//		tw.dump();

		ReadableTupleReader rTR= new ReadableTupleReader("/Users/ziweigu/Desktop/DB-Project1/DBPrac/samples/input/db/data/Boats");
//		System.out.println(rTR.readData());
		// Test logger
		Logger log= Logger.getInstance();
		try {
			log.dumpMessage("Start reading...");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ReadableTupleWriter rTW= new ReadableTupleWriter("re");

		rTW.addTable(rTR.readData());
		rTW.dump();
		// Test logger
		try {
			log.dumpMessage("Finish writing...");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

}
