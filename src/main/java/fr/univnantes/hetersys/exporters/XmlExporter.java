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
import org.xml.sax.SAXException;

import fr.univnantes.hetersys.graph.Arc;
import fr.univnantes.hetersys.graph.Node;

public class XmlExporter implements Exporter
{
	private Document document;
	private Element currentElement;
	private String automataName;
	private Node graph;
	private String name;

	public XmlExporter(String automataName, String name)
	{  
		this.automataName = automataName;
		this.name = name;
        this.createFileStructure();
	}

	private void createFileStructure(){
		File fXmlFile = new File(this.name);
		
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} 
		catch (ParserConfigurationException e) {
			System.err.println("Cannot create XML document");
		}
		
		try {
			this.document = docBuilder.parse(fXmlFile);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		for(Arc arc: node.getArcs()){
			this.generateNodes(parentElt, arc.getNext(), visitedNodes, compteur + 70);
		}
	}

	private void generateTransitions(Element parentElt, Node node)
	{
		for(Arc arc: node.getArcs()){
			Element arcElt = this.document.createElement("transition");
			
			Element sourceElt = this.document.createElement("source");
			sourceElt.setAttribute("ref", String.valueOf(node.getName()));
			
			Element targetElt = this.document.createElement("target");
			targetElt.setAttribute("ref", String.valueOf(arc.getNext().getName()));			
			
			arcElt.appendChild(sourceElt);
			arcElt.appendChild(targetElt);
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

		//this.currentElement.appendChild(tpl);
		
		this.generateDeclaration(tpl);
		this.generateNodes(tpl, graph, new ArrayList<Node>(), 0);
		this.generateTransitions(tpl, graph);

		return tpl;
	}

	@Override
	public void generateProject(File file, Node graph)
	{
		this.graph = graph;
		this.generateDeclaration(this.currentElement);
		this.generateTemplate(this.currentElement, graph);
		this.writeInFile(file);
	}
	
	private void writeInFile(File file){

        Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");            

            Source src = new DOMSource(this.document);
            Result result = new StreamResult(file);
            transformer.transform(src, result);
            
		}
		catch (TransformerFactoryConfigurationError | TransformerException e) {
			System.err.println("Cannot write generated XML to " + file.getAbsolutePath());
		} 
	}

	public void insertTemplate(String name, Node graph){

		
		this.graph = graph;
		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		this.document.getDocumentElement().normalize();
	 
		System.out.println("Root element :" + this.document.getDocumentElement().getNodeName());
		Element root = this.document.getDocumentElement();
		Element elt = generateTemplate(root, this.graph);
		Element system = null;
		for (int i = 0; i < root.getChildNodes().getLength(); i++) {
			org.w3c.dom.Node n = root.getChildNodes().item(i);
			if(n.getNodeName().equals("system")){
				system = (Element) n;
			}
		}
		
		System.out.println(system.getNodeName());
		root.insertBefore(elt, system);
		writeInFile(new File(name));
	}
}
