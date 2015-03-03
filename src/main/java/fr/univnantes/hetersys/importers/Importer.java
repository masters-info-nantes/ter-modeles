package fr.univnantes.hetersys.importers;

import java.io.File;
import java.text.ParseException;

import fr.univnantes.hetersys.graph.Node;

public abstract class Importer {
	
	protected Node graph;
	
	public Importer(){
		this.graph = null;
	}
	
	/**
	 * Contruct a graph from the given ressource file
	 * @param file file to read
	 * @return true if the importation succeed, otherwise false
	 */
	public abstract boolean load(File file) throws ParseException;
	
	public final Node getGraph(){
		return this.graph;
	}
}
