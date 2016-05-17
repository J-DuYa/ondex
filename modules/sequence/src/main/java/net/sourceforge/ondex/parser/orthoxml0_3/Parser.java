package net.sourceforge.ondex.parser.orthoxml0_3;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sourceforge.ondex.annotations.Authors;
import net.sourceforge.ondex.annotations.Custodians;
import net.sourceforge.ondex.annotations.DataURL;
import net.sourceforge.ondex.annotations.DatabaseTarget;
import net.sourceforge.ondex.annotations.Status;
import net.sourceforge.ondex.annotations.StatusType;
import net.sourceforge.ondex.annotations.metadata.AttributeNameRequired;
import net.sourceforge.ondex.annotations.metadata.DataSourceRequired;
import net.sourceforge.ondex.annotations.metadata.ConceptClassRequired;
import net.sourceforge.ondex.annotations.metadata.EvidenceTypeRequired;
import net.sourceforge.ondex.annotations.metadata.RelationTypeRequired;
import net.sourceforge.ondex.args.ArgumentDefinition;
import net.sourceforge.ondex.args.FileArgumentDefinition;
import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.EvidenceType;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXGraphMetaData;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.core.RelationType;
import net.sourceforge.ondex.parser.ONDEXParser;

import org.codehaus.stax2.XMLInputFactory2;

import com.ctc.wstx.io.CharsetNames;
import java.util.ArrayList;
import net.sourceforge.ondex.args.StringArgumentDefinition;

/**
 * Parser for the OrthoXML format
 * http://orthoxml.org/
 * 
 * This format is used especially by Inparanoid
 * http://inparanoid.sbc.su.se/download/current/orthoXML/
 * 
 * @author keywan, singha
 *
 */
@Status(description = "Tested May 2016 (Ajit Singh)", status = StatusType.EXPERIMENTAL)
@Authors(authors = {"Keywan Hassani-Pak", "Ajit Singh"}, emails = {"keywan at users.sourceforge.net", ""})
@DatabaseTarget(name = "OrthoXML", description = "OrthoXML is designed broadly to allow the storage and comparison of orthology data from any ortholog database. This Ondex parser for OrthoXML was tested on OrthoXML files provided by Inparanoid.", version = "0.3", url = "http://orthoxml.org/")
@DataURL(name = "OrthoXML file",
        description = "This parser requires as input a single OrthoXML file.",
        urls = {"http://inparanoid.sbc.su.se/download/current/orthoXML/"})
@Custodians(custodians = {"Keywan Hassani-pak", "Ajit Singh"}, emails = {"keywan at users.sourceforge.net", ""})
//@DataSourceRequired(ids = {MetaData.cvInparanoid })
@DataSourceRequired(ids = {ArgumentNames.DATA_SOURCE })
@ConceptClassRequired(ids = {MetaData.ccProtein })
@EvidenceTypeRequired(ids = {MetaData.etIMPD}) 
@RelationTypeRequired(ids = {MetaData.rtOrtholog, MetaData.rtParalog}) 
@AttributeNameRequired(ids = {MetaData.atCONF, MetaData.atTaxid})
public class Parser extends ONDEXParser {

	private HashMap<Integer,Integer> ondexConcepts;

	private ConceptClass ccProtein;
	private DataSource dataSourceInparanoid;
	private RelationType rtOrtholog;
	private RelationType rtParalog;
	private EvidenceType etIMPD;
	private AttributeName anTaxId;
	private AttributeName anConf;



	@Override
	public ArgumentDefinition<?>[] getArgumentDefinitions() {
                StringArgumentDefinition data_source = new StringArgumentDefinition(
                ArgumentNames.DATA_SOURCE, ArgumentNames.DATA_SOURCE_DESC, false,
                null, true);
                
                FileArgumentDefinition inputDir= new FileArgumentDefinition(FileArgumentDefinition.INPUT_FILE, "Absolute path to an OrthoXML input file", true, true, false, false);

		return new ArgumentDefinition<?>[]{inputDir, data_source};
	}

