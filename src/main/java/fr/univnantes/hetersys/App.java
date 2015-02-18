package fr.univnantes.hetersys;

import fr.univnantes.hetersys.dotToXml.loadingDot;
import fr.univnantes.hetersys.dotToXml.saveToXml;

public class App {
	public static void main(String[] args) 
	{

		loadingDot l = new loadingDot("test.dot");
		l.loadDot();
		l.affichageGraph();
		saveToXml s = new saveToXml(l.getGraph(),"test.xml");
		s.suppressionFile();
		s.generationProjet("test");
	}	
}
