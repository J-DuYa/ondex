package net.sourceforge.ondex.core.soapgraph;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import javax.xml.ws.Holder;

import net.sourceforge.ondex.core.Attribute;
import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.ConceptAccession;
import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.ConceptName;
import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.EvidenceType;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.util.BitSetFunctions;
import net.sourceforge.ondex.exception.type.AccessDeniedException;
import net.sourceforge.ondex.exception.type.NullValueException;
import net.sourceforge.ondex.marshal.Marshaller;
import net.sourceforge.ondex.webservice.client.ONDEXapiWS;
import net.sourceforge.ondex.webservice.client.WSAttribute;
import net.sourceforge.ondex.webservice.client.WSConcept;
import net.sourceforge.ondex.webservice.client.WSConceptAccession;
import net.sourceforge.ondex.webservice.client.WSConceptName;
import net.sourceforge.ondex.webservice.client.WSEvidenceType;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Wrapper to represent SOAP concepts.
 * 
 * @author taubertj
 */
public class SOAPConcept implements ONDEXConcept {

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
	SOAPONDEXGraph parent = null;

	/**
	 * connected web service graph
	 */
	ONDEXapiWS soapGraph = null;

	/**
	 * Initialises back references to soap graph for concept.
	 * 
	 * @param parent
	 *            parent SOAPGraph
	 * @param cid
	 *            concept id
	 */
	public SOAPConcept(SOAPONDEXGraph parent, Integer cid) {
		this.parent = parent;
		soapGraph = parent.soapGraph;
		graphId = parent.graphId;
		id = cid;
	}

