package fr.univnantes.hetersys.exporters;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.univnantes.hetersys.graph.Arc;
import fr.univnantes.hetersys.graph.Node;
import fr.univnantes.hetersys.importers.DotImporter;
import fr.univnantes.hetersys.importers.Importer;
public class UppaalExporter implements Exporter
{
	private Document document;
	/**
	 * Uppaal project file
	 */
	private File uppaalProject;
	/**
	 * Channels found in the uppaal project
	 * @see loadChannels
	 */
	private Set<String> channels;
	
	/**
	 * Name of automata found in uppaal project
	 */
	private Set<String> projectAutomataNames;
	/**
	 * Fill if an automata with the same name has been
	 * found in the uppaal project
	 */
	private Element existingTemplate;
	/**
	 * Set to true if at least one channel from the graph
	 * is in the Uppaal project
	 * @see checkChannelsExistence
	 * @see updateFile
	 */
	private boolean channelLink;
	/**
	 * Set to true if the automate contains a channel
	 */
	private boolean channelsInAutomata;
	/**
	 * Name of the automata to insert, it must be different
	 * from others allready in the uppaal project
	 */
	private String automataName;
	/**
	 * Graph which represents the dot automata
	 */
	private Node graph;
	public UppaalExporter(String automataName, Node graph)
	{
		super();
		
		this.automataName = automataName;
		this.graph = graph;
		this.channels = new HashSet<String>();
		this.projectAutomataNames = new HashSet<String>();
		this.existingTemplate = null;
		this.channelLink = false;
		this.channelsInAutomata = false;
		this.uppaalProject = null;
		this.document = null;		
	}
	@Override
	public void loadExistingFile(File file) {
		this.uppaalProject = file;
		// Load project structure
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.document = docBuilder.parse(this.uppaalProject);
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println("Cannot create XML document");
		}
		// Load channels
		this.loadChannels();
	}
	@Override
	public void updateFile() throws IOException
	{
		// Checks before export
		if(this.uppaalProject == null){
			throw new IOException("Uppaal project not yet loaded, please call loadExistingProject method.");
		}
		// Load xml structure
		Element root = this.document.getDocumentElement();
		Element template = generateTemplate(this.graph);
		// Search system element to insert the template before
		Element system = null;
		NodeList rootChildren = root.getChildNodes();
		for (int i = 0; i < rootChildren.getLength(); i++){
			org.w3c.dom.Node n = rootChildren.item(i);
			if(n.getNodeName().equals("system")){
				system = (Element) n;
			}
		}
		// Check project format
		if(system == null){
			throw new IOException("Cannot find <system> element in " + this.uppaalProject.getName());
		}
		// Update project file
		if(this.existingTemplate == null){
			root.insertBefore(template, system);
		}
		else {
			root.replaceChild(template, this.existingTemplate);
		}
		System.out.println("\tTemplate "+ this.automataName + " inserted with success!\n");
		// Save changes to uppaal project file
		this.writeInFile();
		System.out.println(
				"> The uppaal project \"" + this.uppaalProject.getName() +
				"\" has been updated with \"" + this.automataName + "\" automata"
				);
	}
	/*------------------------------------- Questions to user -------------------------------------*/
	@Override
	public Set<String> checkChannelsExistence(){
		return this.checkChannelsExistence(this.graph, new ArrayList<Node>());
	}
	private Set<String> checkChannelsExistence(Node node, List<Node> visitedNodes){
		Set<String> channelsToAdd = new HashSet<String>();
		for(Arc arc: node.getOutputArcs()){
			String[] labelParse = DotImporter.parseDotLabel(arc.getName());
			// Channel found in arc label
			if(labelParse.length == 2){
				this.channelsInAutomata = true;
				String channel = labelParse[0];
				if(!this.channels.contains(channel)){
					channelsToAdd.add(channel);
				}
				else {
					this.channelLink = true;
				}
			}
			visitedNodes.add(node);
			if(!visitedNodes.contains(arc.getNext())){
				channelsToAdd.addAll(this.checkChannelsExistence(arc.getNext(), visitedNodes));
			}
		}
		for (Arc arc : node.getInputArcs()) {
			if(!visitedNodes.contains(arc.getNext())){
				channelsToAdd.addAll(this.checkChannelsExistence(arc.getNext(), visitedNodes));
			}
		}
		return channelsToAdd;
	}

	@Override
	public boolean checkAutomataHasChannelLink(){
		return this.channelsInAutomata && this.channelLink;
	}

	@Override
	public boolean checkAutomataAllreadyExists(){
		Element root = this.document.getDocumentElement();
		NodeList rootChildren = root.getChildNodes();
		// "template" represents one automata which is a direct child
		// of the root
		// It's sub element "name" contains the name of the automata
		for (int i = 0; i < rootChildren.getLength(); i++){
			org.w3c.dom.Node n = rootChildren.item(i);
			// Looking for template
			if(n.getNodeName().equals("template")){
				Element currentTemplate = (Element) n;
				// Looking for name
				NodeList templateChildren = currentTemplate.getElementsByTagName("name");
				for(int j = 0; j < templateChildren.getLength(); j++){
					// The "location" subelement has also a "name" sub element, so check
					Element current = (Element) templateChildren.item(j);
					if(current.getParentNode().getNodeName().equals("template")){
						this.projectAutomataNames.add(current.getTextContent());
						// The template allready exists and has been found
						if(current.getTextContent().equals(this.automataName)){
							this.existingTemplate = currentTemplate;
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	/*-------------------------------- Load and update project file -------------------------------*/	
	/**
	 * Loads all channels contained in the uppaal project
	 */
	private void loadChannels(){
		
		Pattern pattern = Pattern.compile("chan\\s+(?:\\w+\\s*,\\s*)*\\w+\\s*;");
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		
		try {
			XPathExpression expr = xpath.compile("nta/declaration");
			String string = (String) expr.evaluate(this.document, XPathConstants.STRING);
			Matcher matcher = pattern.matcher(string);
			
			while(matcher.find()){		
				// Remove begin and end to keep channnels
				String cleaned = matcher.group().
								 replaceFirst("chan\\s+", "").
								 replaceFirst("\\s*;", "")
				;
				
				// Split on comma and take care of spaces
				String[] splitChans = cleaned.split("\\s*,\\s*");
				channels.addAll(Arrays.asList(splitChans));
			}

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void addChannel(String channel){
		this.channels.add(channel);
		String chan = "chan "+channel+";\n";
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		Element declaration = null;
		
		try {
			declaration = (Element) xpath.compile("nta/declaration").evaluate(this.document, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		declaration.appendChild(document.createTextNode(chan));
		writeInFile();
	}
	/**
	 * All changed are made in the loaded document structure
	 * This method make theses changes real in the uppaal project file
	 */
	private void writeInFile(){
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			Source src = new DOMSource(this.document);
			Result result = new StreamResult(this.uppaalProject);
			transformer.transform(src, result);
		}
		catch (TransformerFactoryConfigurationError | TransformerException e) {
			System.err.println("Cannot write generated XML to " + this.uppaalProject.getAbsolutePath());
		}
	}
	/*--------------------------------- Generate parts of automata --------------------------------*/
	/**
	 * Changes the graph internal representation in xml uppaal
	 * In uppaal xml: "template" element contains one automata
	 * @param graph Graph which represents an automata
	 * @return Generated xml element
	 */
	private Element generateTemplate(Node graph)
	{
		Element tpl = this.document.createElement("template"),
				name = this.document.createElement("name");
		name.setAttribute("x", "0");
		name.setAttribute("x", "0");
		name.appendChild(this.document.createTextNode(this.automataName));
		tpl.appendChild(name);
		this.generateDeclaration(tpl);
		this.generateNodes(tpl, this.graph, new ArrayList<Node>(), 0);
		this.generateEntryNode(tpl, this.graph);
		this.generateTransitions(tpl, this.graph, new ArrayList<Node>());
		return tpl;
	}
	
	/**
	 * Add graph entry point into uppaal project
	 * 
	 * <init ref="a"/>
	 * 
	 * @param parentElt Element which represents the automata (template element)
	 * @param graph Graph which represents an automata
	 */
	private void generateEntryNode(Element parentElt, Node graph) {
		List<Node> entryPoints = graph.getEntryPoints();

		Element initElt = this.document.createElement("init");
		initElt.setAttribute("ref", entryPoints.get(0).getName());
		parentElt.appendChild(initElt);		
	}
	/**
	 * Browse the automata graph and turn nodes into xml uppaal nodes
	 *
	 * <location id="a" x="0" y="0">
	 * <name>a</name>
	 * </location>
	 *
	 * @see graph
	 *
	 * @param parentElt Element which represents the automata (template element)
	 * @param node Current node to transform (for recursivity)
	 * @param visitedNodes Nodes allready visited (for recursivity)
	 * @param compteur Avoid setting all nodes in the position
	 */
	private void generateNodes(Element parentElt, Node node, List<Node> visitedNodes, int compteur)
	{
		if(visitedNodes.contains(node)){
			return;
		}
		visitedNodes.add(node);
		Element nodeElt = this.document.createElement("location");
		nodeElt.setAttribute("id", node.getName());
		nodeElt.setAttribute("x", String.valueOf(compteur));
		nodeElt.setAttribute("y", String.valueOf(compteur));
		Element nameElt = this.document.createElement("name");
		nameElt.appendChild(this.document.createTextNode(node.getName()));
		nodeElt.appendChild(nameElt);
		parentElt.appendChild(nodeElt);
		List<Arc> allArcs = new ArrayList<Arc>();
		allArcs.addAll(node.getOutputArcs());
		allArcs.addAll(node.getInputArcs());
		for(Arc arc: allArcs){
			this.generateNodes(parentElt, arc.getNext(), visitedNodes, compteur + 70);
		}
	}
	/**
	 * Browse the automata graph and turn arcs into xml uppaal transitions
	 *
	 * <transition>
	 * <source ref="a"/>
	 * <target ref="b"/>
	 * <label kind="synchronisation">vendredi?13</label>
	 * </transition>
	 *
	 * @see graph
	 *
	 * @param parentElt Element which represents the automata (template element)
	 * @param node Current node with arcs to transform (for recursivity)
	 * @param visitedNodes Nodes allready visited (for recursivity)
	 */
	private void generateTransitions(Element parentElt, Node node, List<Node> visitedNodes)
	{
		for(Arc arc: node.getOutputArcs()){
			Element arcElt = this.document.createElement("transition");
			Element sourceElt = this.document.createElement("source");
			sourceElt.setAttribute("ref", String.valueOf(node.getName()));
			Element targetElt = this.document.createElement("target");
			targetElt.setAttribute("ref", String.valueOf(arc.getNext().getName()));
			arcElt.appendChild(sourceElt);
			arcElt.appendChild(targetElt);
			if(!("".equals(arc.getName())))
			{
				Element labelElt = this.document.createElement("label");
				labelElt.setAttribute("kind", "synchronisation");
				labelElt.appendChild(this.document.createTextNode(arc.getName()));
				arcElt.appendChild(labelElt);
			}
			parentElt.appendChild(arcElt);
			visitedNodes.add(node);
			if(!visitedNodes.contains(arc.getNext())){
				this.generateTransitions(parentElt, arc.getNext(), visitedNodes);
			}
		}
		for (Arc arc : node.getInputArcs()) {
			if(!visitedNodes.contains(arc.getNext())){
				this.generateTransitions(parentElt, arc.getNext(), visitedNodes);
			}
		}
	}
	/**
	 * Generate declaration sub element contains some codes
	 * Don't know what exactly..
	 * @param parentElt Element which represents the automata (template element)
	 */
	private void generateDeclaration(Element parentElt)
	{
		Element decl = this.document.createElement("declaration");
		parentElt.appendChild(decl);
	}

	@Override
	public String[] getAutomataList() {		
		return this.projectAutomataNames.toArray(new String[0]);
	}
}