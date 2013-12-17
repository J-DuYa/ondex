package net.sourceforge.ondex.core.soapgraph;

import javax.xml.bind.JAXBElement;

import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.ConceptAccession;
import net.sourceforge.ondex.exception.type.AccessDeniedException;
import net.sourceforge.ondex.webservice.client.ONDEXapiWS;
import net.sourceforge.ondex.webservice.client.WSConceptAccession;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Wrapper to represent SOAP concept accessions.
 * 
 * @author taubertj
 * 
 */
public class SOAPConceptAccession implements ConceptAccession {

	/**
	 * accession as part of key
	 */
	String accession = null;

	/**
	 * DataSource id as part of key
	 */
	String elementOfCVId = null;

	/**
	 * unique graph id
	 */
	Long graphId = null;

	/**
	 * concept id
	 */
	Integer id = null;

	/**
	 * back reference to parent SOAP graph
	 */
	SOAPONDEXGraph parent;

	/**
	 * connected web service graph
	 */
	ONDEXapiWS soapGraph = null;

	/**
	 * Initialises back references to soap graph for concept and accession.
	 * 
	 * @param parent
	 *            parent SOAPGraph
	 * @param cid
	 *            concept id
	 * @param accession
	 *            the accession
	 * @param elementOfCVId
	 *            the element of id
	 */
	public SOAPConceptAccession(SOAPONDEXGraph parent, Integer cid,
			String accession, String elementOfCVId) {
		this.parent = parent;
		soapGraph = parent.soapGraph;
		graphId = parent.graphId;
		id = cid;
		// this is the combined key
		this.accession = accession;
		this.elementOfCVId = elementOfCVId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SOAPConceptAccession) {
			SOAPConceptAccession ca = (SOAPConceptAccession) obj;
			return accession.equals(ca.accession)
					&& elementOfCVId.equals(ca.elementOfCVId);
		}
		return false;
	}

	@Override
	public String getAccession() {
		return accession;
	}

	@Override
	public DataSource getElementOf() {
		return new SOAPDataSource(parent, elementOfCVId);
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
		return accession.hashCode() + elementOfCVId.hashCode();
	}

	@Override
	public boolean isAmbiguous() {
		try {
			WSConceptAccession acc = soapGraph.getConceptAccession(graphId, id,
					accession, elementOfCVId);
			if (acc != null)
				return acc.getIsAmbiguous().getValue().booleanValue();
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		// default
		return true;
	}

	@Override
	public void setAmbiguous(boolean ambiguous) throws AccessDeniedException {
		try {
			JAXBElement<Boolean> old = soapGraph.getConceptAccession(graphId,
					id, accession, elementOfCVId).getIsAmbiguous();
			old.setValue(Boolean.valueOf(ambiguous));
			soapGraph
					.getConceptAccession(graphId, id, accession, elementOfCVId)
					.setIsAmbiguous(old);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return accession + "(" + elementOfCVId + ")";
	}

}
