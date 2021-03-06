package fr.univnantes.hetersys.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 * This dialog shows the list of automata
 * in the uppaal project
 * 
 * @author jeremy
 */
@SuppressWarnings("serial")
public class AutomataListDialog extends JDialog {
	
	public AutomataListDialog(JFrame parent, String[] automataNames){
		super(parent, "Existing automata", true);
		
		// Interface
		JPanel panelRoot = new JPanel();
		panelRoot.setLayout(new BorderLayout());
		panelRoot.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(panelRoot);
		
		// List of automata names
		JList listAutomata = new JList<String>(automataNames);		
		listAutomata.setEnabled(false);
		
		JLabel labelWarning = new JLabel("<html>List of automata contained in the Uppaal project.\nYour automata can't have a name like one of them.</html>");
		labelWarning.setFont(new Font("Times", Font.BOLD, 12));
		labelWarning.setForeground(new Color(0xFF5722));
		labelWarning.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		JButton buttonClose = new JButton("Close");	
		buttonClose.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		panelRoot.add(labelWarning, BorderLayout.NORTH);
		panelRoot.add(listAutomata, BorderLayout.CENTER);
		
		// Bottom buttons
		JPanel panelBottom = new JPanel();
		panelBottom.setLayout(new BorderLayout());
		panelBottom.setBorder(BorderFactory.createEmptyBorder(5, 90, 0, 90));		
		panelBottom.add(buttonClose, BorderLayout.CENTER);
		
		// End stuff
		panelRoot.add(panelBottom, BorderLayout.SOUTH);
		this.setPreferredSize(new Dimension(300, 300));
	}
	
	public void display(){
		this.pack();
		this.setVisible(true);
	}	
}
