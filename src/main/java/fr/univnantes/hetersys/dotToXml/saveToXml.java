package fr.univnantes.hetersys.dotToXml;

import java.io.File;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.ArrayList;

import fr.univnantes.hetersys.graph.*;

public class saveToXml
{

	private ArrayList<Node> graph;
	private String nameFile;
	private int compteur;

	public saveToXml(ArrayList<Node> graph,String nameFile)
	{
		this.graph = graph;
		this.nameFile = nameFile;
		compteur = 0;
	}

	public void generationNoeud(Node n)
	{
		String texte = "<location id=\"" + n.getName() + "\" x=\""+compteur+"\" y=\""+compteur+"\"> <name>"+n.getName()+"</name> </location> ";
		ecritureDansFile(texte);
		compteur+=70;
	}

	public void generationTransition(Node n)
	{
		ArrayList<Arc> arcs = n.getArcs();
		for(int i=0; i<arcs.size();i++)
		{
			//System.out.println("     name : "+arcs.get(j).getName()+" vers le noeud : "+arcs.get(j).getNext().getName() );
			String texte = "<transition> <source ref=\""+ n.getName() +"\" /> <target ref=\"" + arcs.get(i).getNext().getName() +"\" /> </transition>";
			ecritureDansFile(texte);	
		}
	}

	public void generationDeclaration()
	{
		String texte = "<declaration></declaration>";
		ecritureDansFile(texte);
	}

	public void generationTemplate(String name)
	{
		String texte = "<template> <name x=\"0\" y=\"0\"> "+name+"</name>";
		ecritureDansFile(texte);
		generationDeclaration();
		for(int i=0 ;i<graph.size();i++)
		{
			generationNoeud(graph.get(i));
		}
		for(int i=0 ;i<graph.size();i++)
		{
			generationTransition(graph.get(i));
		}	
		texte = "</template>";
		ecritureDansFile(texte);	
	}

	public void generationProjet(String name)
	{
		String texte = "<nta>";
		ecritureDansFile(texte);
		generationDeclaration();
		generationTemplate(name);
		texte = "</nta>";
		ecritureDansFile(texte);
	}

	public void ecritureDansFile(String texte)
	{
		try
		{
			FileWriter file = new FileWriter(nameFile,true);
			file.write(texte);
			file.close();
		}
		catch(IOException ex){ ex.printStackTrace(); }
	}

	public void suppressionFile()
	{
		File f = new File(nameFile);
		f.delete();
	}

}
