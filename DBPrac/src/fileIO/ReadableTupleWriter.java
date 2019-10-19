package fileIO;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import dataStructure.Tuple;

public class ReadableTupleWriter implements TupleWriter{
	
	private ArrayList<ArrayList<Integer>> data;
	private BufferedOutputStream buffer;
	private FileOutputStream fout;

	
	public ReadableTupleWriter(String name) {
			try {
				fout= new FileOutputStream(name);
				data= new ArrayList<ArrayList<Integer>>();
				buffer = new BufferedOutputStream(fout);
			} catch (Exception e) {
				System.err.print(" Readable Tuple Write initialize fail. ");
				e.printStackTrace();
			}
	}
	
	public void addTable(ArrayList<Tuple> dataTable) {
		for (Tuple tup : dataTable) {
			ArrayList<Integer> a = tup.getTuple();
			data.add(a);
		}
	}
	
	
	public void write() {
		try {
			
			for (ArrayList<Integer> x : data) {
				for (int i= 0; i < x.size() - 1; i++) {
					String out = x.get(i) + ",";
					buffer.write(out.getBytes());
				}
				String out = Integer.toString(x.get(x.size() - 1));
				buffer.write(out.getBytes());
				String newLine = "\n";
				buffer.write(newLine.getBytes());
			}
			
		} catch (IOException e) {
			System.err.print("BinaryTupleWriter dump fails: " + e);
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
//			fout.close();
			buffer.close();
		} catch (IOException e) {
			System.err.println("Fail to close Readable Tuple Writer. ");
			e.printStackTrace();
		}
		
	}

	@Override
	public void write(ArrayList<Tuple> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