	@Override
	public String getId() {
		return "orthoxml_0_3";
	}

	@Override
	public String getName() {
		return "OrthoXML 0.3";
	}

	@Override
	public String getVersion() {
		return "17-05-2016";
	}

	@Override
	public String[] requiresValidators() {
		return new String[]{};
	}

	@Override
	public void start() throws Exception {

		ONDEXGraphMetaData md = graph.getMetaData();
		ccProtein = md.getConceptClass(MetaData.ccProtein);
//		dataSourceInparanoid = md.getDataSource(MetaData.cvInparanoid);
		rtOrtholog = md.getRelationType(MetaData.rtOrtholog);
		rtParalog = md.getRelationType(MetaData.rtParalog);
		etIMPD = md.getEvidenceType(MetaData.etIMPD);
		anTaxId = md.getAttributeName(MetaData.atTaxid);
		anConf = md.getAttributeName(MetaData.atCONF);

		ondexConcepts = new HashMap<Integer,Integer>();

		File xmlFile = new File((String) args.getUniqueValue(FileArgumentDefinition.INPUT_FILE));
                dataSourceInparanoid= md.getDataSource((String) args.getUniqueValue(ArgumentNames.DATA_SOURCE));
//                System.out.println("arg_DataSource= "+ args.getUniqueValue(ArgumentNames.DATA_SOURCE));
                if(dataSourceInparanoid ==null){
                   dataSourceInparanoid= md.createDataSource("OMA", "OMA Standalone", "OMA Standalone");
                  }
                
                // evidence type
                if(etIMPD ==null){
                   etIMPD= md.createEvidenceType("OMA", "OMA Standalone", "OMA Standalone");
                  }

		// setup XMLStreamReader
		System.setProperty("javax.xml.stream.XMLInputFactory",
		"com.ctc.wstx.stax.WstxInputFactory");
		XMLInputFactory2 xmlif = (XMLInputFactory2) XMLInputFactory2.newInstance();
		xmlif.configureForSpeed();
		FileInputStream in = new FileInputStream(xmlFile);
		XMLStreamReader xmlr = xmlif.createXMLStreamReader(in, CharsetNames.CS_UTF8);

		// main loop
		while (xmlr.hasNext()) {
			int event = xmlr.next();

			if (event == XMLStreamConstants.START_ELEMENT) {

				String element = xmlr.getLocalName();

				// delegate to species parser
				if (element.equals("species")) {
					parseSpecies(xmlr);
				}
				// delegate to ortholgGroup parser
				else if(element.equals("orthologGroup")){
					parseClusters(xmlr);
				}
			}
		} // eof
	}

	
	/**
	 * Parser for the xml species element. 
	 * Creates for each gene an Ondex concept.
	 * 
	 * @param xmlr
	 * @throws XMLStreamException
	 */
	private void parseSpecies(XMLStreamReader xmlr) throws XMLStreamException{
		//<species id="G.lamblia" name="Giardia lamblia" NCBITaxId="184922">
		String taxId = xmlr.getAttributeValue(null, "NCBITaxId");
//		System.out.println("TaxID: "+ taxId);

		xmlr.nextTag(); //database
		// <database name="Sanger" version="TANN.GeneDB.pep" geneLink="http://www.genedb.org/genedb/Search?name=" protLink="http://www.genedb.org/genedb/Search?name=">
		String db = xmlr.getAttributeValue(null, "name");
		//map db to Ondex DataSource
		String cvId = mapDBtoOndexmetaData(db);
//		System.out.println("cvId: "+ cvId);
		
		DataSource accDataSource = graph.getMetaData().getDataSource(cvId);
		if(accDataSource == null){
			accDataSource = graph.getMetaData().createDataSource(cvId, cvId, "Unknown DataSource from Inparanoid");
		}

		xmlr.nextTag(); //genes

		int eventType = xmlr.getEventType();

		while (xmlr.hasNext() && eventType != XMLStreamConstants.END_ELEMENT) {

			if (eventType == XMLStreamConstants.START_ELEMENT
					&& xmlr.getLocalName().equals("gene")) {

				// example
				// <gene id="1" geneId="GL50803_17570" protId="GL50803_17570" /> 
//				Integer id = Integer.parseInt(xmlr.getAttributeValue(0));
//				String geneId = xmlr.getAttributeValue(1);
//				String protId = xmlr.getAttributeValue(2);
				Integer id= null;
                                String geneId= null;
                                String protId= null;
                                int attrCount= xmlr.getAttributeCount();
                                for(int i=0; i< attrCount; i++) {
                                    String attrName= xmlr.getAttributeLocalName(i);
                                    if(attrName.equals("id")) {
				       id= Integer.parseInt(xmlr.getAttributeValue(i));
                                      }
                                    else if(attrName.equals("geneId")) {
                                            geneId= xmlr.getAttributeValue(i);//.replaceAll("|", ";");
                                      }
                                    else if(attrName.equals("protId")) {
                                            protId= xmlr.getAttributeValue(i);//.replaceAll("|", ";");
                                      }
                                   }
//                                System.out.println("XML: id: "+ id +", protId: "+ protId +", geneId: "+ geneId);
//                                System.out.println("dataSourceInparanoid: id= "+ dataSourceInparanoid.getId() +", fullName= "+ dataSourceInparanoid.getFullname() +", toString= "+ dataSourceInparanoid.toString());
//                                System.out.println("conceptType: "+ ccProtein +", evidenceType: "+ etIMPD);

				if(!ondexConcepts.containsKey(id)){
/*                                    if(!protId.isEmpty()) {
                                       if(protId.contains(";")) {
                                          String[] protIds= protId.split(";");
                                          for (String protId1 : protIds) {
                                               String p_id = protId1.trim();
                                               System.out.println("createConcept: protId: "+ p_id);
                                               ONDEXConcept c = graph.getFactory().createConcept(p_id, dataSourceInparanoid, ccProtein, etIMPD);
                                               c.createAttribute(anTaxId, taxId, false);
                                              }
                                          }
                                        else {*/
                                           ONDEXConcept c = graph.getFactory().createConcept(protId, dataSourceInparanoid, ccProtein, etIMPD);
					   c.createAttribute(anTaxId, taxId, false);
//                                          }
//                                         }
//					if(!geneId.isEmpty()){
/*                                            if(geneId.contains(";")) {
                                               String[] geneIds= geneId.split(";");
                                                for (String geneId1 : geneIds) {
                                                     String g_id = geneId1.trim();
                                                     System.out.println("createCOnceptName: geneId: "+ g_id);
                                                     c.createConceptName(g_id, false);
                                                     c.createConceptAccession(g_id, accDataSource, true);
                                                }
                                              }
                                            else {*/
//						c.createConceptName(geneId, false);
//						c.createConceptAccession(geneId, accDataSource, true);
//                                            }
//					}	
					if(!protId.isEmpty()){
/*                                            if(protId.contains(";")) {
                                               String[] protIds= protId.split(";");
                                                for (String protId1 : protIds) {
                                                     String p_id = protId1.trim();
                                                     System.out.println("createCOnceptName: protId: "+ p_id);
                                                     c.createConceptName(p_id, false);
                                                     c.createConceptAccession(p_id, accDataSource, true);
                                                }
                                              }
                                            else {*/
						c.createConceptName(protId, true);
						c.createConceptAccession(protId, accDataSource, false);
//                                            }
					}	

					ondexConcepts.put(id, c.getId());
				}


				xmlr.nextTag(); // skip end tag

			}
			eventType = xmlr.next();
		}
	}

