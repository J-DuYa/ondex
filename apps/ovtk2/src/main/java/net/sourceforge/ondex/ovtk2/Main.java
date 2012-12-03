package net.sourceforge.ondex.ovtk2;

import java.io.File;

import javax.swing.JOptionPane;

import net.sourceforge.ondex.ovtk2.ui.OVTK2Desktop;
import net.sourceforge.ondex.ovtk2.util.DesktopUtils;

/**
 * Main class entry point for OVTK2.
 * 
 * @author taubertj
 * 
 */
public class Main {

	/**
	 * Initialises OVTK2.
	 * 
	 * @param args
	 *            the program arguments
	 * @throws Exception
	 */
	public static void main(String[] args) {
		long heapMaxSize = Runtime.getRuntime().maxMemory();
		if (heapMaxSize < 1037959168) {
			JOptionPane
					.showMessageDialog(
							null,
							"You are trying to run OVTK with less than 1GB of heap space.\n" +
							"This might result in problems with large data.");
		}

		OVTK2Desktop instance = OVTK2Desktop.getInstance();

		if (args.length > 0) {
			String filename = args[0];
			File file = new File(filename);
			if (file.exists() && file.canRead()) {
				// busy waiting
				while (!OVTK2Desktop.hasFinishedInit()) {
					synchronized (instance) {
						try {
							instance.wait(100);
						} catch (InterruptedException e) {
							// ignore
						}
					}
				}
				System.out.println("Trying to load: " + file.getAbsolutePath());
				DesktopUtils.openFile(file);
			}
		}
	}
}