package net.sourceforge.ondex.wsapi.result;

import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.ConceptName;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.wsapi.WebServiceEngine;

/**
 * 
 *
 * @author David Withers, Christian Brenninkmeijer
 */
public class WSConcept {

	/**
	 * The ID of the Concept
	 */
	private Integer id;

	/**
	 * Any longer description String for this Concept. Should be
	 * distinct from annotation.  (Optional)
	 */
	private String description;

	/**
	 * The pid String for this Concept. (Optional)
	 */
	private String pid;

    /**
	 * Relevant annotation for this Concept, which is a short
	 * significant human readable description. (Optional)
	 */
	private String annotation;

	/**
	 * Id of the {@link WSCV} (data source) to which this Concept belongs to.
     *
     * Use the method getCV(Long graphId, String cvId) to obtain the full {@link WSCV}.
	 */
	private String elementOf;

	/**
	 * id of the {@link WSConceptClass} of which this Concept belongs to.
     *
     * Use the method getConceptClass(Long graphId, String conceptClassId) to obtain the full {@link WSConceptClass}.
	 */
	private String ofType;

    /**
     * The name part of the first {@link WSConceptName} assoictaede with this concept.
     *
     * Defined as the name returned by the function concept.getName();
     * <p>
     * Use the method getConceptName(Long graphId, Integer conceptId) to obtain the full {@link WSConceptName}.
     * Use the method getConceptNames(Long graphId, Integer conceptId)
     * to obtain all the {@link WSConceptName}s associated with this {@link WSConcept}.
    */
    private String name;

    public WSConcept() {
	}

	public WSConcept(ONDEXConcept concept) {
        if (concept == null){
            id = WebServiceEngine.ERROR_ID;
            description = "";
            elementOf = "";
            annotation = "";
            name = "";
            ofType = "";
        } else {
            id = concept.getId();
            description = concept.getDescription();
            pid = concept.getPID();
            annotation = concept.getAnnotation();
            DataSource dataSource = concept.getElementOf();
            if (dataSource != null) {
                elementOf = dataSource.getId();
            } else {
                elementOf = "";
            }
            ConceptClass conceptClass = concept.getOfType();
            if (conceptClass != null) {
                ofType = conceptClass.getId();
            } else {
                ofType = "";
            }
            ConceptName conceptName = concept.getConceptName();
            if (conceptName == null){
                name = "";
            } else{
                name = conceptName.getName();
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
	 * @param id the new id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the PID.
	 *
	 * @return the PID
	 */
	public String getPID() {
		return pid;
	}

	/**
	 * Sets the PID.
	 *
	 * @param PID the new PID
	 */
	public void setPID(String PID) {
		this.pid = PID;
	}

    /**
	 * Returns the annotation.
	 *
	 * @return the annotation
	 */
	public String getAnnotation() {
		return annotation;
	}

	/**
	 * Sets the annotation.
	 *
	 * @param annotation the new annotation
	 */
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	/**
	 * Returns the id of the elementOf.
	 *
	 * @return the id of the elementOf
	 */
	public String getElementOf() {
		return elementOf;
	}

	/**
	 * Sets the id of the elementOf.
	 *
	 * @param elementOf the id of the new elementOf
	 */
	public void setElementOf(String elementOf) {
		this.elementOf = elementOf;
	}

	/**
	 * Returns the id of the ofType.
	 *
	 * @return the id of the ofType
	 */
	public String getOfType() {
		return ofType;
	}

	/**
	 * Sets the id of the ofType.
	 *
	 * @param ofType the new id of the ofType
	 */
	public void setOfType(String ofType) {
		this.ofType = ofType;
	}

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
