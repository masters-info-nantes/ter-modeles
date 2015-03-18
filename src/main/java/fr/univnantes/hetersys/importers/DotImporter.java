package fr.univnantes.hetersys.importers;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.univnantes.hetersys.graph.Arc;
import fr.univnantes.hetersys.graph.Node;

public class DotImporter extends Importer
{
	/**
	 * Number of current line which is analyzed (begins at 1)
	 * Used if an exception is raised
	 * @see load
	 */
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

		// Analyse each lines separatly
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
	
	/**
	 * Determinates if a line contains nodes and arcs
	 * @param line Line being analyzed
	 * @return true if it contains nodes and arcs, false otherwise
	 */
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
	
	/**
	 * Analyze one line of dot language and generate the graph
	 * in the internal representation
	 * 
	 * @see Node
	 * 
	 * @param line Line being analyzed
	 * @throws ParseException Malformed dot line
	 */
	private void loadLine(String line) throws ParseException
	{						
		String[] nodeTab = line.replaceAll(";", "")
							   .replaceAll(" ", "") // trim not work
							   .trim()
							   .split("->");
		
		// The short expression
		// a -> b -> c[label=foo]
		// means each arcs have the label "foo"
		String[] lastParseNode = parseDotNode(nodeTab[nodeTab.length-1]);
		String label = "";
		
		if(lastParseNode.length > 1)
		{
			label = lastParseNode[1];
		}
		
		// In dot some short expression exists like:  
		// a -> b -> c 
		// Analyze each of them here
		for(int i = 0; i < nodeTab.length - 1; i++)
		{	
			// Parse node to get name 
			String startNodeTab = this.nodeName(nodeTab[i]),
					 endNodeTab = this.nodeName(nodeTab[i+1]);
			
			if(this.graph == null){
				this.graph = new Node(startNodeTab);
			}
			
			// Retrieve start and end node in the graph
			// Creates them if they don't exist
			Node startNode = this.graph.findNode(new Node(startNodeTab));	
			if(startNode == null){
				startNode = new Node(startNodeTab);
			}						
			
			Node endNode = this.graph.findNode(new Node(endNodeTab));
			if(endNode == null){
				endNode = new Node(endNodeTab);
			}
			
			// Create arcs and update graph
			Arc inArc = new Arc(label, endNode);
			startNode.addOutputArc(inArc);
			
			Arc outArc = new Arc(label, startNode);
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
		String labelPattern = "[^\\]]+";
		Pattern pattern = Pattern.compile("(\\w+)(?:\\[label=(" + labelPattern + "|\"" + labelPattern + "\")\\])?");
		Matcher matcher = pattern.matcher(node);

		if(!matcher.matches()){
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

	private String nodeName(String node) throws ParseException
	{
		String[] parseNode = parseDotNode(node);
		return parseNode[0];
	}
	
	/**
	 * Parse a dot label (on transitions)
	 * @param label Label to parse in string format
	 * @return One string if the label does not contains a channel, 
	 * otherwise two strings (channel and expression)
	 */
	public static String[] parseDotLabel(String label){
		// Pattern: <channel id><operator ? or !><expression>
		
		// If the expression contains "?" or "!" it will be not splitted
		String[] parts = label.replaceAll("\"", "").split("\\?|!", 2);
		
		return parts;
	}	
}


 
