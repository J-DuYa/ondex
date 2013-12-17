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
public class CommandLineRunTest {
    
    static File tavernaHome;
    static File outputRoot;
    static final String RESOURCE_PATH = "src/test/resources/";
    static String helloWorld;
    static String echo;
    
    public CommandLineRunTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    	if (System.getenv("TAVERNA_HOME") == null)
    		return;
        tavernaHome = new File(System.getenv("TAVERNA_HOME"));
        outputRoot = new File ("tempDirectory");
        File file = new File(RESOURCE_PATH +  "helloWorld.t2flow");
        helloWorld =  "file:" + file.getAbsolutePath();
        file = new File(RESOURCE_PATH +  "Echo.t2flow");
        echo =  "file:" + file.getAbsolutePath();
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

    /**
     * Prevents test running on System without tavernaHome
     * @return 
     */
    static boolean noTest(){
        return CommandLineWrapperTest.noTest();
    }

    /**
     * Test of wasSuccessful method, of class CommandLineRun.
     */
    @Test
    public void testWasSuccessful() throws TavernaException, IOException, ProcessException {
        if (noTest()) return;
        CommandLineRun instance =  new CommandLineRun(tavernaHome, outputRoot, null, null, helloWorld);
        boolean expResult = true;
        boolean result = instance.wasSuccessful();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputFile method, of class CommandLineRun.
     */
    @Test
    public void testGetOutputFile() throws TavernaException, IOException, ProcessException {
        if (noTest()) return;
        CommandLineRun instance =  new CommandLineRun(tavernaHome, outputRoot, null, null, helloWorld);
        File result = instance.getOutputFile();
        assertTrue(result.exists());
    }

    /**
     * Test of getRunInfo method, of class CommandLineRun.
     */
    @Test
    public void testGetRunInfo() throws TavernaException, IOException {
        if (noTest()) return;
        CommandLineRun instance =  new CommandLineRun(tavernaHome, outputRoot, null, null, helloWorld);
        String expResult = "";
        String result = instance.getRunInfo();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutput method, of class CommandLineRun.
     */
    @Test
    public void testGetOutput() throws TavernaException, IOException, ProcessException {
        if (noTest()) return;
        CommandLineRun instance =  new CommandLineRun(tavernaHome, outputRoot, null, null, helloWorld);
        String result = instance.getOutput();
        assertTrue(result.contains("Run directory"));
        assertTrue(result.contains("-outputdoc="+outputRoot.getAbsolutePath()));
        assertTrue(result.contains(tavernaHome.getAbsolutePath()));
        assertTrue(result.contains("Started"));
        assertTrue(result.contains("Finished by:"));
        assertTrue(result.contains("Run result was: 0"));
    }

    /**
     * Test of destroy method, of class CommandLineRun.
     */
    @Test
    public void testDestroy() throws TavernaException, IOException, ProcessException {
        if (noTest()) return;
        CommandLineRun instance =  new CommandLineRun(tavernaHome, outputRoot, null, null, helloWorld);
        instance.destroy();
        String expResult = "";
        String result = instance.getOutput();
        assertEquals(expResult, result);
    }
    
//    @Test
//    public void testSpaceInInput() throws TavernaException, IOException {
}
