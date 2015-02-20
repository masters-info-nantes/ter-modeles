package fr.univnantes.hetersys.graph;

import java.util.ArrayList;
import java.util.LinkedList;

/** Class for represent {@code Node}.
 * 
 */
public class Node extends Element {
	private String name;
	private ArrayList<Arc> arcs;
	
	/** Constructs a new {@code Node}.
	 * 
	 * @param id the id of the new {@code Node}. @see fr.univnantes.controlflowgraph.Element#Element(int).
	 * @param name the name of the new {@code Node}. {@code name} is the label to print on the {@code Node}.
	 */
	public Node(int id, String name) {
		super(id);
		init(name);
	}
	
	/** Constructs a new {@code Node} with a generated unique id.
	 * 
	 * @param name the name of the new {@code Node}. {@code name} is the label to print on the {@code Node}.
	 */
	public Node(String name){
		super();
		init(name);
	}
	
	private void init(String name) {
		this.name = name;
		arcs = new ArrayList<Arc>();
	}
	
	/** Gets the {@code name} of this {@code Node}.
	 * 
	 * @return The {@code name} of this {@code Node}.
	 */
	public String getName() {
		return this.name;
	}
	
	/** Gets the list of @see fr.univnantes.controlflowgraph.Arc whose start from this {@code Node}.
	 * 
	 * @return The list of arc whose start from this {@code Node}.
	 */
	public ArrayList<Arc> getArcs() {
		return this.arcs;
	}
	
	/** Add a new Arc to this {@code Node}.
	 * 
	 * @param a the new arc to add. Doing this implies this arc start from this {@code Node}.
	 */
	public void addArc(Arc a) {
		this.arcs.add(a);
	}
	
	/** Gets the finality state of this {@code Node}.
	 * 
	 * @return {@code true} if this {@code Node} is final (so does not have any exiting {@code Arc}); {@code false} otherwise.
	 */
	public boolean isFinal() {
		return this.arcs.isEmpty();
	}
	
	public String toString() {
		return this.toString(0);
	}
	
	public String toString(int depth){
		StringBuilder str = new StringBuilder();
		
		for (int i = 0; i < depth; i++) {
			str.append("\t");
		}
		
		str.append(super.toString());
		str.append(",name="+this.name);
		str.append(",arcs["+this.arcs.size()+"]{\n");
		
		for (Arc arc :arcs) {
			str.append(arc.getNext().toString(depth+1) + "\n");
		}
		
		for (int i = 0; i < depth; i++) {
			str.append("\t");
		}		
		str.append("}\n");
		
		return str.toString();		
	}
	
	/** Finds a node in the graph starting by this {@code Node}.
	 * 
	 * @param toFind the {@code Node} to find
	 * 
	 * @return a {@code Node} whose is equals to the {@code Node} in parameter if a {@code Node} correspond to it in graph starting by this {@code Node}; {@code null} otherwise.
	 */
	public Node findNode(Node toFind){

		if(toFind == null){
			return null;
		}
		else if(this.equals(toFind)){
			return this;
		}

		LinkedList<Node> backList = new LinkedList<Node>();
		LinkedList<Node> queue = new LinkedList<Node>();
		
		queue.add(this);
		Node cur = null, next = null;
		
		while(!queue.isEmpty()) {
			cur = queue.remove();
			backList.add(cur);
			
			for(Arc a : cur.getArcs()) {
				next = a.getNext();
				
				if(next.equals(toFind)) {
					return next;
				}
				if(!queue.contains(next) && !backList.contains(next)) {
					queue.add(next);
				}
			}
		}
		return null;
	}
	
	/** Finds a node in the graph starting by this {@code Node}.
	 * 
	 * @param id the id of the {@code Node} to find
	 * 
	 * @return a {@code Node} whose have the same id than {@code Node} in parameter if a {@code Node} correspond to it in graph starting by this {@code Node}; {@code null} otherwise.
	 */
	public Node findNode(int id) {
		return findNode(new Node(id,""));
	}
	
	public boolean equals(Node n){		
		if(this == n){
			return true;
		}
		else {
			return super.equals(n) || this.name.equals(n.name);
		}
	}
}