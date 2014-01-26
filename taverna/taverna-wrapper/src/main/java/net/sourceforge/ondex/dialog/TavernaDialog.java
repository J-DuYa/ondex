package net.sourceforge.ondex.dialog;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.IOException;

import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Abstract class whose main role is to add a Taverna jpeg if available.
 * @author Christian
 *
 */
public abstract class TavernaDialog extends AbstractDialog {

	//####FIELDS####
	
    private String details;
			
	//####CONSTRUCTOR####
	
	TavernaDialog(Frame parent, String title, String details) {
		super(parent, title);
        this.details = details;
 		setupGUI();
	}

	
	//####METHODS####		
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

}
