package net.sourceforge.ondex.mapping.structalign;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sourceforge.ondex.InvalidPluginArgumentException;
import net.sourceforge.ondex.annotations.Authors;
import net.sourceforge.ondex.annotations.Custodians;
import net.sourceforge.ondex.annotations.Status;
import net.sourceforge.ondex.annotations.StatusType;
import net.sourceforge.ondex.args.ArgumentDefinition;
import net.sourceforge.ondex.args.BooleanArgumentDefinition;
import net.sourceforge.ondex.args.RangeArgumentDefinition;
import net.sourceforge.ondex.args.StringArgumentDefinition;
import net.sourceforge.ondex.args.StringMappingPairArgumentDefinition;
import net.sourceforge.ondex.config.LuceneRegistry;
import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.ConceptName;
import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.EvidenceType;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.core.RelationType;
import net.sourceforge.ondex.core.searchable.LuceneEnv;
import net.sourceforge.ondex.core.searchable.LuceneQueryBuilder;
import net.sourceforge.ondex.event.type.GeneralOutputEvent;
import net.sourceforge.ondex.mapping.ONDEXMapping;

import org.apache.lucene.search.Query;

/**
 * Implements the StructAlign mapping.
 * 
 * @author taubertj
 * @version 28.12.2012
 */
@Authors(authors = { "Jan Taubert" }, emails = { "jantaubert at users.sourceforge.net" })
@Custodians(custodians = { "Jan Taubert" }, emails = { "jantaubert at users.sourceforge.net" })
@Status(description = "Tested December 2012 (Jan Taubert)", status = StatusType.STABLE)
public class Mapping extends ONDEXMapping implements ArgumentNames {

	// defines depth of neighbourhood
	private int depth = 1;

	// use only exact synonyms
	private boolean exact = false;

	/**
	 * Constructor
	 */
	public Mapping() {
		super();
	}

	/**
	 * Returns the arguments required by this mapping.
	 * 
	 * @return ArgumentDefinition<?>[]
	 */
	public ArgumentDefinition<?>[] getArgumentDefinitions() {

		StringMappingPairArgumentDefinition pairCC = new StringMappingPairArgumentDefinition(
				EQUIVALENT_CC_ARG, EQUIVALENT_CC_ARG_DESC, false, null, true);

		StringArgumentDefinition attrEquals = new StringArgumentDefinition(
				ATTRIBUTE_EQUALS_ARG, ATTRIBUTE_EQUALS_ARG_DESC, false, null,
				true);

		BooleanArgumentDefinition exactSyns = new BooleanArgumentDefinition(
				EXACT_SYN_ARG, EXACT_SYN_ARG_DESC, false, false);

		RangeArgumentDefinition<Integer> depthArg = new RangeArgumentDefinition<Integer>(
				DEPTH_ARG, DEPTH_ARG_DESC, true, 2, 0, Integer.MAX_VALUE,
				Integer.class);

		StringMappingPairArgumentDefinition ccRestriction = new StringMappingPairArgumentDefinition(
				CONCEPTCLASS_RESTRICTION_ARG,
				CONCEPTCLASS_RESTRICTION_ARG_DESC, false, null, true);

		StringMappingPairArgumentDefinition dsRestriction = new StringMappingPairArgumentDefinition(
				DATASOURCE_RESTRICTION_ARG, DATASOURCE_RESTRICTION_ARG_DESC,
				false, null, true);

		return new ArgumentDefinition<?>[] { pairCC, attrEquals, exactSyns,
				depthArg, ccRestriction, dsRestriction };
	}

	/**
	 * Returns the name of this mapping.
	 * 
	 * @return String
	 */
	public String getName() {
		return new String("StructAlign based mapping");
	}

	/**
	 * Returns the version of this mapping.
	 * 
	 * @return String
	 */
	public String getVersion() {
		return new String("28.12.2012");
	}

	@Override
	public String getId() {
		return "structalign";
	}

	/**
	 * Requires a Lucene index.
	 * 
	 * @return true
	 */
	public boolean requiresIndexedGraph() {
		return true;
	}

