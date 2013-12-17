package net.sourceforge.ondex.wsapi.result;

import net.sourceforge.ondex.core.ConceptName;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.core.RelationType;
import net.sourceforge.ondex.wsapi.exceptions.RelationNotFoundException;
import net.sourceforge.ondex.wsapi.WebServiceEngine;
import org.apache.log4j.Logger;

/**
 * A Lazy definition of a Relation between two concepts reflecting removal of qualifier.
 *
 * This is the current format of Relation. Replacing depreciated WSRelation
 * 
 * @author David Withers, Christian Brenninkmeijer
 */
public class WSRelation {

    private static final Logger logger = Logger.getLogger(WSRelation.class);

    /**
	 * The ID of this Relation
	 */
	private Integer id;

	/**
	 * The id of the "from" {@link WSConcept} of this Relation.
     *
     * Use the method getConcept(Long graphId, Integer conceptId) to obtain the full {@link WSConcept}.
	 */
	private int fromConceptId;

	/**
	 * The name part of first {@link WSConceptName} associated with "from" {@link WSConcept} of this Relation
     *
     * Use the method getConceptName(Long graphId, Integer conceptId) to obtain the full {@link WSConceptName}.
     * Use the method getConceptNames(Long graphId, Integer conceptId)
     * to obtain all the {@link WSConceptName}s associated with a {@link WSConcept}.
	 */
	private String fromConceptName;

    /**
	 * The id of the "to" Concept of this Relation
     *
     * Use the method getConcept(Long graphId, Integer conceptId) to obtain the full {@link WSConcept}.
	 */
	private int toConceptId;

	/**
	 * The name part of first {@link WSConceptName} associated with "to" {@link WSConcept} of this Relation
     *
     * Use the method getConceptName(Long graphId, Integer conceptId) to obtain the full {@link WSConceptName}.
     * Use the method getConceptNames(Long graphId, Integer conceptId)
     * to obtain all the {@link WSConceptName}s associated with a {@link WSConcept}.
	 */
	private String toConceptName;

    /**
	 * The id of the {@link WSRelationType} of this Relation
     *
     * Use the method getRelationType(Long graphId, String relationTypeId) to obtain the full {@link WSRelationType}.
	 */
	private String ofType;

	public WSRelation() {
	}

	public WSRelation(ONDEXRelation relation) throws RelationNotFoundException {
        if (relation == null) {
            id = WebServiceEngine.ERROR_ID;
            fromConceptId = WebServiceEngine.ERROR_ID;
            fromConceptName = "";
            toConceptId = WebServiceEngine.ERROR_ID;
            toConceptName = "";
            ofType = "";
        } else {
            id = relation.getId();
            ONDEXConcept concept = relation.getFromConcept();
            ConceptName conceptName;
            if (concept != null) {
                fromConceptId = concept.getId();
                conceptName = concept.getConceptName();
                if (conceptName == null){
                    fromConceptName = "";
                } else{
                    fromConceptName = conceptName.getName();
                }
            } else {
                throw new RelationNotFoundException("From concept is null in relation "+id, logger);
            }
            concept = relation.getToConcept();
            if (concept != null) {
                toConceptId = concept.getId();
                conceptName = concept.getConceptName();
                if (conceptName == null){
                    toConceptName = "";
                } else{
                    toConceptName = conceptName.getName();
                }
            } else {
                throw new RelationNotFoundException("To concept is null in relation "+id, logger);
            }
            RelationType relationType = relation.getOfType();
            if (relationType != null) {
                ofType = relationType.getId();
            } else {
                ofType = "";
            }
        }
    }

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the id of the fromConcept.
	 * 
	 * @return the id of the fromConcept
	 */
	public int getFromConceptId() {
		return fromConceptId;
	}

	/**
	 * Returns the name of the fromConcept.
	 *
	 * @return the fromConcept
	 */
	public String getFromConceptName() {
		return fromConceptName;
	}

    /**
	 * Sets the id of the fromConcept.
	 * 
	 * @param fromConceptId
	 *            the new id
	 */
	public void setFromConceptId(int fromConceptId) {
		this.fromConceptId = fromConceptId;
	}

    /**
	 * Sets the name of the fromConcept.
	 *
	 * @param fromConceptName
	 *            the new name
	 */
	public void setFromConceptName(String fromConceptName) {
		this.fromConceptName = fromConceptName;
	}

    /**
	 * Returns the id of the toConcept.
	 * 
	 * @return the id of the toConcept
	 */
	public int getToConceptId() {
		return toConceptId;
	}

    /**
	 * Returns the name of the toConcept.
	 *
	 * @return the name of the toConcept
	 */
	public String getToConceptName() {
		return toConceptName;
	}

	/**
	 * Sets the id of the toConcept.
	 * 
	 * @param toConceptId
	 *            the new id
	 */
	public void setToConceptId(int toConceptId) {
		this.toConceptId = toConceptId;
	}

	/**
	 * Sets the name of the toConcept.
	 *
	 * @param toConceptName
	 *            the new name
	 */
	public void setToConceptName(String toConceptName) {
		this.toConceptName = toConceptName;
	}

	/**
	 * Returns the id of ofType
	 * 
	 * @return the id ofType
	 */
	public String getOfType() {
		return ofType;
	}

	/**
	 * Sets the id of ofType.
	 * 
	 * @param ofType
	 *            the new id
	 */
	public void setOfType(String ofType) {
		this.ofType = ofType;
	}

    
 }

