package net.sourceforge.ondex.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian
 */
public class XMTestBase {
    
    static String RESOURCE_PATH = "src/test/resources/";
    static Document small;
    static Document wide; 
    
    public XMTestBase() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        small = smallNoWhitespace();
        wide = wide();
    }

    public static Document newDocument() throws ParserConfigurationException{
        //Create instance of DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //Get the DocumentBuilder
        DocumentBuilder parser = factory.newDocumentBuilder();
        //Create blank DOM Document
        Document doc = parser.newDocument();
        return doc;
    }
    
    public static Document smallNoWhitespace() throws ParserConfigurationException {
        Document doc = newDocument();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        Element childElement = doc.createElement("child");
        childElement.setTextContent("text");
        root.appendChild(childElement);
        return doc;
    }
    
    public static Document wide() throws ParserConfigurationException{
        Document doc = newDocument();
        Element root = doc.createElement("root");
        doc.appendChild(root);
        //Level 1 
        Element childElement = doc.createElement("child");
        childElement.setTextContent("text");
        root.appendChild(childElement);
        Element anotherchildElement = doc.createElement("child");
        root.appendChild(anotherchildElement);
        //Level 2 
        Element grandchildElement = doc.createElement("grandChild");
        grandchildElement.setTextContent("fluff");
        childElement.appendChild(grandchildElement);
        Element anotherGrandchildElement = doc.createElement("grandChild");
        grandchildElement.setTextContent("more fluff");        
        anotherchildElement.appendChild(anotherGrandchildElement);
        //level 3
        Element greatGrandChildElement = doc.createElement("GGC");
        greatGrandChildElement.setTextContent("deep text");
        grandchildElement.appendChild(greatGrandChildElement);
        Element greatGrandChildElement2 = doc.createElement("GGC");
        greatGrandChildElement2.setTextContent("more deep text");
        anotherGrandchildElement.appendChild(greatGrandChildElement2);
        return doc;        
    }

  
}
