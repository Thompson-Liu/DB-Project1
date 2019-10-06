package fileIO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import dataStructure.Tuple;

public class ReadableTupleWriter implements TupleWriter{

	
	
	private ArrayList<ArrayList<Integer>> data;
	private IntBuffer buffer;
	private FileChannel fc ;

	
	public ReadableTupleWriter(String name) {
		// TODO Auto-generated constructor stub
		FileOutputStream fout;
		try {

		fout = new FileOutputStream( name+".txt" );
		this.fc= fout.getChannel();
		data = new ArrayList<ArrayList<Integer>>();
		
		// Double check the size
		this.buffer = IntBuffer.allocate( 256 );
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

		for (int i=0; i<m; i++) {
			for(int j=0;j<n;j++) {
				System.out.println(data.get(i).get(j));
				
				buffer.put(data.get(i).get(j)) ; 
				writePos++;
				buffer.flip();
				
			}
		}
//		fc.write();    
//		.write( buffer);

		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.print("BinaryTupleWriter dump fails: "+e);
			e.printStackTrace();
		}
	}


}
