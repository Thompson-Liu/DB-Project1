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

	private ByteBuffer buffer;
	private String file;
	private FileChannel fc;
	private FileOutputStream fout;
	private int curRow = 0;   //keep counting the position of current page
	private int numAttr;
	private int numRowPage;
	private ArrayList<Tuple> pageData;

	/**
	 * Binary Tuple writer constructor 
	 * 
	 * @param file  the file path 
	 */
	public BinaryTupleWriter(String file) {
		this.file = file;
		try {
			fout = new FileOutputStream(file);
			fc = fout.getChannel();
			pageData = new ArrayList<Tuple>();
			buffer = ByteBuffer.allocate(4096);
		} catch (Exception e) {
			System.err.print("BinaryTupleWrite initialize fail.");
			e.printStackTrace();
		}
	}

	@Override
	public void addNextTuple(Tuple tup) {
		
		numAttr = tup.getTuple().size();		
		
		
		numRowPage = (int) Math.floor((4096 - 8) * 1.0 / (numAttr * 4));
		if (curRow <= numRowPage - 1) {
			pageData.add(tup);
			curRow++;
		} else {
			writeNextPage();
			pageData = new ArrayList<Tuple>();
			pageData.add(tup);
			curRow = 1;
		}
	}

	/**
	 * Write an extra page of data into the buffer 
	 * 
	 */
	private void writeNextPage() {
		buffer.putInt(numAttr);
		buffer.putInt(pageData.size());
		int counter = 8;
		for (int i = 0; i < pageData.size(); i++) {
			for (int j = 0; j < numAttr; j++) {
				buffer.putInt(pageData.get(i).getTuple().get(j));
				counter += 4;
			}
		}
		while (counter < 4096) {
			buffer.putInt(0);
			counter+= 4;
		}
		buffer.flip();
		try {
			fc.write(buffer);
			buffer.clear();
		} catch (IOException e) {
			System.err.println("Error when writing to the file");
			e.printStackTrace();
		}
	}

	/**
	 * write all the data into the file
	 * 
	 * @args data 	the data that will be output
	 * 
	 */
 	public void write(ArrayList<Tuple> data) {
		for (Tuple tuple: data) {
			addNextTuple(tuple);
		}
	}
	
	@Override
	public void reset() {
		try {
			fout = new FileOutputStream(file);
			fc = fout.getChannel();
			pageData = new ArrayList<Tuple>();
			buffer = ByteBuffer.allocate(4096);
		} catch (Exception e) {
			System.err.print("BinaryTupleWrite initialize fail.");
			e.printStackTrace();
		}
	}
	
	@Override
	public void dump() {
		if (pageData == null || pageData.size() == 0) { return; }
		try {
			int numRows= pageData.size();
			int numPages= (int) Math.ceil(1.0 * numRows / numRowPage);
			int counter;
			for (int k= 0; k < numPages; k++ ) {
				buffer.putInt(numAttr);
				buffer.putInt(Math.min(numRows, numRowPage));
				counter= 8;
				for (int i= 0; i < Math.min(numRows, numRowPage); i++ ) {
					for (int j= 0; j < numAttr; j++ ) {

						buffer.putInt(pageData.get(k * numRowPage + i).getTuple().get(j));
						counter+= 4;
					}
				}
				while (counter < 4096) {
					buffer.putInt(0);
					counter+= 4;
				}
				numRows=numRows-numRowPage;
				buffer.flip();
				fc.write(buffer);
			}
		} catch (IOException e) {
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

	@Override
	public String getInfo() {
		return file;
	}
}