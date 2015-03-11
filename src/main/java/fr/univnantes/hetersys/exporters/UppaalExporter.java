package fr.univnantes.hetersys.exporters;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.univnantes.hetersys.graph.Arc;
import fr.univnantes.hetersys.graph.Node;
import fr.univnantes.hetersys.importers.DotImporter;
public class UppaalExporter implements Exporter
{
	private Document document;
	private File uppaalProject;
	private Set<String> channels;
	private List<String> templateName;
	private Element templateBis = null;
	private Element template = null;
	private Element currentTemplate = null;
	
	/**
	 * Set to true if at least one channel from the graph
	 * is in the Uppaal project
	 * @see checkChannelsExistence
	 * @see updateFile
	 */
	private boolean channelLink;
	private boolean channelsInAutomata;
	private String automataName;
	private Node graph;
	public UppaalExporter(String automataName)
	{
		this.automataName = automataName;
		this.channels = new HashSet<String>();
		this.channelLink = false;
		this.channelsInAutomata = false;
		this.uppaalProject = null;
		this.graph = null;
		this.document = null;
		this.templateName = new ArrayList<String>();
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
	public void updateFile(Node graph) throws IOException
	{
		boolean exist = false;
		// Checks before export
		if(this.uppaalProject == null){
			throw new IOException("Uppaal project not yet loaded, please call loadExistingProject method.");
		}
		this.graph = graph;
		this.checkChannelsExistence(graph, new ArrayList<Node>());
		if(this.channelsInAutomata && !this.channelLink){
			Scanner userInput = new Scanner(System.in);
			System.out.println(
					"No channels from the graph is in the Uppaal project. You will not be able to put\n" +
							"your automata and those of the project together. Export anyway ? (yes/default no)"
					);
			String result = userInput.nextLine();
			//userInput.close(); // Don't close
			if(!"yes".equals(result)){
				System.out.println("> Export has been canceled by user.");
				return;
			}
		}
		// Load xml structure
		Element root = this.document.getDocumentElement();
		template = generateTemplate(root, this.graph);
		// Search system element to insert the template before
		Element system = null;
		NodeList rootChildren = root.getChildNodes();
		for (int i = 0; i < rootChildren.getLength(); i++){
			org.w3c.dom.Node n = rootChildren.item(i);
			if(n.getNodeName().equals("system")){
				system = (Element) n;
			}
			if(n.getNodeName().equals("template")){
				templateBis = (Element) n;


				NodeList templateChildren = templateBis.getElementsByTagName("name");
				for(int j = 0; j < templateChildren.getLength(); j++){

					Element current = (Element) templateChildren.item(j);
					if(current.getParentNode().getNodeName().equals("template")){

						System.out.println(current.getTextContent());
						if(current.getTextContent().equals(this.automataName)){
							currentTemplate = current;
							//System.out.println(current.getTextContent());
							exist = true;
							System.out.println("\tCan't add the new template : Already Exist\n");
						}
					}
				}

			}
		}



		if(system == null){
			throw new IOException("Cannot find <system> element in " + this.uppaalProject.getName());
		}

		
		
		if(exist == false){

			System.out.println("\tTemplate "+ this.automataName + " inserted with succes!\n");
			root.insertBefore(template, system);
			// Save changes to uppaal project file
			this.writeInFile();
			System.out.println(
					"> The uppaal project \"" + this.uppaalProject.getName() +
					"\" has been updated with \"" + this.automataName + "\" automata"
					);

		}else
		{
			System.out.println("Template " + this.automataName +" already exists, replace it? (yes/no)");
			Scanner sc = new Scanner(System.in);

			String ans = sc.nextLine();

			if("yes".equals(ans)){
				//System.out.println(templateBis.getTextContent());
				//n2 pas bon
				org.w3c.dom.Node node = (org.w3c.dom.Node) template;
				root.replaceChild(templateBis, node);
				
			}else
			{
				System.out.println("toto");
			}
		}




	}
	/*-------------------------------------- Internal logic ---------------------------------------*/	
	private void checkChannelsExistence(Node node, List<Node> visitedNodes){
		for(Arc arc: node.getOutputArcs()){
			String[] labelParse = DotImporter.parseDotLabel(arc.getName());
			// Channel found in arc label
			if(labelParse.length == 2){
				this.channelsInAutomata = true;
				String channel = labelParse[0];
				if(!this.channels.contains(channel)){
					Scanner userInput = new Scanner(System.in);
					System.out.println(
							"The channel \"" + channel + "\" is in the dot file but not in the Uppaal project.\n" +
									"Add it to the project ? (yes/default no)"
							);
					String result = userInput.nextLine();
					//userInput.close(); // Don't close
					if("yes".equals(result)){
						this.addChannel(channel);
						this.channelLink = true;
						System.out.println("Channel " + channel + " has been added to the Uppaal project.");
					}
				}
				else {
					this.channelLink = true;
				}
			}
			visitedNodes.add(node);
			if(!visitedNodes.contains(arc.getNext())){
				this.checkChannelsExistence(arc.getNext(), visitedNodes);
			}
		}
		for (Arc arc : node.getInputArcs()) {
			if(!visitedNodes.contains(arc.getNext())){
				this.checkChannelsExistence(arc.getNext(), visitedNodes);
			}
		}
	}
	/*-------------------------------- Load and update project file -------------------------------*/	
	private void loadChannels(){
		channels.addAll(Arrays.asList("Riri", "Fifi", "Loulou"));
		// TODO load channels from project file
	}
	private void addChannel(String channel){
		this.channels.add(channel);
		// TODO write in project file
	}
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
	private void generateDeclaration(Element parentElt)
	{
		Element decl = this.document.createElement("declaration");
		parentElt.appendChild(decl);
	}
	private Element generateTemplate(Element parentElt, Node graph)
	{
		Element tpl = this.document.createElement("template"),
				name = this.document.createElement("name");
		name.setAttribute("x", "0");
		name.setAttribute("x", "0");
		name.appendChild(this.document.createTextNode(this.automataName));
		tpl.appendChild(name);
		this.generateDeclaration(tpl);
		this.generateNodes(tpl, graph, new ArrayList<Node>(), 0);
		this.generateTransitions(tpl, this.graph, new ArrayList<Node>());
		return tpl;
	}

	List<String> getListTemplate(){
		return templateName;
	}
}