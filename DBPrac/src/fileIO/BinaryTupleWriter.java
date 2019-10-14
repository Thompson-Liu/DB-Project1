package fileIO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import dataStructure.Tuple;

/**
 *  Support either write table or write tuples,
 *  buffersize([data] field) for tupleWriter is fixed to 4096, if the table size or 
 *  tuples stored in the field [data] exceed this, will first
 *  write the stored [data] to file before adding new tuples or table.
 *  After calling dump the stored [data] will be cleared to keep the buffersize
 */
public class BinaryTupleWriter implements TupleWriter {

	private ArrayList<ArrayList<Integer>> data;
	private ByteBuffer buffer;
	private FileChannel fc;
	private FileOutputStream fout;
	private int curRow;   //keep counting the position of current page
	private int numAttr;
	private int numRowPage;

	public BinaryTupleWriter(String name) {

		try {
			fout= new FileOutputStream(name);
			this.fc= fout.getChannel();
			data= new ArrayList<ArrayList<Integer>>();
			this.buffer= ByteBuffer.allocate(4096);
			curRow=0;
		} catch (Exception e) {
			System.err.print("BinaryTupleWrite initialize fail.");
			e.printStackTrace();
		}
	}
	
	/**
	 * add tuple to current stored [data], compute number of rows per page
	 * if it hasn't been initialized
	 */
	@Override
	public void addTuple(Tuple tup) {
		ArrayList<Integer> temp = tup.getTuple();
		if (data.size()==0) {
			numAttr= temp.size();
			numRowPage= (int) Math.floor((4096 - 8) / (numAttr * 4));
		}
		if(data.size()>=numRowPage) {
			dump();
			reset();
		}
		data.add(temp);
		curRow++;
	}
	
	/**
	 * add a table to current stored [data]
	 */
	@Override
	public void addTable(ArrayList<Tuple> dataTable) {
		if(dataTable.size()==0) return;
		// initialize binary tuple writer fields if hasn't been initialized
		if (data.size()==0) {
			numAttr= dataTable.get(0).getTuple().size();
			numRowPage= (int) Math.floor((4096 - 8) / (numAttr * 4));
		}
		for (Tuple tup : dataTable) {
			addTuple(tup);
		}
	}
	
	@Override
	public void reset() {
		curRow-= numRowPage;
		buffer.clear();
	}

	/**
	 * write back the data
	 */
	@Override
	public void dump() {
		if (data == null || data.size() == 0) { return; }
		try {
			int numRows= data.size();
			int numPages= (int) Math.ceil(1.0 * numRows / numRowPage);
			int counter;
			for (int k= 0; k < numPages; k++ ) {
				buffer.putInt(numAttr);
				buffer.putInt(Math.min(numRows, numRowPage));
				counter= 8;
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
				data= new ArrayList<ArrayList<Integer>>();
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
