package net.sourceforge.ondex.taverna;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 * Testing Class for the TavernaWrapper.
 * 
 * Does nothing more than create and provide a parent frame and JMenu to run Taverna from.
 
 * @author Christian
 */
public class TavernaMini { //implements WindowListener {
        
    private JFrame frame;
        
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("GuiTest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            frame.setJMenuBar(createMenuBar(frame));
        } catch (IOException ex) {
            //Too lazy to work out how to get these out of a runnable.
            ex.printStackTrace();
        } catch (TavernaException ex) {
            //Too lazy to work out how to get these out of a runnable.
            ex.printStackTrace();
        }
        
        Container pane = frame.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        
        frame.setLocation(200, 200);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
        
    /** Create a MenuBar with a parent Jemun, which is passed to the TavernaWrapper
     * 
     * @param parent This Gui's frame
     * @return MenuBar to add to the frame.
     * @throws TavernaException Thrown is the Environment vairables are set incorrectly
     */
    private JMenuBar createMenuBar(JFrame parent) throws TavernaException, IOException {
        JMenuBar menuBar = new JMenuBar();
        //Build the first menu.
        JMenu menu = new JMenu("Taverna");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription("Access Taverna options");

        TavernaWrapper wrapper = new TavernaWrapper(frame);
        wrapper.setWorkflowFile(new File("D:\\taverna\\mixedWorkflow.t2flow"));
        //wrapper.setWorkflowFile(new File("D:\\taverna\\HelloWorld.t2flow"));

        wrapper.attachMenu(menu);
        menuBar.add(menu);
        return menuBar;
    }

    /**
     * Testing root.
     * @param args Ignored.
     * @throws TavernaException Thrown is the Environment vairables are set incorrectly
     */
    public static void main(String[] args) throws TavernaException {
       final TavernaMini guiTest = new TavernaMini();
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               guiTest.createAndShowGUI();
            }
        });
/**/
    }
}
