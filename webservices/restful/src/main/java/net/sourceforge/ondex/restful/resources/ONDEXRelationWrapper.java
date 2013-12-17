package net.sourceforge.ondex.restful.resources;

import net.sourceforge.ondex.core.*;
import net.sourceforge.ondex.exception.type.AccessDeniedException;
import net.sourceforge.ondex.exception.type.NullValueException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * RESTful wrapper for an ONDEXRelation.
 * 
 * RelationId can either be the Integer id or a concatenation in form of
 * "from,to,ofType" or "from,to,qualifier,ofType" all represented by their id.
 * This is similar to what should be returned by getKey().
 * 
 * @author taubertj
 * 
 */
@Path("/graphs/{graphid}/relations/{relationid}")
public class ONDEXRelationWrapper implements ONDEXRelation {

	/**
	 * wrapped ONDEXGraph
	 */
	ONDEXGraph graph = null;

	/**
	 * wrapped ONDEXRelation
	 */
	ONDEXRelation relation = null;

	/**
	 * Retrieve specified ONDEXRelation from graph register.
	 * 
	 * @param gid
	 *            graph id
	 * @param rid
	 *            relation id, can be composite id
	 */
	public ONDEXRelationWrapper(@PathParam("graphid") Integer gid,
			@PathParam("relationid") String rid) {
		// get graph from cache and retrieve relation
		graph = (ONDEXGraph) ONDEXEntryPoint.cache.get(gid).getObjectValue();
		if (!rid.contains(","))
			relation = graph.getRelation(Integer.parseInt(rid));
		else {
			String[] split = rid.split(",");
			ONDEXConcept fromConcept = graph.getConcept(Integer
					.parseInt(split[0]));
			ONDEXConcept toConcept = graph.getConcept(Integer
					.parseInt(split[1]));
			RelationType ofType = graph.getMetaData().getRelationType(split[2]);
			relation = graph.getRelation(fromConcept, toConcept, ofType);
		}
	}

	/**
	 * Returns the whole relation as XML or as html.
	 * 
	 * @return application/xml or text/html
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	public ONDEXRelation get() {
		return relation;
	}

	/**
	 * Proxy method for deleting a relation from a graph.
	 * 
	 * @return application/xml or text/html
	 */
	@DELETE
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	public boolean delete() {
		return graph.deleteRelation(relation.getId());
	}

	@Override
	public Attribute createAttribute(AttributeName attrname, Object value,
			boolean doIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@DELETE
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("attributes/{attributename}")
	@Override
	public boolean deleteAttribute(
			@PathParam("attributename") AttributeName attrname) {
		return relation.deleteAttribute(attrname);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("from")
	@Override
	public ONDEXConcept getFromConcept() {
		return relation.getFromConcept();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("key")
	@Override
	public RelationKey getKey() {
		return relation.getKey();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("oftype")
	@Override
	public RelationType getOfType() {
		return relation.getOfType();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("attributes/{attributename}")
	@Override
	public Attribute getAttribute(
			@PathParam("attributename") AttributeName attrname) {
		return relation.getAttribute(attrname);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("attributes")
	@Override
	public Set<Attribute> getAttributes() {
		return relation.getAttributes();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("to")
	@Override
	public ONDEXConcept getToConcept() {
		return relation.getToConcept();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("inheritedfrom/{relationtype}")
	@Override
	public boolean inheritedFrom(@PathParam("relationtype") RelationType rt) {
		return relation.inheritedFrom(rt);
	}

	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("tag")
	@Override
	public void addTag(ONDEXConcept ac) throws AccessDeniedException,
			NullValueException {
		relation.addTag(ac);
	}

	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("evidence")
	@Override
	public void addEvidenceType(EvidenceType evidencetype) {
		relation.addEvidenceType(evidencetype);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("tags")
	@Override
	public Set<ONDEXConcept> getTags() {
		return relation.getTags();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("evidence")
	@Override
	public Set<EvidenceType> getEvidence() {
		return relation.getEvidence();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("id")
	@Override
	public int getId() {
		return relation.getId();
	}

	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	@Path("tags/{conceptid}")
	@Override
	public boolean removeTag(@PathParam("conceptid") ONDEXConcept ac) {
		return relation.removeTag(ac);
	}

	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	@Path("evidence/{evidencetype}")
	@Override
	public boolean removeEvidenceType(
			@PathParam("evidencetype") EvidenceType evidencetype) {
		return relation.removeEvidenceType(evidencetype);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("sid")
	@Override
	public long getSID() {
		return relation.getSID();
	}

}
