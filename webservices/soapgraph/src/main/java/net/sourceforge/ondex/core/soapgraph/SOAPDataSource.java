package net.sourceforge.ondex.core.soapgraph;

import javax.xml.bind.JAXBElement;

import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.MetaData;
import net.sourceforge.ondex.webservice.client.ONDEXapiWS;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Wrapper to represent SOAP DataSource.
 * 
 * @author taubertj
 * 
 */
public class SOAPDataSource implements DataSource {

	/**
	 * unique graph id
	 */
	Long graphId = null;

	/**
	 * DataSource id as key
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
	 * Initialises back references to soap graph for DataSource.
	 * 
	 * @param parent
	 *            parent SOAPGraph
	 * @param cvid
	 *            DataSource id
	 */
	public SOAPDataSource(SOAPONDEXGraph parent, String cvid) {
		this.parent = parent;
		soapGraph = parent.soapGraph;
		graphId = parent.graphId;
		id = cvid;
	}

	@Override
	public int compareTo(MetaData o) {
		if (o instanceof DataSource)
			return id.compareTo(o.getId());
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SOAPDataSource) {
			return id.equals(((SOAPDataSource) obj).id);
		}
		return false;
	}

	@Override
	public String getDescription() {
		try {
			return soapGraph.getDataSource(graphId, id).getDescription()
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
			return soapGraph.getDataSource(graphId, id).getFullname()
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
			JAXBElement<String> old = soapGraph.getDataSource(graphId, id)
					.getDescription();
			old.setValue(description);
			soapGraph.getDataSource(graphId, id).setDescription(old);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setFullname(String fullname) {
		try {
			JAXBElement<String> old = soapGraph.getDataSource(graphId, id)
					.getFullname();
			old.setValue(fullname);
			soapGraph.getDataSource(graphId, id).setFullname(old);
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
