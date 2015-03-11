package fr.univnantes.hetersys.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * This dialog show a list of channels
 * that the user can add or not to the 
 * uppaal project
 * 
 * @author jeremy
 */
@SuppressWarnings("serial")
public class AddChannelsDialog extends JDialog {
	
	private Object[][] channelsData;
	
	/**
	 * Indicates if the automata has no channel in common
	 * with uppaal project
	 * 
	 * Help to notify the user if he didn't add channels
	 */
	private boolean channelNecessity;
	
	/**
	 * Help gui to determinate if user pushed
	 * cancel or continue button
	 */
	private boolean continueAction;
	
	public AddChannelsDialog(JFrame parent, Set<String> channels, boolean channelNecessity){
		super(parent, "Add missing channels", true);
		
		this.channelNecessity = channelNecessity;
		this.continueAction = true;
		
		// Interface
		JPanel panelRoot = new JPanel();
		panelRoot.setLayout(new BorderLayout());
		panelRoot.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(panelRoot);
		
		// Channels table
		String[] headers = new String[]{ "Channel", "Add?" };
		this.importChannelsInTable(channels);
		
		JTable tableChannels = new JTable(this.channelsData, headers);
		final JCheckBox check = new JCheckBox();
		DefaultCellEditor monedit = new DefaultCellEditor(check);
		tableChannels.getColumnModel().getColumn(1).setCellEditor(monedit);
		
		// Bottom buttons
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new BorderLayout());
		panelButtons.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		JButton buttonCancel = new JButton("Cancel export");
		buttonCancel.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				continueAction = false;
				setVisible(false);	
			}
		});
		
		JButton buttonContinue = new JButton("Continue");
		buttonContinue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(checkCanExit()){
					setVisible(false);
				}
				return;
			}
		});
		
		panelButtons.add(buttonCancel, BorderLayout.WEST);
		panelButtons.add(buttonContinue, BorderLayout.EAST);
		
		// End stuff
		panelRoot.add(new JScrollPane(tableChannels), BorderLayout.CENTER);
		panelRoot.add(panelButtons, BorderLayout.SOUTH);
		
		this.setPreferredSize(new Dimension(300, 300));		
	}
	
	public void display(){
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * Changes set of channel in comprehensible structure
	 * for the JTable component
	 * @param channels
	 */
	private void importChannelsInTable(Set<String> channels){		
		this.channelsData = new Object[channels.size()][2];
		for (int i = 0; i < channelsData.length; i++) {
			this.channelsData[i][0] = channels.toArray()[i];
			this.channelsData[i][1] = new Boolean(false);
		}
	}
	
	/**
	 * Notice the user if the automata has no
	 * common channels with uppaal project
	 * @see channelNecessity
	 * @return true if there are at least one common channel, false otherwise
	 */
	private boolean checkCanExit(){
		if(this.channelNecessity){
			int reply = JOptionPane.showConfirmDialog(this, 
					"No channels from the graph are in the Uppaal project.\n"+ "You will not be able to put " +
					"your automata and those of the project together.\nContinue export ?", 
					"Channels missing", 
					JOptionPane.YES_NO_OPTION
			);
			
	        return reply == JOptionPane.YES_OPTION;
		}
		return true;
	}
	
	// Getters
	public Object[][] getChannelsData(){
		return this.channelsData;
	}
	
	public boolean getContinueAction(){
		return this.continueAction;
	}
}
