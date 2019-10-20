package fileIO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import dataStructure.Tuple;

public class ReadableTupleReader implements TupleReader {
	private BufferedReader buffer;
	private String file;
	private ArrayList<Tuple> data;
	private int dataIndex = 0;

	public ReadableTupleReader(String file) {
		// TODO Auto-generated constructor stub
		this.file=file;
		try {
			buffer= new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		data = readData();	
	}

	public ArrayList<Tuple> readData() {
		ArrayList<Tuple> resource = new ArrayList<Tuple>();
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
	
	
	public ArrayList<Tuple> readNextPage(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		try {
			buffer= new BufferedReader(new FileReader(file));
			data = new ArrayList<Tuple>();
			dataIndex = 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void reset(int index) {
		dataIndex = index;
	}

}
