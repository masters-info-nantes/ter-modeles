package fr.univnantes.hetersys.exporters;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public interface Exporter {	
	public static final String fileExtension = ".xml";	
	
	public void loadExistingFile(File file);
	
	/**
	 * Two automata can't have the same name, this method checks
	 * if one automata from the project has the name
	 * given by the user
	 * @return true if another automata has the same name, false otherwise
	 */	
	public boolean checkAutomataAllreadyExists();
	
	/**
	 * Returns all channels found in the automata
	 * @return Channels from automata
	 */	
	public Set<String> checkChannelsExistence();
	
	/**
	 * Check if the automata has common at least one common
	 * channel with those in the project
	 * @return true if at least one common channel, false otherwise
	 */	
	public boolean checkAutomataHasChannelLink();
	
	/**
	 * Adds the given channel to the project
	 * @param channel Channel name to insert
	 */	
	public void addChannel(String channel);
	
	/**
	 * Returns the name of all automata found in
	 * the project
	 * @return All automata names
	 */	
	public String[] getAutomataList();		
	
	public void updateFile() throws IOException;
}