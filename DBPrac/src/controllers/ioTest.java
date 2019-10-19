package controllers;

import java.io.File;
import java.io.IOException;
import fileIO.*;

public class ioTest {

	public static void main(String[] args) {
		try {
		BinaryTupleReader tr= new BinaryTupleReader(
				"/Users/yutingyang/Desktop/db_prac/DB-Project2/DBPrac/samples/input/db/data/Boats");
//		System.out.println(tr.readData());
//		System.out.println(tr.readData().get(2).printData());
		
//		BinaryTupleWriter tw= new
//		 BinaryTupleWriter("/Users/ziweigu/Desktop/DB-Project1/DBPrac/samples/output/result");
		

		// System.out.println(tr.readData().get(2).printData());
		BinaryTupleWriter tw= new BinaryTupleWriter("/samples/tempdir/result");
		tw.addNextTuple(tr.readNextTuple()); 
		tw.addNextTuple(tr.readNextTuple()); 
		tw.dump();
		
		//try to delete
		File dele = new File("/Users/yutingyang/Desktop/db_prac/DB-Project2/DBPrac/samples/tempdir/result");
		dele.delete();

//		ReadableTupleReader rTR= new ReadableTupleReader("/Users/ziweigu/Desktop/DB-Project1/DBPrac/samples/input/db/data/Boats");
//		// Test logger
//		Logger log= Logger.getInstance();
//		try {
//			log.dumpMessage("Start reading...");
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		ReadableTupleWriter rTW= new ReadableTupleWriter("re");
//
//		rTW.addTable(rTR.readData());
//		rTW.dump();
		
		
//		// Test logger
//		try {
//			log.dumpMessage("Finish writing...");
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		}catch(Exception e) {
			e.printStackTrace();
//			System.err.print("wrong");
		}

	}

}
