package fr.univnantes.hetersys.exporters;

import java.io.IOException;

import fr.univnantes.hetersys.graph.Node;

public interface Exporter {
	public void generateProject(Node graph) throws IOException;
}
