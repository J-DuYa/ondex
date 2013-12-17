/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.ondex.taverna.wrapper;

import java.io.File;
import java.io.IOException;
import net.sourceforge.ondex.taverna.TavernaException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class DataViewerWrapperTest {
    
    public DataViewerWrapperTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    private DataViewerWrapper withHomeSet() throws TavernaException, IOException{
        if (System.getenv("TAVERNA_DATAVIEWER_HOME") == null)
        	return null;
    	File newHome = new File(System.getenv("TAVERNA_DATAVIEWER_HOME"));
        DataViewerWrapper instance = new DataViewerWrapper();
        instance.setDataViewerHome(newHome);
        return instance;
    }
    
    //Test of setDataViewerHome method, of class DataViewerWrapper.
    @Test
    public void testSetDataViewerHome() throws TavernaException, IOException {
        if (withHomeSet() == null)
        	return;
    	boolean result = withHomeSet().isDataViewerHomeOk();
        assertTrue(result);
    }

    @Test
    public void testSetDataViewerHomeNull()  throws TavernaException, IOException{
        DataViewerWrapper instance = new DataViewerWrapper();
        try { 
            instance.setDataViewerHome(null);
            fail("NullPointerException expected");
        } catch (NullPointerException ex){
            //Ok Exception
        }    
    }

    @Test
    public void testSetDataViewerHomeWrongFile() throws TavernaException, IOException {
    	if (System.getenv("TAVERNA_DATAVIEWER_HOME") == null)
        	return;
    	File newHome = new File(System.getenv("TAVERNA_DATAVIEWER_HOME"));
        DataViewerWrapper instance = new DataViewerWrapper();
        try {
            instance.setDataViewerHome(newHome.getParentFile());
            fail("Exception expected");
        } catch (Exception ex){
            //Ok Exception
        }                
    }

    // Test of getDataViewerHome method, of class DataViewerWrapper.
    //See testSetDataViewerHome()
    
    @Test
    public void testGetDataViewerHomeNotSet() throws TavernaException, IOException {
        DataViewerWrapper instance = new DataViewerWrapper();
        try {
            File result = instance.getDataViewerHome();
            fail("TavernaException expexted");
        } catch (TavernaException ex){
            //OK Expected
        }
    }

    // Test of isDataViewerHomeOk method, of class DataViewerWrapper.
    @Test
    public void testIsDataViewerHomeOk() throws TavernaException, IOException {
    	 if (withHomeSet() == null)
         	return;
    	boolean result = withHomeSet().isDataViewerHomeOk();
        assertTrue(result);
    }

    @Test
    public void testIsDataViewerHomeOkNotSet() throws TavernaException, IOException {
        DataViewerWrapper instance = new DataViewerWrapper();
        boolean result = instance.isDataViewerHomeOk();
        assertFalse(result);
    }

    // Test of setDataFile method, of class DataViewerWrapper.
    @Test
    public void testSetDataFile() throws IOException, TavernaException {
    	if (withHomeSet() == null)
        	return;
    	//Input and output files have the same format.
        File dataFile = new File(CommandLineWrapperTest.RESOURCE_PATH +  "ThreeStringsInput.xml");
        DataViewerWrapper instance = withHomeSet();
        instance.setDataFile(dataFile);
        boolean result = instance.hasDataFile();
        assertTrue(result);
    }

    public void testSetDataFileNoHome() throws IOException, TavernaException {
        //Input and output files have the same format.
        File dataFile = new File(CommandLineWrapperTest.RESOURCE_PATH +  "ThreeStringsInput.xml");
        DataViewerWrapper instance = new DataViewerWrapper();
        instance.setDataFile(dataFile);
        boolean result = instance.hasDataFile();
        assertTrue(result);
    }
    
    @Test
    public void testSetDataFileNull() throws IOException, TavernaException {
        try {
            withHomeSet().setDataFile(null);
            fail("NullPointerException expected");
        } catch (NullPointerException ex){
            //Ok Exception
        }    
    }

    // Test of replaceDataFile and hasDataFile methods, of class DataViewerWrapper.
    @Test
    public void testReplaceDataFile() throws Exception {
    	if (withHomeSet() == null)
        	return;
    	//Input and output files have the same format.
        File dataFile = new File(CommandLineWrapperTest.RESOURCE_PATH +  "ThreeStringsInput.xml");
        DataViewerWrapper instance = withHomeSet();
        instance.replaceDataFile(dataFile);
        boolean result = instance.hasDataFile();
        assertTrue(result);
    }

    @Test
    public void testReplaceDataFileNull() throws Exception {
    	if (withHomeSet() == null)
        	return;
    	DataViewerWrapper instance = withHomeSet();
        instance.replaceDataFile(null);
        //replace can be used to remove the old file.
        //So if run fails no automatic file is available
        boolean result = instance.hasDataFile();
        assertFalse(result);
    }
    
}
