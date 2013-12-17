/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.ondex.taverna.wrapper;

import java.io.IOException;
import java.io.File;
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
public class ProcessRunnerTest {
    
    public ProcessRunnerTest() {
    }

    @BeforeClass
    public static void setUpClass()  {
    }

    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of start method, of class ProcessRunner.
     */
    @Test
    public void testStart() throws IOException  {
        File parent = new File ("tempDirectory");
        String[] args = new String[2];
        args[0] = "java";
        args[1] = "-version";
        ProcessRunner instance = new ProcessRunner(args, parent);
        instance.start();
    }

    /**
     * Test of start method, of class ProcessRunner.
     */
    @Test
    public void testNoParent() throws IOException {
        File parent = null;
        String[] args = new String[2];
        args[0] = "java";
        args[1] = "-version";
        ProcessRunner instance = new ProcessRunner(args, parent);
        instance.start();
    }

    /**
     * Test of start method, of class ProcessRunner.
     */
    @Test (expected= IOException.class)
    public void testBadParent() throws Exception {
        File parent = new File("DoesNotExist");
        String[] args = new String[2];
        args[0] = "java";
        args[1] = "-version";
        ProcessRunner instance = new ProcessRunner(args, parent);
        instance.start();
    }

    /**
     * Test of destroy method, of class ProcessRunner.
     */
    @Test
    public void testDestroy() throws IOException, InterruptedException, ProcessException{
        File parent = new File("target/classes");
        String[] args = new String[2];
        args[0] = "java";
        args[1] = "net.sourceforge.ondex.taverna.test.EndlessLooper";
        ProcessRunner instance = new ProcessRunner(args, parent);
        instance.start();
        instance.destroy();
        String result = instance.getOutput();
        assertTrue(result.contains("Proecess Destoryed at"));
    }

    /**
     * Test of getRunInfo method, of class ProcessRunner.
     */
    @Test
    public void testGetRunInfo() throws ProcessException, IOException {
        File parent = new File ("tempDirectory");
        String[] args = new String[2];
        args[0] = "java";
        args[1] = "-version";
        ProcessRunner instance = new ProcessRunner(args, parent);
        String result = instance.getRunInfo();
        assertTrue(result.contains(parent.getAbsolutePath()));
        assertTrue(result.contains("java"));
        assertTrue(result.contains("-version"));
    }

    /**
     * Test of waitFor method, of class ProcessRunner.
     */
    @Test
    public void testWaitFor() throws IOException, InterruptedException, ProcessException {
        File parent = new File ("tempDirectory");
        String[] args = new String[2];
        args[0] = "java";
        args[1] = "-version";
        ProcessRunner instance = new ProcessRunner(args, parent);
        instance.start();
        instance.waitFor();
    }

    /**
     * Test of getOutput method, of class ProcessRunner.
     */
    @Test
    public void testGetOutput() throws IOException, InterruptedException, ProcessException {
        File parent = new File("target/classes");
        String[] args = new String[2];
        args[0] = "java";
        args[1] = "net.sourceforge.ondex.taverna.test.HelloWorld";
        ProcessRunner instance = new ProcessRunner(args, parent);
        instance.start();
        String result = instance.getOutput();
        assertTrue(result.contains("net.sourceforge.ondex.taverna.test.HelloWorld"));
        assertTrue(result.contains("Started:"));
        assertTrue(result.contains("Output>Hello World"));
        assertTrue(result.contains("Output>Good bye"));
        assertTrue(result.contains("Error>Error test"));
        assertTrue(result.contains("Finished by:"));
        assertTrue(result.contains("Run result was: 0"));
    }
    
    @Test
    public void testSpaceInArgs() throws IOException, InterruptedException, ProcessException {
        if (!OSUtils.isWindows())
        	return;
    	File parent = new File("target/classes");
        String[] args = new String[5];
        args[0] = "java";
        args[1] = "net.sourceforge.ondex.taverna.test.Echo";
        args[2] = "NOSpace";
        args[3] = "\"With space and quotes\"";
        args[4] = "Still more spacess no quotes";
        ProcessRunner instance = new ProcessRunner(args, parent);
        instance.start();
        String result = instance.getOutput();
        assertTrue(result.contains("Output>args as length 3"));
        assertTrue(result.contains("Output>0 NOSpace"));
        assertTrue(result.contains("Output>1 With space and quotes"));
        assertTrue(result.contains("Output>2 Still more spacess no quotes"));
    }
    
    @Test (expected= ProcessException.class)
    public void testGetOutputNotStarted() throws IOException, InterruptedException, ProcessException {
        File parent = new File("target/classes");
        String[] args = new String[2];
        args[0] = "java";
        args[1] = "net.sourceforge.ondex.taverna.test.HelloWorld";
        ProcessRunner instance = new ProcessRunner(args, parent);
        String result = instance.getOutput();
        assertEquals("hi", result);
    }
}
