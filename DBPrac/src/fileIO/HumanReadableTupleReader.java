package fileIO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import dataStructure.Tuple;

public class HumanReadableTupleReader {

	public HumanReadableTupleReader() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public Tuple readNextTuple(String fileName) {
		FileInputStream fin;
		try {
			fin= new FileInputStream(fileName);
			FileChannel fc= fin.getChannel();
			ByteBuffer buffer= ByteBuffer.allocate(1024);
			try {
				fc.read(buffer);
				for (int i= 0; i < 1000; i+= 1) {
					System.out.println(buffer.getInt(i));
				}
				return new Tuple();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Tuple();

	}

	@Override
	public void close() {
	}

	@Override
	public void reset() {

	}
}
