package dataStructure;

import java.util.ArrayList;
import java.util.Comparator;

public class TupleComparator implements Comparator<Tuple> {
	private ArrayList<String> order;
	private ArrayList<String> schema;

	public TupleComparator(ArrayList<String> colList,ArrayList<String> schema) {
		this.schema = schema;
		// the new order of sorted data
		this.order = new ArrayList<String>();
		for(String priorityCol : colList) {
			this.order.add(priorityCol);
		}
		for(String col:schema) {
			if(!colList.contains(col)) {
				this.order.add(col);
			}
		}
	}

	// compare two tuples
	@Override
	public int compare(Tuple tup1,Tuple tup2) {
		if (tup1==null) {
			return -1;
		}
		if (tup2==null) {
			return 1;
		}
		int result= 0;
		int ptr= 0;
		while (ptr < this.order.size() && result == 0) {
			result= tup1.getTuple().get(schema.indexOf(this.order.get(ptr))) -
					tup2.getTuple().get(schema.indexOf(this.order.get(ptr)));
			ptr+= 1;
		}
		return result;
	}
}


