package net.sourceforge.ondex.taverna.wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.ondex.taverna.TavernaException;

/**
 * Wrapper around Tavernas data viewer.
 * 
 * @author Christian
 */
public class DataViewerWrapper extends ChangeFirer{

    /** Name of the bat file this is run if windows */
    public static String WINDOWS_LAUCH_FILE = "dataviewer.bat";

    /** Name of the sh script file run if not windows */
    public static String LINUX_LAUCH_FILE = "dataviewer.sh";

    File dataViewerHome;
    
    File dataFile;
    
  /**
    * Sets the directory in which the Taverna Dataviewer tool can be found.
    * 
    * Carries out a minimal validation that the directory at least exists and contains a file with the expected name.
    * The lack of an expception is not a guarantee that the data viewer will run.
    * <p>
    * Fires a ChangeEvent if successful.
    * 
    * @param newHome Directory that contains the scripts to run the taverna data viewer.
    *    May not be null.
    * @throws Exception Normally caused by null or an unusable location. 
    * This includes a location that can not be found, one that can not be read, one that can executed or 
    * one that does not conatin the expected script file, or where the script is not exectuable.
    */    
    public void setDataViewerHome(File newHome) throws TavernaException, IOException{
        dataViewerHome = null;
        Utils.checkDirectory(newHome);
        File tavernaBat = getLaunchFile(newHome);
        if (!tavernaBat.exists()){
            throw new TavernaException (newHome.getAbsolutePath() + 
                    " does not contain an \"" + tavernaBat + "\" File.");
        }
        Utils.checkFileExecutable(tavernaBat);
        this.dataViewerHome = newHome;
        fireStateChanged();
    }
    
   /**
     * Given a directory this method returns the theoretical file that would be used to Luanch the workflow.
     * 
     * No checking is done to see if it actually exists.
     * 
     * @param dataViewerHome Directory expected to hold the bat or sh file.
     * @return Pointer to the expected file.
     */
    public static File getLaunchFile(File dataViewerHome){
        if (Utils.isWindows()){
            return new File (dataViewerHome, WINDOWS_LAUCH_FILE);
        } else {
            return new File (dataViewerHome, LINUX_LAUCH_FILE);            
        }
    }
          
    /* Retreives a pointer to the directory that holds the bat or script file.
     * 
     * Because the setter will only set a correct value this method can be assumed to retrun a correct directory 
     *    or throw an exception. 
     * @return Directory that holds the bat or script file.
     * @throws TavernaException Thrown if the directory has not been set.
     */
    public File getDataViewerHome() throws TavernaException{
        if (dataViewerHome == null){
            throw new TavernaException("Illegal call to getDataViewerHome before it was set");
        }
        return dataViewerHome;
    }
    
   /**
     * Check to see if DataViewerHome has bee set successfully.
     * 
     * The setter is responsible for checking 
     * so in the rare case where the bat file or sh script was moved or deleted after DataViewerHomee was set
     * this method will return a no longer correct result.
     * 
     * @return True if and only if DataViewerHome was successfully set. 
     */
    public boolean isDataViewerHomeOk(){
       return (dataViewerHome != null);
    }

    /**
     * Sets this file as the file to be opened when DataViewer is run.
     * 
     * @param dataFile Valid data file. May not be null.
     * @throws TavernaException If the file does not exist or can not be read.
     * @NullPointerException If the dataFile is null.
     */
    public void setDataFile(File dataFile) throws TavernaException{
        if (dataFile == null){
            throw new NullPointerException("Illegal attempt to set DataFile to null");
        }
        replaceDataFile(dataFile);
    }

    /** 
     * Replaces the file to be opened when DataViewer is run, with this file.
     * 
     * A null value is allowed in which case DataViewer will ready to be opened without a file to load.
     * 
     * @param dataFile Valid data file, or null to clear a previously set one.
     * @throws TavernaException If the file does not exist or can not be read.
     */
    public final void replaceDataFile(File dataFile) throws TavernaException{
        if (dataFile != null){
            if (!dataFile.exists()){
                throw new TavernaException("Data file " + dataFile.getAbsolutePath() + " does not exists.");
            }
            if (!dataFile.canRead()){
                throw new TavernaException("Data file " + dataFile.getAbsolutePath() + " can not be read.");
            }
        }
        this.dataFile = dataFile;
        fireStateChanged();
    }

    /**
     * Checks to see if a datafile has been set with which to open DataViewer.
     * 
     * @return True if and only if a file has been set.
     */
    public boolean hasDataFile() {
        return dataFile != null;
    }
    
    private  ArrayList<String> getLaunch(){
        ArrayList<String> cmd = new ArrayList<String>();
        if (Utils.isWindows()){
            cmd.add(dataViewerHome.getAbsolutePath() + File.separator + WINDOWS_LAUCH_FILE);
        } else {
            cmd.add("sh");
            cmd.add(dataViewerHome.getAbsolutePath() + File.separator + LINUX_LAUCH_FILE);
        }
        return cmd;
        //cmd.add("java");
        //cmd.add("-Xmx300m");
        //cmd.add("-XX:MaxPermSize=140m");
        //cmd.add("-Draven.profile=file:conf/current-profile.xml");
        //cmd.add("-Djava.system.class.loader=net.sf.taverna.raven.prelauncher.BootstrapClassLoader"); 
        //cmd.add("-Draven.launcher.app.main=net.sf.taverna.dataviewer.DataViewerTool");
        //cmd.add("-Draven.launcher.show_splashscreen=false");
        //cmd.add("-jar");
        //cmd.add("lib/" + LAUNCH_FILE);
    }
    
    /*
     * Runs the DataViewer, blocking until it is finished.
     * 
     * If a dataFile has been set the viewer will be asked to open this file.
     * This method blocks until the dataviewer popup has been closed.
     * 
     * @return Output report including parameters with which dataviewer was called, 
     *     plus anything the dataviewer sent to the output and error streams.
     * @throws TavernaException Thrown if DataViewerHome was not set.
     * @throws IOException Thrown if dataViewerHome is not valid after all
     * @throws InterruptedException Thrown if the process is interrupted
     */
    public String runDataViewer() throws TavernaException, IOException, InterruptedException {
       if (dataViewerHome == null){
           throw new TavernaException("DataViewerHome has not been set");
       } 
        ArrayList<String> cmd = getLaunch();
        if (dataFile != null){
            cmd.add(dataFile.getAbsolutePath());
        }
        
        String[] command = new String[0];
        command = cmd.toArray(command);
        ProcessRunner runner = new ProcessRunner(command, dataViewerHome);
        runner.start();
        try {
            return runner.getOutput();
        } catch (ProcessException ex) {
            throw new TavernaException("Unexpected error", ex);
        }
    }
}
