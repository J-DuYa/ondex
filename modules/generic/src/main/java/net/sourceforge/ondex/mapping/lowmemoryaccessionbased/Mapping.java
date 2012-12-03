package net.sourceforge.ondex.mapping.lowmemoryaccessionbased;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.ondex.InvalidPluginArgumentException;
import net.sourceforge.ondex.annotations.Authors;
import net.sourceforge.ondex.annotations.Custodians;
import net.sourceforge.ondex.annotations.Status;
import net.sourceforge.ondex.annotations.StatusType;
import net.sourceforge.ondex.args.ArgumentDefinition;
import net.sourceforge.ondex.args.BooleanArgumentDefinition;
import net.sourceforge.ondex.args.StringArgumentDefinition;
import net.sourceforge.ondex.args.StringMappingPairArgumentDefinition;
import net.sourceforge.ondex.config.LuceneRegistry;
import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.ConceptAccession;
import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.EvidenceType;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.core.RelationType;
import net.sourceforge.ondex.core.searchable.LuceneConcept;
import net.sourceforge.ondex.core.searchable.LuceneEnv;
import net.sourceforge.ondex.core.searchable.LuceneQueryBuilder;
import net.sourceforge.ondex.core.util.BitSetFunctions;
import net.sourceforge.ondex.event.type.AttributeNameMissingEvent;
import net.sourceforge.ondex.event.type.EvidenceTypeMissingEvent;
import net.sourceforge.ondex.event.type.GeneralOutputEvent;
import net.sourceforge.ondex.event.type.RelationTypeMissingEvent;
import net.sourceforge.ondex.exception.type.WrongParameterException;
import net.sourceforge.ondex.mapping.ONDEXMapping;

import org.apache.lucene.search.Query;

/**
 * Implements a ConceptAccession based mapping.
 * 
 * @author taubertj, hindlem
 * @version 21.01.2008
 */
@Authors(authors = { "Jan Taubert", "Matthew Hindle" }, emails = {
		"jantaubert at users.sourceforge.net",
		"matthew_hindle at users.sourceforge.net" })
@Status(description = "Tested March 2010 (hindlem)", status = StatusType.STABLE)
@Custodians(custodians = { "Jochen Weile" }, emails = { "jweile at users.sourceforge.net" })
public class Mapping extends ONDEXMapping implements ArgumentNames {

	// toggles debug messages to system.out
	private static boolean DEBUG = false;

	// default relation type set
	private static String DEFAULT_RELATION = "equ";

	// ignore ambiguous flag of concept accessions
	private boolean ignoreAmbiguity = false;

	// stores DataSource mappings
	private Map<String, Set<DataSource>> dataSource2cvEquals = null;

	// for self DataSource mappings
	private Map<String, Set<DataSource>> dataSource2cvSelf = new HashMap<String, Set<DataSource>>();

	private boolean mapWithinCV = false;

	private final NumberFormat formatter;
	private final NumberFormat format;

	private RelationType rt;
	private EvidenceType eviType;
	private AttributeName hitAttr;

	private HashSet<String> relations;
	private Map<ConceptClass, ConceptClass> ccMapping;

	/**
	 * Simply calls super constructor.
	 */
	public Mapping() {
		formatter = new DecimalFormat(".00");
		format = NumberFormat.getInstance();
	}

