package fileIO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

import dataStructure.Tuple;

public class BinaryTupleWriter implements TupleWriter{
	
	private ArrayList<Tuple> data;
	private ByteBuffer buffer;
	private FileChannel fc ;

	public BinaryTupleWriter(String name) {
		FileOutputStream fout;
		try {
		
		fout = new FileOutputStream( name );
		this.fc= fout.getChannel();
		
		// Double check the size
		this.buffer = ByteBuffer.allocate( 1024 );
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override 
	public void writeTuple(ArrayList<Tuple> data) {
		this.data=data;
	}
	
	
	@Override
	public void dump() {
		
		int m = data.size();
		int n = data.get(0).getTuple().size();
		for (int i=0; i<m; ++i) {
			for(int j=0;j<n;j++) {
				buffer.putInt( data.get(i).getData(j) );
			}
		}
		buffer.flip();
		try {
			fc.write( buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
