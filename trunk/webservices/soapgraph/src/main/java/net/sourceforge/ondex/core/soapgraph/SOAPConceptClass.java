package net.sourceforge.ondex.core.soapgraph;

import javax.xml.bind.JAXBElement;

import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.Hierarchy;
import net.sourceforge.ondex.core.MetaData;
import net.sourceforge.ondex.webservice.client.ONDEXapiWS;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Wrapper to represent SOAP ConceptClass.
 * 
 * @author taubertj
 * 
 */
public class SOAPConceptClass implements ConceptClass {

	/**
	 * unique graph id
	 */
	Long graphId = null;

	/**
	 * ConceptClass id as key
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
	 * Initialises back references to soap graph for ConceptClass.
	 * 
	 * @param parent
	 *            parent SOAPGraph
	 * @param ccid
	 *            ConceptClass id
	 */
	public SOAPConceptClass(SOAPONDEXGraph parent, String ccid) {
		this.parent = parent;
		soapGraph = parent.soapGraph;
		graphId = parent.graphId;
		// this is the key
		id = ccid;
	}

	@Override
	public int compareTo(MetaData o) {
		if (o instanceof ConceptClass)
			return id.compareTo(o.getId());
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SOAPConceptClass) {
			return id.equals(((SOAPConceptClass) obj).id);
		}
		return false;
	}

	@Override
	public String getDescription() {
		try {
			return soapGraph.getConceptClass(graphId, id).getDescription()
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
			return soapGraph.getConceptClass(graphId, id).getFullname()
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
	public ConceptClass getSpecialisationOf() {
		String ccid;
		try {
			ccid = soapGraph.getConceptClass(graphId, id).getSpecialisationOf()
					.getValue();
			if (ccid != null && ccid.length() > 0)
				return new SOAPConceptClass(parent, ccid);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean isAssignableFrom(ConceptClass possibleDescendant) {
		return Hierarchy.Helper.transitiveParent(this, possibleDescendant);
	}

	@Override
	public boolean isAssignableTo(ConceptClass possibleAncestor) {
		return Hierarchy.Helper.transitiveParent(possibleAncestor, this);
	}

	@Override
	public void setDescription(String description) {
		try {
			JAXBElement<String> old = soapGraph.getConceptClass(graphId, id)
					.getDescription();
			old.setValue(description);
			soapGraph.getConceptClass(graphId, id).setDescription(old);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setFullname(String fullname) {
		try {
			JAXBElement<String> old = soapGraph.getConceptClass(graphId, id)
					.getFullname();
			old.setValue(fullname);
			soapGraph.getConceptClass(graphId, id).setFullname(old);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setSpecialisationOf(ConceptClass specialisationOf) {
		try {
			JAXBElement<String> old = soapGraph.getConceptClass(graphId, id)
					.getSpecialisationOf();
			old.setValue(specialisationOf.getId());
			soapGraph.getConceptClass(graphId, id).setSpecialisationOf(old);
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