	/**
	 * Specifies neccessary arguments for this mapping.
	 * 
	 * @return ArgumentDefinition<?>[]
	 */
	public ArgumentDefinition<?>[] getArgumentDefinitions() {
		StringMappingPairArgumentDefinition pairCC = new StringMappingPairArgumentDefinition(
				EQUIVALENT_CC_ARG, EQUIVALENT_CC_ARG_DESC, false, null, true);

		StringMappingPairArgumentDefinition pairCV = new StringMappingPairArgumentDefinition(
				EQUIVALENT_CV_ARG, EQUIVALENT_CV_ARG_DESC, false, null, true);

		StringArgumentDefinition gdsEquals = new StringArgumentDefinition(
				ATTRIBUTE_EQUALS_ARG, ATTRIBUTE_EQUALS_ARG_DESC, false, null,
				true);

		BooleanArgumentDefinition ignoreAmbiguousEquals = new BooleanArgumentDefinition(
				IGNORE_AMBIGUOUS_ARG, IGNORE_AMBIGUOUS_ARG_DESC, false, false);

		BooleanArgumentDefinition mapWithinCV = new BooleanArgumentDefinition(
				WITHIN_CV_ARG, WITHIN_CV_ARG_DESC, false, false);

		StringArgumentDefinition relationType = new StringArgumentDefinition(
				RELATION_TYPE_ARG, RELATION_TYPE_ARG_DESC, false, "equ", false);

		StringArgumentDefinition ccRestriction = new StringArgumentDefinition(
				CONCEPTCLASS_RESTRICTION_ARG,
				CONCEPTCLASS_RESTRICTION_ARG_DESC, false, null, true);

		StringArgumentDefinition cvAccessionRestriction = new StringArgumentDefinition(
				CV_ACCESSION_RESTRICTION_ARG,
				CV_ACCESSION_RESTRICTION_ARG_DESC, false, null, true);

		return new ArgumentDefinition<?>[] { pairCC, pairCV, gdsEquals,
				ignoreAmbiguousEquals, relationType, mapWithinCV,
				ccRestriction, cvAccessionRestriction };
	}

	/**
	 * Returns name of this mapping.
	 * 
	 * @return String
	 */
	public String getName() {
		return new String("Concept accession-based mapping (Memory-efficient)");
	}

	/**
	 * Returns version of this mapping.
	 * 
	 * @return String
	 */
	public String getVersion() {
		return new String("21.01.2008");
	}

	@Override
	public String getId() {
		return "lowmemoryaccessionbased";
	}

	/**
	 * Requires an indexed ondex graph.
	 * 
	 * @return true
	 */
	public boolean requiresIndexedGraph() {
		return true;
	}

	public void start() throws InvalidPluginArgumentException {

		if (args.getUniqueValue(WITHIN_CV_ARG) != null) {
			mapWithinCV = (Boolean) args.getUniqueValue(WITHIN_CV_ARG);
		}

		relations = new HashSet<String>();

		initArgs();

		Set<DataSource> exclusiveCvs = getExclusiveDataSources();

		ccMapping = getAllowedCCs(graph);
		// for (ConceptClass key : ccMapping.keySet()) {
		// System.out.println(key.getId() + " => "
		// + ccMapping.get(key).getId());
		// }

		// define concepts to map from
		Set<ONDEXConcept> itConcepts = getBaseConcepts();
		int total = itConcepts.size();

		fireEventOccurred(new GeneralOutputEvent("Accession based mapping on "
				+ total + " concepts", "[accessionbased.Mapping - start]"));

		long timeStart = System.currentTimeMillis();

		int current = 0;
		for (ONDEXConcept concept : itConcepts) {

			// get actual concept, cv and corresponding concept class
			current++;

			if ((current % 50000d) == 0) {
				System.out.println("Mapping complete on "
						+ formatter.format(((double) current)
								/ ((double) total) * 100d) + "% ("
						+ format.format(current) + " Concepts)");
				if (current % 200000 == 0) {
					System.runFinalization();
				}
			}

			// iterate over all accession of this concept
			for (ConceptAccession conceptAcc : concept.getConceptAccessions()) {

				if (exclusiveCvs != null
						&& !exclusiveCvs.contains(conceptAcc.getElementOf())) {
					continue;
				}

				// accession must not be ambiguous or ignore ambiguous
				if (ignoreAmbiguity || !conceptAcc.isAmbiguous()) {

					Set<DataSource> dataSourceToMapTo = getCVtoMapTo(graph,
							conceptAcc.getElementOf());
					for (ConceptClass cc : getCCtoMapTo(graph,
							concept.getOfType())) {
						// possible DataSource for concept accessions
						for (DataSource dataSource : dataSourceToMapTo) {
							Query query = null;

							if (mapWithinCV) {
								query = LuceneQueryBuilder
										.searchConceptByConceptAccessionExact(
												dataSource, conceptAcc
														.getAccession()
														.toLowerCase(), null,
												cc, ignoreAmbiguity);
							} else {
								query = LuceneQueryBuilder
										.searchConceptByConceptAccessionExact(
												dataSource, conceptAcc
														.getAccession()
														.toLowerCase(), concept
														.getElementOf(), cc,
												ignoreAmbiguity);
							}

							LuceneEnv lenv = LuceneRegistry.sid2luceneEnv
									.get(graph.getSID());
							Set<ONDEXConcept> itResults = lenv
									.searchInConcepts(query);

							// look for the whole concept acc
							createRelationsOnResults(itResults, concept);
						}
					}
				}
			}
		}

		System.out.println("All took "
				+ (System.currentTimeMillis() - timeStart) + " created "
				+ relations.size() + " relations");

		itConcepts = null;
		relations = null;
	}

