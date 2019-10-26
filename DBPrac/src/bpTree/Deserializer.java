package bpTree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Deserializer {

	private FileInputStream fin;
	private FileChannel fc;
	private ByteBuffer buffer;

	public Deserializer(String indexFile) {

		try {
			// Initialize reader, as in BinaryTupleReader
			fin= new FileInputStream(indexFile);
			fc= fin.getChannel();
			buffer= ByteBuffer.allocate(4096);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/** Find the rid of the first key that matches the scan condition */
	public Integer[] getFirstKeyAddress(int lowKey) {
		int loc= initialDescent(lowKey);
		return null;
	}

	/** Performs a DFS to find the location of low key. */
	private int initialDescent(int lowKey) {
		try {
			buffer.clear();
			fc.read(buffer);
			int rootInd= buffer.getInt(0); // first int on the header page is the address of the root
			int numLeaves= buffer.getInt(4); // # of leaves in the tree
			int order= buffer.getInt(8); // order of the tree

			int startInd= rootInd;
			while (true) { // iterative depth-first search
				buffer.clear();
				fc.position(startInd * 4096);
				fc.read(buffer);
				int flag= buffer.getInt(0); // 1: index node; 0: leaf node
				if (flag == 0) return startInd; // leaf node
				int numKeys= buffer.getInt(4); // # keys in the node
				int prevKey= Integer.MAX_VALUE;
				int curKey;
				int pos= 0;
				while (pos < numKeys) {
					curKey= buffer.getInt(8 + 4 * pos); // actual keys in the node start from index 8
					if (lowKey == prevKey || (lowKey > prevKey && lowKey < curKey)) {
						break;
					}
					prevKey= curKey;
					pos++ ;
				}
				startInd= buffer.getInt((2 + numKeys) * 4 + (pos * 4));

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
