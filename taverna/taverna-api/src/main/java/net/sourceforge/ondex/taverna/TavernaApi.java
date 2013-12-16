package net.sourceforge.ondex.taverna;

import java.io.File;
import javax.swing.JMenu;

/**
 * Minimal API for the Taverna Wrapper class which provides only the setup methods.
 * 
 * Users of this API will expect the implementation and the memu items it adds to handle all the interactions.
 * 
 * @author christian
 */
public interface TavernaApi {

   /**
     * Attaches the Taverna Wrapper menu items to this JMenu.
     * 
     * The actual menuItems that are be shown, enabled and their texts will change over time.
     * These depends on the current state of data help by the tool. 
     * @param menu JMenu under which the Taverna specific items should be added.
     * 
     * @throws TavernaException Thrown if for any reason the menu does not allow menuItems being added.
     */ 
   public void attachMenu(JMenu menu) throws TavernaException;
   
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
   public void setTavernaHome(String tavernaHome) throws TavernaException;
   
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
   public void setDataViewerHome(String dataViewerHome) throws TavernaException;
     
    /**
    * Sets the root directory of the output directory into which the output directories and their files will be written.
    *
    * <p> 
    * Creates if required a subdirectory "Output" which will be used as the parent for the individual run output directories.
    * @param rootDir Directory to be uses as the parent for the output directory of each run.
     *@throws TavernaException Thrown when the directory can not be used including because it was null, could not be found, 
    *     was not a directory or could not be written to.
    */
   public void setRootDirectory(File rootDir) throws TavernaException;
   
}
