package net.sourceforge.ondex.wsapi.result;

import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.Unit;

/**
 * Describes the attributes used in a {@link WSGDS}. It has a mandatory
 * name and datatype field, an optional description and unit field.
 *
 * @author David Withers, Christian Brenninkmeijer
 */
public class WSAttributeName extends WSMetaData {
	
	/**
	 * Id of the {@link WSUnit} for this AttributeName (Optional)
     *
     * Use method getUnit(Long graphId, String unitId) to obtain the full {@link WSUnit}.
	 */
	private String unit;

	/**
	 * Datatype for this AttributeName. Must be the name of a Java class.
	 */
	private String datatype;

   /**
     * The id of the specialisationOf {@link WSAttributeName} (Optional).
     *
     * Use the mthod getAttributeName(Long graphId, String attributeNameId)) to get the full {@link WSAttributeName}.
     */
    String specialisationOf;

	public WSAttributeName() {
	}
	
	public WSAttributeName(AttributeName attributeName) {
		super(attributeName);
        if (attributeName == null){
            unit = "";
            datatype = "";
            specialisationOf = "";
        } else {
            Unit attributeNameUnit = attributeName.getUnit();
            if (attributeNameUnit != null) {
                unit = attributeNameUnit.getId();
            } else {
                unit = "";
            }
            datatype = attributeName.getDataTypeAsString();
            AttributeName parent = attributeName.getSpecialisationOf();
            if (parent != null){
                specialisationOf = parent.getId();
            } else {
                specialisationOf = "";
            }
		}
	}

	/**
	 * Returns id of the unit.
     *
     * Use
	 *
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Sets the id of the unit.
	 *
	 * @param unit the new unit
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * Returns the datatype.
	 *
	 * @return the value of datatype
	 */
	public String getDatatype() {
		return datatype;
	}

	/**
	 * Sets the datatype.
	 *
	 * @param datatype the new value for datatype
	 */
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	/**
	 * Returns The id of the specialisationOf {@link WSAttributeName}
	 *
	 * @return The id
	 */
	public String getSpecialisationOf() {
		return specialisationOf;
	}

	/**
	 * Sets The id of the specialisationOf {@link WSAttributeName}.
	 *
	 * @param The id
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
