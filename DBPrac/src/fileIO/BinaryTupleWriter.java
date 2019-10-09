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
	private FileOutputStream fout;

	public BinaryTupleWriter(String name) {

		try {

			fout= new FileOutputStream(name);
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
	}

	/**
	 * write back the data
	 */
	@Override
	public void dump() {
		if (data == null || data.size() == 0) { return; }
//		int writePos= buffer.position();           // index write to the file
		try {
			int numRows= data.size();
			int numAttr= data.get(0).size();
			int numRowPage= (int) Math.floor((4096 - 8) / (numAttr * 4));
			int numPages= (int) Math.ceil(1.0 * numRows / numRowPage);
			for (int k= 0; k < numPages; k++ ) {
				buffer.putInt(numAttr);
				buffer.putInt(Math.min(numRows, numRowPage));

				int counter= 8;
				for (int i= 0; i < Math.min(numRows, numRowPage); i++ ) {
					for (int j= 0; j < numAttr; j++ ) {

						buffer.putInt(data.get(k * numRowPage + i).get(j));
						counter+= 4;
					}
				}

				while (counter < 4096) {
					buffer.putInt(0);
					counter+= 4;
				}

				buffer.flip();
				fc.write(buffer);
				numRows-= numRowPage;
				buffer.clear();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.print("BinaryTupleWriter dump fails: " + e);
			e.printStackTrace();
		}

	}
	
	


	@Override
	public void close() {
		try {
			fc.close();
			fout.close();
		} catch (IOException e) {
			System.err.print("Fail to close Binary tuple writer. ");
			e.printStackTrace();
		}
	}

}
