package fileIO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import dataStructure.Tuple;

public class ReadableTupleReader implements TupleReader {
	private BufferedReader buffer;
	private String file;
	private ArrayList<Tuple> data;
	private int dataIndex= 0;

	public ReadableTupleReader(String file) {
		// TODO Auto-generated constructor stub
		this.file= file;
		try {
			buffer= new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		data= readData();
	}

	public ArrayList<Tuple> readData() {
		ArrayList<Tuple> resource= new ArrayList<Tuple>();
		try {
			String read= null;
			try {
				read= buffer.readLine();
			} catch (IOException e) {
				System.err.println("An error occured during reading from file");
			}

			while (read != null) {
				Tuple nextTuple= new Tuple(read);
				resource.add(nextTuple);
				read= buffer.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		reset();
		return resource;
	}

	@Override
	public void close() {
		try {
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getFileInfo() {
		return this.file;
	}

	@Override
	public Tuple readNextTuple() {
		return data.get(dataIndex);
	}

	public ArrayList<Tuple> readNextPage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		try {
			buffer= new BufferedReader(new FileReader(file));
			data= new ArrayList<Tuple>();
			dataIndex= 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reset(int index) {
		dataIndex= index;
	}

	// 是做什么用的呢？
	@Override
	public void setAtt(int num) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCurRow() {
		return 0;
	}

	@Override
	public int getPage() {
		return 0;
	}

	@Override
	public void reset(int pageInd, int rowInd) {
		// TODO Auto-generated method stub

	}

}
