package fr.univnantes.hetersys.gui;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import fr.univnantes.hetersys.exporters.UppaalExporter;
import fr.univnantes.hetersys.importers.DotImporter;
import fr.univnantes.hetersys.importers.Importer;

/**
 * Controls the program process.
 * Used by the gui
 * @author jeremy
 */
public class Model extends Observable {

	private String dotFile;
	private String uppaalFile;
	private String automataName;
	
	private Importer importer;
	private UppaalExporter exporter;
	
	/**
	 * Channels in automata and not in the uppaal project
	 */
	private Set<String> channelsToAdd;
	
	/**
	 * The last message which can be read by the gui
	 */
	private String lastMessage;
	
	/**
	 * Allows the gui to cut the process and continue
	 * without doing things allready done
	 */
	private int runStep;
	
	public Model(String dotFile, String uppaalFile, String automataName){
		this.dotFile = dotFile;
		this.uppaalFile = uppaalFile;
		this.automataName = automataName;		
		
		this.channelsToAdd = new HashSet<String>();
		this.lastMessage = "";
		this.runStep = 0;
	}
	
	/**
	 * Main loop which can be interrupted
	 * @see runStep
	 */
	public void run(){	
		
		// Import the dot file
		if(this.runStep <= 0){
			this.importer = new DotImporter();
			this.runStep = 1;
			
			try {
				this.importer.load(new File(this.dotFile));
			} 
			catch (ParseException e) {
				this.lastMessage = e.getMessage();
				this.setChanged();
				this.notifyObservers(NotificationType.DOT_PARSE_ERROR);
				return;
			}				
		}
		
		// Loads the uppaal project
		if(this.runStep <= 1){
			File uppaalFile = new File(this.uppaalFile);
			this.exporter = new UppaalExporter(this.automataName, this.importer.getGraph());
			this.exporter.loadExistingFile(uppaalFile);
			
			this.runStep = 2;
			
			if(this.exporter.checkAutomataAllreadyExists()){
				this.lastMessage = "Template \"" + this.automataName +"\" already exists, replace it?";
				this.setChanged();
				this.notifyObservers(NotificationType.AUTOMATA_ALLREADY_EXISTS);				
				return;	
			}		
		}
		
		// Check channels between dot and uppaal project
		if(this.runStep <= 2){
			this.channelsToAdd = this.exporter.checkChannelsExistence();			
			this.runStep = 3;			
			
			if(!channelsToAdd.isEmpty()){
				this.setChanged();
				this.notifyObservers(NotificationType.CHANNELS_MISSING);
				return;	
			}			
		}

		// Update the project files with automata
		try {
			this.exporter.updateFile();
		} 
		catch (IOException e) {
			this.lastMessage = e.getMessage();
			this.setChanged();
			this.notifyObservers(NotificationType.EXPORT_FAILURE);
			return;
		}	
		finally {
			this.runStep = 0;			
		}

		this.lastMessage = "The uppaal project \"" + this.uppaalFile + "\"\n" +
						   "has been updated with \"" + this.automataName + "\" automata";
		this.setChanged();
		this.notifyObservers(NotificationType.EXPORT_SUCCESS);
	}
	
	/**
	 * Continue the main method execution after asking question
	 * to the user with the gui
	 */
	public void continueRun(){
		this.run();
	}
	
	/**
	 * Add channels from the gui (in the automata) in
	 * the uppaal project
	 * @param channels Table of channels with name and true/false
	 * it must be added or not
	 */
	public void addChannels(Object[][] channels){
		for (int i = 0; i < channels.length; i++) {
			String channelName = (String) channels[i][0];
			boolean mustAdd = (Boolean) channels[i][1];
			
			if(mustAdd){
				this.exporter.addChannel(channelName);
			}
		}
	}
	
	// Getters for gui
	public boolean getAutomataChannelNecessity(){
		return !this.exporter.checkAutomataHasChannelLink();
	}
	
	public Set<String> getChannelsToAdd(){
		return this.channelsToAdd;
	}
	
	public String getLastMessage(){
		return this.lastMessage;
	}
}
