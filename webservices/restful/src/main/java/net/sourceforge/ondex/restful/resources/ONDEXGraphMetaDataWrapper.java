package net.sourceforge.ondex.restful.resources;

import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.EvidenceType;
import net.sourceforge.ondex.core.MetaDataFactory;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.ONDEXGraphMetaData;
import net.sourceforge.ondex.core.RelationType;
import net.sourceforge.ondex.core.Unit;

/**
 * RESTful wrapper for an ONDEXGraphMetaData.
 * 
 * @author taubertj
 * 
 */
@Path("/graphs/{graphid}/metadata")
public class ONDEXGraphMetaDataWrapper implements ONDEXGraphMetaData {

	/**
	 * wrapped ONDEXGraphMetaData
	 */
	ONDEXGraphMetaData meta = null;

	/**
	 * Retrieves ONDEXGraphMetaData for given id from register.
	 * 
	 * @param id
	 *            graphid
	 */
	public ONDEXGraphMetaDataWrapper(@PathParam("graphid") int id) {
		// get graph and meta data from cache
		meta = ((ONDEXGraph) ONDEXEntryPoint.cache.get(id).getObjectValue())
				.getMetaData();
	}

	/**
	 * Returns the whole meta data as XML or an overview as html.
	 * 
	 * @return application/xml or text/html
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	public ONDEXGraphMetaData get() {
		return meta;
	}

	@Override
	public void associateGraph(ONDEXGraph g) {
		// TODO
		meta.associateGraph(g);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("checkattributename/{id}")
	@Override
	public boolean checkAttributeName(@PathParam("id") String id) {
		return meta.checkAttributeName(id);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("checkdatasource/{id}")
	@Override
	public boolean checkDataSource(@PathParam("id") String id) {
		return meta.checkDataSource(id);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("checkconceptclass/{id}")
	@Override
	public boolean checkConceptClass(@PathParam("id") String id) {
		return meta.checkConceptClass(id);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("checkevidencetype/{id}")
	@Override
	public boolean checkEvidenceType(@PathParam("id") String id) {
		return meta.checkEvidenceType(id);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("checkrelationtype/{id}")
	@Override
	public boolean checkRelationType(@PathParam("id") String id) {
		return meta.checkRelationType(id);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("checkunit/{id}")
	@Override
	public boolean checkUnit(@PathParam("id") String id) {
		return meta.checkUnit(id);
	}

	@Override
	public AttributeName createAttributeName(String id, String fullname,
			String description, Unit unit, Class<?> datatype,
			AttributeName specialisationOf) {
		// TODO
		return meta.createAttributeName(id, fullname, description, unit,
				datatype, specialisationOf);
	}

	@Override
	public DataSource createDataSource(String id, String fullname,
			String description) {
		// TODO
		return meta.createDataSource(id, fullname, description);
	}

	@Override
	public ConceptClass createConceptClass(String id, String fullname,
			String description, ConceptClass specialisationOf) {
		// TODO
		return meta.createConceptClass(id, fullname, description,
				specialisationOf);
	}

	@Override
	public EvidenceType createEvidenceType(String id, String fullname,
			String description) {
		// TODO
		return meta.createEvidenceType(id, fullname, description);
	}

	@Override
	public RelationType createRelationType(String id, String fullname,
			String description, String inverseName, boolean isAntisymmetric,
			boolean isReflexive, boolean isSymmetric, boolean isTransitiv,
			RelationType specialisationOf) {
		// TODO
		return meta.createRelationType(id, fullname, description, inverseName,
				isAntisymmetric, isReflexive, isSymmetric, isTransitiv,
				specialisationOf);
	}

	@Override
	public Unit createUnit(String id, String fullname, String description) {
		// TODO
		return meta.createUnit(id, fullname, description);
	}

	@DELETE
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("attributenames/{id}")
	@Override
	public boolean deleteAttributeName(@PathParam("id") String id) {
		return meta.deleteAttributeName(id);
	}

	@DELETE
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("datasources/{id}")
	@Override
	public boolean deleteDataSource(@PathParam("id") String id) {
		return meta.deleteDataSource(id);
	}

	@DELETE
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptclasses/{id}")
	@Override
	public boolean deleteConceptClass(@PathParam("id") String id) {
		return meta.deleteConceptClass(id);
	}

	@DELETE
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("evidencetypes/{id}")
	@Override
	public boolean deleteEvidenceType(@PathParam("id") String id) {
		return meta.deleteEvidenceType(id);
	}

	@DELETE
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relationtypes/{id}")
	@Override
	public boolean deleteRelationType(@PathParam("id") String id) {
		return meta.deleteRelationType(id);
	}

	@DELETE
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("units/{id}")
	@Override
	public boolean deleteUnit(@PathParam("id") String id) {
		return meta.deleteUnit(id);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("attributenames/{id}")
	@Override
	public AttributeName getAttributeName(@PathParam("id") String id) {
		return meta.getAttributeName(id);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("attributenames")
	@Override
	public Set<AttributeName> getAttributeNames() {
		return meta.getAttributeNames();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("datasources/{id}")
	@Override
	public DataSource getDataSource(@PathParam("id") String id) {
		return meta.getDataSource(id);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("datasources")
	@Override
	public Set<DataSource> getDataSources() {
		return meta.getDataSources();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptclasses/{id}")
	@Override
	public ConceptClass getConceptClass(@PathParam("id") String id) {
		return meta.getConceptClass(id);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("conceptclasses")
	@Override
	public Set<ConceptClass> getConceptClasses() {
		return meta.getConceptClasses();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("evidencetypes/{id}")
	@Override
	public EvidenceType getEvidenceType(@PathParam("id") String id) {
		return meta.getEvidenceType(id);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("evidencetypes")
	@Override
	public Set<EvidenceType> getEvidenceTypes() {
		return meta.getEvidenceTypes();
	}

	@Override
	public MetaDataFactory getFactory() {
		// TODO
		return meta.getFactory();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relationtypes/{id}")
	@Override
	public RelationType getRelationType(@PathParam("id") String id) {
		return meta.getRelationType(id);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relationtypes")
	@Override
	public Set<RelationType> getRelationTypes() {
		return meta.getRelationTypes();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("units/{id}")
	@Override
	public Unit getUnit(@PathParam("id") String id) {
		return meta.getUnit(id);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("units")
	@Override
	public Set<Unit> getUnits() {
		return meta.getUnits();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("sid")
	@Override
	public long getSID() {
		return meta.getSID();
	}

}
