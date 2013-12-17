package net.sourceforge.ondex.core.soapgraph;

import javax.xml.bind.JAXBElement;

import net.sourceforge.ondex.core.ConceptName;
import net.sourceforge.ondex.exception.type.AccessDeniedException;
import net.sourceforge.ondex.webservice.client.ONDEXapiWS;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Wrapper to represent SOAP concept name.
 * 
 * @author taubertj
 * 
 */
public class SOAPConceptName implements ConceptName {

	/**
	 * unique graph id
	 */
	Long graphId = null;

	/**
	 * concept id
	 */
	Integer id = null;

	/**
	 * name as key
	 */
	String name = null;

	/**
	 * back reference to parent SOAP graph
	 */
	SOAPONDEXGraph parent;

	/**
	 * connected web service graph
	 */
	ONDEXapiWS soapGraph = null;

	/**
	 * Initialises back references to soap graph for concept and name.
	 * 
	 * @param parent
	 *            parent SOAPGraph
	 * @param cid
	 *            concept id
	 * @param name
	 *            the name
	 */
	public SOAPConceptName(SOAPONDEXGraph parent, Integer cid, String name) {
		this.parent = parent;
		soapGraph = parent.soapGraph;
		graphId = parent.graphId;
		id = cid;
		// this is the key
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SOAPConceptName) {
			return name.equals(((SOAPConceptName) obj).name);
		}
		return false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getOwnerId() {
		return id;
	}

	@Override
	public long getSID() {
		return graphId;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean isPreferred() {
		try {
			return soapGraph.getConceptNameWithName(graphId, id, name)
					.getIsPreferred().getValue().booleanValue();
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		// default
		return false;
	}

	@Override
	public void setPreferred(boolean isPreferred) throws AccessDeniedException {
		try {
			JAXBElement<Boolean> old = soapGraph.getConceptNameWithName(
					graphId, id, name).getIsPreferred();
			old.setValue(Boolean.valueOf(isPreferred));
			soapGraph.getConceptNameWithName(graphId, id, name).setIsPreferred(
					old);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return name;
	}

}
