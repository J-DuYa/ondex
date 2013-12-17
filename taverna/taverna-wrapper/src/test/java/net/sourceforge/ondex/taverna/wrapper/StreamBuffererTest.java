/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.ondex.taverna.wrapper;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.OutputStream;
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
public class StreamBuffererTest {
    
    public static String NEW_LINE = System.getProperty("line.separator");

    public StreamBuffererTest() {
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

    /**
     * Test of run method, of class StreamBufferer.
     */
    @Test
    public void testRun() throws IOException {
    	if (!OSUtils.isWindows())
        	return;
    	
        System.out.println("run");
        PipedOutputStream output = new PipedOutputStream();
        
        PipedInputStream input = new PipedInputStream(output,1);
        StringBuilder builder = new StringBuilder();
        StreamBufferer instance = new StreamBufferer(input, "Test", builder);
        instance.start();
        
        output.write("This is a test".getBytes());
        output.write(NEW_LINE.getBytes());
        output.write("Then a bit more".getBytes());
        output.write(NEW_LINE.getBytes());
        output.flush();
        output.close();
        
        String result = builder.toString();
        String expected = "Test>This is a test" + NEW_LINE + "Test>Then a bit more" + NEW_LINE;
        assertEquals(expected,result);
    }
}
