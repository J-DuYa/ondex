package net.sourceforge.ondex.taverna.workflow;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.ondex.xml.XMLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import net.sourceforge.ondex.taverna.TavernaException;

/**
 * XMLBased hack for extracting the required information from a T2Flow workflow.
 *
 * This implementation is a hack which obtains the information based on the expected XML structure.
 * 
 * @author Christian
 */
public class XMLBasedT2Flow implements TavernaWorkflow{

    /**
     * Name of the workflowas given in the T"Flow
     */
    private String workflowName;
    
    /**
     * Map of expected inputs by name and the corrospondoing depth for each input.
     */
    private HashMap<String,Integer>inputs;
    
    /**
     * Extracts the required information by parsing the file as XML and looking for the required information.
     * 
     * Expected locations of information are hard coded in.
     * Only works for the expected T2Flow format.
     * 
     * @param file A T2Flow file
     * @throws TavernaException if the file is not XML parsable or not in the hardcode format.
     */
    public XMLBasedT2Flow(File file) throws TavernaException{
        Document workflow;
        try {
            workflow = XMLReader.readFile(file);
        } catch (Exception ex) {
            throw new TavernaException ("Unable to parse workflow file as XML", ex);
        }
        updateWorkflow(workflow);
    }
      
    /**
     * Extracts the required information by parsing the uri as XML and looking for the required information.
     * 
     * Expected locations of information are hard coded in.
     * Only works for the expected T2Flow format.
     * 
     * @param uri URI to a T2Flow file
     * @throws TavernaException if the file pointed to by the uri is not XML parsable or not in the hardcode format.
     */
    public XMLBasedT2Flow(String uri) throws TavernaException{
        Document workflow; 
        try {
            workflow = XMLReader.readFile(uri);
        } catch (Exception ex) {
            throw new TavernaException ("Unable to parse workflow file as XML", ex);
        }
        updateWorkflow(workflow);
    } 

    /**
     * Extracts the required information from the XML document by looking for the required information.
     * 
     * Expected locations of information are hard coded in.
     * Only works for the expected T2Flow format.
     * 
     * @param workflow XML DOM of a T2Flow 
     * @throws TavernaException if the DOM is not in the hardcode format.
     */
    private void updateWorkflow(Document workflow) throws TavernaException{
        inputs = new HashMap<String,Integer>();
        try { 
            HashMap<String,Integer> newInputs = new HashMap<String,Integer>();
            Element root = workflow.getDocumentElement();
            Element dataflow = XMLReader.getDirectOnlyChildrenByName(root, "dataflow");
            Element name = XMLReader.getDirectOnlyChildrenByName(dataflow, "name");
            workflowName = XMLReader.getText(name);
            Element inputPort = XMLReader.getDirectOnlyChildrenByName(dataflow, "inputPorts");
            List<Node> ports = XMLReader.getDirectChildrenByName(inputPort,"port");
            for (Node port: ports){
                name = XMLReader.getDirectOnlyChildrenByName(port, "name");
                String theName = XMLReader.getText(name);
                Element depth = XMLReader.getDirectOnlyChildrenByName(port, "depth");
                Integer theDepth = new Integer(XMLReader.getText(depth));
                inputs.put(theName, theDepth);
            }
        } catch (Exception ex) {
            throw new TavernaException ("Unable to parse XML as a workflow.", ex);
        }
   }

    @Override
    public String getWorkflowName() {
        return workflowName;
    }

    @Override
    public Map<String, Integer> getInputs() {
        return inputs;
    }
    
}

