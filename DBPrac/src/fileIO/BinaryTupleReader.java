package fileIO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import dataStructure.Tuple;

public class BinaryTupleReader implements TupleReader {
	private String fileName;
	private FileInputStream fin;
	private FileChannel fc;
	private ByteBuffer buffer;
	private int numAttr;
	private int numRows;    // number of rows on current buffer page
	private int curRow;    // keep track of next row to read on the buffer

	public BinaryTupleReader(String fileName) {
		try {
			this.fileName=fileName;
			fin= new FileInputStream(fileName);
			fc= fin.getChannel();
			buffer= ByteBuffer.allocate(4096);
			try {
				fc.read(buffer);
				// Get meta-data
				numAttr= buffer.getInt(0);
				numRows= buffer.getInt(4);
				curRow = 0;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * read the next tuple
	 * @return the tuple
	 */
	@Override
	public Tuple readNextTuple() {
		if (numRows==0) {
			reset();
			return null;
		}
		else {
			Integer[] temp= new Integer[numAttr];
			try {
				for (int j= 0; j < numAttr; j++ ) {
					temp[j]= buffer.getInt(curRow * numAttr * 4 + 8 + j * 4);
				}
				curRow+=1;
				// change to next page if current row is the last row
				if(curRow>= numRows) {
					buffer.clear();
					buffer.putInt(4, 0);
					fc.read(buffer);
					numRows= buffer.getInt(4);
					curRow=0;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return (new Tuple(new ArrayList<Integer>(Arrays.asList(temp))));
		}
	}

//	@Override
//	public ArrayList<Tuple> readNextPage(){
//		if(numRows!=0) {
//			try {
//				ArrayList<Tuple> resource= new ArrayList<Tuple>();
//				for(int i=0;i<numRows ;i++) {
//					if(numRows!=0) {
//						Integer[] temp= new Integer[numAttr];
//						for (int j= 0; j < numAttr; j++ ) {
//							temp[j]= buffer.getInt(i * numAttr * 4 + 8 + j * 4);
//						}
//					}
//
//				}
//				// change to next page if current row is the last row
//				buffer.clear();
//				buffer.putInt(4, 0);
//				fc.read(buffer);
//				numRows= buffer.getInt(4);
//				return resource;
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}

	/**
	 *  read whole data of tuples from the file
	 */
	@Override
	public ArrayList<Tuple> readData(){
		ArrayList<Tuple> resource= new ArrayList<Tuple>();
		try {
			Tuple cur;
			while ((cur=readNextTuple())!=null) {
				resource.add(cur);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
		return resource;
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
			fin= new FileInputStream(fileName);

			fc= fin.getChannel();
			buffer= ByteBuffer.allocate(4096);
			fc.read(buffer);
			// Get meta-data
			numAttr= buffer.getInt(0);
			numRows= buffer.getInt(4);
			curRow = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