	private void initArgs() throws InvalidPluginArgumentException {
		// get mapping options
		String relationType = DEFAULT_RELATION;
		if (args.getOptions().containsKey(RELATION_TYPE_ARG))
			relationType = args.getUniqueValue(RELATION_TYPE_ARG).toString();

		if (args.getOptions().containsKey(IGNORE_AMBIGUOUS_ARG))
			this.ignoreAmbiguity = (Boolean) args
					.getUniqueValue(IGNORE_AMBIGUOUS_ARG);

		fireEventOccurred(new GeneralOutputEvent(
				"ACC mapping ignore ambiguity is " + this.ignoreAmbiguity,
				"[Mapping - setONDEXGraph]"));

		// get the relationtypeset, evidencetype and hit count for the mapping

		rt = graph.getMetaData().getRelationType(relationType);
		if (rt == null) {
			fireEventOccurred(new RelationTypeMissingEvent(MetaData.relType,
					"[Mapping - setONDEXGraph]"));
			rt = graph.getMetaData().getFactory()
					.createRelationType(relationType);
		}

		eviType = graph.getMetaData().getEvidenceType(MetaData.evidence);
		if (eviType == null) {
			fireEventOccurred(new EvidenceTypeMissingEvent(MetaData.evidence,
					"[Mapping - setONDEXGraph]"));
			eviType = graph.getMetaData().getFactory()
					.createEvidenceType(MetaData.evidence);
		}

		hitAttr = graph.getMetaData().getAttributeName(MetaData.accHit);
		if (hitAttr == null) {
			fireEventOccurred(new AttributeNameMissingEvent(MetaData.accHit,
					"[Mapping - setONDEXGraph]"));
			hitAttr = graph.getMetaData().getFactory()
					.createAttributeName(MetaData.accHit, Integer.class);
		}

	}

	private Set<ONDEXConcept> getBaseConcepts()
			throws InvalidPluginArgumentException {
		Set<ONDEXConcept> itConcepts = null;
		Set<ConceptClass> validCCs = getExclusiveConceptClasses();
		if (validCCs.size() > 0) {
			for (ConceptClass cc : validCCs) {
				System.out.println("Filtering on cc " + cc.getId());
				if (itConcepts == null) {
					itConcepts = BitSetFunctions.copy(graph
							.getConceptsOfConceptClass(cc));
				} else {
					itConcepts.addAll(graph.getConceptsOfConceptClass(cc));
				}
			}
		} else {
			itConcepts = BitSetFunctions.copy(graph.getConcepts()); // map to
																	// everything
		}
		return itConcepts;
	}

