package fr.univnantes.hetersys.dotToXml;

import java.util.Scanner; 
import java.io.File;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.ArrayList;

import fr.univnantes.hetersys.graph.*;

public class loadingDot
{

	private String filePath;
	private ArrayList<Node> graph;
	private String nameArc = "(pas de nom)"; // pour le moment les noms des arcs ne sont pas gérés

	public loadingDot(String file)
	{
		filePath = "dotFile/"+file;
		graph = new ArrayList<Node>();
	}

	public ArrayList<Node> getGraph()
	{
		return graph;
	}

	public void loadDot()
	{
		try
		{
			Scanner scanner = new Scanner(new File(filePath));

			// On boucle sur chaque champ detecté
			while (scanner.hasNextLine()) 
			{
			    String line = scanner.nextLine();
			 
			    System.out.println(line);
			    if(true==isUsefulLine(line))
			    {
			    	loadLine(line);
			    }
			}
			 
			scanner.close();
		}
		catch (IOException e) { System.out.println(e); }
	}

	public void loadLine(String line)
	{
		String[] tab = line.split("->");
		for(int i=0;i<tab.length;i++)
		{
			//System.out.println(tab[i].trim());
			if(tab.length-i > 1)
			{
				addArc(cleanString(tab[i]),cleanString(tab[i+1]));
			}
		}
	}

	public boolean isUsefulLine(String line)
	{
		String[] tab = line.split(" ");
		for(int i=0; i<tab.length;i++)
		{
			if( ("digraph".equals(tab[i]))||("graph".equals(tab[i])) )
				return false;
		}
		if("}".equals(line.trim()))
		{
			return false;
		}

		return true;
	}

	public String cleanString(String s)
	{
		String[] tab = s.split(";");
		return tab[0].trim();
	}

	public void addArc(String nodeStart, String nodeEnd)
	{
		addNode(nodeStart);
		addNode(nodeEnd);
		Arc arc = new Arc(nameArc,getNodeOnList(nodeEnd));
		getNodeOnList(nodeStart).addArc(arc);
	}

	public void addNode(String node)
	{
		if(-1==positionNodeOnList(node))
		{
			graph.add(new Node(node));
		}
	}

	/* La valeur de la position de départ et de retour si le noeud est inexistant est -1 */
	public int positionNodeOnList(String node)
	{
		int pos = -1; 
		for(int i=0; i<graph.size() ;i++)
		{
			if(graph.get(i).getName().equals(node))
			{
				pos = i;
			}
		}
		return pos;
	}

	/* retourne un noeud si il existe, null sinon */
	public Node getNodeOnList(String node)
	{
		for(int i=0; i<graph.size() ;i++)
		{
			if(graph.get(i).getName().equals(node))
			{
				return graph.get(i);
			}
		}
		return null;
	}

	public void affichageGraph()
	{
		for(int i=0;i<graph.size();i++)
		{
			Node n = graph.get(i);
			System.out.println("Noeud : "+n.getName());
			if(n.isFinal())
			{
				System.out.println(" --> Noeud final");
			}
			else
			{
				System.out.println(" --> Sous Noeud : ");
				ArrayList<Arc> arcs = n.getArcs();
				for(int j=0; j<arcs.size();j++)
				{
					System.out.println("     name : "+arcs.get(j).getName()+" vers le noeud : "+arcs.get(j).getNext().getName() );
				}
			}
		}
	}

}


 
