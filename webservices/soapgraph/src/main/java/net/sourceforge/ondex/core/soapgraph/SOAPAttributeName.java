package net.sourceforge.ondex.core.soapgraph;

import javax.xml.bind.JAXBElement;

import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.Hierarchy;
import net.sourceforge.ondex.core.MetaData;
import net.sourceforge.ondex.core.Unit;
import net.sourceforge.ondex.webservice.client.ONDEXapiWS;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Wrapper to represent SOAP attribute name.
 * 
 * @author taubertj
 * 
 */
public class SOAPAttributeName implements AttributeName {

	/**
	 * unique graph id
	 */
	Long graphId = null;

	/**
	 * attribute name as key
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
	 * Initialises back references to soap graph for attribute name.
	 * 
	 * @param parent
	 *            parent SOAPGraph
	 * @param attrname
	 *            the attribute name
	 */
	public SOAPAttributeName(SOAPONDEXGraph parent, String attrname) {
		this.parent = parent;
		soapGraph = parent.soapGraph;
		graphId = parent.graphId;
		// this is the key
		this.id = attrname;
	}

	@Override
	public int compareTo(MetaData o) {
		if (o instanceof AttributeName)
			return id.compareTo(o.getId());
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SOAPAttributeName) {
			return id.equals(((SOAPAttributeName) obj).id);
		}
		return false;
	}

	@Override
	public Class<?> getDataType() {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(soapGraph.getAttributeName(graphId, id)
					.getDatatype().getValue());
		} catch (ClassNotFoundException e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return clazz;
	}

	@Override
	public String getDataTypeAsString() {
		try {
			return soapGraph.getAttributeName(graphId, id).getDatatype()
					.getValue();
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getDescription() {
		try {
			return soapGraph.getAttributeName(graphId, id).getDescription()
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
			return soapGraph.getAttributeName(graphId, id).getFullname()
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
	public AttributeName getSpecialisationOf() {
		String aid;
		try {
			aid = soapGraph.getAttributeName(graphId, id).getSpecialisationOf()
					.getValue();
			if (aid != null && aid.length() > 0)
				return new SOAPAttributeName(parent, aid);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Unit getUnit() {
		String unitid;
		try {
			unitid = soapGraph.getAttributeName(graphId, id).getUnit()
					.getValue();
			if (unitid != null && unitid.length() > 0)
				return new SOAPUnit(parent, unitid);
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
	public boolean isAssignableFrom(AttributeName possibleDescendant) {
		return Hierarchy.Helper.transitiveParent(this, possibleDescendant);
	}

	@Override
	public boolean isAssignableTo(AttributeName possibleAncestor) {
		return Hierarchy.Helper.transitiveParent(possibleAncestor, this);
	}

	@Override
	public void setDescription(String description) {
		try {
			JAXBElement<String> old = soapGraph.getAttributeName(graphId, id)
					.getDescription();
			old.setValue(description);
			soapGraph.getAttributeName(graphId, id).setDescription(old);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setFullname(String fullname) {
		try {
			JAXBElement<String> old = soapGraph.getAttributeName(graphId, id)
					.getFullname();
			old.setValue(fullname);
			soapGraph.getAttributeName(graphId, id).setFullname(old);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setSpecialisationOf(AttributeName specialisationOf) {
		try {
			JAXBElement<String> old = soapGraph.getAttributeName(graphId, id)
					.getSpecialisationOf();
			old.setValue(specialisationOf.getId());
			soapGraph.getAttributeName(graphId, id).setSpecialisationOf(old);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setUnit(Unit unit) {
		try {
			JAXBElement<String> old = soapGraph.getAttributeName(graphId, id)
					.getUnit();
			old.setValue(unit.getId());
			soapGraph.getAttributeName(graphId, id).setUnit(old);
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
