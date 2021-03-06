package bpTree;

import java.util.ArrayList;

/** The class that represents a B+-tree node. */
public abstract class Node {

	public abstract int getType();

	public abstract int getNumElement();

	public abstract int getPage();

	public abstract int leastKey();

	public abstract ArrayList<Node> getChildren();

}
