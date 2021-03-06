package fileIO;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import dataStructure.Tuple;

public class ReadableTupleWriter implements TupleWriter {

	private ArrayList<ArrayList<Integer>> data;
	private ArrayList<Integer> index;
	private BufferedOutputStream buffer;
	private FileOutputStream fout;
	private String name;

	public ReadableTupleWriter(String name) {
		try {
			this.name= name;
			fout= new FileOutputStream(name);
			data= new ArrayList<ArrayList<Integer>>();
			buffer= new BufferedOutputStream(fout);
		} catch (Exception e) {
			System.err.print(" Readable Tuple Write initialize fail. ");
			e.printStackTrace();
		}
	}

	@Override
	public void addNextTuple(Tuple tup) {
		data.add(tup.getTuple());
	}

	@Override
	public void write(ArrayList<Tuple> dataTable) {
		for (Tuple tup : dataTable) {
			data.add(tup.getTuple());
		}
	}

	@Override
	public void reset() {
		try {
			fout= new FileOutputStream(name);
			data= new ArrayList<ArrayList<Integer>>();
			buffer= new BufferedOutputStream(fout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			buffer.close();
		} catch (IOException e) {
			System.err.println("Fail to close Readable Tuple Writer. ");
			e.printStackTrace();
		}
	}

	@Override
	public void dump() {
		try {
			for (ArrayList<Integer> x : data) {
				for (int i= 0; i < x.size() - 1; i++ ) {
					String out= x.get(i) + ",";
					buffer.write(out.getBytes());
				}
				String out= Integer.toString(x.get(x.size() - 1));
				buffer.write(out.getBytes());
				String newLine= "\n";
				buffer.write(newLine.getBytes());
			}
		} catch (IOException e) {
			System.err.print("BinaryTupleWriter dump fails: " + e);
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getInfo() {
		return name;
	}

	@Override
	public void addNextValue(int val) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset(int page) {
		// TODO Auto-generated method stub
	}
	
	
}
