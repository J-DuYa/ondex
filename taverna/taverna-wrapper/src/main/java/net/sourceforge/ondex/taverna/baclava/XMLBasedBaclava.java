package net.sourceforge.ondex.taverna.baclava;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.sourceforge.ondex.taverna.TavernaException;
import net.sourceforge.ondex.xml.XMLReader;

/**
 * Wrapper around a Baclava uri to find the required information.
 * 
 * This implementation is a hack which obtains the information based on the expected XML structure.
 * 
 * @author Christian
 */
public class XMLBasedBaclava implements Baclava{
    
    /**
     * Keys found in the expected place in the file
     */
    private ArrayList<String> keys;
    
    /**
     * Wraps a uri keeping a list of the data keys.
     * 
     * @param uri Points to a single barclava file containing at least the required inputs (case senstivie).
     *        For a file begin with the "file:" prefix.
     * @throws TavernaException If the uri could not be read and parsed into xml.
     */
    public XMLBasedBaclava(String uri) throws TavernaException{
        keys = new ArrayList<String>();
        
        try {
            Document doc = XMLReader.readFile(uri);
            Element root = doc.getDocumentElement();
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++){
                Node node = children.item(i);
                if (node instanceof Element){
                    Element element = (Element)node;
                    keys.add(element.getAttribute("key"));
                }    
            }
        } catch (Exception ex) {
            throw new TavernaException ("Exception while reading Baclava uri");
        }
    }

    @Override
    public boolean hasValue(String key) {
        return keys.contains(key);
    }
}
