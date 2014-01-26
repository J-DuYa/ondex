package net.sourceforge.ondex.core.soapgraph;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.JAXBElement;

import net.sourceforge.ondex.core.Attribute;
import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.ONDEXEntity;
import net.sourceforge.ondex.exception.type.AccessDeniedException;
import net.sourceforge.ondex.marshal.Marshaller;
import net.sourceforge.ondex.webservice.client.ONDEXapiWS;
import net.sourceforge.ondex.webservice.client.WSAttribute;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Wrapper to represent SOAP concept Attribute.
 * 
 * @author taubertj
 */
public class SOAPConceptAttribute implements Attribute {

	/**
	 * attribute name as key
	 */
	String attrname = null;

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
	 * Initialises back references to soap graph for concept and Attribute.
	 * 
	 * @param parent
	 *            parent SOAPGraph
	 * @param cid
	 *            concept id
	 * @param attrname
	 *            the attribute name
	 */
	public SOAPConceptAttribute(SOAPONDEXGraph parent, Integer cid, String attrname) {
		this.parent = parent;
		soapGraph = parent.soapGraph;
		graphId = parent.graphId;
		id = cid;
		// this is the key
		this.attrname = attrname;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SOAPConceptAttribute) {
			SOAPConceptAttribute gds = (SOAPConceptAttribute) obj;
			Object value = getValue();
			if (value != null)
				return attrname.equals(gds.attrname)
						&& value.equals(gds.getValue());
		}
		return false;
	}

	@Override
	public AttributeName getOfType() {
		return new SOAPAttributeName(parent, attrname);
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
	public Object getValue() {
		try {
			WSAttribute gds = soapGraph.getConceptAttribute(graphId, id, attrname);
			if (gds != null)
				return Marshaller.getMarshaller().fromXML(
						gds.getValueAsXML().getValue());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int hashCode() {
		return attrname.hashCode() + getValue().hashCode();
	}

	@Override
	public boolean isDoIndex() {
		try {
			return soapGraph.getConceptAttribute(graphId, id, attrname).isDoIndex();
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		// default
		return false;
	}

	@Override
	public void setDoIndex(boolean doIndex) throws AccessDeniedException {
		try {
			soapGraph.getConceptAttribute(graphId, id, attrname).setDoIndex(doIndex);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setValue(Object value) {
		try {
			String xml = Marshaller.getMarshaller().toXML(value);
			JAXBElement<String> old = soapGraph.getConceptAttribute(graphId, id,
					attrname).getValueAsXML();
			old.setValue(xml);
			soapGraph.getConceptAttribute(graphId, id, attrname).setValueAsXML(old);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return attrname;
	}

	@Override
	public boolean inheritedFrom(AttributeName attributeName) {
		try {
			throw new OperationNotSupportedException("");
		} catch (OperationNotSupportedException e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int compareTo(Attribute o) {
		return this.getOfType().compareTo(o.getOfType());
	}

	@Override
	public Class<? extends ONDEXEntity> getOwnerClass() {
		return SOAPConcept.class;
	}
}
