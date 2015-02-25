package fr.univnantes.hetersys.exporters;

import java.io.File;

import fr.univnantes.hetersys.graph.Node;

public interface Exporter {
	public void generateProject(File file, Node graph);

	public void insertTemplate(String string, Node graph);
}
