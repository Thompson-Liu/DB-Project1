package utils;

import java.util.ArrayList;

import dataStructure.Tuple;
import fileIO.BinaryTupleWriter;
import fileIO.ReadableTupleWriter;
import fileIO.TupleWriter;
import test.TestGenerator;

public class DataGenerator {

	private int count;

	public DataGenerator(int numData, String inputDir, int baseRange, int baseLength, int baseCount) {
		count = numData;
		for (int i = 0; i < numData; ++i) {
		
			BinaryTupleWriter testDataWriter0= new BinaryTupleWriter(
					inputDir + "/db/data/testRelation" + count++);
			ReadableTupleWriter testDataWriter1= new ReadableTupleWriter(
					inputDir + "/db/data/testRelation" + count+++"humanReadable");
			generateRandomData(testDataWriter0, (i +1) * baseRange, baseLength + i, baseCount * (i + 1));

		}
	}

	private void generateRandomData(TupleWriter writer, int range, int length, int count) {
		TestGenerator testGen= new TestGenerator(range, length, count);
		ArrayList<Tuple> randData= testGen.generateTuples();

		writer.write(randData);
		writer.dump();
	}
}

