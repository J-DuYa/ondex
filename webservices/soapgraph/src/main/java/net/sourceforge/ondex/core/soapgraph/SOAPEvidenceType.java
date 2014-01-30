package net.sourceforge.ondex.core.soapgraph;

import javax.xml.bind.JAXBElement;

import net.sourceforge.ondex.core.EvidenceType;
import net.sourceforge.ondex.core.MetaData;
import net.sourceforge.ondex.webservice.client.ONDEXapiWS;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Wrapper to represent SOAP EvidenceType.
 * 
 * @author taubertj
 * 
 */
public class SOAPEvidenceType implements EvidenceType {

	/**
	 * unique graph id
	 */
	Long graphId = null;

	/**
	 * EvidenceType id as key
	 */
	String id = null;

	/**
	 * back reference to parent SOAP graph
	 */
	SOAPONDEXGraph parent;

	/**
	 * connected web service graph
	 */
	ONDEXapiWS soapGraph = null;

	/**
	 * Initialises back references to soap graph for EvidenceType.
	 * 
	 * @param parent
	 *            parent SOAPGraph
	 * @param etid
	 *            EvidenceType id
	 */
	public SOAPEvidenceType(SOAPONDEXGraph parent, String etid) {
		this.parent = parent;
		soapGraph = parent.soapGraph;
		graphId = parent.graphId;
		id = etid;
	}

	@Override
	public int compareTo(MetaData o) {
		if (o instanceof EvidenceType)
			return id.compareTo(o.getId());
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SOAPEvidenceType) {
			return id.equals(((SOAPEvidenceType) obj).id);
		}
		return false;
	}

	@Override
	public String getDescription() {
		try {
			return soapGraph.getEvidenceType(graphId, id).getDescription()
					.getValue();
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getFullname() {
		try {
			return soapGraph.getEvidenceType(graphId, id).getFullname()
					.getValue();
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getSID() {
		return graphId;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public void setDescription(String description) {
		try {
			JAXBElement<String> old = soapGraph.getEvidenceType(graphId, id)
					.getDescription();
			old.setValue(description);
			soapGraph.getEvidenceType(graphId, id).setDescription(old);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setFullname(String fullname) {
		try {
			JAXBElement<String> old = soapGraph.getEvidenceType(graphId, id)
					.getFullname();
			old.setValue(fullname);
			soapGraph.getEvidenceType(graphId, id).setFullname(old);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return id;
	}

}
