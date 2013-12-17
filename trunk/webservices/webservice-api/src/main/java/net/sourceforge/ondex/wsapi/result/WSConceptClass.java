package net.sourceforge.ondex.wsapi.result;

import net.sourceforge.ondex.core.ConceptClass;

/**
 * Information about the Class of a Concept. It has a mandatory name and an
 * optional description field for additional information. A ConceptClass can be
 * a specialisation of another ConceptClass.
 * 
 * @author David Withers, Christian Brenninkmeijer
 */
public class WSConceptClass extends WSMetaData {

    /**
     * The id of the specialisationOf {@link WSConceptClass} (Optional).
     *
     * Use the method getConceptClass(Long graphId, String conceptClassId) to obtain the full {@link WSConceptClass}.
     */
    String specialisationOf;

	public WSConceptClass() {
	}

	public WSConceptClass(ConceptClass conceptClass) {
		super(conceptClass);
        specialisationOf = "";
        if (conceptClass != null) {
            ConceptClass specialisationOfConceptClass = conceptClass
				.getSpecialisationOf();
            ConceptClass parent = conceptClass.getSpecialisationOf();
            if (parent != null){
                specialisationOf =parent.getId();
            }
        }
	}

	/**
	 * Returns The id of the specialisationOf {@link WSConceptClass}
	 * 
	 * @return The id
	 */
	public String getSpecialisationOf() {
		return specialisationOf;
	}

	/**
	 * Sets The id of the specialisationOf {@link WSConceptClass}
	 * 
	 * @param specialisationOf
	 *            The id
	 */
	public void setSpecialisationOf(String specialisationOf) {
		this.specialisationOf = specialisationOf;
	}

	/*********************************************************************/
	/**** workaround for taverna bug that ignores inheritance in wsdl ****/
	/*********************************************************************/

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*********************************************************************/

}
