package fr.univnantes.hetersys;

import fr.univnantes.hetersys.dotToXml.loadingDot;

public class App {
	public static void main(String[] args) 
	{

		loadingDot l = new loadingDot("test.dot");
		l.loadDot();
		l.affichageGraph();
	}	
}
