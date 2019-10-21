package fileIO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import dataStructure.Tuple;

public class BinaryTupleReader implements TupleReader {
	private String file;          // the directory + file name
	private FileInputStream fin;
	private FileChannel fc;
	private ByteBuffer buffer;
	private int numAttr;
	private int numRows;    // number of rows on current buffer page
	private int curRow;    // keep track of next row to read on the buffer
	private ArrayList<Tuple> pageData;
	private int rowsPerPage;

	public BinaryTupleReader(String file) {
		try {
			this.file= file;
			this.pageData= new ArrayList<Tuple>();
			fin= new FileInputStream(file);
			fc= fin.getChannel();
			buffer= ByteBuffer.allocate(4096);
			curRow= 0;
			numRows= 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		pageData= getNextPage();
	}

	private ArrayList<Tuple> getNextPage() {
		buffer.clear();
		buffer.putInt(4, 0);
		try {
			fc.read(buffer);
			this.numAttr= buffer.getInt(0);
			this.numRows= buffer.getInt(4);
			if (this.numRows != 0) {
				this.rowsPerPage= (int) Math.floor(1.0 * (4096 - 8) / this.numAttr);
				pageData= new ArrayList<Tuple>();
				// Populate the dataTable
				for (int i= 0; i < this.numRows; i++ ) {
//					if(numAttr==104) {
//						System.out.println("stop");
//						break;
//					}
					// System.out.println("i is :"+i);
					// System.out.println("num attributes are : "+this.numAttr);
					// System.out.println("numRows are : "+this.numRows);
					ArrayList<Integer> temp= new ArrayList<Integer>(numAttr);
					for (int j= 0; j < numAttr; j++ ) {
						temp.add(buffer.getInt(i * numAttr * 4 + 8 + j * 4));
					}
					pageData.add(new Tuple(temp));
				}
				return pageData;
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error when reading into the buffer");
		}
		return null;
	}

	@Override
	public Tuple readNextTuple() {
		if (curRow <= this.numRows - 1) {
			return pageData.get(curRow++ );
		} else {
			curRow= curRow - numRows;
			pageData= getNextPage();
			if (this.numRows > 0) {
				return pageData.get(curRow++ );
			} else {
				return null;
			}
		}
	}

	@Override
	public void setAtt(int num) {

		this.numAttr= num;
	}

	@Override
	public String getFileInfo() {
		return this.file;
	}

	@Override
	public void close() {
		try {
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reset() {
		try {
			fin= new FileInputStream(file);
			fc= fin.getChannel();
			buffer= ByteBuffer.allocate(4096);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pageData= getNextPage();
	}

	@Override
	public void reset(int index) {
		// Using position(long newPosition) method in FileChannel
		try {
//			rowsPerPage= 4088 / (4 * numAttr);
//			int numPages= (index+1) / rowsPerPage;
//			curRow=(index) % rowsPerPage;
//			int newIndex= numPages * 4096 + 8 + curRow * (4 * numAttr);
			int pageIndex= (index + 1) / this.rowsPerPage;
			curRow= index % rowsPerPage;
			fc.position(pageIndex);
			this.pageData= getNextPage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}