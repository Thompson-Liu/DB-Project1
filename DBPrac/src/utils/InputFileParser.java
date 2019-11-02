package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class InputFileParser {
	
	private String inputDir;
	private BufferedReader reader;
	
	public InputFileParser(String dir) {
		inputDir = dir; 
		
		// Initialize the input file reader
		try {
			reader = new BufferedReader(new FileReader(dir));
		} catch (FileNotFoundException e) {
			System.err.println("Input file not found");
			e.printStackTrace();
		}
	}
	
	public String getDir() {
		String inputDir = null;
		try {
			inputDir = reader.readLine();
		} catch (IOException e) {
			System.err.println("Having error reading from the file");
			e.printStackTrace();
		}
		
		return inputDir;
	}
	
	public int getFlag() {
		int buildIndex = -1;
		try {
			buildIndex = Integer.parseInt(reader.readLine());
		} catch (IOException e) {
			System.err.println("Having error reading from the file");
			e.printStackTrace();
		}
		return buildIndex;
	}
}
