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

			ByteBuffer buffer= ByteBuffer.allocate(4096);
			try {
				//				fin.close();
				fc.read(buffer);
				// Get meta-data
				int numAttr= buffer.getInt(0);
				int numRows= buffer.getInt(1);

				int numPages = (int) Math.ceil((numRows * numAttr * 4) / (4096 - 8));
				int maxRows = (int) Math.floor((4096 - 8) / 4 / numAttr);
				System.out.println(numPages);
				System.out.println(numRows);

				for (int k =0 ; k<numPages;k++) {


					for (int i= 0; i < Math.min(numRows, maxRows); i+= 1) {
						Integer[] currTuple= new Integer[numAttr];
						for(int j=0;j<numAttr;j++) {
							currTuple[j]= buffer.getInt(i*numAttr*4+8+j*4);

						}

						//					int pos= (i - 2) % numAttr;
						//					currTuple[pos]= buffer.getInt(i);
						//					if (pos == numAttr - 1) {
						//					numRows-=maxRows;
						//					System.out.println(i);
						Tuple a = new Tuple(new ArrayList<Integer>(Arrays.asList(currTuple)));
						System.out.println(currTuple[2]);

						resource.add(new Tuple(new ArrayList<Integer>(Arrays.asList(currTuple))));
					}
					buffer= ByteBuffer.allocate(4096);
					fc.read(buffer);
					

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
