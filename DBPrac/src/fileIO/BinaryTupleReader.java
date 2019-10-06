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
	private ArrayList<Tuple> resource;

	public BinaryTupleReader(String fileName) {
		// TODO Auto-generated constructor stub
		resource= new ArrayList<Tuple>();
		try {
			FileInputStream fin= new FileInputStream(fileName);
			FileChannel fc= fin.getChannel();
			ByteBuffer buffer= ByteBuffer.allocate(1024);
			try {
//				fin.close();
				fc.read(buffer);
				// Get meta-data
				int numAttr= buffer.getInt(0);
				int numRows= buffer.getInt(1);
				Integer[] currTuple= new Integer[numAttr];
				for (int i= 9; i < numRows; i+= 1) {
					for(int j=0;j<numAttr;j++) {
						
					}
					int pos= (i - 2) % numAttr;
					currTuple[pos]= buffer.getInt(i);
					if (pos == numAttr - 1) {
						resource.add(new Tuple(new ArrayList<Integer>(Arrays.asList(currTuple))));
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<Tuple> readData() {
		return resource;
	}

	@Override
	public void close() {
	}

	@Override
	public void reset() {

	}

}
