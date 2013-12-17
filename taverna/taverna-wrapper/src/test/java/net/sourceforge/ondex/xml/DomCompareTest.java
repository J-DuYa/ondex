package net.sourceforge.ondex.xml;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian
 */
public class DomCompareTest extends XMTestBase{
    
    public DomCompareTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        small = smallNoWhitespace();
        wide = wide();
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

    // Test of trimEmptyTextNodes method, of class DomCompare.
    // Test of trimEmptyTextNodes method, of class XMLReader.
    @Test
    public void testTrimEmptyTextNodes() throws ParserConfigurationException, SAXException, IOException {
        File file = new File(RESOURCE_PATH +  "smallWithWhiteSpace.xml");
        Document read =  XMLReader.readFile(file);
        DomCompare.trimEmptyTextNodes(read);
        String result = DomCompare.compareNodes(small, read);
        assertEquals(DomCompare.NO_DIFFERENCE, result);
    }

    //No test for input null as while is while makes sense that trim of null is null this does not need to be guaranteed
    
    /**
     * Test of compareNodes method, of class DomCompare.
     */
    @Test
    public void testCompareNodes_3args() throws ParserConfigurationException, SAXException, IOException {
        File file = new File(RESOURCE_PATH +  "smallWithWhiteSpace.xml");
        Document withSpace =  XMLReader.readFile(file);
        file = new File(RESOURCE_PATH +  "smallNoWhiteSpace.xml");
        Document noSpace =  XMLReader.readFile(file);
        boolean trimEmptyTextNodes = false;
        String expResult = "";
        String result = DomCompare.compareNodes(withSpace, noSpace, true);
        assertEquals(DomCompare.NO_DIFFERENCE, result);
    }

    /**
     * Test of compareNodes method, of class DomCompare.
     */
    @Test
    public void testCompareNodes_Node_Node() throws ParserConfigurationException, SAXException, IOException {
        File file = new File(RESOURCE_PATH +  "smallNoWhiteSpace.xml");
        Document noSpace =  XMLReader.readFile(file);
        String result = DomCompare.compareNodes(small, noSpace);
        assertEquals(DomCompare.NO_DIFFERENCE, result);
    }
}
