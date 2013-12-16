package net.sourceforge.ondex.core.soapgraph;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import javax.xml.ws.Holder;

import net.sourceforge.ondex.core.Attribute;
import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.EvidenceType;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.core.RelationKey;
import net.sourceforge.ondex.core.RelationType;
import net.sourceforge.ondex.core.base.RelationKeyImpl;
import net.sourceforge.ondex.core.util.BitSetFunctions;
import net.sourceforge.ondex.exception.type.AccessDeniedException;
import net.sourceforge.ondex.exception.type.NullValueException;
import net.sourceforge.ondex.marshal.Marshaller;
import net.sourceforge.ondex.webservice.client.ONDEXapiWS;
import net.sourceforge.ondex.webservice.client.WSAttribute;
import net.sourceforge.ondex.webservice.client.WSConcept;
import net.sourceforge.ondex.webservice.client.WSEvidenceType;
import net.sourceforge.ondex.webservice.client.WSRelation;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Wrapper to represent SOAP relations.
 * 
 * @author taubertj
 */
public class SOAPRelation implements ONDEXRelation {

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
	SOAPONDEXGraph parent = null;

	/**
	 * connected web service graph
	 */
	ONDEXapiWS soapGraph = null;

	/**
	 * Initialises back references to soap graph for relation.
	 * 
	 * @param parent
	 *            parent SOAPGraph
	 * @param rid
	 *            relation id
	 */
	public SOAPRelation(SOAPONDEXGraph parent, Integer rid) {
		this.parent = parent;
		soapGraph = parent.soapGraph;
		graphId = parent.graphId;
		id = rid;
	}

	@Override
	public void addTag(ONDEXConcept ac) throws AccessDeniedException,
			NullValueException {
		try {
			soapGraph.addTagRelation(graphId, id, ac.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void addEvidenceType(EvidenceType evidencetype) {
		try {
			soapGraph
					.addEvidenceTypeRelation(graphId, id, evidencetype.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public Attribute createAttribute(AttributeName attrname, Object value,
			boolean doIndex) {
		try {
			String xml = Marshaller.getMarshaller().toXML(value);
			soapGraph.createRelationAttribute(graphId, id, new Holder<String>(
					attrname.getId()), xml, doIndex);
			return new SOAPRelationAttribute(parent, id, attrname.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean deleteAttribute(AttributeName attrname) {
		try {
			return soapGraph.deleteRelationAttribute(graphId, id,
					attrname.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SOAPRelation) {
			return id.equals(((SOAPRelation) obj).id);
		}
		return false;
	}

	@Override
	public Set<ONDEXConcept> getTags() {
		BitSet set = new BitSet();
		try {
			for (WSConcept wscon : soapGraph.getTagsRelation(graphId, id).getWSConcept()) {
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
	public Set<EvidenceType> getEvidence() {
		Set<EvidenceType> set = new HashSet<EvidenceType>();
		try {
			for (WSEvidenceType wset : soapGraph.getEvidenceRelation(graphId,
					id).getWSEvidenceType()) {
				set.add(new SOAPEvidenceType(parent, wset.getId().getValue()));
			}
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return set;
	}

	@Override
	public ONDEXConcept getFromConcept() {
		try {
			return new SOAPConcept(parent, soapGraph.getRelation(graphId, id)
					.getFromConceptId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public RelationKey getKey() {
		try {
			WSRelation r = soapGraph.getRelation(graphId, id);
			return new RelationKeyImpl(graphId, r.getFromConceptId(),
					r.getToConceptId(), r.getOfType().getValue());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public RelationType getOfType() {
		try {
			return new SOAPRelationType(parent, soapGraph
					.getRelation(graphId, id).getOfType().getValue());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Attribute getAttribute(AttributeName attrname) {
		try {
			if (soapGraph.getRelationAttribute(graphId, id, attrname.getId()) != null)
				return new SOAPRelationAttribute(parent, id, attrname.getId());
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
			for (WSAttribute wsgds : soapGraph.getRelationAttributes(graphId,
					id).getWSAttribute()) {
				set.add(new SOAPRelationAttribute(parent, id, wsgds.getTypeOf()
						.getValue()));
			}
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.unmodifiableSet(set);
	}

	@Override
	public long getSID() {
		return graphId;
	}

	@Override
	public ONDEXConcept getToConcept() {
		try {
			return new SOAPConcept(parent, soapGraph.getRelation(graphId, id)
					.getToConceptId());
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
	public boolean inheritedFrom(RelationType rt) {
		try {
			return soapGraph.inheritedFromRelation(graphId, id, rt.getId());
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
			return soapGraph.removeTagRelation(graphId, id, ac.getId());
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
			return soapGraph.removeEvidenceTypeRelation(graphId, id,
					evidencetype.getId());
		} catch (WebserviceException_Exception e) {
			parent.fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		// indicates error
		return false;
	}

	@Override
	public String toString() {
		return id.toString();
	}

}
