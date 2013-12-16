package net.sourceforge.ondex.taverna;

import java.io.IOException;
import net.sourceforge.ondex.taverna.wrapper.DataViewerWrapper;
import net.sourceforge.ondex.taverna.wrapper.CommandLineWrapper;
import javax.swing.JFrame;

import java.io.File;
import javax.swing.JMenu;

/**
 * Provides the setting up methods for the Taverna CommandLine and DataViewer Wrappers.
 * 
 * Provides a unique access point to pass in the parent frame, set the root/data directory, 
 * set Travena and DataViewer home (if required).
 * <p>
 * This sets up a {@link CommandLineWrapper} and a {@link DataViwerWrapper} which 
 * both called Taverna using bat or sh files.
 * <p>
 * Then a JMenu is passed in, onto which the Menu items that control Taverna are placed.
 * These are backed by {@link TavernaMenuManager} that keeps the menu up to date and 
 *    the {@link TavernaActionListener} that obtains settings from the users and calls taverna.
 * 
 * <p>
 * This class wraps the functionality offered by {@link CommandLineWrapper} and {@link DataViewerWrapper}.
 * @author Christian
 */
public class TavernaWrapper implements TavernaApi{

    private TavernaMenuManager menuManager;
    
    private TavernaActionListener listener;
    
    private CommandLineWrapper commandLine;
    
    private DataViewerWrapper dataViewer;
    
    /**
     * Constructs a Wrapper which uses the frame as the parent for any popups.
     * 
     * Sets up the wrapper so the methods can be called or a JMenu can be passed in.
     * <p>
     * Attemps to discover the location of the Taverna Command Line, and Data Viewer tools 
     *     based on the System Properties "TAVERNA_HOME" and "TAVERNA_DATAVIEWER_HOME".
     * 
     * @param parent Frame to be used as a parent for all popups.
     * @throws TavernaException This will be thrown if the expected system properties exist 
     *     but point to an incorrect location.
     */
    public TavernaWrapper(JFrame parent) throws TavernaException{
        commandLine = new CommandLineWrapper();
        setTavernaHome(System.getenv("TAVERNA_HOME"));
        dataViewer = new DataViewerWrapper();
        setDataViewerHome(System.getenv("TAVERNA_DATAVIEWER_HOME"));
        
        listener = new TavernaActionListener (parent, commandLine, dataViewer);
     }
    
   /**
    * Alternative method for setting the directory in which the Taverna Command Line tool can be found.
    * 
    * The prefer way is to set the Environment variable "TAVERNA_HOME".
    * This method however allow the value to come from the calling application or its config file.
    * <p>
    * Null values are safely ignored so there is no requirement to check for null before calling this method.
    * </p>
    * @param tavernaHome Path to directory that points to the location where Taverna Command Line can be found or null.
    * @throws TavernaException Normally caused by some non null value pointing to an unusable location. 
    * This includes a location that can not be found, one that can not be read, one that can executed or 
    * one that does not conatin the expected script file.
    */
    @Override
    public final void setTavernaHome(String tavernaHome) throws TavernaException {
        System.out.println(commandLine);
        if (tavernaHome != null && !tavernaHome.isEmpty()){
            setTavernaHome(new File(tavernaHome));        
        } 
    }

   /**
    * Alternative method for setting the directory in which the Taverna Command Line tool can be found.
    * 
    * The prefer way is to set the Environment variable "TAVERNA_HOME".
    * This method however allow the value to come from the calling application or its config file.
    * 
    * @param tavernaHome Directory that contains the scripts to run the taverna command line.
    *    May not be null.
    * @throws TavernaException Normally caused by null or an unusable location. 
    * This includes a location that can not be found, one that can not be read, one that can executed or 
    * one that does not conatin the expected script file, or where the script is not exectuable.
    */
    public final void setTavernaHome(File tavernaHome) throws TavernaException{
        try {
            commandLine.setTavernaHome(tavernaHome);
        } catch (IOException ex) {
            throw new TavernaException("Error setting Taverna home", ex);
        }
   }
            
   /**
    * Alternative method for setting the directory in which the Taverna Data Viewer tool can be found.
    * 
    * The prefer way is to set the Environment variable "TAVERNA_DATAVIEWER_HOME"".
    * This method however allow the value to come from the calling application or its config file.
    * <p>
    * Null values are safely ignored so there is no requirement to check for null before calling this method.
    * </p>
    * @param dataViewerHome Path to directory that points to the location where Taverna Data Viewer tool can be found or null.
    * @throws TavernaException Normally caused by some non null value pointing to an unusable location. 
    * This includes a location that can not be found, one that can not be read, one that can executed or 
    * one that does not conatin the expected script file.
    */
    @Override
    public final void setDataViewerHome(String dataViewerHome) throws TavernaException {
        if (dataViewerHome != null && !dataViewerHome.isEmpty()){
            try {
                dataViewer.setDataViewerHome(new File (dataViewerHome));
            } catch (IOException ex) {
                throw new TavernaException("Error setting Taverna home", ex);
            }
        } 
    }
    
   /**
    * Sets the root directory of the output directory into which the output directories and their files will be written.
    *
    * <p> 
    * Creates if required a subdirectory "Output" which will be used as the parent for the individual run output directories.
    * @param rootDir Directory to be uses as the parent for the output directory of each run.
     *@throws TavernaException Thrown when the directory can not be used including because it was null, could not be found, 
    *     was not a directory or could not be written to.
    */
    @Override
    public void setRootDirectory(File rootDir) throws TavernaException {
        if (!rootDir.exists()){
            throw new TavernaException(rootDir.getAbsolutePath() + " does not exists");
        }
        File outputDir = new File(rootDir, "Output");
        if (!outputDir.exists()){
            outputDir.mkdir();
        }
        try {
            commandLine.setOutputRootDirectory(outputDir);
        } catch (IOException ex) {
            throw new TavernaException("Error setting Taverna home", ex);
        }      
    }

   /**
     * Attaches the Taverna Wrapper menu items to this JMenu.
     * 
     * The actual menuItems that are be shown, enabled and their texts will change over time.
     * These depends on the current state of data help by the tool. 
     * @param menu JMenu under which the Taverna specific items should be added.
     */ 
    @Override
    public void attachMenu(JMenu menu) {
        menuManager = new TavernaMenuManager(menu, commandLine, dataViewer, listener);
    }

    /**
     * Testing method to be called from TavernaMini
     * @param file
     * @throws IOException
     * @throws TavernaException 
     */
    void setWorkflowFile(File file) throws IOException, TavernaException{
        commandLine.setWorkflowFile(file);
    }
}
