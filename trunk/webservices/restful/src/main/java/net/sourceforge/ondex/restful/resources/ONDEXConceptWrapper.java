package net.sourceforge.ondex.restful.resources;

import net.sourceforge.ondex.core.*;
import net.sourceforge.ondex.exception.type.AccessDeniedException;
import net.sourceforge.ondex.exception.type.NullValueException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * RESTful wrapper for an ONDEXConcept.
 * 
 * @author taubertj
 * 
 */
@Path("/graphs/{graphid}/concepts/{conceptid}")
public class ONDEXConceptWrapper implements ONDEXConcept {

	/**
	 * wrapped ONDEXConcept
	 */
	ONDEXConcept concept = null;

	/**
	 * wrapped ONDEXGraph
	 */
	ONDEXGraph graph = null;

	/**
	 * Retrieve specified ONDEXConcept from graph register.
	 * 
	 * @param gid
	 *            graph id
	 * @param cid
	 *            concept id
	 */
	public ONDEXConceptWrapper(@PathParam("graphid") Integer gid,
			@PathParam("conceptid") Integer cid) {
		// get graph from cache and retrieve concept
		graph = (ONDEXGraph) ONDEXEntryPoint.cache.get(gid).getObjectValue();
		concept = graph.getConcept(cid);
	}

	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("tag")
	@Override
	public void addTag(ONDEXConcept ac) throws AccessDeniedException,
			NullValueException {
		concept.addTag(ac);
	}

	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("evidence")
	@Override
	public void addEvidenceType(EvidenceType evidencetype) {
		concept.addEvidenceType(evidencetype);
	}

	@Override
	public ConceptAccession createConceptAccession(String accession,
			DataSource elementOf, boolean ambiguous) {
		// TODO
		return concept.createConceptAccession(accession, elementOf, ambiguous);
	}

	@Override
	public ConceptName createConceptName(String name, boolean isPreferred) {
		// TODO
		return concept.createConceptName(name, isPreferred);
	}

	@Override
	public Attribute createAttribute(AttributeName attrname, Object value,
			boolean doIndex) throws AccessDeniedException, NullValueException {
		// TODO
		return concept.createAttribute(attrname, value, doIndex);
	}

	/**
	 * Proxy method for deleting a concept from a graph.
	 * 
	 * @return application/xml or text/html
	 */
	@DELETE
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	public boolean delete() {
		return graph.deleteConcept(concept.getId());
	}

	@DELETE
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptaccessions/{accession}/{elementof}")
	@Override
	public boolean deleteConceptAccession(
			@PathParam("accession") String accession,
			@PathParam("elementof") DataSource elementOf) {
		return concept.deleteConceptAccession(accession, elementOf);
	}

	@DELETE
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptnames/{name}")
	@Override
	public boolean deleteConceptName(@PathParam("name") String name) {
		return concept.deleteConceptName(name);
	}

	@DELETE
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("attributes/{attributename}")
	@Override
	public boolean deleteAttribute(
			@PathParam("attributename") AttributeName attrname) {
		return concept.deleteAttribute(attrname);
	}

	/**
	 * Returns the whole concept as XML or as html.
	 * 
	 * @return application/xml or text/html
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	public ONDEXConcept get() {
		return concept;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("annotation")
	@Override
	public String getAnnotation() {
		return concept.getAnnotation();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptaccessions/{accession}/{elementof}")
	@Override
	public ConceptAccession getConceptAccession(
			@PathParam("accession") String accession,
			@PathParam("elementof") DataSource elementOf) {
		return concept.getConceptAccession(accession, elementOf);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptaccessions")
	@Override
	public Set<ConceptAccession> getConceptAccessions() {
		return concept.getConceptAccessions();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptname")
	@Override
	public ConceptName getConceptName() {
		return concept.getConceptName();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptnames/{name}")
	@Override
	public ConceptName getConceptName(@PathParam("name") String name) {
		return concept.getConceptName(name);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptnames")
	@Override
	public Set<ConceptName> getConceptNames() {
		return concept.getConceptNames();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("tags")
	@Override
	public Set<ONDEXConcept> getTags() {
		return concept.getTags();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("description")
	@Override
	public String getDescription() {
		return concept.getDescription();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("elementof")
	@Override
	public DataSource getElementOf() {
		return concept.getElementOf();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("evidence")
	@Override
	public Set<EvidenceType> getEvidence() {
		return concept.getEvidence();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("attributes/{attributename}")
	@Override
	public Attribute getAttribute(
			@PathParam("attributename") AttributeName attrname) {
		return concept.getAttribute(attrname);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("attributes")
	@Override
	public Set<Attribute> getAttributes() {
		return concept.getAttributes();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("id")
	@Override
	public int getId() {
		return concept.getId();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("oftype")
	@Override
	public ConceptClass getOfType() {
		return concept.getOfType();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("pid")
	@Override
	public String getPID() {
		return concept.getPID();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("sid")
	@Override
	public long getSID() {
		return concept.getSID();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("inheritedfrom/{conceptclass}")
	@Override
	public boolean inheritedFrom(@PathParam("conceptclass") ConceptClass cc) {
		return concept.inheritedFrom(cc);
	}

	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	@Path("tags/{conceptid}")
	@Override
	public boolean removeTag(@PathParam("conceptid") ONDEXConcept ac) {
		return concept.removeTag(ac);
	}

	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	@Path("evidence/{evidencetype}")
	@Override
	public boolean removeEvidenceType(
			@PathParam("evidencetype") EvidenceType evidencetype) {
		return concept.removeEvidenceType(evidencetype);
	}

	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("annotation")
	@Override
	public void setAnnotation(String annotation) {
		concept.setAnnotation(annotation);
	}

	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("description")
	@Override
	public void setDescription(String description) {
		concept.setDescription(description);
	}

	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("pid")
	@Override
	public void setPID(String pid) {
		concept.setPID(pid);
	}

}
