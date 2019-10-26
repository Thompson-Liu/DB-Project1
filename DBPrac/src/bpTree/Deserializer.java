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
	public int[] getRid(int lowKey, int highKey) {
		int loc= initialDescent(lowKey);
		try {
			buffer.clear();
			fc.position(loc * 4096);
			fc.read(buffer);
			int flag= buffer.getInt(0);
			if (flag == 1) return null; // non-leaf nodes
			int numElements= buffer.getInt(4);
			int pos= 8; // starting pos
			// A data entry: k, # of rids, (p,t) for each rid
			for (int i= 0; i < numElements; i++ ) {
				int key= buffer.getInt(pos);
				int numRids= buffer.getInt(pos + 4);
				if (key >= lowKey &&
					key <= highKey) { return new int[] { buffer.getInt(pos + 8), buffer.getInt(pos + 12) }; }
				pos+= 8 + numRids * 8; // skip the first two metadata and numRids rids (each rid is 8 bytes)
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
					if (lowKey == prevKey || (lowKey > prevKey && lowKey < curKey)) { // Found the node where lowKey is
						break;
					}
					prevKey= curKey;
					pos++ ;
				}
				startInd= buffer.getInt((2 + numKeys) * 4 + (pos * 4)); // metadata (2), keys (numKeys), pos to be
																	    // skipped (pos), multiply by 4 to get bytes

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
