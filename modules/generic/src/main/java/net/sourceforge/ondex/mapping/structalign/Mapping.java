package net.sourceforge.ondex.mapping.structalign;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sourceforge.ondex.InvalidPluginArgumentException;
import net.sourceforge.ondex.annotations.Authors;
import net.sourceforge.ondex.annotations.Custodians;
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
 * @version 21.08.2008
 */
@Authors(authors = {"Jan Taubert"}, emails = {"jantaubert at users.sourceforge.net"})
@Custodians(custodians = {"Jochen Weile"}, emails = {"jweile at users.sourceforge.net"})
public class Mapping extends ONDEXMapping implements ArgumentNames {

    // defines depth of neighborhood
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
                EQUIVALENT_CC_ARG,
                EQUIVALENT_CC_ARG_DESC,
                false, null, true);

        StringArgumentDefinition gdsEquals = new StringArgumentDefinition(
                ATTRIBUTE_EQUALS_ARG,
                ATTRIBUTE_EQUALS_ARG_DESC,
                false, null, true);

        BooleanArgumentDefinition exactSyns = new BooleanArgumentDefinition(
                EXACT_SYN_ARG, EXACT_SYN_ARG_DESC, false, false);

        RangeArgumentDefinition<Integer> depthArg = new RangeArgumentDefinition<Integer>(
                DEPTH_ARG, DEPTH_ARG_DESC, true, 2, 0, Integer.MAX_VALUE, Integer.class);

        StringMappingPairArgumentDefinition ccRestriction = new StringMappingPairArgumentDefinition(
                CONCEPTCLASS_RESTRICTION_ARG,
                CONCEPTCLASS_RESTRICTION_ARG_DESC, false, null, true);

        StringMappingPairArgumentDefinition cvRestriction = new StringMappingPairArgumentDefinition(
                DATASOURCE_RESTRICTION_ARG, DATASOURCE_RESTRICTION_ARG_DESC, false, null, true);

        return new ArgumentDefinition<?>[]{
                pairCC,
                gdsEquals,
                exactSyns,
                depthArg,
                ccRestriction,
                cvRestriction
        };
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
        return new String("21.01.2008");
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
            fireEventOccurred(new GeneralOutputEvent("Change depth for neighborhood.. ", "[Mapping - setArguments]"));
            this.depth = ((Integer) args.getUniqueValue(DEPTH_ARG));
        }
        fireEventOccurred(new GeneralOutputEvent(
                "Use StructAlign based mapping with depth for neighborhood " + this.depth, "[Mapping - setArguments]"));

        if (args.getOptions().containsKey(EXACT_SYN_ARG)) {
            fireEventOccurred(new GeneralOutputEvent("Change exact synonym matching.. ", "[Mapping - setArguments]"));
            this.exact = (Boolean) args.getUniqueValue(EXACT_SYN_ARG);
        }
        fireEventOccurred(new GeneralOutputEvent(
                "Use StructAlign based mapping with exact synonym matching set to " + this.exact, "[Mapping - setArguments]"));

        // get restrictions on ConceptClasses or CVs on relations
        Map<DataSource, DataSource> dataSourceMapping = getAllowedDataSources(graph);
        Map<ConceptClass, ConceptClass> ccMapping = getAllowedCCs(graph);

        // get the relationtypeset, evidencetype and hit count for the mapping
        RelationType rtSet = graph.getMetaData().getRelationType(MetaData.relType);
        EvidenceType eviType = graph.getMetaData().getEvidenceType(MetaData.evidence + this.depth);
        AttributeName hitAttr = graph.getMetaData().getAttributeName(MetaData.hitAttr);

        // contains hits of similar cc, but different DataSource using search with
        // concept names
        Map<Integer, Set<Integer>> conceptID2hitConceptIDs =
                new HashMap<Integer, Set<Integer>>();

        // iterate over all concepts
        for (ONDEXConcept concept : graph.getConcepts()) {

            // get actual concept and cv
            DataSource conceptDataSource = concept.getElementOf();

            // create a new HashSet for hit concept IDs, even if it will stay empty
            Set<Integer> hitConceptIDs = new HashSet<Integer>();
            conceptID2hitConceptIDs.put(concept.getId(), hitConceptIDs);

            // add all concept names for this concept
            HashSet<String> cnames = new HashSet<String>();
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
                    Query query = LuceneQueryBuilder.searchConceptByConceptNameExact(
                            name,
                            concept.getElementOf(),
                            cc);

                    // iterator over search results
                    LuceneEnv lenv = LuceneRegistry.sid2luceneEnv.get(graph.getSID());
                    // iterate over hit concepts
                    for (ONDEXConcept hitConcept : lenv.searchInConcepts(query)) {
                        DataSource hitConceptDataSource = hitConcept.getElementOf();

                        // CC equal, DataSource not
                        if (!conceptDataSource.equals(hitConceptDataSource)) {
                            if (this.evaluateMapping(graph, hitConcept, concept, false)) {
                                // add hit concept ID to set for current concept
                                hitConceptIDs.add(hitConcept.getId());
                            }
                        }
                    }
                }
            }
        }

        fireEventOccurred(new GeneralOutputEvent("Finished looking for ConceptName hits.", "[Mapping - setONDEXGraph]"));
        fireEventOccurred(new GeneralOutputEvent("Starting building reachability lists for depth 1.", "[Mapping - setONDEXGraph]"));

        // all precalculated connectivity lists
        Map<Integer, Map<RelationType, Set<Integer>>> connectivity =
                new HashMap<Integer, Map<RelationType, Set<Integer>>>();

        // reachability may be different from connectivity
        Map<Integer, Map<RelationType, Set<Integer>>> reachability =
                new HashMap<Integer, Map<RelationType, Set<Integer>>>();

        // iterate over all relations to fill reachability list of depth 1
        Set<ONDEXRelation> itRelations = graph.getRelations();

        int processed = 0;
        long totals = itRelations.size();
        for (ONDEXRelation r : itRelations) {

            processed++;

            if (processed % 1000 == 0) {
                System.out.println(processed + " out of " + totals
                        + " Concepts processed.");
            }

            // get next relation and associated concepts
            ONDEXConcept fromConcept = r.getFromConcept();
            ONDEXConcept toConcept = r.getToConcept();
            Integer fromID = fromConcept.getId();
            Integer toID = toConcept.getId();

            // check for same DataSource and not selfloop
            if (!fromID.equals(toID) && fromConcept.getElementOf().equals(toConcept.getElementOf())) {

                // check if entries for fromID already exists, otherwise init
                if (!connectivity.containsKey(fromID.intValue())) {
                    connectivity.put(fromID,
                            new HashMap<RelationType, Set<Integer>>());
                    reachability.put(fromID,
                            new HashMap<RelationType, Set<Integer>>());
                }

                // check if entries for toID already exists, otherwise init
                if (!connectivity.containsKey(toID.intValue())) {
                    connectivity.put(toID,
                            new HashMap<RelationType, Set<Integer>>());
                    reachability.put(toID,
                            new HashMap<RelationType, Set<Integer>>());
                }

                // get current neighbors of fromID
                Map<RelationType, Set<Integer>> neighborsFrom =
                        connectivity.get(fromID.intValue());
                Map<RelationType, Set<Integer>> reachabilityFrom =
                        reachability.get(fromID.intValue());

                // get current neighbors of toID
                Map<RelationType, Set<Integer>> neighborsTo =
                        connectivity.get(toID.intValue());
                Map<RelationType, Set<Integer>> reachabilityTo =
                        reachability.get(toID.intValue());

                RelationType rt = r.getOfType();
//					
                // check if rt already in neighborsFrom
                if (!neighborsFrom.containsKey(rt)) {
                    neighborsFrom.put(rt, new HashSet<Integer>());
                    reachabilityFrom.put(rt, new HashSet<Integer>());
                }

                // check if rt already in neighborsTo
                if (!neighborsTo.containsKey(rt)) {
                    neighborsTo.put(rt, new HashSet<Integer>());
                    reachabilityTo.put(rt, new HashSet<Integer>());
                }

                // add toID for this RelationType to neighborsFrom
                neighborsFrom.get(rt).add(toID.intValue());
                reachabilityFrom.get(rt).add(toID.intValue());

                // add fromID for this RelationType to neighborsTo
                neighborsTo.get(rt).add(fromID.intValue());
                reachabilityTo.get(rt).add(fromID.intValue());
            }
        }

        fireEventOccurred(new GeneralOutputEvent("Finished building reachability lists for depth 1.", "[Mapping - setONDEXGraph]"));

        // perform a breadth first search
        for (int i = 1; i < this.depth; i++) {

            // iterate over all fromIDs
            Iterator<Integer> itFromID = reachability.keySet().iterator();
            while (itFromID.hasNext()) {

                // find current neighbors for a certain relation type
                int fromID = itFromID.next();
                Map<RelationType, Set<Integer>> neighbors =
                        reachability.get(fromID);

                // iterate over all RelationTypes present
                for (RelationType relationType : neighbors.keySet()) {

                    // iterate over all connected IDs
                    for (Object o : neighbors.get(relationType)) {

                        // get neighbors of connected ones
                        Map<RelationType, Set<Integer>> connected =
                                connectivity.get(o);

                        if (connected != null) {

                            // join neighbors according to RelationType
                            for (RelationType rt : connected.keySet()) {
                                Set<Integer> newneighbors = connected.get(rt);

                                // join sets together
                                if (!neighbors.containsKey(rt)) {
                                    neighbors.put(rt, new HashSet<Integer>(newneighbors));
                                }
                                else {
                                    neighbors.get(rt).addAll(newneighbors);
                                }
                            }
                        }
                    }
                }
            }

            fireEventOccurred(new GeneralOutputEvent("Finished building reachability lists for depth " + (i + 1) + ".", "[Mapping - setONDEXGraph]"));
        }

        fireEventOccurred(new GeneralOutputEvent("Finished building reachability lists.", "[Mapping - setONDEXGraph]"));

        // will contain the ID combinations to be used for relations
        Map<Integer, Map<Integer,Integer>> relations = new HashMap<Integer, Map<Integer,Integer>>();

        // iterate over all concept IDs
        for (Integer conceptID : conceptID2hitConceptIDs.keySet()) {

            // get hit set for current concept ID
            Set<Integer> hitConceptIDs = conceptID2hitConceptIDs.get(conceptID);

            // for non-empty hit concepts proceed
            if (!hitConceptIDs.isEmpty()) {

                // get neighbours for current concept ID
                Map<RelationType, Set<Integer>>
                        rt2reachableConceptIDs = reachability.get(conceptID);

                if (rt2reachableConceptIDs != null) {

                    // look at hits for current concept ID
                    for (Integer hitConceptID : hitConceptIDs) {

                        // get neighbours for each hit
                        Map<RelationType, Set<Integer>>
                                rt2reachableHitConceptIDs = reachability.get(hitConceptID);

                        if (rt2reachableHitConceptIDs != null) {

                            // get intersection of reltaion types for current
                            // pair of concepts
                            HashSet<RelationType> intersection = new HashSet<RelationType>();
                            intersection.addAll(rt2reachableHitConceptIDs.keySet());
                            intersection.retainAll(rt2reachableConceptIDs.keySet());

                            // check if concepts can be reached by the same rt
                            if (!intersection.isEmpty()) {

                                // iterate over intersections of rts
                                for (RelationType rt : intersection) {

                                    // get all reachable concepts for current rt
                                    Set<Integer> reachableConceptIDs =
                                            rt2reachableConceptIDs.get(rt);
                                    Set<Integer> reachableHitConceptIDs =
                                            rt2reachableHitConceptIDs.get(rt);

                                    // compare neighbourhoods for concept name
                                    // matches
                                    for (Integer reachableConceptID : reachableConceptIDs) {

                                        // build intersection between concept
                                        // name matches
                                        Set<Integer> intIntersection = new HashSet<Integer>();
                                        intIntersection.addAll(conceptID2hitConceptIDs
                                                .get(reachableConceptID));
                                        intIntersection.retainAll(reachableHitConceptIDs);

                                        // if intersection not empty create
                                        // relation
                                        if (!intIntersection.isEmpty()) {

                                            // get concepts for ids
                                            ONDEXConcept fromConcept = graph.getConcept(conceptID);
                                            ONDEXConcept toConcept = graph.getConcept(hitConceptID);

                                            // check DataSource conditions
                                            DataSource fromDataSource = fromConcept.getElementOf();
                                            DataSource toDataSource = toConcept.getElementOf();
                                            if (dataSourceMapping.size() > 0 && !toDataSource.equals(dataSourceMapping.get(fromDataSource))) {
                                                continue;
                                            }

                                            // check ConceptClass conditions
                                            ConceptClass fromCC = fromConcept.getOfType();
                                            ConceptClass toCC = toConcept.getOfType();
                                            if (ccMapping.size() > 0 && !toCC.equals(ccMapping.get(fromCC))) {
                                                continue;
                                            }

                                            // between different DataSource
                                            if (!fromConcept.getElementOf().equals(toConcept.getElementOf())) {

                                                // check if fromConcept ID
                                                // already contained
                                                if (relations.get(conceptID.intValue()) == null) {
                                                    relations.put(conceptID, new HashMap<Integer,Integer>());
                                                }

                                                // get all relations for
                                                // fromConcept
                                                Map<Integer,Integer> relationHits = relations.get(conceptID.intValue());

                                                // get old hits for toConcept
                                                // and increment
                                                int hits = relationHits.get(hitConceptID.intValue()) + 1;

                                                // store new hit value
                                                relationHits.put(hitConceptID.intValue(), hits);
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

        fireEventOccurred(new GeneralOutputEvent("Finished graph neighborhood alignment.", "[Mapping - setONDEXGraph]"));

        int unidirectional = 0;

        // iterator over all found relations
        Iterator<Integer> itFromConceptID = relations.keySet().iterator();
        while (itFromConceptID.hasNext()) {

            int fromConceptID = itFromConceptID.next();
            Map<Integer,Integer> relationHits = relations.get(fromConceptID);

            // get toConcept IDs
            Iterator<Integer> itToConceptID = relationHits.keySet().iterator();
            while (itToConceptID.hasNext()) {
                int toConceptID = itToConceptID.next();
                int score = relationHits.get(toConceptID);

                // check for bidirectional hits
                if (relations.containsKey(toConceptID)
                        && relations.get(toConceptID).containsKey(fromConceptID)) {

                    // get concepts for ids
                    ONDEXConcept fromConcept = graph.getConcept(fromConceptID);
                    ONDEXConcept toConcept = graph.getConcept(toConceptID);

                    // different CVs
                    if (!fromConcept.getElementOf().equals(toConcept.getElementOf())) {

                        // get relation if existing
                        ONDEXRelation relation =
                                graph.getRelation(fromConcept, toConcept, rtSet);

                        if (relation == null) {
                            // create not existing relation
                            relation = graph.getFactory().createRelation(fromConcept, toConcept, rtSet, eviType);
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
                "Uni directional hits excluded = " + unidirectional, "[Mapping - setONDEXGraph]"));
    }

    public String[] requiresValidators() {
        return new String[0];
    }
}