	public void createRelationsOnResults(Set<ONDEXConcept> itResults,
			ONDEXConcept concept) throws InvalidPluginArgumentException {

		DataSource conceptDataSource = concept.getElementOf();
		ConceptClass conceptOfType = concept.getOfType();

		// search for this concept accession
		for (ONDEXConcept hitConcept : itResults) {

			// get hit concept and relevant attributes
			// from results
			if (hitConcept instanceof LuceneConcept) {
				hitConcept = ((LuceneConcept) hitConcept).getParent();
			}

			// no self loops
			int fromInt = concept.getId();
			int toInt = hitConcept.getId();
			if (fromInt == toInt) {
				continue;
			}

			// sanity check if mapping already found
			if (graph.getRelation(concept, hitConcept, rt) != null) {
				continue; // we have already identified this direction
			} else if (ccMapping.size() == 0
					&& graph.getRelation(hitConcept, concept, rt) != null) {
				continue; // directionality does not play a role
			}

			DataSource hitConceptDataSource = hitConcept.getElementOf();
			ConceptClass hitConceptOfType = hitConcept.getOfType();

			// check ConceptClass restriction conditions
			if (ccMapping.size() > 0
					&& !hitConceptOfType.equals(ccMapping.get(conceptOfType))) {
				continue;
			}

			// only map between different concept data source or
			// equal data sources
			if (this.mapWithinCV
					|| !conceptDataSource.equals(hitConceptDataSource)) {

				// concept accession has to be from same data source
				for (ConceptAccession hitConceptAcc : hitConcept
						.getConceptAccessions()) {

					if (evaluateMapping(graph, hitConcept, concept, mapWithinCV)) {
						// DataSource must be the same and
						// accession must not be
						// ambiguous or ignore ambiguous
						if (ignoreAmbiguity || !hitConceptAcc.isAmbiguous()) {

							ONDEXConcept fromConcept = concept;
							ONDEXConcept toConcept = hitConcept;
							if (ccMapping.size() == 0) {
								// create only one predictable direction
								if (hitConceptOfType.compareTo(conceptOfType) < 0) {
									fromConcept = hitConcept;
									toConcept = concept;
								}
							}

							if (createRelation(fromConcept, toConcept, rt,
									eviType)) {
								break;
								// break from accessions we have enough to map
								// these
							}
						}
					}
				}
			} else {
				if (DEBUG) {
					fireEventOccurred(new GeneralOutputEvent(
							"Not matching CVs between hitconcepts: "
									+ concept.getPID() + " with "
									+ concept.getElementOf().getId() + " and "
									+ hitConcept.getPID() + " with "
									+ hitConcept.getElementOf().getId(),
							"[Mapping - setONDEXGraph]"));
				}
			}
		}
	}

	/**
	 * gets the set of conceptclasses as from in restrictions, if empty return
	 * than map to anything
	 * 
	 * @return
	 */
	private Set<ConceptClass> getExclusiveConceptClasses()
			throws InvalidPluginArgumentException {
		Set<ConceptClass> ccs = new HashSet<ConceptClass>();

		if (args.getOptions().get(ArgumentNames.CONCEPTCLASS_RESTRICTION_ARG) != null) {

			List<String> valueList = args.getObjectValueList(
					ArgumentNames.CONCEPTCLASS_RESTRICTION_ARG, String.class);

			for (String value : valueList) {
				ConceptClass cc0;
				// optional could contain a pair of concept classes
				for (String s : value.split(",")) {
					try {
						cc0 = graph.getMetaData().getConceptClass(s);
						if (cc0 != null)
							ccs.add(cc0);
						else
							System.out
									.println("ConceptClass not found in MetaData "
											+ value.toString());
					} catch (WrongParameterException e) {
						System.out.println(e.getMessage());
						continue;
					}
				}
			}
		}
		return ccs;
	}

