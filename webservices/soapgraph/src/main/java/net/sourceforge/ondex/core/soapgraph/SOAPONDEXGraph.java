package net.sourceforge.ondex.core.soapgraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Set;

import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.EntityFactory;
import net.sourceforge.ondex.core.EvidenceType;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.ONDEXGraphMetaData;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.core.RelationType;
import net.sourceforge.ondex.core.util.BitSetFunctions;
import net.sourceforge.ondex.webservice.client.EvidenceTypeIdList;
import net.sourceforge.ondex.webservice.client.ONDEXapiWS;
import net.sourceforge.ondex.webservice.client.ONDEXapiWSService;
import net.sourceforge.ondex.webservice.client.WSConcept;
import net.sourceforge.ondex.webservice.client.WSGraph;
import net.sourceforge.ondex.webservice.client.WSRelation;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Implementation wrapped around the web service.
 * 
 * @author taubertj
 */
public class SOAPONDEXGraph implements ONDEXGraph {

	/**
	 * list of action listeners to trigger error events
	 */
	private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();

	/**
	 * Adds a ActionListener for error messages.
	 * 
	 * @param l
	 *            ActionListener
	 */
	public void addActionListener(ActionListener l) {
		actionListeners.add(l);
	}

	/**
	 * Removes a ActionListener for error messages.
	 * 
	 * @param l
	 *            ActionListner
	 */
	public void removeActionListener(ActionListener l) {
		actionListeners.remove(l);
	}

