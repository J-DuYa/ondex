package net.sourceforge.ondex.taverna;

import net.sourceforge.ondex.taverna.wrapper.DataViewerWrapper;
import net.sourceforge.ondex.taverna.wrapper.CommandLineWrapper;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Builds and manages the MenuItems that can be used to control the TavernaCommandLine and DataViewer wrappers.
 * 
 * Once the menus have been set up this class regsisters itself to listen to changes from both wrappers.
 * Whenever these change the menus are adjusted based on the lastest information provided by the wrappers.
 * 
 * The role of reacting to menu clicks is delegated to the <code>TavernaActionListener</code>.
 * 
 * @author Christian
 */
public class TavernaMenuManager implements ChangeListener{
     
    CommandLineWrapper commandLine;
    
    DataViewerWrapper dataViewer;
    
    JMenu parentMenu;    
    JMenuItem getTavernaHome;
    JMenuItem getDataViewerHome;
    JMenuItem getFileWorkflow;
    JMenuItem getURLWorkflow;
    JMenuItem getInputs;
    JMenuItem getInputFile;
    JMenuItem getInputURL;
    JMenuItem runWorkflow;
    JMenuItem showResults;
    
    private final static String SET_WORKFLOW = "Set Workflow";
    private final static String FROM_FILE  = " from file";
    private final static String FROM_URL = " from URL";
    private final static String SET_WORKFLOW_FILE = SET_WORKFLOW + FROM_FILE;
    private final static String SET_WORKFLOW_URL = SET_WORKFLOW + FROM_URL;
    private final static String REPLACE_WORKFLOW = "Replace Workflow ";
    private final static String RUN_WORKFLOW = "Run Workflow: ";
    private final static String SET_INPUTS = "Set inputs";
    private final static String REPLACE_INPUTS = "Replace inputs";
    private final static String FOR = " for workflow: ";
    private final static String SET_INPUTS_FILE = SET_INPUTS + FROM_FILE;
    private final static String SET_INPUTS_URL = SET_INPUTS + FROM_URL;
    private final static String SHOW_RESULTS = "Show Results";
    private final static String PREVIOUS_WORKFLOW = " for previous workflow";
            
    /**
     * Creates all the possible MenuItems adding them to the JMenu.
     * 
     * All the menu items are added even if they are not relative to the current state of the wrappers.
     * They are then updated to the current wrapper state which will included hiding some and disabling others.
     * <p>
     * The menu manager adds itself as a change Listener to both wrappers.
     * 
     * @param menu
     * @param commandLineWrapper
     * @param dataViewerWrapper
     * @param listener 
     */
    public TavernaMenuManager(JMenu menu, CommandLineWrapper commandLineWrapper, DataViewerWrapper dataViewerWrapper, 
            TavernaActionListener listener){
        this.commandLine  = commandLineWrapper;
        commandLine.addChangeListener(this);
        this.dataViewer = dataViewerWrapper;
        dataViewer.addChangeListener(this);
        parentMenu = menu;
        getTavernaHome = createMenuItem("Set TravernaHome", "getTavernaHome", listener);
        getFileWorkflow = createMenuItem(SET_WORKFLOW_FILE, "getFileWorkflow", listener);
        getURLWorkflow = createMenuItem(SET_WORKFLOW_URL, "getURLWorkflow", listener);
        getInputs = createMenuItem(SET_INPUTS, "getInputs", listener);
        getInputFile = createMenuItem(SET_INPUTS_FILE, "getInputFile", listener);
        getInputURL = createMenuItem(SET_INPUTS_URL,"getInputURL", listener);
        runWorkflow = createMenuItem(RUN_WORKFLOW, "runWorkflow", listener);
        getDataViewerHome = createMenuItem("Set DataViewerHome", "getDataViewerHome", listener);
        showResults = createMenuItem(SHOW_RESULTS + PREVIOUS_WORKFLOW,"showResults", listener);
        reset();
    }
    
