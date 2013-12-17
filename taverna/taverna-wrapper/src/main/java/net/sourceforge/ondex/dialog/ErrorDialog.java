package net.sourceforge.ondex.dialog;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Dialog for showing a throwable to the user.
 * 
 * @author weilej adapted bu Christian
 *
 */
public class ErrorDialog extends AbstractDialog {


    //####FIELDS####
		
	private Throwable throwable;
	
	//####CONSTRUCTOR####
	
	private ErrorDialog(Frame parent, Throwable throwable) {
		super(parent,"Error");
		this.throwable = throwable;
        
  		setupGUI();
	}
    
	//####METHODS####
    @Override
    void addPanelDetails(StringBuilder b) {
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        throwable.printStackTrace(pw);
        b.append(writer.toString());
    }

    @Override
    JPanel makeMsgPanel() {
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.PAGE_AXIS));
		
		String msg = (throwable.getMessage() != null) ? throwable.getMessage() : throwable.toString();
		rightPanel.add(new JLabel(" "));
		rightPanel.add(new JLabel("An error occurred:"));
		rightPanel.add(new JLabel(msg));
		
		return rightPanel;
	}
        
    @Override
    BufferedImage getImage(){
        InputStream stream = getClass().getResourceAsStream("/error25.png"); 
        //File imgFile = new File("config" + File.separator + "themes" + File.separator + "default" + File.separator + "icons" 
        //        + File.separator + "error25.png");
		//if (imgFile.exists() && imgFile.canRead()) {
			try {
				return ImageIO.read(stream);
 			} catch (IOException ioe) {
				return null;
			}
        //} else {
        //    return null;
        //}
    }

    /**
     * Displays a dialog to the user, where the details include the stack trace.
     * 
     * @param parent Frame to root the popup on
     * @param throwable An Error or Exception that needs to be reported to the user. 
     */
	public static void show(Frame parent, Throwable throwable) {
		new ErrorDialog(parent, throwable);
	}
	

}
