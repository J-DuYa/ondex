package net.sourceforge.ondex.dialog;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.sourceforge.ondex.taverna.wrapper.Destoryable;

/**
 * Popup that gives information about a running workflow and provides a link to stop it.
 * 
 * This code is currently lazily implemented for a workflow but coule be easily changed for any runnable.
 *
 * @author Christian
 *
 */
public class RunningDialog extends AbstractDialog implements ActionListener{

	//####FIELDS####
	
    private String workflowName;
    
    private String details;
		
    private Destoryable runner;
    	
	//####CONSTRUCTOR####
	
    /**
     * Creates a popup that gives information about a running workflow and provides a link to stop it.
     * 
     * @param parent Somewhere to root the popup on.
     * @param runner Link to the running workflow so that a stop/destroy message can be passed back.
     * @param workflowName Name of the running workflow
     * @param details Longer to Very long text that will be displayed in an openable scrollable area.
     */
	public RunningDialog(Frame parent, Destoryable runner, String workflowName, String details) {
		super(parent,"Running workflow");
        this.workflowName = workflowName;
        this.details = details;
        this.runner = runner;
 		setupGUI();
	}

	//####METHODS####	
	JPanel makeMsgPanel() {
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.PAGE_AXIS));
		
		rightPanel.add(new JLabel("Runnig Workflow"));
		rightPanel.add(new JLabel(workflowName));
		
		return rightPanel;
	}
	
    @Override
    void addPanelDetails(StringBuilder b) {
        b.append(details);
        b.append(NEW_LINE);
    }
	
	@Override
    BufferedImage getImage(){
        InputStream stream = getClass().getResourceAsStream("/taverna.jpeg"); 
        try {
            return ImageIO.read(stream);
        } catch (IOException ioe) {
            return null;
        }
    }

    @Override
    JPanel createButtonPanel() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p.add(makeButton("Stop Workflow","StopWorkflow"));
		p.add(makeButton("OK","ok"));
		return p;
	}

   	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
        System.out.println(cmd);
		if (cmd.equals("StopWorkflow")) {
			runner.destroy();
            System.out.println("ggg");
            this.setVisible(false);
		} 
		super.actionPerformed(e);
	}

}
