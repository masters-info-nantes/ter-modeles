package fr.univnantes.hetersys.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class Gui extends JFrame implements ActionListener {

	private JTextField textDotPath;
	private JTextField textUppaalPath;
	
	public Gui(){
		super("Hetersys");
		
		// Interface
		JPanel panelRoot = new JPanel();
		panelRoot.setLayout(new BorderLayout());
		panelRoot.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(panelRoot);
		
		// Panel resources			
		JPanel panelResources = new JPanel();
		panelResources.setLayout(new SpringLayout());
		panelResources.setBorder(BorderFactory.createTitledBorder("Resources"));		
		
		this.textDotPath = new JTextField("No file selected");
		this.textDotPath.setEditable(false);
		
		this.textUppaalPath = new JTextField("No file selected");
		this.textUppaalPath.setEditable(false);
		
		JButton buttonBrowseDot = new JButton("Browse..");
		buttonBrowseDot.setToolTipText("Dot file");
		buttonBrowseDot.addActionListener(this);
		
		JButton buttonBrowseUppaal = new JButton("Browse..");
		buttonBrowseUppaal.setToolTipText("Uppaal file");
		buttonBrowseUppaal.addActionListener(this);
		
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
		
		JTextField textAutomataName = new JTextField("automata name");
		JButton buttonAutomataList = new JButton("Show list");
		
		panelAutomata.add(new JLabel("Automata name: "));
		panelAutomata.add(textAutomataName);
		panelAutomata.add(new JLabel("Project automata: "));
		panelAutomata.add(buttonAutomataList);
		
		SpringUtilities.makeCompactGrid(panelAutomata, 2, 2, 6, 6, 6, 6);		
		
		// Panel export
		JPanel panelExport = new JPanel();
		panelExport.setLayout(new BorderLayout());
		panelExport.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		JButton buttonExport = new JButton("Export..");
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

	@Override
	public void actionPerformed(ActionEvent e) {
		
		JButton buttonSource = (JButton) e.getSource();
		
		JFileChooser fileChooser = new JFileChooser();
		
		int fileSelected = fileChooser.showOpenDialog(null);
		if(fileSelected != JFileChooser.APPROVE_OPTION){
			return;
		}
		
		File selectedFile = fileChooser.getSelectedFile();
		switch(buttonSource.getToolTipText()){
			case "Dot file":
				textDotPath.setText(selectedFile.getAbsolutePath());
			break;
			
			case "Uppaal file":
				textUppaalPath.setText(selectedFile.getAbsolutePath());
			break;			
		}
	}
}
