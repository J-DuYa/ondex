package net.sourceforge.ondex.wsapi.result;

import net.sourceforge.ondex.core.DataSource;

/**
 * The controlled vocabulary (DataSource) for the created Concepts and Relations. It has
 * a mandatory name and an optional description field for additional
 * information.
 * 
 * @author David Withers
 */
public class WSDataSource extends WSMetaData {

	public WSDataSource() {
	}

	public WSDataSource(DataSource dataSource) {
		super(dataSource);
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