    private JMenuItem createMenuItem(String text, String actionText, TavernaActionListener listener){
        JMenuItem item = new JMenuItem(text);
        item.setActionCommand(actionText);
        item.addActionListener(listener);
        parentMenu.add(item);
        return item;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        reset();
    }
    
    private void reset(){
        checkTravernaHome();
        checkWorkflowSet();
        checkInputs();
        checkReadyToRun();
        checkDataViewer();
    }
        
    private void checkTravernaHome(){    
        if (commandLine.isTavernaHomeOk()){
            getTavernaHome.setVisible(false);
            getFileWorkflow.setVisible(true);
            getURLWorkflow.setVisible(true);
            getInputs.setVisible(true);
            getInputFile.setVisible(true);
            getInputURL.setVisible(true);
            runWorkflow.setVisible(true);
        } else {
            getTavernaHome.setVisible(true);
            getFileWorkflow.setVisible(false);
            getURLWorkflow.setVisible(false);
            getInputs.setVisible(false);
            getInputFile.setVisible(false);
            getInputURL.setVisible(false);
            runWorkflow.setVisible(false);            
        }
    }
    
    private void checkDataViewer() {
        if (dataViewer.isDataViewerHomeOk()){
            getDataViewerHome.setVisible(false);
            showResults.setVisible(true);
            if (dataViewer.hasDataFile()){
                if (commandLine.getWorkflowName() == null) {
                    showResults.setText(SHOW_RESULTS);
                } else {
                    showResults.setText(SHOW_RESULTS + FOR + commandLine.getWorkflowName());
                }
            } else {
                showResults.setText(SHOW_RESULTS + PREVIOUS_WORKFLOW);
            }
        } else {
            getDataViewerHome.setVisible(true);
            showResults.setVisible(false);
        }
    }
        
    private void checkWorkflowSet(){
        String workflowName = commandLine.getWorkflowName();
        if (workflowName == null){
            getFileWorkflow.setText(SET_WORKFLOW_FILE);
            getURLWorkflow.setText(SET_WORKFLOW_URL);
            runWorkflow.setText(RUN_WORKFLOW);
        } else {
            getFileWorkflow.setText(REPLACE_WORKFLOW + workflowName + FROM_FILE);
            getURLWorkflow.setText(REPLACE_WORKFLOW + workflowName + FROM_URL);
            runWorkflow.setText(RUN_WORKFLOW + workflowName);  
        }
    }
    
    private void checkInputs(){
         if (commandLine.needsInputs()){
            getInputs.setEnabled(true);
            getInputFile.setEnabled(true);             
            getInputURL.setEnabled(true);
            if (commandLine.readyToRun()) {
                getInputs.setText(REPLACE_INPUTS + FOR + commandLine.getWorkflowName());
                getInputFile.setText(REPLACE_INPUTS + FOR + commandLine.getWorkflowName() + FROM_FILE);
                getInputURL.setText(REPLACE_INPUTS + FOR + commandLine.getWorkflowName() + FROM_URL);                
            } else {
                getInputs.setText(SET_INPUTS + FOR + commandLine.getWorkflowName());
                getInputFile.setText(SET_INPUTS + FOR + commandLine.getWorkflowName() + FROM_FILE);
                getInputURL.setText(SET_INPUTS + FOR + commandLine.getWorkflowName() + FROM_URL);
            }
         } else {
            getInputs.setEnabled(false);
            getInputFile.setEnabled(false);             
            getInputURL.setEnabled(false);
            getInputs.setText(SET_INPUTS);
            getInputFile.setText(SET_INPUTS_FILE);
            getInputURL.setText(SET_INPUTS_URL);
         }
    }
    
    private void checkReadyToRun(){
        if (commandLine.readyToRun()){
            runWorkflow.setEnabled(true); 
        } else {
            runWorkflow.setEnabled(false);             
        }
    }

}
