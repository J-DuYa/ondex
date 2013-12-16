package net.sourceforge.ondex.taverna.wrapper;

import java.io.FileNotFoundException;
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
public class UtilsTest {
    
    static String RESOURCE_PATH = "src/test/resources/";

    public UtilsTest() {
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

    // Test of checkDirectory method, of class Utils.
    @Test
    public void testCheckDirectory() throws IOException {
        System.out.println("checkDirectory");
        File dir = new File (RESOURCE_PATH);
        Utils.checkDirectory(dir);
    }

    @Test (expected= NullPointerException.class)
    public void testCheckDirectoryNull() throws IOException {
        System.out.println("checkDirectory");
        File dir = null;
        Utils.checkDirectory(dir);
    }
    
    @Test (expected= IOException.class)
    public void testCheckDirectoryNotDirectory() throws IOException {
        System.out.println("checkDirectory");
        File dir = new File(RESOURCE_PATH +  "notDirectory");
        Utils.checkDirectory(dir);
    }
    
    @Test (expected= IOException.class)
    public void testCheckDirectoryNotExist() throws IOException {
        System.out.println("checkDirectory");
        File dir = new File(RESOURCE_PATH +  "DoesNotExist");
        Utils.checkDirectory(dir);
    }
    
    // Test of checkFileExecutable method, of class Utils.
    @Test
    public void testCheckFileExecutable() throws IOException {
    	if (!OSUtils.isWindows())
        	return;
        File file = new File(RESOURCE_PATH +  "HelloWorld.t2flow");
        Utils.checkFileExecutable(file);
    }

    @Test (expected= NullPointerException.class)
    public void testCheckFileExecutableNull() throws IOException {
        File file = null;
        Utils.checkFileExecutable(file);
    }

    @Test (expected= IOException.class)
    public void testCheckFileExecutableDirectory() throws IOException {
        File file = new File (RESOURCE_PATH);
        Utils.checkFileExecutable(file);
    }

    @Test (expected= FileNotFoundException.class)
    public void testCheckFileExecutableNotThere() throws IOException {
        File file = new File(RESOURCE_PATH +  "DoesNotExist");
        Utils.checkFileExecutable(file);
    }

    // Test of checkFile method, of class Utils.
    @Test
    public void testCheckFile() throws IOException {
        File file = new File(RESOURCE_PATH +  "HelloWorld.t2flow");
        Utils.checkFile(file);
    }

    @Test (expected= NullPointerException.class)
    public void testCheckFileNull() throws IOException {
        File file = null;
        Utils.checkFile(file);
    }

    @Test (expected= IOException.class)
    public void testCheckFileDirectory() throws IOException {
        File file = new File (RESOURCE_PATH);
        Utils.checkFile(file);
    }

    @Test (expected= FileNotFoundException.class)
    public void testCheckFileNotThere() throws IOException {
        File file = new File(RESOURCE_PATH +  "DoesNotExist");
        Utils.checkFile(file);
    }

    // Test of createCalendarBasedDirectory method, of class Utils.
    @Test
    public void testCreateCalendarBasedDirectory() throws IOException {
        System.out.println("createCalendarBasedDirectory");
        File parent = new File ("tempDirectory");
        parent.mkdir();
        File result = Utils.createCalendarBasedDirectory(parent);
        assertTrue(result.isDirectory());
        File[] children = result.listFiles();
        assertTrue(children.length == 0);
    }
}
