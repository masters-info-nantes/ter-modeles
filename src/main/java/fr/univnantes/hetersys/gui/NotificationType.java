package fr.univnantes.hetersys.gui;

/**
 * Qualification for notifications between Model
 * and Gui classes (MVC pattern)
 * @author jeremy
 */
public enum NotificationType {
	DOT_PARSE_ERROR,
	CHANNELS_MISSING,
	AUTOMATA_ALLREADY_EXISTS,
	EXPORT_FAILURE,
	EXPORT_SUCCESS
}
