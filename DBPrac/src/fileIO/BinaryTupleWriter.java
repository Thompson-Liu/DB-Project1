package fileIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

import dataStructure.Tuple;

public class BinaryTupleWriter implements TupleWriter{
	
	private ArrayList<ArrayList<Integer>> data;
	private ByteBuffer buffer;
	private FileChannel fc ;

	public BinaryTupleWriter(String name) {
		FileOutputStream fout;
		try {

		fout = new FileOutputStream( name+".txt" );
		this.fc= fout.getChannel();
		data = new ArrayList<ArrayList<Integer>>();
		
		// Double check the size
		this.buffer = ByteBuffer.allocate( 1024 );
		} 
		catch (Exception e) {
			System.err.print("BinaryTupleWrite initialize fail.");
			e.printStackTrace();
		}
	}
	
	@Override 
	public void writeTable(ArrayList<Tuple> dataTable) {
		for (Tuple tup : dataTable) {
			ArrayList<Integer> a = tup.getTuple();
			data.add(tup.getTuple());
		}
	}
	
	
	@Override
	public void dump() {
		int writePos = buffer.position();           // index write to the file
		try {
		int m = data.size();
		int n=data.get(0).size();
		ByteBuffer buffer2 = ByteBuffer.allocate( 1024 );

		for (int i=0; i<2; i++) {
			for(int j=0;j<1;j++) {
				System.out.println(data.get(i).get(j));
				
				buffer.putInt(2);
				writePos++;
				buffer.flip();
				
			}
		}
//		buffer.putInt(0,23);
//		buffer.putChar('c');
		fc.write( buffer);

		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.print("BinaryTupleWriter dump fails: "+e);
			e.printStackTrace();
		}
	}

}