	/**
	 * gets the set of cv as from in restrictions, if empty return than map to
	 * anything
	 * 
	 * @return
	 */
	private Set<DataSource> getExclusiveDataSources()
			throws InvalidPluginArgumentException {
		Set<DataSource> cvs = new HashSet<DataSource>();

		if (args.getOptions().get(ArgumentNames.CV_ACCESSION_RESTRICTION_ARG) != null) {

			List<String> valueList = args.getObjectValueList(
					ArgumentNames.CV_ACCESSION_RESTRICTION_ARG, String.class);

			for (String value : valueList) {
				DataSource dataSource;
				try {
					dataSource = graph.getMetaData().getDataSource(value);
					cvs.add(dataSource);
				} catch (WrongParameterException e) {
					System.out.println("DataSource " + value + " is not found");
					continue;
				}
			}
			return cvs;
		}
		return null;
	}

	/**
	 * Create a relationship
	 * 
	 * @param fromConcept
	 *            relation from
	 * @param toConcept
	 *            relation to
	 * @param rtSet
	 *            relation type
	 * @param eviType
	 *            evidence
	 */
	private boolean createRelation(ONDEXConcept fromConcept,
			ONDEXConcept toConcept, RelationType rtSet, EvidenceType eviType) {

		// unwrap
		if (fromConcept instanceof LuceneConcept)
			fromConcept = ((LuceneConcept) fromConcept).getParent();
		// unwrap
		if (toConcept instanceof LuceneConcept)
			toConcept = ((LuceneConcept) toConcept).getParent();

		String key = fromConcept.getId() + "|" + toConcept.getId();

		if (!relations.contains(key)) {
			ONDEXRelation relation = graph.getFactory().createRelation(
					fromConcept, toConcept, rtSet, eviType);
			relations.add(key);
			if (relation != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the set of DataSource to which are equivalent to the current
	 * DataSource.
	 * <p/>
	 * By default the "from" DataSource is included in the result.
	 * 
	 * @param graph
	 *            AbstractONDEXGraph
	 * @param from
	 *            DataSource
	 * @return Set<DataSource>
	 */
	private Set<DataSource> getCVtoMapTo(ONDEXGraph graph, DataSource from)
			throws InvalidPluginArgumentException {

		// parse mapping arguments for CVs
		if (args.getOptions().containsKey(EQUIVALENT_CV_ARG)
				&& dataSource2cvEquals == null) {
			dataSource2cvEquals = new HashMap<String, Set<DataSource>>(5);
			Iterator<String> cvIt = args.getObjectValueList(EQUIVALENT_CV_ARG,
					String.class).iterator();
			while (cvIt.hasNext()) {
				String[] mapping = cvIt.next().split(",");
				if (mapping.length == 2) {
					if (!dataSource2cvEquals.containsKey(mapping[0]))
						dataSource2cvEquals.put(mapping[0],
								new HashSet<DataSource>());
					if (!dataSource2cvEquals.containsKey(mapping[1]))
						dataSource2cvEquals.put(mapping[1],
								new HashSet<DataSource>());
					DataSource dataSource0 = graph.getMetaData().getDataSource(
							mapping[0]);
					DataSource dataSource1 = graph.getMetaData().getDataSource(
							mapping[1]);
					dataSource2cvEquals.get(mapping[0]).add(dataSource0);
					dataSource2cvEquals.get(mapping[0]).add(dataSource1);
					dataSource2cvEquals.get(mapping[1]).add(dataSource0);
					dataSource2cvEquals.get(mapping[1]).add(dataSource1);
				}
			}
		}

		// return equivalent CVs or only selfmatch
		if (dataSource2cvEquals != null
				&& dataSource2cvEquals.containsKey(from.getId())) {
			return dataSource2cvEquals.get(from.getId());
		} else {
			if (!dataSource2cvSelf.containsKey(from.getId())) {
				dataSource2cvSelf.put(from.getId(), new HashSet<DataSource>());
				dataSource2cvSelf.get(from.getId()).add(from);
			}
			return dataSource2cvSelf.get(from.getId());
		}
	}

	public String[] requiresValidators() {
		return new String[0];
	}

}
