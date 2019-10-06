package fileIO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import dataStructure.Tuple;

public class BinaryTupleWriter implements TupleWriter {

	private ArrayList<ArrayList<Integer>> data;
	private ByteBuffer buffer;
	private FileChannel fc;

	public BinaryTupleWriter(String name) {
		FileOutputStream fout;
		try {

			fout= new FileOutputStream(name + ".txt");
			this.fc= fout.getChannel();
			data= new ArrayList<ArrayList<Integer>>();

			this.buffer= ByteBuffer.allocate(4096);
		} catch (Exception e) {
			System.err.print("BinaryTupleWrite initialize fail.");
			e.printStackTrace();
		}
	}

	@Override
	public void writeTable(ArrayList<Tuple> dataTable) {
		for (Tuple tup : dataTable) {
			ArrayList<Integer> a= tup.getTuple();
			data.add(tup.getTuple());
		}
		int writePos= buffer.position();           // index write to the file
		try {
			int numRows= data.size();
			int numAttr= data.get(0).size();
			int numRowPage= (int) Math.floor((4096-8)/(numAttr*4));
			int numPages = (int) Math.ceil(numRows/numRowPage);
			System.out.println(numRowPage);
			for (int k=0;k<numPages;k++) {
				buffer.putInt(numRows);
				buffer.putInt(numAttr);
				for (int i= 0; i < Math.min(numRows,numRowPage); i++ ) {
					for (int j= 0; j < numAttr; j++ ) {
						System.out.println(k*numRowPage+i);
						buffer.putInt(data.get(k*numRowPage+i).get(j));
					}
				}
				
				numRows-=numRowPage;
				
				buffer.flip();
				fc.write(buffer);
			}
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.print("BinaryTupleWriter dump fails: " + e);
			e.printStackTrace();
		}
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub

	}

}
