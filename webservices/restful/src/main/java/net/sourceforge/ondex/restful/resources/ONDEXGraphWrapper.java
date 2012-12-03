package net.sourceforge.ondex.restful.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
import net.sourceforge.ondex.restful.util.EntityWrapper;

/**
 * RESTful wrapper for an ONDEXGraph.
 * 
 * @author taubertj
 * 
 */
@Path("/graphs/{graphid}")
public class ONDEXGraphWrapper implements ONDEXGraph {

	/**
	 * wrapped ONDEXGraph
	 */
	ONDEXGraph graph = null;

	/**
	 * Retrieves ONDEXGraph for given id from register.
	 * 
	 * @param id
	 *            graphid
	 */
	public ONDEXGraphWrapper(@PathParam("graphid") int id) {
		// get graph from cache
		graph = (ONDEXGraph) ONDEXEntryPoint.cache.get(id).getObjectValue();
	}

	/**
	 * Returns the whole graph as XML or as html.
	 * 
	 * @return application/xml or text/html
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	public ONDEXGraph get() {
		return graph;
	}

	/**
	 * Returns all the IDs of concepts and relations in the graph.
	 * 
	 * @return IDs of concepts and relations
	 */
	@GET
	@Path("entities")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<EntityWrapper> getEntitiesIDs() {
		List<EntityWrapper> ids = new ArrayList<EntityWrapper>();

		// first get concepts in list
		for (ONDEXConcept c : graph.getConcepts()) {
			EntityWrapper wrapper = new EntityWrapper();
			wrapper.setId(c.getId());
			ids.add(wrapper);
		}

		// second add relations to it
		for (ONDEXRelation r : graph.getRelations()) {
			EntityWrapper wrapper = new EntityWrapper();
			wrapper.setId(r.getId());
			wrapper.setFrom(r.getFromConcept().getId());
			wrapper.setTo(r.getToConcept().getId());
			ids.add(wrapper);
		}
		return ids;
	}

	@Override
	public ONDEXConcept createConcept(String pid, String annotation,
			String description, DataSource elementOf, ConceptClass ofType,
			Collection<EvidenceType> evidence) {
		// TODO
		return graph.createConcept(pid, annotation, description, elementOf,
				ofType, evidence);
	}

	@Override
	public ONDEXRelation createRelation(ONDEXConcept fromConcept,
			ONDEXConcept toConcept, RelationType ofType,
			Collection<EvidenceType> evidence) {
		// TODO
		return graph.createRelation(fromConcept, toConcept, ofType, evidence);
	}

	/**
	 * Implemented in ONDEXConceptWrapper.
	 */
	@Override
	public boolean deleteConcept(int id) {
		return graph.deleteConcept(id);
	}

	/**
	 * Implemented in ONDEXConceptWrapper using composite id.
	 */
	@Override
	public boolean deleteRelation(ONDEXConcept fromConcept,
			ONDEXConcept toConcept, RelationType ofType) {
		return graph.deleteRelation(fromConcept, toConcept, ofType);
	}

	/**
	 * Implemented in ONDEXRelationWrapper.
	 */
	@Override
	public boolean deleteRelation(int id) {
		return graph.deleteRelation(id);
	}

	/**
	 * Implemented in ONDEXConceptWrapper.
	 */
	@Override
	public ONDEXConcept getConcept(int id) {
		return graph.getConcept(id);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("concepts")
	@Override
	public Set<ONDEXConcept> getConcepts() {
		return graph.getConcepts();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptsofattributename/{anid}")
	@Override
	public Set<ONDEXConcept> getConceptsOfAttributeName(
			@PathParam("anid") AttributeName an) {
		return graph.getConceptsOfAttributeName(an);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptsofdatasource/{dsid}")
	@Override
	public Set<ONDEXConcept> getConceptsOfDataSource(
			@PathParam("dsid") DataSource dataSource) {
		return graph.getConceptsOfDataSource(dataSource);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptsofconceptclass/{ccid}")
	@Override
	public Set<ONDEXConcept> getConceptsOfConceptClass(
			@PathParam("ccid") ConceptClass cc) {
		return graph.getConceptsOfConceptClass(cc);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptsoftag/{conceptid}")
	@Override
	public Set<ONDEXConcept> getConceptsOfTag(
			@PathParam("conceptid") ONDEXConcept ac) {
		return graph.getConceptsOfTag(ac);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptsofevidencetype/{etid}")
	@Override
	public Set<ONDEXConcept> getConceptsOfEvidenceType(
			@PathParam("etid") EvidenceType et) {
		return graph.getConceptsOfEvidenceType(et);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("tags")
	@Override
	public Set<ONDEXConcept> getAllTags() {
		return graph.getAllTags();
	}

	@Override
	public EntityFactory getFactory() {
		// TODO
		return graph.getFactory();
	}

	@Override
	public ONDEXGraphMetaData getMetaData() {
		// TODO
		return graph.getMetaData();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public String getName() {
		return graph.getName();
	}

	/**
	 * Implemented in ONDEXRelationWrapper using composite id.
	 */
	@Override
	public ONDEXRelation getRelation(ONDEXConcept fromConcept,
			ONDEXConcept toConcept, RelationType ofType) {
		return graph.getRelation(fromConcept, toConcept, ofType);
	}

	/**
	 * Implemented in ONDEXRelationWrapper.
	 */
	@Override
	public ONDEXRelation getRelation(int id) {
		return graph.getRelation(id);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relations")
	@Override
	public Set<ONDEXRelation> getRelations() {
		return graph.getRelations();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relationsofattributename/{anid}")
	@Override
	public Set<ONDEXRelation> getRelationsOfAttributeName(
			@PathParam("anid") AttributeName an) {
		return graph.getRelationsOfAttributeName(an);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relationsofdatasource/{dsid}")
	@Override
	public Set<ONDEXRelation> getRelationsOfDataSource(
			@PathParam("dsid") DataSource dataSource) {
		return graph.getRelationsOfDataSource(dataSource);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relationsofconcept/{conceptid}")
	@Override
	public Set<ONDEXRelation> getRelationsOfConcept(
			@PathParam("conceptid") ONDEXConcept concept) {
		return graph.getRelationsOfConcept(concept);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relationsofconceptclass/{ccid}")
	@Override
	public Set<ONDEXRelation> getRelationsOfConceptClass(
			@PathParam("ccid") ConceptClass cc) {
		return graph.getRelationsOfConceptClass(cc);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relationsoftag/{conceptid}")
	@Override
	public Set<ONDEXRelation> getRelationsOfTag(
			@PathParam("conceptid") ONDEXConcept ac) {
		return graph.getRelationsOfTag(ac);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relationsofevidencetype/{etid}")
	@Override
	public Set<ONDEXRelation> getRelationsOfEvidenceType(
			@PathParam("etid") EvidenceType et) {
		return graph.getRelationsOfEvidenceType(et);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relationsofrelationtype/{rtid}")
	@Override
	public Set<ONDEXRelation> getRelationsOfRelationType(
			@PathParam("rtid") RelationType rt) {
		return graph.getRelationsOfRelationType(rt);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("readonly")
	@Override
	public boolean isReadOnly() {
		return graph.isReadOnly();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("sid")
	@Override
	public long getSID() {
		return graph.getSID();
	}

}
