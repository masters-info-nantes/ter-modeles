package fr.univnantes.hetersys;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Observable;

import fr.univnantes.hetersys.exporters.Exporter;
import fr.univnantes.hetersys.exporters.UppaalExporter;
import fr.univnantes.hetersys.importers.DotImporter;
import fr.univnantes.hetersys.importers.Importer;

public class Model extends Observable {

	private String dotFile;
	private String uppaalFile;
	private String automataName;
	
	public Model(String dotFile, String uppaalFile, String automataName){
		this.dotFile = dotFile;
		this.uppaalFile = uppaalFile;
		this.automataName = automataName;
	}
	
	public void run(){		
		Importer importer = new DotImporter();
		try {
			importer.load(new File(this.dotFile));
		} 
		catch (ParseException e) {
			this.setChanged();
			this.notifyObservers(e.getMessage());
			return;
		}				
		
		File uppaalFile = new File(this.uppaalFile);
		Exporter exporter = new UppaalExporter(this.automataName);
		exporter.loadExistingFile(uppaalFile);
		
		try {
			exporter.updateFile(importer.getGraph());
		} 
		catch (IOException e) {
			this.setChanged();
			this.notifyObservers(e.getMessage());	
			return;
		}
	}
}
