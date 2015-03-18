package fr.univnantes.hetersys.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;

public class Controller implements ActionListener {
	private Gui window;
	private Model model;
	
	public void setGui(Gui window){
		this.window = window;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton buttonSource = (JButton) e.getSource();
		
		switch(buttonSource.getText()){
			case Gui.LIST_AUTOMATA_BUTTON_TEXT:
				window.displayAutomataList(model.getAutomataList());
			break;
			
			case Gui.EXPORT_BUTTON_TEXT:
				String dotFile = this.window.getDotFilePath(),
					   uppaalFile = this.window.getUppaalFilePath(),
					   automataName = this.window.getAutomataName();
				
				// Check all required data are available
				if(Gui.NO_FILE_SELECTED.equals(dotFile) || 
						Gui.NO_FILE_SELECTED.equals(uppaalFile) ||
						"".equals(automataName))
				{
					this.window.displayInfo("Dot file, uppaal project and automata name must be completed");
					return;
				}
				
				this.model = new Model(dotFile, uppaalFile, automataName);
				this.model.addObserver(this.window);
				this.model.runFromBeginning();
				this.window.changeAutomataListButtonState(true);
				
			break;
			
			case Gui.BROWSE_BUTTON_TEXT:
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
		
		// TODO Add filter on file extensions
		
		int fileSelected = fileChooser.showOpenDialog(null);
		if(fileSelected != JFileChooser.APPROVE_OPTION){
			return;
		}
		
		File selectedFile = fileChooser.getSelectedFile();
		switch(button.getToolTipText()){
			case "Dot file":
				this.window.updateDotPath(selectedFile.getAbsolutePath());
			break;
			
			case "Uppaal file":
				this.window.updateUppaalPath(selectedFile.getAbsolutePath());
				this.window.changeAutomataListButtonState(false);
			break;			
		}	
	}
}
