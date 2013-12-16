package net.sourceforge.ondex.taverna.wrapper;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
public class TavernaInputTest {
    
    public TavernaInputTest() {
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

    //Test constructor
    @Test
    public void testConstructorDepthZero() throws TavernaException{
        TavernaInput instance = new TavernaInput("Foo", 0);
        assertFalse(instance.hasValue());
    }
    
    @Test
    public void testConstructorDepthOne() throws TavernaException{
        TavernaInput instance = new TavernaInput("Foo", 1);
        assertFalse(instance.hasValue());
    }

    @Test
    public void testConstructorDepthTwo(){
        try {
            TavernaInput instance = new TavernaInput("Foo", 2);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    @Test
    public void testConstructorDepthMinus(){
        try {
            TavernaInput instance = new TavernaInput("Foo", -1);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }
    
    @Test
    public void testConstructorNull() throws TavernaException{
        try {
            TavernaInput instance = new TavernaInput(null, 0);
            fail("NullPointerException expected");
        } catch (NullPointerException e){
            //OK expected
        }
    }

    @Test
    public void testConstructorEmptyName(){
        try {
            TavernaInput instance = new TavernaInput("", 0);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    // Test of setStringInput method, of class TavernaInput.
    @Test
    public void testSetStringInput() throws TavernaException {
        TavernaInput instance = new TavernaInput("Foo", 0);
        instance.setStringInput("Something");
        List<String> args = instance.getInputArguements();
        assertEquals(3, args.size());
        assertEquals("-inputvalue", args.get(0));
        assertEquals("Foo", args.get(1));
        assertEquals("\"Something\"", args.get(2));
        assertTrue(instance.hasValue());
    }

    @Test
    public void testSetStringInputWithSpace() throws TavernaException {
        TavernaInput instance = new TavernaInput("Foo", 0);
        instance.setStringInput("Some thing");
        List<String> args = instance.getInputArguements();
        assertEquals(3, args.size());
        assertEquals("-inputvalue", args.get(0));
        assertEquals("Foo", args.get(1));
        assertEquals("\"Some thing\"", args.get(2));
        assertTrue(instance.hasValue());
    }

    //Taverna Excepts arguements that are not deep enough
    @Test
    public void testSetStringInputDepth1() throws TavernaException {
        TavernaInput instance = new TavernaInput("Foo", 1);
        instance.setStringInput("Some thing");
        List<String> args = instance.getInputArguements();
        assertEquals(3, args.size());
        assertEquals("-inputvalue", args.get(0));
        assertEquals("Foo", args.get(1));
        assertEquals("\"Some thing\"", args.get(2));
        assertTrue(instance.hasValue());
    }

    // Test of setStringsInput method, of class TavernaInput.
    @Test
    public void testSetStringsInput() throws TavernaException {
        String[] value = {"some","thing"};
        TavernaInput instance = new TavernaInput("Foo", 1);
        instance.setStringsInput(value);
        List<String> args = instance.getInputArguements();
        assertEquals(6, args.size());
        char delimiter = args.get(2).charAt(1);
        assertEquals("-inputdelimiter", args.get(0));
        assertEquals("Foo", args.get(1));
        assertEquals("\"" + delimiter + "\"", args.get(2));
        assertEquals("-inputvalue", args.get(3));
        assertEquals("Foo", args.get(4));
        assertEquals("\"some" + delimiter + "thing\"", args.get(5));
        assertTrue(instance.hasValue());
    }

    @Test
    public void testSetStringsInputSpecialCharaters() throws TavernaException {
        String[] value = {"some thing","now,more"};
        TavernaInput instance = new TavernaInput("Foo", 1);
        instance.setStringsInput(value);
        List<String> args = instance.getInputArguements();
        assertEquals(6, args.size());
        char delimiter = args.get(2).charAt(1);
        assertTrue(' ' != delimiter);
        assertTrue(',' != delimiter);
        assertEquals("-inputdelimiter", args.get(0));
        assertEquals("Foo", args.get(1));
        assertEquals("\"" + delimiter + "\"", args.get(2));
        assertEquals("-inputvalue", args.get(3));
        assertEquals("Foo", args.get(4));
        assertEquals("\"some thing" + delimiter + "now,more\"", args.get(5));
        assertTrue(instance.hasValue());
    }

    //Taverna does not accept arguemets that are too deep
    @Test
    public void testSetStringsInputDepthZero() throws TavernaException {
        String[] value = {"some","thing"};
        TavernaInput instance = new TavernaInput("Foo", 0);
        try {
            instance.setStringsInput(value);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
        assertFalse(instance.hasValue());
    }

    //Taverna does not accept empty lists
    @Test
    public void testSetStringsInputEmptyList() throws TavernaException {
        String[] value = new String[0];
        TavernaInput instance = new TavernaInput("Foo", 1);
        try {
            instance.setStringsInput(value);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
        assertFalse(instance.hasValue());
    }

    // Test of setURIInput method, of class TavernaInput.
    @Test
    public void testSetURIInput() throws TavernaException {
        TavernaInput instance = new TavernaInput("Foo", 0);
        instance.setSingleURIInput("http://example.com");
        List<String> args = instance.getInputArguements();
        assertEquals(3, args.size());
        assertEquals("-inputfile", args.get(0));
        assertEquals("Foo", args.get(1));
        assertEquals("http://example.com", args.get(2));
        assertTrue(instance.hasValue());
    }

    @Test
    public void testSetURIInputEmpty() throws TavernaException {
        TavernaInput instance = new TavernaInput("Foo", 0);
        try {
            instance.setSingleURIInput("");
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
        assertFalse(instance.hasValue());
    }

    @Test
    public void testSetURIInputNull() throws TavernaException {
        TavernaInput instance = new TavernaInput("Foo", 0);
        try {
            instance.setSingleURIInput(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e){
            //OK expected
        }
        assertFalse(instance.hasValue());
    }
    
    // Test of setFileInput method, of class TavernaInput.
    @Test
    public void testSetFileInput() throws TavernaException, IOException {
        File file = new File(CommandLineWrapperTest.RESOURCE_PATH + "notDirectory");
        TavernaInput instance = new TavernaInput("Foo", 0);
        instance.setSingleFileInput(file);
        List<String> args = instance.getInputArguements();
        assertEquals(3, args.size());
        String expected = "file:" + file.getAbsolutePath();
        assertEquals("-inputfile", args.get(0));
        assertEquals("Foo", args.get(1));
        assertEquals(expected, args.get(2));
        assertTrue(instance.hasValue());
    }

    @Test
    public void testSetFileInputBadFile() throws TavernaException, IOException {
        File file = new File(CommandLineWrapperTest.RESOURCE_PATH + "XXXXXX");
        TavernaInput instance = new TavernaInput("Foo", 0);
        try {
            instance.setSingleFileInput(file);
            fail("FileNotFoundException expected");
        } catch (FileNotFoundException ex){
            //ok expected
        }
        assertFalse(instance.hasValue());
    }

    @Test
    public void testSetFileInputNull() throws TavernaException, IOException {
        TavernaInput instance = new TavernaInput("Foo", 0);
        try {
            instance.setSingleFileInput(null);
            fail("NullPointerException expected");
        } catch (NullPointerException ex){
            //ok expected
        }
        assertFalse(instance.hasValue());
    }

    /**
     * Test of getName method, of class TavernaInput.
     */
    @Test
    public void testGetName() throws TavernaException {
        TavernaInput instance = new TavernaInput("Foo", 0);
        String result = instance.getName();
        assertEquals("Foo", result);
    }

    // Test of commandLineArguement method, of class TavernaInput.
    //Success tested at same time as setters
    @Test
    public void testCommandLineArguementNoInput() throws TavernaException {
        TavernaInput instance = new TavernaInput("Foo", 0);
        try {
            List<String> result = instance.getInputArguements();
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
        assertFalse(instance.hasValue());
    }
}