	/**
	 * Returns list of ActionListeners for capturing error messages.
	 * 
	 * @return Collection<ActionListener>
	 */
	public Collection<ActionListener> getActionListeners() {
		return actionListeners;
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 * 
	 */
	void fireActionEvent(Object entity, String message) {
		for (ActionListener l : actionListeners) {
			ActionEvent actionEvent = new ActionEvent(entity,
					ActionEvent.ACTION_PERFORMED, message);
			l.actionPerformed(actionEvent);
		}
	}

	/**
	 * unique graph id
	 */
	long graphId;

	/**
	 * graph name
	 */
	String name = null;

	/**
	 * connected web service graph
	 */
	ONDEXapiWS soapGraph = null;

	/**
	 * Constructor which connects to a given URL and graph
	 * 
	 * @param url
	 *            URL to connect to
	 * @param graph
	 *            graph to connect to
	 */
	public SOAPONDEXGraph(URL url, WSGraph graph) {

		// connect to webservice from URL for graph name
		ONDEXapiWSService ondexService = new ONDEXapiWSService(url);
		soapGraph = ondexService.getONDEXapiWSPort();
		graphId = graph.getId().getValue();
		name = graph.getName().getValue();
	}

	/**
	 * Creates a new memory graph with a given name.
	 * 
	 * @param url
	 *            Webservice URL
	 * @param name
	 *            Graph name
	 * @param usePersistent
	 *            use a persistent representation instead of in-memory
	 * @throws CaughtException_Exception
	 * @throws IllegalNameException_Exception
	 * @throws GraphNotFoundException_Exception
	 */
	public SOAPONDEXGraph(URL url, String name, boolean usePersistent)
			throws WebserviceException_Exception {
		// connect to webservice from URL for graph name
		ONDEXapiWSService ondexService = new ONDEXapiWSService(url);
		soapGraph = ondexService.getONDEXapiWSPort();
		if (!usePersistent)
			graphId = soapGraph.createMemoryGraph(name);
		else
			graphId = soapGraph.createGraph(name);
		System.out.println("Using graphId " + graphId);
		if (graphId == 0)
			throw new RuntimeException("graph id is 0");
		this.name = name;
	}

	/**
	 * Deletes the wrapped graph from the web-service.
	 * 
	 * @return success message
	 */
	public String delete() {
		try {
			return soapGraph.deleteGraph(graphId);
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		graphId = 0;
		return "Failure deleting graph.";
	}

	@Override
	public ONDEXConcept createConcept(String pid, String annotation,
			String description, DataSource elementOf, ConceptClass ofType,
			Collection<EvidenceType> evidence) {
		EvidenceTypeIdList evidenceTypeIdList = new EvidenceTypeIdList();
		for (EvidenceType et : evidence) {
			evidenceTypeIdList.getString().add(et.getId());
		}
		Integer cid;
		try {
			cid = soapGraph.createConcept(graphId, pid, annotation,
					description, elementOf.getId(), ofType.getId(),
					evidenceTypeIdList);
			return new SOAPConcept(this, cid);
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ONDEXRelation createRelation(ONDEXConcept fromConcept,
			ONDEXConcept toConcept, RelationType ofType,
			Collection<EvidenceType> evidence) {
		EvidenceTypeIdList evidenceTypeIdList = new EvidenceTypeIdList();
		for (EvidenceType et : evidence) {
			evidenceTypeIdList.getString().add(et.getId());
		}
		Integer rid;
		try {
			rid = soapGraph.createRelation(graphId, fromConcept.getId(),
					toConcept.getId(), ofType.getId(), evidenceTypeIdList);
			return new SOAPRelation(this, rid);
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean deleteConcept(int id) {
		try {
			return soapGraph.deleteConcept(graphId, id);
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteRelation(int id) {
		try {
			return soapGraph.deleteRelation(graphId, id);
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean deleteRelation(ONDEXConcept fromConcept,
			ONDEXConcept toConcept, RelationType ofType) {
		WSRelation wsrel;
		try {
			wsrel = soapGraph.getRelationOfType(graphId, fromConcept.getId(),
					toConcept.getId(), ofType.getId());
			return soapGraph.deleteRelation(graphId, wsrel.getId().getValue());
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public ONDEXConcept getConcept(int id) {
		return new SOAPConcept(this, id);
	}

	@Override
	public Set<ONDEXConcept> getConcepts() {
		BitSet set = new BitSet();
		try {
			for (WSConcept wscon : soapGraph.getConcepts(graphId)
					.getWSConcept()) {
				set.set(wscon.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.unmodifiableSet(BitSetFunctions.create(this,
				ONDEXConcept.class, set));
	}

	@Override
	public Set<ONDEXConcept> getConceptsOfAttributeName(AttributeName an) {
		BitSet set = new BitSet();
		try {
			for (WSConcept wscon : soapGraph.getConceptsOfAttributeName(
					graphId, an.getId()).getWSConcept()) {
				set.set(wscon.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.unmodifiableSet(BitSetFunctions.create(this,
				ONDEXConcept.class, set));
	}

	@Override
	public Set<ONDEXConcept> getConceptsOfConceptClass(ConceptClass cc) {
		BitSet set = new BitSet();
		try {
			for (WSConcept wscon : soapGraph.getConceptsOfConceptClass(graphId,
					cc.getId()).getWSConcept()) {
				set.set(wscon.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.create(this, ONDEXConcept.class, set);
	}

	@Override
	public Set<ONDEXConcept> getConceptsOfTag(ONDEXConcept ac) {
		BitSet set = new BitSet();
		try {
			for (WSConcept wscon : soapGraph.getConceptsOfTag(graphId,
					ac.getId()).getWSConcept()) {
				set.set(wscon.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.create(this, ONDEXConcept.class, set);
	}

	@Override
	public Set<ONDEXConcept> getConceptsOfDataSource(DataSource dataSource) {
		BitSet set = new BitSet();
		try {
			for (WSConcept wscon : soapGraph.getConceptsOfDataSource(graphId,
					dataSource.getId()).getWSConcept()) {
				set.set(wscon.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.create(this, ONDEXConcept.class, set);
	}

	@Override
	public Set<ONDEXConcept> getConceptsOfEvidenceType(EvidenceType et) {
		BitSet set = new BitSet();
		try {
			for (WSConcept wscon : soapGraph.getConceptsOfEvidenceType(graphId,
					et.getId()).getWSConcept()) {
				set.set(wscon.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.create(this, ONDEXConcept.class, set);
	}

	@Override
	public Set<ONDEXConcept> getAllTags() {
		BitSet set = new BitSet();
		try {
			for (WSConcept wscon : soapGraph.getTagsGraph(graphId)
					.getWSConcept()) {
				set.set(wscon.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.create(this, ONDEXConcept.class, set);
	}

	@Override
	public EntityFactory getFactory() {
		return new EntityFactory(this);
	}

	@Override
	public ONDEXGraphMetaData getMetaData() {
		return new SOAPONDEXGraphMetaData(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ONDEXRelation getRelation(int id) {
		return new SOAPRelation(this, id);
	}

	@Override
	public ONDEXRelation getRelation(ONDEXConcept fromConcept,
			ONDEXConcept toConcept, RelationType ofType) {
		WSRelation wsrel;
		try {
			wsrel = soapGraph.getRelationOfType(graphId, fromConcept.getId(),
					toConcept.getId(), ofType.getId());
			return new SOAPRelation(this, wsrel.getId().getValue());
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Set<ONDEXRelation> getRelations() {
		BitSet set = new BitSet();
		try {
			for (WSRelation wsrel : soapGraph.getRelations(graphId)
					.getWSRelation()) {
				set.set(wsrel.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.unmodifiableSet(BitSetFunctions.create(this,
				ONDEXRelation.class, set));
	}

	@Override
	public Set<ONDEXRelation> getRelationsOfAttributeName(AttributeName an) {
		BitSet set = new BitSet();
		try {
			for (WSRelation wsrel : soapGraph.getRelationsOfAttributeName(
					graphId, an.getId()).getWSRelation()) {
				set.set(wsrel.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.create(this, ONDEXRelation.class, set);
	}

	@Override
	public Set<ONDEXRelation> getRelationsOfConcept(ONDEXConcept concept) {
		BitSet set = new BitSet();
		try {
			for (WSRelation wsrel : soapGraph.getRelationsOfConcept(graphId,
					concept.getId()).getWSRelation()) {
				set.set(wsrel.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.create(this, ONDEXRelation.class, set);
	}

	@Override
	public Set<ONDEXRelation> getRelationsOfConceptClass(ConceptClass cc) {
		BitSet set = new BitSet();
		try {
			for (WSRelation wsrel : soapGraph.getRelationsOfConceptClass(
					graphId, cc.getId()).getWSRelation()) {
				set.set(wsrel.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.create(this, ONDEXRelation.class, set);
	}

	@Override
	public Set<ONDEXRelation> getRelationsOfTag(ONDEXConcept ac) {
		BitSet set = new BitSet();
		try {
			for (WSRelation wsrel : soapGraph.getRelationsOfTag(graphId,
					ac.getId()).getWSRelation()) {
				set.set(wsrel.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.create(this, ONDEXRelation.class, set);
	}

	@Override
	public Set<ONDEXRelation> getRelationsOfDataSource(DataSource dataSource) {
		BitSet set = new BitSet();
		try {
			for (WSRelation wsrel : soapGraph.getRelationsOfDataSource(graphId,
					dataSource.getId()).getWSRelation()) {
				set.set(wsrel.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.create(this, ONDEXRelation.class, set);
	}

	@Override
	public Set<ONDEXRelation> getRelationsOfEvidenceType(EvidenceType et) {
		BitSet set = new BitSet();
		try {
			for (WSRelation wsrel : soapGraph.getRelationsOfEvidenceType(
					graphId, et.getId()).getWSRelation()) {
				set.set(wsrel.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.create(this, ONDEXRelation.class, set);
	}

	@Override
	public Set<ONDEXRelation> getRelationsOfRelationType(RelationType rt) {
		BitSet set = new BitSet();
		try {
			for (WSRelation wsrel : soapGraph.getRelationsOfRelationType(
					graphId, rt.getId()).getWSRelation()) {
				set.set(wsrel.getId().getValue().intValue());
			}
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		return BitSetFunctions.create(this, ONDEXRelation.class, set);
	}

	@Override
	public long getSID() {
		return graphId;
	}

	@Override
	public boolean isReadOnly() {
		try {
			return soapGraph.isReadOnly(graphId);
		} catch (WebserviceException_Exception e) {
			fireActionEvent(e, e.getMessage());
			e.printStackTrace();
		}
		// just to be careful
		return true;
	}
}
