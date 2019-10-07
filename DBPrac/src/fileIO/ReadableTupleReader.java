package fileIO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import dataStructure.Tuple;

public class ReadableTupleReader implements TupleReader {
	private ArrayList<Tuple> resource;

	public ReadableTupleReader(String fileName) {
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
				int numRows= buffer.getInt(4);
				int rowPerPage= (int) Math.floor((4096 - 8) / (4 * numAttr));
//				System.out.println("row per page is " + rowPerPage);
//				System.out.println("number of pages is " + numPages);
//				System.out.println("number of rows is " + numRows);
				int counter=0;
				while (numRows != 0) {
//					System.out.println("number of rows is " + numRows);
					for (int i= 0; i <  numRows; i+= 1) {
						Integer[] currTuple= new Integer[numAttr];
						for (int j= 0; j < numAttr; j++ ) {
							currTuple[j]= buffer.getInt(i * numAttr * 4 + 8 + j * 4);
						}
//						System.out.println(currTuple[2]);
						resource.add(new Tuple(new ArrayList<Integer>(Arrays.asList(currTuple))));
						counter++;
//						System.out.println("row number is " + i);
			
					}
					
					
					buffer.clear();		
					buffer.putInt(4,0);
					fc.read(buffer);
					numRows = buffer.getInt(4);
				}
				System.out.println("counter is " + counter);
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
