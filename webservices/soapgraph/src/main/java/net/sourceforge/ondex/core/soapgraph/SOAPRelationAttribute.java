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
 * Wrapper to represent SOAP relation Attribute.
 * 
 * @author taubertj
 */
public class SOAPRelationAttribute implements Attribute {

	/**
	 * attribute name as key
	 */
	String attrname = null;

	/**
	 * unique graph id
	 */
	Long graphId = null;

	/**
	 * relation id
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
	 * Initialises back references to soap graph for relation and Attribute.
	 * 
	 * @param parent
	 *            parent SOAPGraph
	 * @param rid
	 *            relation id
	 * @param attrname
	 *            the attribute name
	 */
	public SOAPRelationAttribute(SOAPONDEXGraph parent, Integer rid,
			String attrname) {
		this.parent = parent;
		soapGraph = parent.soapGraph;
		graphId = parent.graphId;
		id = rid;
		// this is the key
		this.attrname = attrname;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SOAPRelationAttribute) {
			SOAPRelationAttribute gds = (SOAPRelationAttribute) obj;
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
			WSAttribute gds = soapGraph.getRelationAttribute(graphId, id,
					attrname);
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
			return soapGraph.getRelationAttribute(graphId, id, attrname)
					.isDoIndex();
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
			soapGraph.getRelationAttribute(graphId, id, attrname).setDoIndex(
					Boolean.valueOf(doIndex));
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setValue(Object value) {
		try {
			String xml = Marshaller.getMarshaller().toXML(value);
			JAXBElement<String> old = soapGraph.getRelationAttribute(graphId,
					id, attrname).getValueAsXML();
			old.setValue(xml);
			soapGraph.getRelationAttribute(graphId, id, attrname)
					.setValueAsXML(old);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
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
	public String toString() {
		return attrname;
	}

	@Override
	public int compareTo(Attribute o) {
		return this.getOfType().compareTo(o.getOfType());
	}

	@Override
	public Class<? extends ONDEXEntity> getOwnerClass() {
		return SOAPRelation.class;
	}

}
