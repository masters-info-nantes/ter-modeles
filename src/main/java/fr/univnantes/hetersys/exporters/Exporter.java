package fr.univnantes.hetersys.exporters;
import java.io.File;
import java.io.IOException;
import fr.univnantes.hetersys.graph.Node;
public interface Exporter {
	public void loadExistingFile(File file);
	public void updateFile() throws IOException;
}