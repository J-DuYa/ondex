package net.sourceforge.ondex.restful.resources;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sourceforge.ondex.algorithm.dijkstra.DijkstraQueue;
import net.sourceforge.ondex.algorithm.dijkstra.PathNode;
import net.sourceforge.ondex.config.Config;
import net.sourceforge.ondex.config.LuceneRegistry;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.core.searchable.LuceneConcept;
import net.sourceforge.ondex.core.searchable.LuceneEnv;
import net.sourceforge.ondex.core.searchable.LuceneQueryBuilder;
import net.sourceforge.ondex.event.ONDEXEvent;
import net.sourceforge.ondex.event.type.GeneralOutputEvent;
import net.sourceforge.ondex.logging.ONDEXLogger;
import net.sourceforge.ondex.restful.util.OXLResponseBuilder;

import org.apache.lucene.search.Query;

/**
 * RESTful wrapper for queries on an ONDEXGraph.
 * 
 * @author taubertj
 * 
 */
@Path("/graphs/{graphid}/query")
public class ONDEXQueryWrapper {

	/**
	 * wrapped ONDEXGraph
	 */
	ONDEXGraph graph = null;

	/**
	 * Index for ONDEXGraph
	 */
	LuceneEnv lucene = null;

	/**
	 * Retrieves ONDEXGraph for given id from register.
	 * 
	 * @param id
	 *            graphid
	 */
	public ONDEXQueryWrapper(@PathParam("graphid") int id) {
		// get graph from cache
		graph = (ONDEXGraph) ONDEXEntryPoint.cache.get(id).getObjectValue();

		// already loaded Lucene index
		lucene = ONDEXEntryPoint.indicies.get(id);

		if (lucene == null) {

			// each graph id gets its own index directory
			String dir = Config.ondexDir + File.separator + "index"
					+ File.separator + id;

			// get index for graph
			lucene = getIndex(graph, dir);

			// add to global list to make sure it gets closed again
			ONDEXEntryPoint.indicies.put(id, lucene);
		}
	}

