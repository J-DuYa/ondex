package net.sourceforge.ondex.parser.uniprot.xml.component;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import net.sourceforge.ondex.InvalidPluginArgumentException;
import net.sourceforge.ondex.parser.uniprot.sink.DbLink;
import net.sourceforge.ondex.parser.uniprot.sink.Disease;
import net.sourceforge.ondex.parser.uniprot.sink.Protein;

/**
 * Parses UniProt comment elements, so far looks only for disruption phenotype
 * 10/02/2016: Now, also creates 'disease' object.
 * @author keywan, singha
 *
 */
public class CommentBlockParser extends AbstractBlockParser {

	private static final String EVIDENCE = "evidence";
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String ACRONYM = "acronym";
	private static final String DESCRIPTION = "description";
	private static final String MIM = "mim";
	private static final String TEXT = "text";

	@Override
	public void parseElement(XMLStreamReader staxXmlReader)
			throws XMLStreamException, InvalidPluginArgumentException {

		String type = staxXmlReader.getAttributeValue(0);
		if(type.equals("disruption phenotype")){
			int event = staxXmlReader.nextTag();
			if (event == XMLStreamConstants.START_ELEMENT && 
					staxXmlReader.getLocalName().equals("text")){
			
				String phenotype = staxXmlReader.getElementText();
				Protein.getInstance().setDisruptionPhenotype(phenotype);
			
			}
            
		}

                else if(type.equals("disease")) {
                   // create 'disease' object
                   Disease disease= null;

		   String disease_evidence= null;
		   String disease_id= null;
		   String disease_name= null;
		   String disease_acronym= null;
		   String disease_description= null;
		   String disease_mim_id= null;
		   String disease_text= null;

                   // get disease evidence
		   for (int i=0;i<staxXmlReader.getAttributeCount();i++) {
			String field= staxXmlReader.getAttributeLocalName(i);
			if (field.equalsIgnoreCase(EVIDENCE)) {
			    disease_evidence= staxXmlReader.getAttributeValue(i);
			    break;
			   } 
		       }

                   while (staxXmlReader.hasNext()) {
                          int event= staxXmlReader.next();
                        /*  System.out.println("Event= "+ event +", isCharacters ?= "+ staxXmlReader.isCharacters() +
                                  ", isWhitespace ?= "+ staxXmlReader.isWhiteSpace());*/
                          if(event == XMLStreamConstants.CHARACTERS && staxXmlReader.isWhiteSpace()) {
                             // skip whitespace
                            }
                          
                          if (event == XMLStreamConstants.START_ELEMENT) {
                            //  System.out.println("staxXmlReader.getLocalName()= "+ staxXmlReader.getLocalName() +", isStartElement ?= "+ staxXmlReader.isStartElement());
			      if (staxXmlReader.getLocalName().equalsIgnoreCase("disease")) {
                                  // get disease id
                                  for (int i=0;i<staxXmlReader.getAttributeCount();i++) {
                                       String field= staxXmlReader.getAttributeLocalName(i);
                                       if (field.equalsIgnoreCase(ID)) {
                                           disease_id= staxXmlReader.getAttributeValue(i);
                                           break;
                                          }
                                      }
                                 }
                              
                              if (staxXmlReader.getLocalName().equalsIgnoreCase(NAME)) {
                                  // get disease name
                                  disease_name= staxXmlReader.getElementText().trim();
				}
                              if (staxXmlReader.getLocalName().equalsIgnoreCase(ACRONYM)) {
                                  // get disease acronym
                                  disease_acronym= staxXmlReader.getElementText();
				}
                              if (staxXmlReader.getLocalName().equalsIgnoreCase(DESCRIPTION)) {
                                  // get disease description
                                  disease_description= staxXmlReader.getElementText().trim();
				}

                              // get disease dbReference
                              if (staxXmlReader.getLocalName().equalsIgnoreCase("dbReference")) {
//                                  DbLink dbLink= new DbLink();
                                  for (int i=0;i<staxXmlReader.getAttributeCount();i++) {
                                       String field= staxXmlReader.getAttributeLocalName(i);
                                       if (field.equalsIgnoreCase(ID)) { // MIM id
                                           disease_mim_id= staxXmlReader.getAttributeValue(i);
                                           break;
                                          }
                                      }
/*                                  dbLink.setDbName(MIM);
                                  dbLink.setAccession(disease_mim_id);
                                  if (disease != null ) {
                                      disease.addReference(dbLink);
                                     }
                                  else {
                                      Protein.getInstance().addDbReference(dbLink);
                                     }*/
				}

                              // get disease text
                              if (staxXmlReader.getLocalName().equalsIgnoreCase(TEXT)) {
                                  disease_text= staxXmlReader.getElementText().trim();
                                }
                         }
                          
                          // check if end of a disease block has been reached.
                          if(event== XMLStreamConstants.END_ELEMENT && staxXmlReader.getLocalName().equals("comment")) {
                          //   System.out.println("staxXmlReader.getLocalName()= "+ staxXmlReader.getLocalName() +", isEndElement ?= "+ staxXmlReader.isEndElement());
                             // reached the end of a disease block
                             break;
                            }
                   }
                   
                   System.out.print("Disease id: "+ disease_id);
                   System.out.print(", name: "+ disease_name);
                   System.out.print(", acronym: "+ disease_acronym);
                   System.out.print(", dbReference: MIM id: "+ disease_mim_id);
                   System.out.print(", evidence: "+ disease_evidence);
                   System.out.print(", description: "+ disease_description);
                   System.out.println(", text: "+ disease_text);

                   disease= new Disease();
                   // Add to disease object.
                   if (disease_id != null) {
                       disease.setDiseaseID(disease_id);
                     }
                   if (disease_name != null)
                       disease.setDiseaseName(disease_name);
                   if (disease_acronym != null)
                       disease.setDiseaseAcronym(disease_acronym);
                   if (disease_evidence != null)
                       disease.setDiseaseEvidence(disease_evidence);
                   if (disease_description != null)
                       disease.setDiseaseDescription(disease_description);
                   // Disease MIM ID.
                   if (disease_mim_id != null) {
                       disease.setMimID(disease_mim_id);
                       DbLink dbLink= new DbLink();
                       dbLink.setDbName(MIM);
                       dbLink.setAccession(disease_mim_id);
                       if(disease != null ) {
                          disease.addReference(dbLink);
                         }
                       else {
                           Protein.getInstance().addDbReference(dbLink);
                          }
                     }
                   // Disease Text
                   if (disease_text != null)
                       disease.setDiseaseText(disease_text);

                   // add Disease object
                   Protein.getInstance().addDisease(disease);

            System.out.println("\t Disease parsing done... \n");
        }

    }
}
