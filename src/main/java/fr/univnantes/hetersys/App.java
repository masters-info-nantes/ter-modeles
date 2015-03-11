package fr.univnantes.hetersys;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import fr.univnantes.hetersys.exporters.Exporter;
import fr.univnantes.hetersys.exporters.UppaalExporter;
import fr.univnantes.hetersys.graph.Node;
import fr.univnantes.hetersys.gui.Gui;
import fr.univnantes.hetersys.importers.DotImporter;
import fr.univnantes.hetersys.importers.Importer;

public class App {
	public static void main(String[] args) 
	{
		//Gui fenetre = new Gui();
		//fenetre.display();		
		
		System.out.println("> Import dot file");
		
		Importer importer = new DotImporter();
		try {
			importer.load(new File("dotFile/test.dot"));
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
		
		System.out.println("> Export in uppaal project");		
		
		File uppaalFile = new File("jobbers.xml");
		Exporter exporter = new UppaalExporter("test");
		exporter.loadExistingFile(uppaalFile);
		
		try {
			exporter.updateFile(importer.getGraph());
		} 
		catch (IOException e) {
			System.err.println(e.getMessage());
		}			
	}	
}
