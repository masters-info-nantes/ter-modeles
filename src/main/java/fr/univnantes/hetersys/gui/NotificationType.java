package fr.univnantes.hetersys.gui;

/**
 * Qualification for notifications between Model
 * and Gui classes (MVC pattern)
 * @author jeremy
 */
public enum NotificationType {
	
	/**
	 * The input dot file is malformed
	 * @see Importer
	 */
	DOT_PARSE_ERROR,
	
	/**
	 * Some channels in the input automata
	 * are not in the uppaal project
	 * @see Exporter, checkChannelsExistence
	 */
	CHANNELS_MISSING,
	
	/**
	 * An automata with the same name as the 
	 * input one allready exists in the project
	 * @see Exporter, checkAutomataAllreadyExists
	 */
	AUTOMATA_ALLREADY_EXISTS,
	
	/**
	 * Problems during saving changes to the uppaal 
	 * project file
	 * @see Exporter, updateFile
	 */
	EXPORT_FAILURE,
	
	/**
	 * Export automata to the uppaal project 
	 * has been ended without troubles
	 * @see Exporter, updateFile
	 */
	EXPORT_SUCCESS
}
