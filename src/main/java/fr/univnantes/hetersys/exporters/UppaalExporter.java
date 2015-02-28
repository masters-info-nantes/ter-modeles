package fr.univnantes.hetersys.exporters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.univnantes.hetersys.graph.Arc;
import fr.univnantes.hetersys.graph.Node;

public class UppaalExporter implements Exporter
{
	private Document document;
	private Element currentElement;
	private String automataName;
	private Node graph;
	private File uppaalProject;

	public UppaalExporter(String automataName, File uppaalProject)
	{  
		this.automataName = automataName;
		this.uppaalProject = uppaalProject;
        this.createFileStructure();
	}

	private void createFileStructure(){
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.document = docBuilder.parse(this.uppaalProject);			
		} 
		catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println("Cannot create XML document");
		}
	}
	
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

		for(Arc arc: node.getOutputArcs()){
			this.generateNodes(parentElt, arc.getNext(), visitedNodes, compteur + 70);
		}
	}

	private void generateTransitions(Element parentElt, Node node)
	{
		for(Arc arc: node.getOutputArcs()){
			Element arcElt = this.document.createElement("transition");
			
			Element sourceElt = this.document.createElement("source");
			sourceElt.setAttribute("ref", String.valueOf(node.getName()));
			
			Element targetElt = this.document.createElement("target");
			targetElt.setAttribute("ref", String.valueOf(arc.getNext().getName()));	

			Element labelElt = this.document.createElement("label");
			labelElt.setAttribute("kind", "synchronisation");
			if(!("(pas de nom)".equals(arc.getName())))
			{
				labelElt.appendChild(this.document.createTextNode(arc.getName()));	
			}						
			
			arcElt.appendChild(sourceElt);
			arcElt.appendChild(targetElt);
			arcElt.appendChild(labelElt);
			parentElt.appendChild(arcElt);
			
			this.generateTransitions(parentElt, arc.getNext());
		}
	}

	private void generateDeclaration(Element parentElt)
	{
		Element decl = this.document.createElement("declaration");
		parentElt.appendChild(decl);
	}

	private Element generateTemplate(Element parentElt, Node graph)
	{
		Element tpl  = this.document.createElement("template"),
				name = this.document.createElement("name");
		
		name.setAttribute("x", "0");
		name.setAttribute("x", "0");
		name.appendChild(this.document.createTextNode(this.automataName));

		tpl.appendChild(name);
		
		this.generateDeclaration(tpl);
		this.generateNodes(tpl, graph, new ArrayList<Node>(), 0);
		this.generateTransitions(tpl, graph);

		return tpl;
	}

	@Override
	public void generateProject(Node graph) throws IOException
	{		
		this.graph = graph;
		//this.document.getDocumentElement().normalize();

		// Load xml structure
		Element root = this.document.getDocumentElement();
		Element template = generateTemplate(root, this.graph);
		
		// Search system element to insert the template before
		Element system = null;
		NodeList rootChildren = root.getChildNodes(); 
		
		for (int i = 0; i < rootChildren.getLength(); i++){
			org.w3c.dom.Node n = rootChildren.item(i);
			if(n.getNodeName().equals("system")){
				system = (Element) n;
			}
		}

		if(system == null){
			throw new IOException("Cannot find <system> element in " + this.uppaalProject.getName());
		}
		root.insertBefore(template, system);
	
		// Save changes to uppaal project file
		this.writeInFile();
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
}
