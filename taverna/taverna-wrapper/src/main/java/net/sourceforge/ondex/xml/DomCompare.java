package net.sourceforge.ondex.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @version $Rev: 514087 $ $Date: 2007-03-03 01:13:40 -0500 (Sat, 03 Mar 2007) $
 */
public class DomCompare {
    
    private StringBuilder report;
    
    private static String NEW_LINE = System.getProperty("line.separator");
    
    public static String NO_DIFFERENCE = "No difference";
    
    private DomCompare(){
        report = new StringBuilder();
    }
    
    public static void trimEmptyTextNodes(Node node) {
        Element element = null;
        if (node instanceof Document) {
            element = ((Document) node).getDocumentElement();
        } else if (node instanceof Element) {
            element = (Element) node;
        } else {
            return;
        }

        List<Node> nodesToRemove = new ArrayList<Node>();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                trimEmptyTextNodes(child);
            } else if (child instanceof Text) {
                Text t = (Text) child;
                if (t.getData().trim().length() == 0) {
                    nodesToRemove.add(child);
                }
            }
        }

        for (Node n : nodesToRemove) {
            element.removeChild(n);
        }
    }

    public static String compareNodes(Node expected, Node actual, boolean trimEmptyTextNodes) {
        if (trimEmptyTextNodes) {
            trimEmptyTextNodes(expected);
            trimEmptyTextNodes(actual);
        }
        return compareNodes(expected, actual);
    }

    private String getFullName(Node node){
        if (node instanceof Document) {
            return "";
        }
        String localName = node.getNodeName();
        if (node instanceof Element){
            Element element =  (Element)node;
            localName = localName + element.getAttribute("key");
            localName = localName + element.getAttribute("index");
        }
        return getFullName(node.getParentNode()) + "." + localName;
    }
  
    private int logNode(Node node){
        if (node instanceof Document) {
            return 1;
        }
        int tab = logNode(node.getParentNode())+ 1;
        for (int i = 0; i < tab; i++){
            report.append("  ");
        }
        report.append (node.getNodeName());
        if (node instanceof Element){
            Element element =  (Element)node;
            String key = element.getAttribute("key");
            if (!key.isEmpty()){
                report.append(" key=\"");
                report.append(key);
                report.append("\"");
            }
            String index = element.getAttribute("index");
            if (!index.isEmpty()){
                report.append(" index=\"");
                report.append(index);
                report.append("\"");
            }
        }
        report.append(NEW_LINE);
        return tab;
    }

    private void logDifference(Node expected, String difference){
        report.append(difference);
        report.append(NEW_LINE);
        logNode(expected);
    }
    
    public static String compareNodes(Node expected, Node actual) {
        DomCompare inner = new DomCompare();
        inner.innerCompareNodes(expected, actual);
        if (inner.report.length() > 0){
            return inner.report.toString();
        } else {
             return NO_DIFFERENCE;
        }
    }
    
    private void innerCompareNodes(Node expected, Node actual) {
        if (expected.getNodeType() != actual.getNodeType()) {
            logDifference(expected, "Different types of nodes. Expected: " + expected.getNodeType() + 
                    " Actual: " + actual.getNodeType());
            return;
        }
        if (expected instanceof Document) {
            Document expectedDoc = (Document) expected;
            Document actualDoc = (Document) actual;
            innerCompareNodes(expectedDoc.getDocumentElement(), actualDoc.getDocumentElement());
        } else if (expected instanceof Element) {
            Element expectedElement = (Element) expected;
            Element actualElement = (Element) actual;

            // compare element names
            if (!expectedElement.getTagName().equals(actualElement.getTagName())) {
                logDifference(expectedElement, "Tag name " + 
                        expectedElement.getTagName() + " does not match " + 
                        actualElement.getTagName());
                return;
            }
            // compare element ns
            String expectedNS = expectedElement.getNamespaceURI();
            String actualNS = actualElement.getNamespaceURI();
            if ((expectedNS == null && actualNS != null)
                    || (expectedNS != null && !expectedNS.equals(actualNS))) {
                logDifference(expectedElement, "Element namespace name " + 
                        expectedNS + " do not match: " + actualNS);
                return;
            }

            //String elementName = "{" + expectedElement.getNamespaceURI() + "}"
            //    + actualElement.getTagName();
            //String elementName = getFullName(expectedElement);
            // compare attributes
            NamedNodeMap expectedAttrs = expectedElement.getAttributes();
            NamedNodeMap actualAttrs = actualElement.getAttributes();
            if (countNonNamespaceAttribures(expectedAttrs) != countNonNamespaceAttribures(actualAttrs)) {
                logDifference(expectedElement, "Number of attributes do not match up expected:"
                        + countNonNamespaceAttribures(expectedAttrs) + " "
                        + " actual: " + countNonNamespaceAttribures(actualAttrs));
                return;
            }
            for (int i = 0; i < expectedAttrs.getLength(); i++) {
                Attr expectedAttr = (Attr) expectedAttrs.item(i);
                if (expectedAttr.getName().startsWith("xmlns")) {
                    continue;
                }   
                Attr actualAttr = null;
                if (expectedAttr.getNamespaceURI() == null) {
                    actualAttr = (Attr) actualAttrs.getNamedItem(expectedAttr.getName());
                } else {
                    actualAttr = (Attr) actualAttrs.getNamedItemNS(expectedAttr.getNamespaceURI(),
                    expectedAttr.getLocalName());
                }
                if (actualAttr == null) {
                    logDifference (expectedElement, "No attribute found:" + expectedAttr);
                    return;
                }
                if (!expectedAttr.getValue().equals(actualAttr.getValue())) {
                    logDifference (expectedElement, "Attribute values do not match: "
                        + expectedAttr.getValue() + " " + actualAttr.getValue());
                    return;
                }
            }

            // compare children
            NodeList expectedChildren = expectedElement.getChildNodes();
            NodeList actualChildren = actualElement.getChildNodes();
            if (expectedChildren.getLength() != actualChildren.getLength()) {
                logDifference(expectedElement, "Number of children do not match up Expected: "
                    + expectedChildren.getLength() + " Actual: " + actualChildren.getLength());
                return;
            }
            for (int i = 0; i < expectedChildren.getLength(); i++) {
                Node expectedChild = expectedChildren.item(i);
                Node actualChild = actualChildren.item(i);
                innerCompareNodes(expectedChild, actualChild);
            }
        } else if (expected instanceof Text) {
            String expectedData = ((Text) expected).getData().trim();
            String actualData = ((Text) actual).getData().trim();

            if (!expectedData.equals(actualData)) {
                logDifference(expected.getParentNode(), "Text does not match: " + expectedData + " " + actualData);
            }
        }
    }

    private int countNonNamespaceAttribures(NamedNodeMap attrs) {
        int n = 0;
        for (int i = 0; i < attrs.getLength(); i++) {
            Attr attr = (Attr) attrs.item(i);
            if (!attr.getName().startsWith("xmlns")) {
                n++;
            }
        }
        return n;
    }
    
}
