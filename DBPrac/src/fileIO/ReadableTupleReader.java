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
	private ArrayList<Tuple> resource;
	private BufferedReader buffer;
	

	public ReadableTupleReader() {
		// TODO Auto-generated constructor stub
		resource= new ArrayList<Tuple>();
	}

	@Override
	public ArrayList<Tuple> readData(String fileName) {
		try {
			
			buffer= new BufferedReader(new FileReader(fileName));
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
	public void reset() {
		resource= new ArrayList<Tuple>();
	}

}
