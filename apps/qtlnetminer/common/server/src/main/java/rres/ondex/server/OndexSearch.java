package rres.ondex.server;

import java.util.regex.Pattern;

import net.sourceforge.ondex.core.Attribute;
import net.sourceforge.ondex.core.ConceptAccession;
import net.sourceforge.ondex.core.ConceptName;
import net.sourceforge.ondex.core.ONDEXConcept;

/**
 * Straight forward in-memory search implementation.
 * 
 * @author keywan
 */
public class OndexSearch {

	/**
	 * Searches different fields of a concept for a query or pattern
	 * and highlights them
	 * 
	 * @param concept
	 * @param p
	 * @param search
	 * @return true if one of the concept fields contains the query
	 */
	public static boolean find(ONDEXConcept concept, String regex, boolean highlight) {
		
		// check for empty keyword here
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		
		String highlighter = "<SPAN style=\"BACKGROUND-COLOR: #ffff00\">$0</SPAN>";

//		Pattern undoHighlight = Pattern
//				.compile("<SPAN style=\"BACKGROUND-COLOR: #ffff00\">(.*?)</SPAN>");
		
		boolean found = false;

		// search in pid
		String pid = concept.getPID();
		if (isMatching(p, pid)){
			found = true;
		}	

		// search in annotation
		String anno = concept.getAnnotation();
		if (isMatching(p, anno)){
			found = true;
			
			if(highlight){
				// search and replace all matching regular expressions
				String newAnno = p.matcher(anno).replaceAll(highlighter);
				concept.setAnnotation(newAnno);
			}
		}	

		// search in description
		String desc = concept.getDescription();
		if (isMatching(p, desc)){
			found = true;
			
			if(highlight){
				// search and replace all matching regular expressions
				String newDesc = p.matcher(desc).replaceAll(highlighter);
				concept.setAnnotation(newDesc);
			}
		}	

		// search in concept names
		for (ConceptName cno : concept.getConceptNames()) {
			String cn = cno.getName();
			if (isMatching(p, cn)){
				found = true;
//				boolean isPref = cno.isPreferred();
//				if(concept.deleteConceptName(cn)){
//					// search and replace all matching regular expressions
//					String newName = p.matcher(cn).replaceAll(
//							"<SPAN style=\"BACKGROUND-COLOR: #ffff00\">$0</SPAN>");
//					concept.createConceptName(newName, isPref);
//				}
			}	
		}

		// search in concept accessions
		for (ConceptAccession ca : concept.getConceptAccessions()) {
			String accession = ca.getAccession();
			if (isMatching(p, accession)){
				found = true;
//				boolean isAmb = ca.isAmbiguous();
//				DataSource elementOf = ca.getElementOf();
//				if(concept.deleteConceptAccession(accession, elementOf)){
//					// search and replace all matching regular expressions
//					String newAcc = p.matcher(accession).replaceAll(
//							"<SPAN style=\"BACKGROUND-COLOR: #ffff00\">$0</SPAN>");
//					concept.createConceptAccession(newAcc, elementOf, isAmb);
//				}
			}
		}

		for (Attribute attribute : concept.getAttributes()) {
        	if(attribute.getOfType().getId().equals("AA") ||
        			attribute.getOfType().getId().equals("NA")){
        		continue;
        	}
        	if(attribute.getOfType().getId().startsWith("AA_") ||
        			attribute.getOfType().getId().startsWith("NA_")){
        		continue;
        	}	
			
			String value = attribute.getValue().toString();
			// undo previous highlighting
			//String undoText = undoHighlight.matcher(value).replaceAll("$1");

			if (isMatching(p, value)) {
				found = true;

				if(highlight){
					// search and replace all matching regular expressions
					String newGDS = p.matcher(value).replaceAll(highlighter);
					attribute.setValue(newGDS);

				}
				
			}
		}

		// if nothing found
		return found;
	}



	/**
	 * Tests if there is a match of this query within the search string
	 * 
	 * @param p
	 *            the pattern (if null then literal match .contains is used)
	 * @param query
	 *            the query to match
	 * @param target
	 *            the target to match to
	 * @return is there a match
	 */
	public static boolean isMatching(Pattern p, String target) {

		return p.matcher(target).find();
		
	}

}
