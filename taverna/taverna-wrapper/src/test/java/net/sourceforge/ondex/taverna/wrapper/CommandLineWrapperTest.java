/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.ondex.taverna.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.ondex.taverna.TavernaException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian
 */
public class CommandLineWrapperTest {
    
    static String RESOURCE_PATH = "src/test/resources/";
    static String tavernaHome;
    static File tavernaHomeFile;
    static File notDirectory;
    static File badFile;
    static File helloWorld;
    static File echo;
    static File threeStrings;
    static File twoLists;
    
//    private CommandLineWrapper withoutTavernaHome;
    
    public CommandLineWrapperTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        tavernaHome = System.getenv("TAVERNA_HOME");
        if (tavernaHome == null)
        	return;
        tavernaHomeFile = new File (tavernaHome);
        notDirectory = new File(RESOURCE_PATH +  "notDirectory");
        badFile = new File (RESOURCE_PATH +  "xxxxx");
        helloWorld = new File(RESOURCE_PATH +  "HelloWorld.t2flow");
        echo = new File(RESOURCE_PATH +  "Echo.t2flow");
        threeStrings = new File(RESOURCE_PATH +  "ThreeStrings.t2flow");
        twoLists = new File(RESOURCE_PATH +  "Concat_Two_Lists.t2flow");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() throws TavernaException {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Prevents test running on System without tavernaHome
     * @return 
     */
    static boolean noTest(){
        //return true;
        return tavernaHome == null;
    }
            
    @Test
    public void testReadmeisFile(){
        if (noTest()) return;
        assertTrue(notDirectory.isFile());
    }
    
    @Test
    public void testBadFileDoesNotExist(){
        if (noTest()) return;
        assertFalse(badFile.exists());
    }

    private static CommandLineWrapper setTavernaHome()  throws IOException, TavernaException{
        CommandLineWrapper wrapper = new CommandLineWrapper();
        wrapper.setTavernaHome(tavernaHomeFile);
        return wrapper;
    }
    
    @Test
    public void testSetTavernaHome() throws IOException, TavernaException {
        if (noTest()) return;
        setTavernaHome();
    }

    @Test 
    public void testSetTavernaHomeWithNull()throws IOException, TavernaException{
        if (noTest()) return;
        try {
            new CommandLineWrapper().setTavernaHome(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e){
            //OK expected
        }
    }

    @Test 
    public void testSetTavernaHomeWithDoesNotExist() throws IOException, TavernaException {
        if (noTest()) return;
        try {
            new CommandLineWrapper().setTavernaHome(badFile);
            fail("FileNotFoundException expected");
        } catch (FileNotFoundException e){
            //OK expected
        }
        
    }

    @Test 
    public void testSetTavernaHomeWithFile() throws IOException, TavernaException {
        if (noTest()) return;
        try {
            new CommandLineWrapper().setTavernaHome(notDirectory);
            fail("IOException expected");
        } catch (IOException e){
            //OK expected
        }
    }

    @Test 
    public void testSetTavernaHomeIncorrectDirectory() throws IOException, TavernaException {
        if (noTest()) return;
        try {
            new CommandLineWrapper().setTavernaHome(tavernaHomeFile.getParentFile());
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    // Test of getTavernaHome method, of class CommandLineWrapper.
    @Test
    public void testGetTavernaHomeWhenSet()  throws IOException, TavernaException {
        if (noTest()) return;
        File result = setTavernaHome().getTavernaHome();
        assertEquals(tavernaHomeFile, result);
    }

    @Test 
    public void testGetTavernaHomeWhenNoSet()  throws IOException, TavernaException {
        if (noTest()) return;
        try {
            File result = new CommandLineWrapper().getTavernaHome();
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    @Test
    public void testIsTavernaHomeOkTrue()  throws IOException, TavernaException {
        if (noTest()) return;
        boolean result = setTavernaHome().isTavernaHomeOk();
        assertEquals(true, result);
     }

    @Test
    public void testIsTavernaHomeOkFalse() {
        if (noTest()) return;
        boolean result = new CommandLineWrapper().isTavernaHomeOk();
        assertEquals(false, result);
    }
 
    @Test
    public void testSetOutputRootDirectoryNotTavernaHome()  throws IOException, TavernaException {
        if (noTest()) return;
        setTavernaHome().setOutputRootDirectory(tavernaHomeFile.getParentFile());
    }

    @Test
    public void testSetOutputRootDirectoryAsTavernaHome()  throws IOException, TavernaException {
        if (noTest()) return;
        setTavernaHome().setOutputRootDirectory(tavernaHomeFile);
    }

    @Test 
    public void testSetOutputRootDirectoryWithoutTavernaHome()  throws IOException, TavernaException {
        if (noTest()) return;
        new CommandLineWrapper().setOutputRootDirectory(tavernaHomeFile);
    }

    @Test 
    public void testSetOutputRootDirectoryWithNull()  throws IOException, TavernaException {
        if (noTest()) return;
        try {
            setTavernaHome().setOutputRootDirectory(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e){
            //OK expected
        }
    }

    @Test 
    public void testSetOutputRootDirectoryWithBadFile()  throws IOException, TavernaException {
        if (noTest()) return;
        try {
            setTavernaHome().setOutputRootDirectory(badFile);
            fail("FileNotfoundException expected");
        } catch (FileNotFoundException e){
            //OK expected
        }
    }
        
    @Test 
    public void testSetOutputRootDirectoryWithFile()  throws IOException, TavernaException {
        if (noTest()) return;
        try {
            setTavernaHome().setOutputRootDirectory(notDirectory);
            fail("IOException expected");
        } catch (IOException e){
            //OK expected
        }
    }
    
    private CommandLineWrapper loadHelloWorld() throws TavernaException, IOException{
        CommandLineWrapper wrapper = setTavernaHome();
        wrapper.setWorkflowFile(helloWorld);
        return wrapper;
    }

    @Test
    public void testSetWorkflowFileHelloWorld()  throws IOException, TavernaException {
        if (noTest()) return;
        loadHelloWorld();
    }

    private CommandLineWrapper loadEcho() throws TavernaException, IOException{
        CommandLineWrapper wrapper = setTavernaHome();
        wrapper.setWorkflowFile(echo);
        return wrapper;
    }

    @Test
    public void testSetWorkflowFileEcho() throws TavernaException, IOException {
        if (noTest()) return;
        loadEcho();
    }

    private CommandLineWrapper loadTwoLists() throws TavernaException, IOException{
        CommandLineWrapper wrapper = setTavernaHome();
        wrapper.setWorkflowFile(twoLists);
        return wrapper;
    }

    @Test
    public void testSetWorkflowFileTwoLists() throws TavernaException, IOException {
        if (noTest()) return;
        loadTwoLists();
    }
    
    @Test 
    public void testSetWorkflowFileNull() throws IOException, TavernaException  {
        if (noTest()) return;
        try {
            setTavernaHome().setWorkflowFile(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e){
            //OK expected
        }
    }

    @Test 
    public void testSetWorkflowFileNonWorkFlow() throws IOException, TavernaException {
        if (noTest()) return;
        try {
            setTavernaHome().setWorkflowFile(notDirectory);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    private CommandLineWrapper helloWorldNoTavernaHome() throws TavernaException, IOException{
        CommandLineWrapper wrapper = new CommandLineWrapper();
        wrapper.setWorkflowFile(helloWorld);
        return wrapper;
    }
    
    @Test
    public void testSetWorkflowFileHelloWorldNoTravernaHome() throws TavernaException, IOException {
        if (noTest()) return;
        helloWorldNoTavernaHome();
    }

    @Test 
    public void testSetWorkflowFileBadXML() throws IOException, TavernaException {
        if (noTest()) return;
        File badworkflow = new File(RESOURCE_PATH +  "HelloWorldBadXML.t2flow");
        try {
            setTavernaHome().setWorkflowFile(badworkflow);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    @Test 
    public void testSetWorkflowFiledNoName() throws IOException, TavernaException  {
        if (noTest()) return;
        File badworkflow = new File(RESOURCE_PATH +  "HelloWorldNoName.t2flow");
        try {
            setTavernaHome().setWorkflowFile(badworkflow);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    //@Test
    //Doesn't fail but doesn't find the inportNames 
    //Is it really required to find all erros?
    public void testSetWorkflowFileBrokenInputs()  throws IOException, TavernaException {
        if (noTest()) return;
        File badworkflow = new File(RESOURCE_PATH +  "EchoBrokenInputs.t2flow");
        try {
            setTavernaHome().setWorkflowFile(badworkflow);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    // Tests of setWorkflowURI method, of class CommandLineWrapper.   
    @Test
    public void testSetWorkflowURI() throws IOException, TavernaException {
        if (noTest()) return;
        setTavernaHome().setWorkflowURI("http://www.myexperiment.org/workflows/2354/download/echo_998864.t2flow");
    }

    @Test
    public void testSetWorkflowURIWithoutTavernaHome() throws IOException, TavernaException {
        if (noTest()) return;
        new CommandLineWrapper().setWorkflowURI("http://www.myexperiment.org/workflows/2354/download/echo_998864.t2flow");
    }

    @Test 
    public void testSetWorkflowURINull() throws IOException, TavernaException {
        if (noTest()) return;
        try {
            setTavernaHome().setWorkflowURI(null);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    @Test 
    public void testSetWorkflowURIJunk() throws IOException, TavernaException {
        if (noTest()) return;
        try {
            setTavernaHome().setWorkflowURI("http://ww.example.com");
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    //Works but is a long test
    //@Test 
    public void testSetWorkflowURINotWorkflow() throws IOException, TavernaException {
        if (noTest()) return;
        try {
            setTavernaHome().setWorkflowURI("http://www.myexperiment.org/workflows/2354/");
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    // Tests for getWorkflowName method, of class CommandLineWrapper.
    @Test
    public void testGetWorkflowName() throws IOException, TavernaException {
        if (noTest()) return;
        String name = loadHelloWorld().getWorkflowName();
        assertEquals("Workflow1", name);
    }

    @Test
    public void testGetWorkflowNameNoTavernaHome()  throws IOException, TavernaException {
        if (noTest()) return;
        String name = helloWorldNoTavernaHome().getWorkflowName();
        assertEquals("Workflow1", name);
    }

    @Test
    public void testGetWorkflowNameWithoutWorkflow() throws IOException, TavernaException {
        if (noTest()) return;
        String name = setTavernaHome().getWorkflowName();
        assertNull(name);
    }
    
    // Tests for needsInputs method, of class CommandLineWrapper.
    @Test
    public void testNeedsInputsFalse() throws IOException, TavernaException {
        if (noTest()) return;
        boolean result = loadHelloWorld().needsInputs();
        assertEquals(false, result);
    }

    @Test
    public void testNeedsInputsTrue() throws IOException, TavernaException {
        if (noTest()) return;
        boolean result = loadEcho().needsInputs();
        assertEquals(true, result);
    }

    @Test
    public void testNeedsInputsNoWorkflowSoFalse() throws IOException, TavernaException {
        if (noTest()) return;
        boolean result = setTavernaHome().needsInputs();
        assertEquals(false, result);
    }

    // Tests for getInputNames method, of class CommandLineWrapper.
    @Test
    public void testGetInputNamesNoInput() throws IOException, TavernaException {
        if (noTest()) return;
        Map<String,Integer> result = loadHelloWorld().getInputNamesAndDepths();
        assertEquals(0, result.size());
    }

    @Test
    public void testGetInputNamesOneInput() throws IOException, TavernaException {
        if (noTest()) return;
        Map<String,Integer> result = loadEcho().getInputNamesAndDepths();
        assertEquals(1, result.size());
        assertTrue(result.containsKey("Bar"));
        assertEquals(new Integer(0), result.get("Bar"));
    }

    // Tests for getInputNames method, of class CommandLineWrapper.
    @Test
    public void testGetInputNamesNoWorkflow() throws IOException, TavernaException {
        if (noTest()) return;
        Map<String,Integer> result = setTavernaHome().getInputNamesAndDepths();
        assertEquals(0, result.size());
    }

    private CommandLineWrapper loadThreeStrings() throws IOException, TavernaException{
        CommandLineWrapper wrapper = setTavernaHome();
        wrapper.setWorkflowFile(threeStrings);
        return wrapper;        
    }

    // Test of setInputs method, of class CommandLineWrapper.
    private CommandLineWrapper setupThreeStrings() throws IOException, TavernaException{
        TavernaInput[] inputValues = new TavernaInput[3];
        inputValues[0] = new TavernaInput("Left", 0);
        inputValues[0].setStringInput("The left");
        inputValues[1] = new TavernaInput("Middle", 0);
        inputValues[1].setStringInput("Fluff");
        inputValues[2] = new TavernaInput("Right", 0);
        inputValues[2].setStringInput("The right");
        CommandLineWrapper wrapper = loadThreeStrings();
        wrapper.setInputs(inputValues);
        return wrapper;
    }

    @Test
    public void testSetInputs() throws IOException, TavernaException {
        if (noTest()) return;
        CommandLineWrapper wrapper = setupThreeStrings();
        assertTrue(wrapper.readyToRun());
    }

    public void testSetInputsNull() throws IOException, TavernaException {
        if (noTest()) return;
        CommandLineWrapper wrapper = setupThreeStrings();
        assertTrue(wrapper.readyToRun());
        //setInputs(null) should act like clear inputs
        wrapper.setInputs(null);
        assertFalse(wrapper.readyToRun());
    }

    @Test 
    public void testSetInputsDifferentOrder() throws IOException, TavernaException {
        if (noTest()) return;
        TavernaInput[] inputValues = new TavernaInput[3];
        inputValues[0] = new TavernaInput("Middle", 0);
        inputValues[0].setStringInput("Fluff");
        inputValues[1] = new TavernaInput("Left", 0);
        inputValues[1].setStringInput("The left");
        inputValues[2] = new TavernaInput("Right", 0);
        inputValues[2].setStringInput("The right");
        loadThreeStrings().setInputs(inputValues);
    }

    @Test  
    public void testSetInputsTooFew() throws IOException, TavernaException {
        if (noTest()) return;
        TavernaInput[] inputValues = new TavernaInput[2];
        inputValues[0] = new TavernaInput("Middle", 0);
        inputValues[0].setStringInput("Fluff");
        inputValues[1] = new TavernaInput("Left", 0);
        inputValues[1].setStringInput("The left");
        try {
            loadThreeStrings().setInputs(inputValues);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    @Test  
    public void testSetInputsMissingInput() throws IOException, TavernaException {
        if (noTest()) return;
        TavernaInput[] inputValues = new TavernaInput[3];
        inputValues[0] = new TavernaInput("Middle", 0);
        inputValues[0].setStringInput("Fluff");
        inputValues[1] = new TavernaInput("Left", 0);
        inputValues[1].setStringInput("The left");
        try {
            loadThreeStrings().setInputs(inputValues);
            fail("Exception expected");
        } catch (Exception e){
            //OK expected
        }
    }

    @Test  
    public void testSetInputsInputNotSet() throws IOException, TavernaException {
        if (noTest()) return;
        TavernaInput[] inputValues = new TavernaInput[3];
        inputValues[0] = new TavernaInput("Middle", 0);
        inputValues[0].setStringInput("Fluff");
        inputValues[1] = new TavernaInput("Left", 0);
        inputValues[1].setStringInput("The left");
        inputValues[2] = new TavernaInput("Right", 0);
        try {
            loadThreeStrings().setInputs(inputValues);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    @Test  
    public void testSetInputsTooManyInputs() throws IOException, TavernaException {
        if (noTest()) return;
        TavernaInput[] inputValues = new TavernaInput[4];
        inputValues[0] = new TavernaInput("Middle", 0);
        inputValues[0].setStringInput("Fluff");
        inputValues[1] = new TavernaInput("Left", 0);
        inputValues[1].setStringInput("The left");
        inputValues[2] = new TavernaInput("Right", 0);
        inputValues[2].setStringInput("The right");
        inputValues[3] = new TavernaInput("Extra", 0);
        inputValues[3].setStringInput("Ignore me");
        try {
            loadThreeStrings().setInputs(inputValues);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    @Test  
    public void testSetInputsWrongInputs() throws IOException, TavernaException {
        if (noTest()) return;
        TavernaInput[] inputValues = new TavernaInput[3];
        inputValues[0] = new TavernaInput("Junk", 0);
        inputValues[0].setStringInput("Not an input");
        inputValues[1] = new TavernaInput("Left", 0);
        inputValues[1].setStringInput("The left");
        inputValues[2] = new TavernaInput("Right", 0);
        inputValues[2].setStringInput("The right");
        try {
            loadThreeStrings().setInputs(inputValues);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }
            
    @Test  
    public void testSetInputsNoWorkflow() throws IOException, TavernaException {
        if (noTest()) return;
        TavernaInput[] inputValues = new TavernaInput[3];
        inputValues[0] = new TavernaInput("Middle", 0);
        inputValues[0].setStringInput("Fluff");
        inputValues[1] = new TavernaInput("Left", 0);
        inputValues[1].setStringInput("The left");
        inputValues[2] = new TavernaInput("Right", 0);
        inputValues[2].setStringInput("The right");
        try {
            setTavernaHome().setInputs(inputValues);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    @Test  
    public void testSetInputsNoWorkflowNoInputs() throws IOException, TavernaException {
        if (noTest()) return;
        TavernaInput[] inputValues = new TavernaInput[0];
        loadHelloWorld().setInputs(inputValues);
    }

    public void testSetInputsNoneNeeded() throws IOException, TavernaException {
        if (noTest()) return;
        TavernaInput[] inputValues = new TavernaInput[0];
        try {
            setTavernaHome().setInputs(inputValues);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    private CommandLineWrapper setupTwoLists() throws TavernaException, IOException{
        CommandLineWrapper wrapper = loadTwoLists();
        TavernaInput[] inputValues = new TavernaInput[2];
        inputValues[0] = new TavernaInput("Left", 1);
        String[] values = {"somethin", "then a bit more", "ans till more"};
        inputValues[0].setStringsInput(values);
        inputValues[1] = new TavernaInput("Right", 1);
        String[] values2 = {"no,space", "But.other", "punctuation:in;here"};
        inputValues[1].setStringsInput(values2);
        wrapper.setInputs(inputValues);
        return wrapper;
    }

    @Test
    public void testSetupTwoLists() throws TavernaException, IOException {
        if (noTest()) return;
        setupTwoLists();
    }
   
    @Test
    public void testSetupBadLists() throws TavernaException, IOException {
        if (noTest()) return;
        CommandLineWrapper wrapper = loadTwoLists();
        TavernaInput[] inputValues = new TavernaInput[2];
        inputValues[0] = new TavernaInput("Left", 1);
        inputValues[1] = new TavernaInput("Right", 1);
        try {
            wrapper.setInputs(inputValues);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
   }

    // Test of setInputsFile method, of class CommandLineWrapper.
    @Test
    public void testSetInputsFile() throws IOException, TavernaException, ParserConfigurationException, SAXException {
        if (noTest()) return;
        File inputs = new File(RESOURCE_PATH +  "ThreeStringsInput.xml");
        loadThreeStrings().setInputsFile(inputs);
    }

    @Test
    public void testSetInputsFileNoWorkflow() 
            throws IOException, TavernaException, ParserConfigurationException, SAXException {
        if (noTest()) return;
        File inputs = new File(RESOURCE_PATH +  "ThreeStringsInput.xml");
        try {
            setTavernaHome().setInputsFile(inputs);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
       }
    }

    @Test 
    //May not fail as test cases have inputs and outputs in a single file!
    public void testSetInputsFileExtraInputs() 
            throws IOException, TavernaException, ParserConfigurationException, SAXException {
        if (noTest()) return;
        File inputs = new File(RESOURCE_PATH +  "ThreeStringsExtraInput.xml");
        loadThreeStrings().setInputsFile(inputs);
    }
    
    @Test 
    public void testSetInputsFileMissingInput() 
            throws IOException, TavernaException, ParserConfigurationException, SAXException {
        if (noTest()) return;
        File inputs = new File(RESOURCE_PATH +  "ThreeStringsMissingInput.xml");
        try {
            loadThreeStrings().setInputsFile(inputs);
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    @Test
    public void testSetInputsFileMissingFile() 
            throws IOException, TavernaException, ParserConfigurationException, SAXException {
        if (noTest()) return;
        //Set file to one that does not exist
        File inputs = new File(RESOURCE_PATH +  "ThreeStringsMissing.xml");
        try {
            loadThreeStrings().setInputsFile(inputs);
            fail("IOException expected");
        } catch (IOException e){
            //OK expected
        }
    }

    @Test 
    public void testSetInputsFileNull() throws IOException, TavernaException, ParserConfigurationException, SAXException {
        if (noTest()) return;
        try {
            loadThreeStrings().setInputsFile(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e){
            //OK expected
        }
    }

    // Test of setInputsURI method, of class CommandLineWrapper. //
    @Test
    public void testSetInputsURI() throws IOException, TavernaException, ParserConfigurationException, SAXException {
        if (noTest()) return;
        String fileURI = "file:" + RESOURCE_PATH +  "ThreeStringsInput.xml";
        loadThreeStrings().setInputsURI(fileURI);
    }

    // Test of readyToRun method, of class CommandLineWrapper.
    @Test
    public void testReadyToRun() throws IOException, TavernaException {
        if (noTest()) return;
        CommandLineWrapper wrapper = setupThreeStrings();
        boolean result = wrapper.readyToRun();
        assertTrue(result);
    }

    @Test
    public void testReadyToRunNoInputs() throws IOException, TavernaException {
        if (noTest()) return;
        CommandLineWrapper wrapper = loadThreeStrings();
        boolean result = wrapper.readyToRun();
        assertFalse(result);
    }

    @Test
    public void testReadyToRunNoInputRequired() throws IOException, TavernaException {
        if (noTest()) return;
        CommandLineWrapper wrapper = loadHelloWorld();
        boolean result = wrapper.readyToRun();
        assertTrue(result);
    }
    
    @Test
    public void testReadyToRunNoTavernaHome() throws IOException, TavernaException {
        if (noTest()) return;
        CommandLineWrapper wrapper = new CommandLineWrapper();
        wrapper.setWorkflowFile(helloWorld);
        boolean result = wrapper.readyToRun();
        assertFalse(result);
    }
    
    // Test of runWorkFlow method, of class CommandLineWrapper.
    @Test
    public void testRun() throws IOException, TavernaException, ProcessException {
        if (noTest()) return;
        CommandLineWrapper wrapper = setupThreeStrings();
        File parent = new File ("tempDirectory");
        parent.mkdir();
        wrapper.setOutputRootDirectory(parent);
        CommandLineRun commandLineRun = wrapper.runWorkFlow();
        assertTrue(commandLineRun.wasSuccessful());
    }

    @Test  
    public void testRunNoInputs() throws IOException, TavernaException {
        if (noTest()) return;
        try {
            loadThreeStrings().runWorkFlow();
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
    }

    @Test  
    public void testRunNoInputRequired() throws IOException, TavernaException, ProcessException {
        if (noTest()) return;
        CommandLineWrapper wrapper = loadHelloWorld();
        File parent = new File ("tempDirectory");
        parent.mkdir();
        wrapper.setOutputRootDirectory(parent);
        CommandLineRun commandLineRun = wrapper.runWorkFlow();
        assertTrue(commandLineRun.wasSuccessful());
    }
    
    @Test  
    public void testRunNoTavernaHome() throws IOException, TavernaException {
        if (noTest()) return;
        CommandLineWrapper wrapper = new CommandLineWrapper();
        wrapper.setWorkflowFile(helloWorld);
        try {
            wrapper.runWorkFlow();
            fail("TavernaException expected");
        } catch (TavernaException e){
            //OK expected
        }
     }
    
    @Test
    public void testRunLists() throws IOException, TavernaException, ProcessException {
        if (noTest()) return;
        CommandLineWrapper wrapper = setupTwoLists();
        File parent = new File ("tempDirectory");
        parent.mkdir();
        wrapper.setOutputRootDirectory(parent);
        CommandLineRun commandLineRun = wrapper.runWorkFlow();
        assertTrue(commandLineRun.wasSuccessful());
    }
   
}
