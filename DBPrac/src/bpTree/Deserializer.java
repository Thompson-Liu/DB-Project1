package bpTree;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Deserializer {

	private FileInputStream fin;
	private FileChannel fc;
	private ByteBuffer buffer;
	private int rootInd;
	private int numLeaves;
	private int startLeaf;

	/** The deserializer deserializes a serialized B+-tree, which is stored in an index file.
	 * 
	 * @param indexFile: the index file available */
	public Deserializer(String indexFile) {

		try {
			// Initialize reader, as in BinaryTupleReader
			fin= new FileInputStream(indexFile);
			fc= fin.getChannel();
			buffer= ByteBuffer.allocate(4096);
			fc.read(buffer);
			rootInd= buffer.getInt(0); // first int on the header page is the address of the root
			numLeaves= buffer.getInt(4); // # of leaves in the tree
			startLeaf= 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/** Find the rid of the first key that falls within the range of the scan.
	 * 
	 * @param lowKey: the lower bound of the range of the scan (inclusive)
	 * @param highKey: the higher bound of the range of the scan (inclusive) */
	public int[] getRid(int lowKey, int highKey) {
		startLeaf= initialDescent(lowKey);
		try {
			buffer.clear();
			fc.position(startLeaf * 4096);
			fc.read(buffer);
			int flag= buffer.getInt(0);
			assert (flag == 0); // must be leaf nodes
			int numElements= buffer.getInt(4);
			int pos= 8; // starting pos
			// A data entry contains: k, # of rids, (p,t) for each rid
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

	/** Performs a root-to-leaf descent to find the location of the first key that's greater than low
	 * key. */
	private int initialDescent(int lowKey) {
		try {
			int startInd= rootInd; // Start from root
			while (true) { // iterative depth-first search
				buffer.clear(); // clear existing buffer
				fc.position(startInd * 4096); // set file channel
				fc.read(buffer);
				int flag= buffer.getInt(0); // 1: index node; 0: leaf node
				if (flag == 0) return startInd; // found the leaf node
				int numKeys= buffer.getInt(4); // # of keys in the node
				int prevKey= Integer.MAX_VALUE;
				int curKey;
				int pos= 0;
				while (pos < numKeys) {
					curKey= buffer.getInt(8 + 4 * pos); // actual keys in the node start from index 8
					if (lowKey == prevKey || (lowKey > prevKey && lowKey < curKey)) { // Found the node where lowKey is
																					  // or where it would be
						break;
					}
					prevKey= curKey;
					pos++ ;
				}
				startInd= buffer.getInt((2 + numKeys + pos) * 4); // metadata (2), keys (numKeys), pos to be
																  // skipped (pos), multiply by 4 to get bytes

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public int getNumLeaves() {
		return numLeaves;
	}

	public int getStartLeaf() {
		return startLeaf;
	}

}
