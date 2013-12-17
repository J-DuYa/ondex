package net.sourceforge.ondex.wsapi.result;

import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.ConceptAccession;
//import net.sourceforge.ondex.webservice2.WebServiceEngine;

/**
 * A ConceptAccession is a reference into a external data source. It consists of
 * accession, a DataSource and ambiguous flag. It belongs to a Concept.
 *
 * New Format with Data Source
 *
 * @author taubertj, Christian Brenninkmeijer
 * 
 */
public class WSConceptAccession {

	/**
	 * The accession is the reference String to the external data source.
	 */
	private String accession;

	/**
	 * {@link WSCV} (data source) to which this ConceptAccession belongs to.
     *
	 */
	private WSDataSource elementOf;
	
	/**
	 * The ambiguous property of a ConceptAccession. The default is true. (Optional)
	 */
	private Boolean isAmbiguous;
	
	public WSConceptAccession() {
	}
	
	public WSConceptAccession(ConceptAccession conceptAccession) {
        if (conceptAccession == null){
            accession = "";
            isAmbiguous = true;
            elementOf = new WSDataSource(null);
        } else {
            accession = conceptAccession.getAccession();
            DataSource dataSource = conceptAccession.getElementOf();
            if (dataSource != null) {
                elementOf = new WSDataSource(dataSource);
            } else {
                elementOf = new WSDataSource(null);
            }
            isAmbiguous = conceptAccession.isAmbiguous();
        }
	}
		
	/**
	 * Returns the accession.
	 * 
	 * @return the accession
	 */
	public String getAccession() {
		return accession;
	}

	/**
	 * Sets the accession.
	 * 
	 * @param accession
	 *            the new accession
	 */
	public void setAccession(String accession) {
		this.accession = accession;
	}

	/**
	 * Returns the elementOf.
	 *
	 * @return the elementOf
	 */
	public WSDataSource getElementOf() {
		return elementOf;
	}

	/**
	 * Sets the elementOf.
	 *
	 * @param elementOf the new elementOf
	 */
	public void setElementOf(WSDataSource elementOf) {
		this.elementOf = elementOf;
	}

	/**
	 * Returns the isAmbiguous.
	 * 
	 * @return the isAmbiguous
	 */
	public Boolean getIsAmbiguous() {
		return isAmbiguous;
	}

	/**
	 * Sets the isAmbiguous.
	 * 
	 * @param isAmbiguous
	 *            the new isAmbiguous
	 */
	public void setIsAmbiguous(Boolean isAmbiguous) {
		this.isAmbiguous = isAmbiguous;
	}
}
