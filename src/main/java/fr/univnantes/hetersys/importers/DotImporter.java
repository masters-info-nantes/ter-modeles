package fr.univnantes.hetersys.importers;

import java.util.Scanner; 
import java.io.File;
import java.io.FileNotFoundException;

import fr.univnantes.hetersys.graph.Arc;
import fr.univnantes.hetersys.graph.Node;

public class DotImporter extends Importer
{
	private String nameArc; 

	public DotImporter()
	{
		super();
		
		// pour le moment les noms des arcs ne sont pas gérés		
		this.nameArc = "(pas de nom)"; 
	}

	@Override
	public boolean load(File file)
	{
		Scanner scanner;
		try {
			scanner = new Scanner(file);
		} 
		catch (FileNotFoundException e) {
			System.err.println("Cannot access to " + file.getAbsolutePath());
			return false;
		}

		// On boucle sur chaque champ detecté
		while (scanner.hasNextLine()) 
		{
		    String line = scanner.nextLine();			
		    System.out.println(line);
			
		    if(this.isUsefulLine(line))
		    {
		    	this.loadLine(line);
		    }
		}
		 
		scanner.close();
		return true;
	}
	
	private boolean isUsefulLine(String line)
	{
		// Graph definition line
		if(line.contains("digraph") || line.contains("graph")){
			return false;
		}		
		
		// End of graph definition
		else if("}".equals(line.trim()))
		{
			return false;
		}

		return true;
	}
	
	private void loadLine(String line)
	{						
		String[] nodeTab = line.replaceAll(";", "")
							   .replaceAll(" ", "") // trim not work
							   .trim()
							   .split("->");
		
		for(int i = 0; i < nodeTab.length - 1; i++)
		{	
			if(this.graph == null){
				this.graph = new Node(nodeTab[i]);
			}

			Node startNode = this.graph.findNode(new Node(nodeTab[i]));	
			if(startNode == null){
				startNode = new Node(nodeTab[i]);
			}
			
			Node endNode = this.graph.findNode(new Node(nodeTab[i+1]));
			if(endNode == null){
				endNode = new Node(nodeTab[i+1]);
			}						
			
			Arc arc = new Arc(this.nameArc, endNode);
			startNode.addArc(arc);
		}
	}
}


 
