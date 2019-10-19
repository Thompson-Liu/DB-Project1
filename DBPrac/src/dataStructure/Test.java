package dataStructure;
import java.util.*;

public class Test {
	public static void main(String[] args) {
	    ArrayList<Integer> a = new ArrayList<Integer>();
	    ArrayList<Integer> b = new ArrayList<Integer>();
	    a.add(1);
	    b.add(1);
	    Tuple x = new Tuple(a);
	    Tuple y = new Tuple(b);
	    PriorityQueue<Integer> pi = new PriorityQueue();
	    HashMap<Tuple, Integer> n = new HashMap<Tuple, Integer>();
	    n.put(x, 1);
	    n.put(x, 2);
	    n.put(null, 3);
	    pi.add(3);
	    pi.add(3);
	    pi.add(null);
//	    for (Tuple k : n.keySet()) {
//	      System.out.println(n.get(k));
//	    }
	    for (Integer k : pi) {
	    	System.out.println(k);
	    }
	  }
}