	@Override
	public void addTag(ONDEXConcept ac) throws AccessDeniedException,
			NullValueException {
		try {
			soapGraph.addTagConcept(graphId, id, ac.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void addEvidenceType(EvidenceType evidencetype) {
		try {
			soapGraph.addEvidenceTypeConcept(graphId, id, evidencetype.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public ConceptAccession createConceptAccession(String accession,
			DataSource elementOf, boolean ambiguous) {
		try {
			soapGraph.createConceptAccession(graphId, id, new Holder<String>(
					accession), elementOf.getId(), Boolean.valueOf(ambiguous));
			return new SOAPConceptAccession(parent, id, accession,
					elementOf.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ConceptName createConceptName(String name, boolean isPreferred) {
		try {
			soapGraph.createConceptName(graphId, id, new Holder<String>(name),
					Boolean.valueOf(isPreferred));
			return new SOAPConceptName(parent, id, name);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Attribute createAttribute(AttributeName attrname, Object value,
			boolean doIndex) throws AccessDeniedException, NullValueException {
		try {
			String xml = Marshaller.getMarshaller().toXML(value);
			soapGraph.createConceptAttribute(graphId, id, new Holder<String>(
					attrname.getId()), xml, doIndex);
			return new SOAPConceptAttribute(parent, id, attrname.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean deleteConceptAccession(String accession, DataSource elementOf) {
		try {
			return soapGraph.deleteConceptAccession(graphId, id, accession,
					elementOf.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteConceptName(String name) {
		try {
			return soapGraph.deleteConceptName(graphId, id, name);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteAttribute(AttributeName attrname) {
		try {
			return soapGraph.deleteConceptAttribute(graphId, id,
					attrname.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SOAPConcept) {
			return id.equals(((SOAPConcept) obj).id);
		}
		return false;
	}

	@Override
	public String getAnnotation() {
		try {
			return soapGraph.getConcept(graphId, id).getAnnotation().getValue();
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ConceptAccession getConceptAccession(String accession,
			DataSource elementOf) {
		try {
			if (soapGraph.getConceptAccession(graphId, id, accession,
					elementOf.getId()) != null)
				return new SOAPConceptAccession(parent, id, accession,
						elementOf.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Set<ConceptAccession> getConceptAccessions() {
		Set<ConceptAccession> set = new HashSet<ConceptAccession>();
		try {
			for (WSConceptAccession wsca : soapGraph.getConceptAccessions(
					graphId, id).getWSConceptAccession()) {
				set.add(new SOAPConceptAccession(parent, id, wsca
						.getAccession().getValue(), wsca.getElementOf()
						.getValue().getId().getValue()));
			}
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.unmodifiableSet(set);
	}

	@Override
	public ConceptName getConceptName() {
		try {
			WSConceptName name = soapGraph.getConceptName(graphId, id);
			if (name != null)
				return new SOAPConceptName(parent, id, name.getName()
						.getValue());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ConceptName getConceptName(String name) {
		return new SOAPConceptName(parent, id, name);
	}

	@Override
	public Set<ConceptName> getConceptNames() {
		Set<ConceptName> set = new HashSet<ConceptName>();
		try {
			for (WSConceptName wsname : soapGraph.getConceptNames(graphId, id)
					.getWSConceptName()) {
				set.add(new SOAPConceptName(parent, id, wsname.getName()
						.getValue()));
			}
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.unmodifiableSet(set);
	}

	@Override
	public Set<ONDEXConcept> getTags() {
		BitSet set = new BitSet();
		try {
			for (WSConcept wscon : soapGraph.getTagsConcept(graphId, id).getWSConcept()) {
				set.set(wscon.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.unmodifiableSet(BitSetFunctions.create(parent,
				ONDEXConcept.class, set));
	}

	@Override
	public String getDescription() {
		try {
			return soapGraph.getConcept(graphId, id).getDescription()
					.getValue();
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public DataSource getElementOf() {
		try {
			return new SOAPDataSource(parent, soapGraph.getConcept(graphId, id)
					.getElementOf().getValue());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Set<EvidenceType> getEvidence() {
		Set<EvidenceType> set = new HashSet<EvidenceType>();
		try {
			for (WSEvidenceType wset : soapGraph
					.getEvidenceConcept(graphId, id).getWSEvidenceType()) {
				set.add(new SOAPEvidenceType(parent, wset.getId().getValue()));
			}
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.unmodifiableSet(set);
	}

	@Override
	public Attribute getAttribute(AttributeName attrname) {
		try {
			if (soapGraph.getConceptAttribute(graphId, id, attrname.getId()) != null)
				return new SOAPConceptAttribute(parent, id, attrname.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Set<Attribute> getAttributes() {
		Set<Attribute> set = new HashSet<Attribute>();
		try {
			for (WSAttribute wsgds : soapGraph
					.getConceptAttributes(graphId, id).getWSAttribute()) {
				set.add(new SOAPConceptAttribute(parent, id, wsgds.getTypeOf()
						.getValue()));
			}
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.unmodifiableSet(set);
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public ConceptClass getOfType() {
		try {
			return new SOAPConceptClass(parent, soapGraph
					.getConcept(graphId, id).getOfType().getValue());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getPID() {
		try {
			return soapGraph.getConcept(graphId, id).getPID().getValue();
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
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
	public boolean inheritedFrom(ConceptClass cc) {
		try {
			return soapGraph.inheritedFromConcept(graphId, id, cc.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		// default
		return false;
	}

	@Override
	public boolean removeTag(ONDEXConcept ac) {
		try {
			return soapGraph.removeTagConcept(graphId, id, ac.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		// indicates error
		return false;
	}

	@Override
	public boolean removeEvidenceType(EvidenceType evidencetype) {
		try {
			return soapGraph.removeEvidenceTypeConcept(graphId, id,
					evidencetype.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		// indicates error
		return false;
	}

	@Override
	public void setAnnotation(String annotation) {
		try {
			soapGraph.setAnnotation(graphId, id, annotation);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setDescription(String description) {
		try {
			soapGraph.setDescription(graphId, id, description);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void setPID(String pid) {
		try {
			soapGraph.setPID(graphId, id, pid);
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public String toString() {
		return id.toString();
	}

}
