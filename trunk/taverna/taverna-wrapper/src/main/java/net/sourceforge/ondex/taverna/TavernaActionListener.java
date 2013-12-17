package net.sourceforge.ondex.taverna;

import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import net.sourceforge.ondex.taverna.wrapper.DataViewerWrapper;
import net.sourceforge.ondex.taverna.wrapper.CommandLineWrapper;
import net.sourceforge.ondex.dialog.ErrorDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.sourceforge.ondex.dialog.MessageDialog;
import net.sourceforge.ondex.dialog.RunningDialog;
import net.sourceforge.ondex.dialog.inputs.InputGui;
import net.sourceforge.ondex.taverna.wrapper.CommandLineRun;
import net.sourceforge.ondex.taverna.wrapper.TavernaInput;
import org.xml.sax.SAXException;

/**
 * An ActionListener, that handles the GUI part of getting information for the users.
 * 
 * Used by the main Taverna Menu but not by the input dialogs which include their own actionListeners.
 * 
 * @author Christian
 */
public class TavernaActionListener implements ActionListener{

    private Frame parent;
    
    private Icon icon;
    
    private CommandLineWrapper commandLine;
    
    private DataViewerWrapper dataViewer;
    
    /** 
     * Creates an ActionListener, that handles the GUI part of getting information for the users.
     * 
     * Used by the main Taverna Menu but not by the input dialogs which include their own actionListeners.
     * 
     * @author Christian
     * 
     * @param parent Parent to use for all popups
     * @param commandLine Wrapper around a Taverna CommandLine bat or script
     * @param dataViewer Wrapper around a Taverna DataViwer bat or script
     * @throws TavernaException 
     */
    public TavernaActionListener(JFrame parent, CommandLineWrapper commandLine, DataViewerWrapper dataViewer) 
            throws TavernaException{
        this.parent = parent;
        this.commandLine = commandLine;
        this.dataViewer = dataViewer;
        InputStream stream = getClass().getResourceAsStream("/taverna.jpeg"); 
        try {
            icon = new ImageIcon(ImageIO.read(stream));
        } catch (IOException ioe) {
            //Just notice it and move on. Can be null.
            ioe.printStackTrace();
        }
     }
    
