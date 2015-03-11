package fr.univnantes.hetersys;

import fr.univnantes.hetersys.gui.Controller;
import fr.univnantes.hetersys.gui.Gui;

public class App {
	public static void main(String[] args) 
	{
		Controller controller = new Controller();
		
		Gui fenetre = new Gui(controller);
		fenetre.display();		
		
		controller.setGui(fenetre);
	}	
}
