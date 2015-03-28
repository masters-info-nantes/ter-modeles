package fr.univnantes.hetersys.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Node extends Element {
	private String name;
	private ArrayList<Arc> inputArcs;
	private ArrayList<Arc> outputArcs;
	
	public Node(int id, String name) {
		super(id);
		init(name);
	}
	
	public Node(String name){
		super();
		init(name);
	}
	
	private void init(String name) {
		this.name = name;
		
		this.inputArcs = new ArrayList<Arc>();
		this.outputArcs = new ArrayList<Arc>();
	}	
		
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
			
			List<Arc> allArcs = new ArrayList<Arc>();
			allArcs.addAll(cur.getOutputArcs());			
			allArcs.addAll(cur.getInputArcs());
			
			for(Arc a : allArcs) {
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
	
	public Node findNode(int id) {
		return findNode(new Node(id,""));
	}
	
	public List<Node> getEntryPoints(){

		LinkedList<Node> entryPoints = new LinkedList<Node>();
		LinkedList<Node> backList = new LinkedList<Node>();
		LinkedList<Node> queue = new LinkedList<Node>();
		
		queue.add(this);
		Node cur = null, next = null;
		
		while(!queue.isEmpty()) {
			cur = queue.remove();		
			backList.add(cur);
			
			// Entry points have no input arcs
			if(cur.getInputArcs().isEmpty()){
				entryPoints.add(cur);
			}
			
			List<Arc> allArcs = new ArrayList<Arc>();
			allArcs.addAll(cur.getOutputArcs());			
			allArcs.addAll(cur.getInputArcs());
			
			for(Arc a : allArcs) {
				next = a.getNext();
				
				if(!queue.contains(next) && !backList.contains(next)) {
					queue.add(next);
				}
			}
		}
		
		// Assume the root is an entry point
		// Problem: a -> b, b -> a 
		// says "a" is not an entry point because is 
		// got input arcs
		if(entryPoints.isEmpty()){
			entryPoints.add(this);
		}
		
		return entryPoints;
	}	
	
	public void addInputArc(Arc a) {
		this.inputArcs.add(a);
	}

	public void addOutputArc(Arc a) {
		this.outputArcs.add(a);
	}	
	
	/*-------- Getters --------------------*/
	public String getName() {
		return this.name;
	}
	
	public ArrayList<Arc> getInputArcs() {
		return this.inputArcs;
	}

	public ArrayList<Arc> getOutputArcs() {
		return this.outputArcs;
	}		
	
	public boolean isFinal() {
		return this.outputArcs.isEmpty();
	}	
	
	/*-------- Inherited methods ----------*/
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		
		LinkedList<Node> backList = new LinkedList<Node>();
		LinkedList<Node> queue = new LinkedList<Node>();
		
		queue.add(this);
		Node cur = null, next = null;
		
		while(!queue.isEmpty()) {
			cur = queue.remove();
			backList.add(cur);
			
			str.append(cur.getName());
			List<Arc> allArcs = new ArrayList<Arc>();
			allArcs.addAll(cur.getOutputArcs());			
			allArcs.addAll(cur.getInputArcs());
			
			for(Arc a : allArcs) {
				next = a.getNext();
				if(!queue.contains(next) && !backList.contains(next)) {
					queue.add(next);
				}
			}
		}

		return str.toString();		
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