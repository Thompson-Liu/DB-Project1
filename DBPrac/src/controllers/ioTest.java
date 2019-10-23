package controllers;

import java.io.File;
import fileIO.*;

public class ioTest {

	public static void main(String[] args) {
		try {
		BinaryTupleReader tr= new BinaryTupleReader(
				"/Users/yutingyang/Desktop/db_prac/DB-Project2/DBPrac/samples/input/db/data/Boats");
		
//		BinaryTupleWriter tw= new
//		 BinaryTupleWriter("/Users/ziweigu/Desktop/DB-Project1/DBPrac/samples/output/result");
		

		BinaryTupleWriter tw= new BinaryTupleWriter("/Users/yutingyang/Desktop/db_prac/DB-Project2/DBPrac/samples/tempdir/result");
		System.out.println(tr.readNextTuple().printData());
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
