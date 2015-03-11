package fr.univnantes.hetersys.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

@SuppressWarnings("serial")
public class Gui extends JFrame implements Observer {

	public final static String NO_FILE_SELECTED = "No file selected";
	
	private JTextField textDotPath;
	private JTextField textUppaalPath;
	private JTextField textAutomataName;
	
	private AutomataListDialog dialogAutomata;
	private AddChannelsDialog dialogChannels;
	
	public Gui(Controller controller){
		super("Hetersys");
		
		this.dialogAutomata = new AutomataListDialog(this);
		
		// Interface
		JPanel panelRoot = new JPanel();
		panelRoot.setLayout(new BorderLayout());
		panelRoot.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(panelRoot);
		
		// Panel resources			
		JPanel panelResources = new JPanel();
		panelResources.setLayout(new SpringLayout());
		panelResources.setBorder(BorderFactory.createTitledBorder("Resources"));		
		
		this.textDotPath = new JTextField(/*Gui.NO_FILE_SELECTED*/"dotFile/test.dot");
		this.textDotPath.setEditable(false);
		
		this.textUppaalPath = new JTextField(/*Gui.NO_FILE_SELECTED*/"jobbers.xml");
		this.textUppaalPath.setEditable(false);
		
		JButton buttonBrowseDot = new JButton("Browse..");
		buttonBrowseDot.setToolTipText("Dot file");
		buttonBrowseDot.addActionListener(controller);
		
		JButton buttonBrowseUppaal = new JButton("Browse..");
		buttonBrowseUppaal.setToolTipText("Uppaal file");
		buttonBrowseUppaal.addActionListener(controller);
		
		panelResources.add(new JLabel("Automata (Dot): "));
		panelResources.add(this.textDotPath);
		panelResources.add(buttonBrowseDot);
		panelResources.add(new JLabel("UPPAAL project: "));
		panelResources.add(this.textUppaalPath);
		panelResources.add(buttonBrowseUppaal);
		
		SpringUtilities.makeCompactGrid(panelResources, 2, 3, 6, 6, 6, 6);
				
		// Panel automata
		JPanel panelAutomata = new JPanel();
		panelAutomata.setLayout(new SpringLayout());
		panelAutomata.setBorder(BorderFactory.createTitledBorder("Automata"));
		
		textAutomataName = new JTextField(/*""*/"test");
		JButton buttonAutomataList = new JButton("Show list");
		buttonAutomataList.addActionListener(controller);
		
		panelAutomata.add(new JLabel("Automata name: "));
		panelAutomata.add(textAutomataName);
		panelAutomata.add(new JLabel("Project automata: "));
		panelAutomata.add(buttonAutomataList);
		
		SpringUtilities.makeCompactGrid(panelAutomata, 2, 2, 6, 6, 6, 6);		
		
		// Panel export
		JPanel panelExport = new JPanel();
		panelExport.setLayout(new BorderLayout());
		panelExport.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		JButton buttonExport = new JButton("Export");
		buttonExport.addActionListener(controller);
		
		panelExport.add(buttonExport, BorderLayout.EAST);
		
		// End stuff
		panelRoot.add(panelResources, BorderLayout.NORTH);
		panelRoot.add(panelAutomata, BorderLayout.CENTER);
		panelRoot.add(panelExport, BorderLayout.SOUTH);		
		
		this.setPreferredSize(new Dimension(450, 280));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void display(){
		this.pack();
		this.setVisible(true);
	}

	/*----------------------- Access to data -----------------------------*/
	public String getDotFilePath(){
		return this.textDotPath.getText();
	}
	
	public String getUppaalFilePath(){
		return this.textUppaalPath.getText();
	}

	public String getAutomataName(){
		return this.textAutomataName.getText();
	}
	
	/*---------------------- Controller setters ---------------------------*/
	public void displayAutomataList(){
		dialogAutomata.display();		
	}
	
	public void updateDotPath(String path){
		this.textDotPath.setText(path);
	}	
	
	public void updateUppaalPath(String path){
		this.textUppaalPath.setText(path);
	}	

	public void afficherInfo(String content){
		JOptionPane.showMessageDialog(this, content);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		NotificationType notif = (NotificationType) arg;
		Model model = (Model) o;
		
		switch(notif){
			case DOT_PARSE_ERROR: case EXPORT_FAILURE: case EXPORT_SUCCESS:
				JOptionPane.showMessageDialog(this, model.getLastMessage());
			break;
			
			case CHANNELS_MISSING:
				this.dialogChannels = new AddChannelsDialog(this, model.getChannelsToAdd(), model.getAutomataChannelNecessity());				
				this.dialogChannels.display();
					
				// User cancel export
				if(!this.dialogChannels.getContinueAction()){
					return;
				}
				
				// Continue export
				model.addChannels(this.dialogChannels.getChannelsData());
				model.continueRun();
			break;
			
			case AUTOMATA_ALLREADY_EXISTS:
				int reply = JOptionPane.showConfirmDialog(this, model.getLastMessage(), 
						"Automata duplication", 
						JOptionPane.YES_NO_OPTION
				);
				
		        if(reply != JOptionPane.YES_OPTION){
		        	return;
		        }
		        
				model.continueRun();				
			break;
		}
	}
}
