package fr.univnantes.hetersys;

import java.io.File;
import java.io.IOException;

import fr.univnantes.hetersys.exporters.Exporter;
import fr.univnantes.hetersys.exporters.UppaalExporter;
import fr.univnantes.hetersys.graph.Node;
import fr.univnantes.hetersys.importers.DotImporter;
import fr.univnantes.hetersys.importers.Importer;

public class App {
	public static void main(String[] args) 
	{
		System.out.println("> Import dot file");
		
		Importer importer = new DotImporter();
		importer.load(new File("dotFile/test.dot"));
		
		System.out.println("> Export in uppaal project");		
		File uppaalFile = new File("jobbers.xml");
		Exporter exporter = new UppaalExporter("test", uppaalFile);
		try {
			exporter.generateProject(importer.getGraph());
		} 
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		System.out.println("> Test: graph entry points");
		for (Node node : importer.getGraph().getEntryPoints()) {
			System.out.println(node.getName());
		}		
	}	
}
