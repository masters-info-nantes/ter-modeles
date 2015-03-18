package fr.univnantes.hetersys.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;

public class Controller implements ActionListener {
	private Gui fenetre;
	private Model model;
	
	public void setGui(Gui fenetre){
		this.fenetre = fenetre;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton buttonSource = (JButton) e.getSource();
		
		switch(buttonSource.getText()){
			case "Show list":
				fenetre.displayAutomataList();
			break;
			
			case "Integrate":
				String dotFile = this.fenetre.getDotFilePath(),
					   uppaalFile = this.fenetre.getUppaalFilePath(),
					   automataName = this.fenetre.getAutomataName();
				
				// Check all required data are available
				if(Gui.NO_FILE_SELECTED.equals(dotFile) || 
						Gui.NO_FILE_SELECTED.equals(uppaalFile) ||
						"".equals(automataName))
				{
					this.fenetre.afficherInfo("Dot file, uppaal project and automata name must be completed");
					return;
				}
				
				this.model = new Model(dotFile, uppaalFile, automataName);
				this.model.addObserver(this.fenetre);
				this.model.runFromBeginning();
				
			break;
			
			case "Browse..":
				this.browseAction(buttonSource);
			break;
		}
	}
	
	/**
	 * For buttons which launch file selecter
	 * @param button Pressed button
	 */
	private void browseAction(JButton button){
		JFileChooser fileChooser = new JFileChooser();
		
		int fileSelected = fileChooser.showOpenDialog(null);
		if(fileSelected != JFileChooser.APPROVE_OPTION){
			return;
		}
		
		File selectedFile = fileChooser.getSelectedFile();
		switch(button.getToolTipText()){
			case "Dot file":
				this.fenetre.updateDotPath(selectedFile.getAbsolutePath());
			break;
			
			case "Uppaal file":
				this.fenetre.updateUppaalPath(selectedFile.getAbsolutePath());
			break;			
		}	
	}
}
