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
			
			String[] tabLabel = loadLabel(nodeTab[i+1]);
			
			Node endNode;
			Arc inArc;
			if(tabLabel == null)
			{
				endNode = this.graph.findNode(new Node(nodeTab[i+1]));
				
				if(endNode == null){
					endNode = new Node(nodeTab[i+1]);
				}

				inArc = new Arc(this.nameArc, endNode);
			}
			else
			{
				endNode = this.graph.findNode(new Node(tabLabel[0]));
				
				if(endNode == null){
					endNode = new Node(tabLabel[0]);
				}
				
				inArc = new Arc(tabLabel[1], endNode);
			}							
		
			//Arc inArc = new Arc(this.nameArc, endNode);
			startNode.addOutputArc(inArc);
			
			Arc outArc = new Arc(this.nameArc, startNode);
			endNode.addInputArc(outArc);
		}
	}

	/* Si il ya un label -> retourne le nomd du noeud en case 0 et le nom de la transition en case 1*/
	private String[] loadLabel(String att)
	{
		System.out.println("start loadLabel : "+att);
		if(att.length()>1)
		{
			String[] tab = att.split("\\[");

			if(tab.length == 2)
			{
				String label = tab[1].replaceAll("]","");
				tab[1] = label.split("=")[1];
				return tab;
			}
		}	
		return null;
	}	
}


 
