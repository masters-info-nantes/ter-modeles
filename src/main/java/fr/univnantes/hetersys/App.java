package fr.univnantes.hetersys;

import java.io.File;

import fr.univnantes.hetersys.exporters.Exporter;
import fr.univnantes.hetersys.exporters.XmlExporter;
import fr.univnantes.hetersys.importers.DotImporter;
import fr.univnantes.hetersys.importers.Importer;

public class App {
	public static void main(String[] args) 
	{
		Importer importer = new DotImporter();
		importer.load(new File("dotFile/test.dot"));

		System.out.println(importer.getGraph());
		
		Exporter exporter = new XmlExporter("test");
		exporter.generateProject(new File("test.xml"), importer.getGraph());	
	}	
}