	/**
	 * Parser for the xml orthologGroup element. 
	 * Creates for each seed-ortholog a relation of type ortholog and
	 * for each inparalog pair a relation of type paralog.
	 * 
	 * @param xmlr
	 * @throws XMLStreamException
	 */
	private void parseClusters(XMLStreamReader xmlr) throws XMLStreamException{

		// skip cluster score
//		xmlr.nextTag();
		xmlr.nextTag(); // skip to "GeneRef" tag.

		//cluster genes
//		ONDEXConcept[] seed = new ONDEXConcept[2];
                ArrayList<ONDEXConcept> seed= new ArrayList<ONDEXConcept>();
		int count = 0;

		while (xmlr.hasNext() && !xmlr.getLocalName().equals("orthologGroup")) {

			//go to geneRef
//			xmlr.nextTag();

			if (xmlr.getEventType() == XMLStreamConstants.START_ELEMENT
					&& xmlr.getLocalName().equals("geneRef")) {

				// <geneRef id="1">
				//   <score id="inparalog" value="1"/>
				//   <score id="bootstrap" value="1.00" /> 
				// </geneRef>
				Integer id = Integer.parseInt(xmlr.getAttributeValue(null, "id"));
				ONDEXConcept gene = graph.getConcept(ondexConcepts.get(id));
                                
                                // SKIP score for now.
				// go to score element
//				xmlr.nextTag();
				//get the inparalog score
//				Double scoreInparalog = Double.parseDouble(xmlr.getAttributeValue(1));

//				ONDEXRelation r = null;
                                seed.add(gene);
                                
/*				if(count < 2){
					seed[count] = gene;
					count++;
					if(seed[0] != null && seed[1] != null){
						r = graph.getFactory().createRelation(seed[0], seed[1], rtOrtholog, etIMPD);
					}	

				}
                                else{

					if(seed[0].getAttribute(anTaxId).getValue().equals(gene.getAttribute(anTaxId).getValue())){
						r = graph.getFactory().createRelation(seed[0], gene, rtParalog, etIMPD);
					}else{
						r = graph.getFactory().createRelation(seed[1], gene, rtParalog, etIMPD);
					}
				}

				if(r != null){
					r.createAttribute(anConf, scoreInparalog, false);
				}*/

			}
			//go to geneRef
			xmlr.nextTag();
		}
                
            // Create "ortho" relations for all concepts in "seed" ArrayList.
            for (int i=0; i< seed.size(); i++) {
                 ONDEXConcept seed1= seed.get(i);
                 for (int j=i+1; j< seed.size(); j++) {
                      ONDEXConcept seed2= seed.get(j);
		      ONDEXRelation r = graph.getFactory().createRelation(seed1, seed2, rtOrtholog, etIMPD);
                     }
                }

            seed.clear(); // empty the list of ortholog concepts
                
            // Create "para" relations for all paralog concepts in "seed" ArrayList.
            // TO DO
                
            // empty the list of paralog concepts
            // TO DO
	}
	
	/**
	 * Mapping of database names to DataSource ids used in the Ondex MetaData
	 * 
	 * @param name
	 * @return
	 */
	private String mapDBtoOndexmetaData(String name){
		
		String cvId = name;
		
		if(name.equalsIgnoreCase("Gramene") || name.equalsIgnoreCase("VITVI")){
			cvId = "ENSEMBL";
		}
                // If "ARATH" (a. thaliana), cvId= "TAIR"
                else if(name.equalsIgnoreCase("ARATH")){
			cvId = "TAIR";
		}
                // If "POTRI" (poplar), cvId= "JCI"
                else if(name.equalsIgnoreCase("PHYTOZOME_POTRI")){
			cvId = "PHYTOZOME";
		}
                else if(name.equalsIgnoreCase("PHYTOZOME_SAPUR")){
			cvId = "PHYTOZOME";
		}
		
		return cvId;
	}

}