    /**
     * Pops up a Dialog asking the user to set Taverna home.
     * 
     * The prefer way is to set the Environment variable "TAVERNA_DATAVIEWER_HOME"".
     * This method however allow the value to come from the user.
     * <p>
     * The selected value is not saved so will be lost when the program is terminated.
     * 
     * Exceptions caused by picking an incorrect file are shown to the user via a second popup.
     */
    private void getTavernaHome() throws TavernaException {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY );
        int returnVal = chooser.showOpenDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                commandLine.setTavernaHome(file);
            } catch (Exception ex) {
                ErrorDialog.show(parent, ex);
            }
            JOptionPane.showMessageDialog(parent, commandLine.getTavernaHome().getAbsoluteFile() + 
                    " set as Traverna Home. To avoid having to do this every time consider setting the "
                    + "Environment Variable \"TAVERNA_HOME\".", "TravernaHome Set", JOptionPane.PLAIN_MESSAGE, icon);
        }
    }
        
    /**
     * Pops up a Dialog asking the user to set Data Viewer home.
     * 
     * The prefer way is to set the Environment variable "TAVERNA_DATAVIEWER_HOME"".
     * This method however allow the value to come from the calling application or its config file.
     * <p>
     * The selected value is not saved so will be lost when the program is terminated.
     * 
     * Exceptions caused by picking an incorrect file are shown to the user via a second popup.
     */
    private void getDataViewerHome() throws TavernaException{
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY );
        int returnVal = chooser.showOpenDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                dataViewer.setDataViewerHome(file);
            } catch (Exception ex) {
                ErrorDialog.show(parent, ex);
            }   
            JOptionPane.showMessageDialog(parent, dataViewer.getDataViewerHome().getAbsoluteFile() + 
                    " set as DataViewer Home. " +
                    " To avoid having to do this every time consider setting the Environment Variable "
                    + "\"TAVERNA_DATAVIEWER_HOME\".", "TAVERNA_DATAVIEWER_HOME Set", JOptionPane.PLAIN_MESSAGE, icon);
         }
    }
    
    /**
     * Allows the user to identify and load a file based workflow using the FileChooser.
     * @throws TavernaException If the choosen file could be identified as not a workflow file.
     *     The absence of an Exception can not be taken as a confirmation that the workflow is valid 
     *     let alone will work.
     */
    private void getFileWorkflow() throws IOException, TavernaException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Taverna T2Workflows", "t2Flow");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            commandLine.setWorkflowFile(chooser.getSelectedFile());          
        }
    }
    
    /**
     * Allows the user to identify and load a file based on a URI.
     * 
     * Legal URI values are ones that both javax.xml.parsers.DocumentBuilder and Taverna CommandLine can handle.
     * It is highly recommended that only Absolute URI's including Schema are used.
     * @throws TavernaException If the choosen file could be identified as not a workflow file.
     *     The absence of an Exception can not be taken as a confirmation that the workflow is valid 
     *     let alone will work.
     */
    private void getURLWorkflow() throws TavernaException{
        String uri = JOptionPane.showInputDialog(parent, "Enter URI of the desired workflow", "Workflow URI", 
                JOptionPane.QUESTION_MESSAGE);
        System.out.println("uri: "+ uri);
        Thread t = new WorkflowURISetter(uri);
        t.start();
    }
    
    /**
     * Pops up a Gui that requests users to provide values for each of the inputs in the selected workflow.
     * 
     * Assumes that a valid workflow has previously been selected.
     * 
     * @throws TavernaException Calling this method without a valid workflow being set.
     *     Other causes are techincally possible but not expected.
     */
    private void getInputs() throws TavernaException, ParseException{
        InputGui mapper = new InputGui(parent, commandLine.getWorkflowName(), commandLine.getInputNamesAndDepths());
        TavernaInput[] inputs = mapper.getValueArray();
        commandLine.setInputs(inputs);
    }
    
    
    /**
     * Pops up a FileChooser that request the user to select a Baclava file that has the required inputs.
     * 
     * Assumes that a valid workflow has previously been selected.
     * 
     * @throws TavernaException Calling this method without a valid workflow being set.
     *     Other causes are techincally possible but not expected.
     */
    private void getInputsFile() throws IOException, TavernaException, ParserConfigurationException, SAXException{
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Baclava files", "xml");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            commandLine.setInputsFile(chooser.getSelectedFile());          
        }
    }
        
    private void getInputURL() throws TavernaException, ParserConfigurationException, SAXException, IOException{
        String uri = JOptionPane.showInputDialog(parent, "Enter URI of the inputs Baclava", "Inputs URI", 
                JOptionPane.QUESTION_MESSAGE);
        commandLine.setInputsURI(uri);
    }

    private void runWorkflow() {
        Thread t = new Thread() {
            @Override
            public void run() {
                JDialog runMessage = null;
                try {
                    CommandLineRun commandLineRun  = commandLine.runWorkFlow();
                    runMessage = new RunningDialog(parent, commandLineRun, commandLine.getWorkflowName(), 
                            commandLineRun.getRunInfo());
                    String result = commandLineRun.getOutput();
                    runMessage.dispose();
                    new MessageDialog(parent, "Workflow Run Finished", result);
                    dataViewer.replaceDataFile(commandLineRun.getOutputFile());
                } catch (Exception ex) {
                    ErrorDialog.show(parent, ex);
                } finally {
                    if (runMessage != null){
                        runMessage.dispose();  
                    }
                }                      
            }
        };
        t.start();
    }
    
    private void showResults() throws Exception{
        String message = dataViewer.runDataViewer();
        new MessageDialog(parent, "Data Viewer result", message);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String cmd = ae.getActionCommand();
        try {
        if (cmd.equals("getTavernaHome")) {
            getTavernaHome();
            } else if (cmd.equals("getFileWorkflow")){
                getFileWorkflow();
            } else if (cmd.equals("getURLWorkflow")){
                getURLWorkflow();
            } else if (cmd.equals("getInputs")){
                getInputs();
            } else if (cmd.equals("getInputFile")){
                getInputsFile();
            } else if (cmd.equals("getInputURL")){
                getInputURL();
            } else if (cmd.equals("runWorkflow")){
                runWorkflow();            
            } else if (cmd.equals("getDataViewerHome")){
                getDataViewerHome();
            } else if (cmd.equals("showResults")){
                showResults();
            }
        } catch (Exception ex) {
            ErrorDialog.show(parent, ex);
        }                       
    }

private class WorkflowURISetter extends Thread{  
    
    String uri;
    
    WorkflowURISetter(String uri){
        this.uri = uri;
    }
    
    @Override
    public void run() {
        MessageDialog messageDialog = new MessageDialog(parent, "Loading Workflow",
                "Please wait while workflow is loaded and inputNames are extracted.");
        messageDialog.setVisible(true);
        try {
            if (uri != null){
                commandLine.setWorkflowURI(uri);             
            }
        } catch (Exception ex) {
            ErrorDialog.show(parent, ex);
        } finally {
            messageDialog.dispose(); 
        }                      
    }
};

}
