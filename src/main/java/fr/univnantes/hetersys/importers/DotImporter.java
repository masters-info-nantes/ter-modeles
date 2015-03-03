package fr.univnantes.hetersys.importers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;

import fr.univnantes.hetersys.graph.Arc;
import fr.univnantes.hetersys.graph.Node;

public class DotImporter extends Importer
{
	
	private int currentLine;

	public DotImporter()
	{
		super();
		this.currentLine = 1;
	}

	@Override
	public boolean load(File file) throws ParseException
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
		    System.out.println("----->"+line);
			
		    if(this.isUsefulLine(line))
		    {
		    	this.loadLine(line);
		    }
		    
		    this.currentLine++;
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
	
	private void loadLine(String line) throws ParseException
	{						
		String[] nodeTab = line.replaceAll(";", "")
							   .replaceAll(" ", "") // trim not work
							   .trim()
							   .split("->");
		
		for(int i = 0; i < nodeTab.length - 1; i++)
		{	
			// Parse node to get name and label separated
			String[] startNodeTab = this.parseDotNode(nodeTab[i]),
					 endNodeTab   = this.parseDotNode(nodeTab[i+1]);
			
			// Don't use label on start node
			if(startNodeTab.length > 1 ){
				nodeTab[i] = startNodeTab[0];
			}
			
			if(this.graph == null){
				this.graph = new Node(nodeTab[i]);
			}
			
			// Retrieve start and end node in the graph
			// Creates them if they don't exist
			Node startNode = this.graph.findNode(new Node(nodeTab[i]));	
			if(startNode == null){
				startNode = new Node(nodeTab[i]);
			}						
			
			Node endNode = this.graph.findNode(new Node(endNodeTab[0]));
			if(endNode == null){
				endNode = new Node(endNodeTab[0]);
			}
			
			// Create arcs and update graph
			Arc inArc = new Arc((endNodeTab.length > 1) ? endNodeTab[1] : "", endNode);
			startNode.addOutputArc(inArc);
			
			Arc outArc = new Arc("", startNode);
			endNode.addInputArc(outArc);
		}
	}

	/**
	 * Parse a dot node 
	 * @param node Node in string format
	 * @return node Node name and label value
	 * @warning If no label, the table size is 1
	 * @throws ParseException The node is not well formated
	 */
	private String[] parseDotNode(String node) throws ParseException
	{		
		// ?: = not capture the group
		Pattern pattern = Pattern.compile("(\\w*)(?:\\[label=(\\w*)\\])?");
		Matcher matcher = pattern.matcher(node);
		
		if(!matcher.find()){
			throw new ParseException("Dot element not recognized: " + node, this.currentLine);
		}
		
		List<String> parsedNode = new ArrayList<String>();
		parsedNode.add(matcher.group(1));
		
		String labelVal = matcher.group(2);
		if(labelVal != null){
			parsedNode.add(labelVal);
		}
		
		return (String[]) parsedNode.toArray(new String[parsedNode.size()]);
	}	
}


 
