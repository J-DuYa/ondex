package net.sourceforge.ondex.web;

import java.awt.Cursor;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Required as stub for capturing URL click events
 * 
 * @author taubertj
 * 
 */
class ActivatedHyperlinkListener implements HyperlinkListener {

	JFrame frame = null;

	Cursor oldCursor = null;

	ActivatedHyperlinkListener(JFrame contentFrame) {
		this.frame = contentFrame;
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
		HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
		final URL url = hyperlinkEvent.getURL();
		if (type == HyperlinkEvent.EventType.ACTIVATED && url != null) {
			System.out.println("URL: " + url);
			try {
				Desktop.getDesktop().browse(url.toURI());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else if (type == HyperlinkEvent.EventType.ENTERED) {
			// change cursor to hand
			oldCursor = frame.getCursor();
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else if (type == HyperlinkEvent.EventType.EXITED) {
			// change cursor back
			frame.setCursor(oldCursor);
		}
	}
}
