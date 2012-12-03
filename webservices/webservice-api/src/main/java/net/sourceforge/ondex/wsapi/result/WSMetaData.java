package net.sourceforge.ondex.wsapi.result;

import net.sourceforge.ondex.core.MetaData;

/**
 * Parent class for information classes like DataSource and ConceptClass. It contains
 * properties for the mandatory field id and the optional fields fullname and
 * description.
 * 
 * @author David Withers
 */
public abstract class WSMetaData {

	/**
	 * A short ID
	 */
	protected String id;

	/**
	 * Longer name (Optional)
	 */
	protected String fullname;

	/**
	 * Description (Optional)
	 */
	protected String description;

	public WSMetaData() {
	}

	public WSMetaData(MetaData metaData) {
        if (metaData == null) {
          id = "";
          fullname = "";
          description = "";
        } else {
            id = metaData.getId();
            fullname = metaData.getFullname();
            description = metaData.getDescription();
        }
	}

	// /**
	// * Returns the id.
	// *
	// * @return the id
	// */
	// public String getId() {
	// return id;
	// }
	//
	// /**
	// * Sets the id.
	// *
	// * @param id the new id
	// */
	// public void setId(String id) {
	// this.id = id;
	// }
	//
	// /**
	// * Returns the fullname.
	// *
	// * @return the fullname
	// */
	// public String getFullname() {
	// return fullname;
	// }
	//
	// /**
	// * Sets the fullname.
	// *
	// * @param fullname the new fullname
	// */
	// public void setFullname(String fullname) {
	// this.fullname = fullname;
	// }
	//
	// /**
	// * Returns the description.
	// *
	// * @return the description
	// */
	// public String getDescription() {
	// return description;
	// }
	//
	// /**
	// * Sets the description.
	// *
	// * @param description the new description
	// */
	// public void setDescription(String description) {
	// this.description = description;
	// }

}
