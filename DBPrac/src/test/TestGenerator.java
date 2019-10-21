package test;

import java.util.ArrayList;

import dataStructure.Tuple;

public class TestGenerator {
	
	private int dataRange;
	private int tupleLength;
	private int tupleCount;
	
	public TestGenerator(int range, int length, int count) {
		dataRange = range;
		tupleLength = length;
		tupleCount = count;
	}
	
	public ArrayList<Tuple> generateTuples() {
		ArrayList<Tuple> data = new ArrayList<Tuple>();
		for (int i = 0; i < tupleCount; ++i) {
			ArrayList<Integer> tupArray = new ArrayList<Integer>();
			for (int j = 0; j < tupleLength; ++j) {
				int rand = (int)(Math.floor(Math.random() * dataRange));
				tupArray.add(rand);
			}
			Tuple tup = new Tuple(tupArray);
			data.add(tup);
		}
		return data;
	}
}