	/**
	 * Decodes a URL encoded string
	 * 
	 * @param term
	 * @return
	 */
	private String decodeURL(String term) {
		try {
			return URLDecoder.decode(term, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new WebApplicationException(e,
					Response.Status.INTERNAL_SERVER_ERROR);
		}
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
	 * Returns the whole graph as OXL.
	 * 
	 * @return application/gzip
	 */
	@GET
	@Path("oxl")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response getAsOXL() {
		return OXLResponseBuilder.build(graph.getSID() + "", graph);
	}

	/**
	 * Returns or constructs new index for a given graph.
	 * 
	 * @param graph
	 * @param dir
	 * @return
	 */
	private LuceneEnv getIndex(ONDEXGraph graph, String dir) {

		long start = System.currentTimeMillis();
		ONDEXLogger logger = new ONDEXLogger();

		if (dir != null && new File(dir).exists()) {
			LuceneEnv lenv = new LuceneEnv(dir, false);
			lenv.addONDEXListener(logger);
			lenv.setONDEXGraph(graph);
			return lenv;
		}

		LuceneEnv lenv = new LuceneEnv(dir, true);
		lenv.addONDEXListener(logger);
		lenv.setONDEXGraph(graph);
		LuceneRegistry.sid2luceneEnv.put(graph.getSID(), lenv);

		logger.eventOccurred(new ONDEXEvent(this, new GeneralOutputEvent(
				"Lucene took " + (System.currentTimeMillis() - start)
						+ " msec.", "getIndex")));

		return lenv;
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("listofaccessiondatasources")
	public Set<String> getListOfConceptAccDataSources() {
		return lucene.getListOfConceptAccDataSources();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("listofconceptattributes")
	public Set<String> getListOfConceptAttrNames() {
		return lucene.getListOfConceptAttrNames();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("listofrelationattributes")
	public Set<String> getListOfRelationAttrNames() {
		return lucene.getListOfRelationAttrNames();
	}

	/**
	 * Returns a concept's opposite one on a relation.
	 * 
	 * @param c_curr
	 *            the concept on the one end.
	 * @param r_curr
	 *            the relation.
	 * @return the concept on the other end.
	 */
	private ONDEXConcept getOppositeConcept(ONDEXConcept c_curr,
			ONDEXRelation r_curr) {
		return (r_curr.getFromConcept().equals(c_curr)) ? r_curr.getToConcept()
				: r_curr.getFromConcept();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("concepts")
	public Set<ONDEXConcept> queryConcepts() {
		// simply return all concepts
		return graph.getConcepts();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("concepts/{queryterm}")
	public Set<ONDEXConcept> queryConcepts(@PathParam("queryterm") String term) {
		term = decodeURL(term);

		// query across all concept fields
		Query[] queries = new Query[] {
				LuceneQueryBuilder.searchConceptByDescriptionExact(term),
				LuceneQueryBuilder.searchConceptByConceptAccessionExact(term,
						true, lucene.getListOfConceptAccDataSources()),
				LuceneQueryBuilder.searchConceptByConceptNameExact(term),
				LuceneQueryBuilder.searchConceptByConceptAttributeExact(term,
						lucene.getListOfConceptAttrNames()) };

		// compose combined query
		Query q = LuceneQueryBuilder.searchConceptByAnnotationExact(term);
		q = q.combine(queries);

		// query result
		return lucene.searchInConcepts(q);
	}

	/**
	 * Returns the list of concepts as OXL.
	 * 
	 * @return application/gzip
	 */
	@GET
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	@Path("concepts/{queryterm}/oxl")
	public Response queryConceptsAsOXL(@PathParam("queryterm") String term) {
		// get query results
		Set<ONDEXConcept> concepts = queryConcepts(term);
		return responseIncludingRelations(concepts);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("concepts/{queryterm}/neighbours/{depth}")
	public Set<ONDEXConcept> queryConceptsNeighbourhood(
			@PathParam("queryterm") String term, @PathParam("depth") int depth) {
		// get query results
		Set<ONDEXConcept> concepts = queryConcepts(term);

		// get neighbours
		Set<ONDEXConcept> neighbours = new HashSet<ONDEXConcept>();
		for (ONDEXConcept c : concepts) {
			// unwrap lucene concept
			if (c instanceof LuceneConcept)
				c = ((LuceneConcept) c).getParent();
			recurse(neighbours, c, depth);
		}

		return neighbours;
	}

	/**
	 * Returns the list of concepts as OXL.
	 * 
	 * @return application/gzip
	 */
	@GET
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	@Path("concepts/{queryterm}/neighbours/{depth}/oxl")
	public Response queryConceptsNeighbourhoodAsOXL(
			@PathParam("queryterm") String term, @PathParam("depth") int depth) {
		// get query results
		Set<ONDEXConcept> concepts = queryConceptsNeighbourhood(term, depth);
		return responseIncludingRelations(concepts);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("concepts/{queryterm}/shortestpath")
	public Set<ONDEXConcept> queryConceptsShortestPath(
			@PathParam("queryterm") String term) {
		// get query results
		Set<ONDEXConcept> concepts = queryConcepts(term);

		// for better pair-wise handling
		ONDEXConcept[] array = concepts.toArray(new ONDEXConcept[concepts
				.size()]);
		Set<ONDEXConcept> results = new HashSet<ONDEXConcept>();
		for (int i = 0; i < array.length; i++) {
			for (int j = i + 1; j < array.length; j++) {
				// unwrap lucene concept
				LuceneConcept start = (LuceneConcept) array[i];
				LuceneConcept stop = (LuceneConcept) array[j];
				PathNode result = search(start.getParent(), stop.getParent());
				if (result != null) {
					// get shortest paths between concepts
					Set<ONDEXConcept> neighbours = new HashSet<ONDEXConcept>();
					traceBack(result, neighbours);
					results.addAll(neighbours);
				}
				// add seed nodes to results
				results.add(start.getParent());
				results.add(stop.getParent());
			}
		}

		return results;
	}

	/**
	 * Returns the list of concepts as OXL.
	 * 
	 * @return application/gzip
	 */
	@GET
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	@Path("concepts/{queryterm}/shortestpath/oxl")
	public Response queryConceptsShortestPathAsOXL(
			@PathParam("queryterm") String term) {
		// get query results
		Set<ONDEXConcept> concepts = queryConceptsShortestPath(term);
		return responseIncludingRelations(concepts);
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relations")
	public Set<ONDEXRelation> queryRelations() {
		// simply return all relations
		return graph.getRelations();
	}

	@GET
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_XML })
	@Path("relations/{queryterm}")
	public Set<ONDEXRelation> queryRelations(@PathParam("queryterm") String term) {
		// compose query
		Query q = LuceneQueryBuilder.searchRelationByRelationAttributeExact(
				decodeURL(term), lucene.getListOfRelationAttrNames());

		// query result
		return lucene.searchInRelations(q);
	}

	/**
	 * Returns the list of relations as OXL.
	 * 
	 * @return application/gzip
	 */
	@GET
	@Path("relations/{queryterm}/oxl")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response queryRelationsAsOXL(@PathParam("queryterm") String term) {
		// get query results
		Set<ONDEXRelation> relations = queryRelations(term);
		return responseIncludingConcepts(relations);
	}

	/**
	 * Recursively performing a DFS on the graph to retrieve neighbours.
	 */
	private void recurse(Set<ONDEXConcept> concepts, ONDEXConcept root,
			int depth) {

		// add root as neighbour
		concepts.add(root);

		if (depth > 0) {
			// next recursion step
			depth--;
			for (ONDEXRelation r : graph.getRelationsOfConcept(root)) {
				ONDEXConcept from = r.getFromConcept();
				ONDEXConcept to = r.getToConcept();

				// prevent self loops
				if (!from.equals(to)) {
					// outgoing relations
					if (root.equals(from)) {
						recurse(concepts, to, depth);
					}
					// incoming relations
					else {
						recurse(concepts, from, depth);
					}
				}
			}
		}
	}

	/**
	 * Fills sub-graph of relations with all its corresponding concepts and
	 * returns as OXL graph.
	 * 
	 * @param relations
	 *            list of relations
	 * @return OXL graph
	 */
	private Response responseIncludingConcepts(Set<ONDEXRelation> relations) {

		// add related concepts
		Set<ONDEXConcept> concepts = new HashSet<ONDEXConcept>();
		for (ONDEXRelation r : relations) {
			concepts.add(r.getFromConcept());
			concepts.add(r.getToConcept());
		}

		// create response
		return OXLResponseBuilder.build(graph.getSID() + "", concepts,
				relations);
	}

	/**
	 * Fills sub-graph with all connecting relations and returns as OXL graph.
	 * 
	 * @param concepts
	 *            list of concepts
	 * @return OXL graph
	 */
	private Response responseIncludingRelations(Set<ONDEXConcept> concepts) {

		// get possible connecting relations
		Set<ONDEXRelation> relations = new HashSet<ONDEXRelation>();
		for (ONDEXConcept c : concepts) {
			// unwrap lucene concept
			if (c instanceof LuceneConcept)
				c = ((LuceneConcept) c).getParent();
			for (ONDEXRelation r : graph.getRelationsOfConcept(c)) {
				if (concepts.contains(r.getFromConcept())
						&& concepts.contains(r.getToConcept())) {
					relations.add(r);
				}
			}
		}

		// create response
		return OXLResponseBuilder.build(graph.getSID() + "", concepts,
				relations);
	}

	/**
	 * This method contains the actual implementation of the algorithm. it
	 * performs the BFS/Dijkstra search and returns a result set.
	 * 
	 * @return the result set.
	 */
	private PathNode search(ONDEXConcept startConcept, ONDEXConcept stopConcept) {

		PathNode node_curr, node_succ;

		ONDEXConcept c_curr, c_succ;

		Set<ONDEXRelation> relations;

		PathNode node_root = new PathNode(startConcept.getId());
		DijkstraQueue queue = new DijkstraQueue(node_root);

		while (queue.moreOpenElements()) {
			// get next
			node_curr = queue.dequeue();
			c_curr = graph.getConcept(node_curr.getCid());
			// check if goal reached
			if (c_curr.getId() == stopConcept.getId())
				return node_curr;
			if ((relations = graph.getRelationsOfConcept(c_curr)) != null) {
				for (ONDEXRelation r_curr : relations) {
					if (r_curr.getFromConcept().equals(r_curr.getToConcept()))
						continue; // loops of size one are evil ;)
					c_succ = getOppositeConcept(c_curr, r_curr);

					node_succ = new PathNode(c_succ.getId());
					node_succ.setParent(node_curr, r_curr.getId());
					node_succ.setG(node_curr.getG() + 1);

					queue.enqueueIfBetterOrNew(node_succ);
				}
			}
			queue.considerClosed(node_curr);
		}
		return null;
	}

	/**
	 * recursive method for backtracing inside the result set of the algorithm.
	 * 
	 * @param n
	 *            the current node.
	 */
	private void traceBack(PathNode n, Set<ONDEXConcept> concepts) {
		if (n.getParent() != null) {
			ONDEXConcept c = graph.getConcept(n.getParent().getCid());
			if (!concepts.contains(c)) {
				concepts.add(c);
				traceBack(n.getParent(), concepts);
			}
		}
	}
}