	@Override
	public void start() throws InvalidPluginArgumentException {

		if (args.getOptions().containsKey(DEPTH_ARG)) {
			fireEventOccurred(new GeneralOutputEvent(
					"Change depth for neighborhood.. ", getCurrentMethodName()));
			this.depth = ((Integer) args.getUniqueValue(DEPTH_ARG));
		}
		fireEventOccurred(new GeneralOutputEvent(
				"Use StructAlign based mapping with depth for neighborhood "
						+ this.depth, getCurrentMethodName()));

		if (args.getOptions().containsKey(EXACT_SYN_ARG)) {
			fireEventOccurred(new GeneralOutputEvent(
					"Change exact synonym matching.. ", getCurrentMethodName()));
			this.exact = (Boolean) args.getUniqueValue(EXACT_SYN_ARG);
		}
		fireEventOccurred(new GeneralOutputEvent(
				"Use StructAlign based mapping with exact synonym matching set to "
						+ this.exact, getCurrentMethodName()));

		// get restrictions on ConceptClasses or DataSources on relations
		Map<DataSource, DataSource> dataSourceMapping = getAllowedDataSources(graph);
		Map<ConceptClass, ConceptClass> ccMapping = getAllowedCCs(graph);

		// get the relation type, evidence type and hit count for the mapping
		RelationType relType = graph.getMetaData().getRelationType(
				MetaData.relType);
		EvidenceType eviType = graph.getMetaData().getEvidenceType(
				MetaData.evidence + this.depth);
		AttributeName hitAttr = graph.getMetaData().getAttributeName(
				MetaData.hitAttr);

		// contains hits of similar cc, but different data source using search
		// with concept names
		Map<ONDEXConcept, Set<ONDEXConcept>> concept2hitConcepts = new HashMap<ONDEXConcept, Set<ONDEXConcept>>();

		// iterate over all concepts
		for (ONDEXConcept concept : graph.getConcepts()) {

			// get actual concept and data source
			DataSource conceptDataSource = concept.getElementOf();

			// create a new HashSet for hit concepts, even if it will stay empty
			Set<ONDEXConcept> hitConcepts = new HashSet<ONDEXConcept>();
			concept2hitConcepts.put(concept, hitConcepts);

			// add all concept names for this concept
			Set<String> cnames = new HashSet<String>();
			for (ConceptName cn : concept.getConceptNames()) {
				// use only exact synonyms
				if (!exact || cn.isPreferred()) {
					String name = LuceneEnv.stripText(cn.getName());
					cnames.add(name);
				}
			}

			// deal with CC mapping
			for (ConceptClass cc : getCCtoMapTo(graph, concept.getOfType())) {
				// iterate over all striped concept names
				for (String name : cnames) {
					Query query = LuceneQueryBuilder
							.searchConceptByConceptNameExact(name,
									concept.getElementOf(), cc);

					// iterator over search results
					LuceneEnv lenv = LuceneRegistry.sid2luceneEnv.get(graph
							.getSID());
					// iterate over hit concepts
					for (ONDEXConcept hitConcept : lenv.searchInConcepts(query)) {
						DataSource hitConceptDataSource = hitConcept
								.getElementOf();

						// CC equal, DataSource not
						if (!conceptDataSource.equals(hitConceptDataSource)) {
							if (this.evaluateMapping(graph, hitConcept, concept)) {
								// add hit concept to set for current concept
								hitConcepts.add(hitConcept);
							}
						}
					}
				}
			}
		}

		fireEventOccurred(new GeneralOutputEvent(
				"Finished looking for ConceptName hits.",
				getCurrentMethodName()));
		fireEventOccurred(new GeneralOutputEvent(
				"Starting building reachability lists for depth 1.",
				getCurrentMethodName()));

		// all pre-calculated connectivity lists
		Map<ONDEXConcept, Map<RelationType, Set<ONDEXConcept>>> connectivity = new HashMap<ONDEXConcept, Map<RelationType, Set<ONDEXConcept>>>();

		// reachability may be different from connectivity
		Map<ONDEXConcept, Map<RelationType, Set<ONDEXConcept>>> reachability = new HashMap<ONDEXConcept, Map<RelationType, Set<ONDEXConcept>>>();

		// iterate over all relations to fill reachability list of depth 1
		Set<ONDEXRelation> itRelations = graph.getRelations();

		NumberFormat decimalFormat = new DecimalFormat(".00");
		NumberFormat numberFormat = NumberFormat.getInstance();
		int processed = 0;
		long totals = itRelations.size();
		double increments = totals / 50;
		fireEventOccurred(new GeneralOutputEvent("StructAlign mapping on "
				+ totals + " Relations", getCurrentMethodName()));

		for (ONDEXRelation r : itRelations) {

			if (processed > 0 && processed % increments == 0) {
				fireEventOccurred(new GeneralOutputEvent(
						"Building reachability lists complete on "
								+ decimalFormat.format(processed / totals
										* 100d) + "% ("
								+ numberFormat.format(processed)
								+ " Relations)", getCurrentMethodName()));
				if (processed % 200000 == 0) {
					System.runFinalization();
				}
			}

			processed++;

			// get next relation and associated concepts
			ONDEXConcept fromConcept = r.getFromConcept();
			ONDEXConcept toConcept = r.getToConcept();

			// check for same DataSource and not self-loop
			if (!fromConcept.equals(toConcept)
					&& fromConcept.getElementOf().equals(
							toConcept.getElementOf())) {

				// check if entries for fromConcept already exists, otherwise
				// init
				if (!connectivity.containsKey(fromConcept)) {
					connectivity.put(fromConcept,
							new HashMap<RelationType, Set<ONDEXConcept>>());
					reachability.put(fromConcept,
							new HashMap<RelationType, Set<ONDEXConcept>>());
				}

				// check if entries for toConcept already exists, otherwise init
				if (!connectivity.containsKey(toConcept)) {
					connectivity.put(toConcept,
							new HashMap<RelationType, Set<ONDEXConcept>>());
					reachability.put(toConcept,
							new HashMap<RelationType, Set<ONDEXConcept>>());
				}

				// get current neighbours of fromConcept
				Map<RelationType, Set<ONDEXConcept>> neighborsFrom = connectivity
						.get(fromConcept);
				Map<RelationType, Set<ONDEXConcept>> reachabilityFrom = reachability
						.get(fromConcept);

				// get current neighbours of toConcept
				Map<RelationType, Set<ONDEXConcept>> neighborsTo = connectivity
						.get(toConcept);
				Map<RelationType, Set<ONDEXConcept>> reachabilityTo = reachability
						.get(toConcept);

				RelationType rt = r.getOfType();

				// check if relType already in neighborsFrom
				if (!neighborsFrom.containsKey(rt)) {
					neighborsFrom.put(rt, new HashSet<ONDEXConcept>());
					reachabilityFrom.put(rt, new HashSet<ONDEXConcept>());
				}

				// check if relType already in neighborsTo
				if (!neighborsTo.containsKey(rt)) {
					neighborsTo.put(rt, new HashSet<ONDEXConcept>());
					reachabilityTo.put(rt, new HashSet<ONDEXConcept>());
				}

				// add toConcept for this RelationType to neighborsFrom
				neighborsFrom.get(rt).add(toConcept);
				reachabilityFrom.get(rt).add(toConcept);

				// add fromConcept for this RelationType to neighborsTo
				neighborsTo.get(rt).add(fromConcept);
				reachabilityTo.get(rt).add(fromConcept);
			}
		}

		fireEventOccurred(new GeneralOutputEvent(
				"Finished building reachability lists for depth 1.",
				getCurrentMethodName()));

		// perform a breadth first search
		for (int i = 1; i < this.depth; i++) {

			// iterate over all fromConcepts
			for (ONDEXConcept fromConcept : reachability.keySet()) {

				// find current neighbours for a certain relation type
				Map<RelationType, Set<ONDEXConcept>> neighbors = reachability
						.get(fromConcept);

				// iterate over all RelationTypes present
				for (RelationType relationType : neighbors.keySet()) {

					// iterate over all connected concepts
					for (ONDEXConcept c : neighbors.get(relationType)) {

						// get neighbours of connected ones
						Map<RelationType, Set<ONDEXConcept>> connected = connectivity
								.get(c);

						if (connected != null) {

							// join neighbours according to RelationType
							for (RelationType rt : connected.keySet()) {
								Set<ONDEXConcept> newneighbors = connected
										.get(rt);

								// join sets together
								if (!neighbors.containsKey(rt)) {
									neighbors.put(rt,
											new HashSet<ONDEXConcept>(
													newneighbors));
								} else {
									neighbors.get(rt).addAll(newneighbors);
								}
							}
						}
					}
				}
			}

			fireEventOccurred(new GeneralOutputEvent(
					"Finished building reachability lists for depth " + (i + 1)
							+ ".", getCurrentMethodName()));
		}

		fireEventOccurred(new GeneralOutputEvent(
				"Finished building reachability lists.", getCurrentMethodName()));

		// will contain the concept combinations to be used for relations
		Map<ONDEXConcept, Map<ONDEXConcept, Integer>> relations = new HashMap<ONDEXConcept, Map<ONDEXConcept, Integer>>();

		// iterate over all concepts
		for (ONDEXConcept fromConcept : concept2hitConcepts.keySet()) {

			// get hit set for current concept
			Set<ONDEXConcept> hitConcepts = concept2hitConcepts
					.get(fromConcept);

			// for non-empty hit concepts proceed
			if (!hitConcepts.isEmpty()) {

				// get neighbours for current concept
				Map<RelationType, Set<ONDEXConcept>> rt2reachableConcepts = reachability
						.get(fromConcept);

				if (rt2reachableConcepts != null) {

					// look at hits for current concept
					for (ONDEXConcept toConcept : hitConcepts) {

						// get neighbours for each hit
						Map<RelationType, Set<ONDEXConcept>> rt2reachableHitConcepts = reachability
								.get(toConcept);

						if (rt2reachableHitConcepts != null) {

							// get intersection of relation types for current
							// pair of concepts
							Set<RelationType> intersection = new HashSet<RelationType>();
							intersection.addAll(rt2reachableHitConcepts
									.keySet());
							intersection.retainAll(rt2reachableConcepts
									.keySet());

							// check if concepts can be reached by the same
							// relType
							if (!intersection.isEmpty()) {

								// iterate over intersections of relTypes
								for (RelationType rt : intersection) {

									// get all reachable concepts for current
									// relType
									Set<ONDEXConcept> reachableConcepts = rt2reachableConcepts
											.get(rt);
									Set<ONDEXConcept> reachableHitConcepts = rt2reachableHitConcepts
											.get(rt);

									// compare neighbourhoods for concept name
									// matches
									for (ONDEXConcept reachableConcept : reachableConcepts) {

										// build intersection between concept
										// name matches
										Set<ONDEXConcept> hitIntersection = new HashSet<ONDEXConcept>();
										hitIntersection
												.addAll(concept2hitConcepts
														.get(reachableConcept));
										hitIntersection
												.retainAll(reachableHitConcepts);

										// if intersection not empty create
										// relation
										if (!hitIntersection.isEmpty()) {

											// check DataSource conditions
											DataSource fromDataSource = fromConcept
													.getElementOf();
											DataSource toDataSource = toConcept
													.getElementOf();
											if (dataSourceMapping.size() > 0
													&& !toDataSource
															.equals(dataSourceMapping
																	.get(fromDataSource))) {
												continue;
											}

											// check ConceptClass conditions
											ConceptClass fromCC = fromConcept
													.getOfType();
											ConceptClass toCC = toConcept
													.getOfType();
											if (ccMapping.size() > 0
													&& !toCC.equals(ccMapping
															.get(fromCC))) {
												continue;
											}

											// between different DataSource
											if (!fromConcept.getElementOf()
													.equals(toConcept
															.getElementOf())) {

												// check if fromConcept already
												// contained
												if (relations.get(fromConcept) == null) {
													relations
															.put(fromConcept,
																	new HashMap<ONDEXConcept, Integer>());
												}

												// get all relations for
												// fromConcept
												Map<ONDEXConcept, Integer> relationHits = relations
														.get(fromConcept);

												// get old hits for toConcept
												// and increment
												int hits = relationHits
														.get(toConcept) + 1;

												// store new hit value
												relationHits.put(toConcept,
														hits);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		fireEventOccurred(new GeneralOutputEvent(
				"Finished graph neighborhood alignment.",
				getCurrentMethodName()));

		int unidirectional = 0;

		// iterator over all found relations
		for (ONDEXConcept fromConcept : relations.keySet()) {

			Map<ONDEXConcept, Integer> relationHits = relations
					.get(fromConcept);

			// get toConcept
			for (ONDEXConcept toConcept : relationHits.keySet()) {
				int score = relationHits.get(toConcept);

				// check for bidirectional hits
				if (relations.containsKey(toConcept)
						&& relations.get(toConcept).containsKey(fromConcept)) {

					// different data sources
					if (!fromConcept.getElementOf().equals(
							toConcept.getElementOf())) {

						// get relation if existing
						ONDEXRelation relation = graph.getRelation(fromConcept,
								toConcept, relType);

						if (relation == null) {
							// create not existing relation
							relation = graph.getFactory().createRelation(
									fromConcept, toConcept, relType, eviType);
						}

						Set<EvidenceType> etit = relation.getEvidence();
						if (!etit.contains(eviType)) {
							// existing relations, add evi type
							relation.addEvidenceType(eviType);
						}

						// set confidence value
						relation.createAttribute(hitAttr, score, false);
					}
				} else {
					unidirectional++;
				}
			}
		}

		fireEventOccurred(new GeneralOutputEvent(
				"Uni directional hits excluded = " + unidirectional,
				getCurrentMethodName()));
	}

	public String[] requiresValidators() {
		return new String[0];
	}
}