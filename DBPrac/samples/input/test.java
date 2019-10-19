import java.util.HashMap;
import java.util.*;

public class Tuple {

  private ArrayList<Integer> dataTuple;

  /**
   * Return a tuple obejct that represents the data stored in Arraylist
   * 
   * @param tuple The data that will be converted into an arraylist
   */
  public Tuple(ArrayList<Integer> tuple) {
    dataTuple = tuple;
  }

  public static void main(String[] args) {
    ArrayList<Integer> a = new ArrayList<Integer>();
    ArrayList<Integer> b = new ArrayList<Integer>();
    a.add(1);
    b.add(1);
    Tuple x = new Tuple(a);
    Tuple y = new Tuple(b);
    HashMap<Tuple, Integer> n = new HashMap<Tuple, Integer>();
    n.put(x, 1);
    n.put(y, 2);
    for (Tuple k : n.keySet()) {
      System.out.println(n.get(k));
    }
  }
}