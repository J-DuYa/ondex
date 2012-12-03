package net.sourceforge.ondex.dialog;

import java.awt.Frame;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Creates a non modual popup which displays the title and what can be very long details.
 * 
 * This allows the program to close down the popup when it is no longer relative.
 * 
 * @author weilej and Christian
 *
 */
public class MessageDialog extends TavernaDialog {

	//####FIELDS####
	
    private String details;
			
	//####CONSTRUCTOR####
	
    /**
     * Creates a non modual popup which displays the title and what can be very long details.
     * 
     * As the details are displayed in a scrollable text area 
     * the size of the details String is only limited but the available memory.
     * 
     * @param parent Somewhere to root the popup on.
     * @param title Short text to be displayed on the title and main area.
     * @param details Longer to Very long text that will be displayed in an openable scrollable area.
     */
	public MessageDialog(Frame parent, String title, String details) {
		super(parent, title, details);
        this.details = details;
 		setupGUI();
	}
	
	//####METHODS####	
	JPanel makeMsgPanel() {
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.PAGE_AXIS));
		
		rightPanel.add(new JLabel(getTitle()));
		
		return rightPanel;
	}
	
}
