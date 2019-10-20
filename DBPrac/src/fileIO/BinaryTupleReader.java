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
	private int curRow= 0;    // keep track of next row to read on the buffer
	private ArrayList<Tuple> pageData;

	public BinaryTupleReader(String file) {
		try {
			this.file= file;
			this.pageData= new ArrayList<Tuple>();
			fin= new FileInputStream(file);
			fc= fin.getChannel();
			buffer= ByteBuffer.allocate(4096);
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
			numAttr= buffer.getInt(0);
			this.numRows= buffer.getInt(4);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error when reading into the buffer");
		}

		if (this.numRows != 0) {
			pageData = new ArrayList<Tuple>();
			// Populate the dataTable
			for (int i= 0; i < this.numRows; i++ ) {
				ArrayList<Integer> temp= new ArrayList<Integer>(numAttr);
				
				for (int j= 0; j < numAttr; j++ ) {
					
					temp.add(buffer.getInt(i * numAttr * 4 + 8 + j * 4));
				}
				pageData.add(new Tuple(temp));
			}
			return pageData;
		} else {
			return null;
		}
	}

	@Override
	public Tuple readNextTuple() {
		if (curRow <= this.numRows - 1) {
			return pageData.get(curRow++);
		} else {
			curRow= 0;
			pageData= getNextPage();
			if (this.numRows > 0) {
				return pageData.get(curRow++ );
			} else {
				return null;
			}
		}
	}
	
	public void setAtt(int num) {
		this.numAttr=num;
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
			int rowsPerPage= 4988 / (4 * numAttr);
			int numPages= index / rowsPerPage;
			int newIndex= numPages * 4996 + 8 + (index % rowsPerPage) & (4 * numAttr);
			curRow=index % rowsPerPage;
			fc.position(newIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}