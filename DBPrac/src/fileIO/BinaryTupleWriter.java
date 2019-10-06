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

			// Double check the size
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
			int m= data.size();
			int n= data.get(0).size();
//		System.out.println(m);
//		System.out.println(n);

			for (int i= 0; i < m; i++ ) {
				for (int j= 0; j < n; j++ ) {
					System.out.println(data.get(i).get(j));
					buffer.putInt(data.get(i).get(j));
					writePos+= 1;
				}
			}
			buffer.flip();
			fc.write(buffer);

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
