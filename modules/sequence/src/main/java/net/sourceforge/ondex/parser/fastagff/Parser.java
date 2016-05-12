package net.sourceforge.ondex.parser.fastagff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.ondex.annotations.Authors;
import net.sourceforge.ondex.annotations.Custodians;
import net.sourceforge.ondex.annotations.DataURL;
import net.sourceforge.ondex.annotations.Status;
import net.sourceforge.ondex.annotations.StatusType;
import net.sourceforge.ondex.args.ArgumentDefinition;
import net.sourceforge.ondex.args.FileArgumentDefinition;
import net.sourceforge.ondex.args.IntegerRangeArgumentDefinition;
import net.sourceforge.ondex.args.StringArgumentDefinition;
import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.ConceptClass;
import net.sourceforge.ondex.core.DataSource;
import net.sourceforge.ondex.core.EvidenceType;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXGraphMetaData;
import net.sourceforge.ondex.core.RelationType;
import net.sourceforge.ondex.parser.ONDEXParser;


@Status(description = "Parser for Genes and Proteins using GFF3 (for genes), FASTA (for proteins) and mapping files [optional] (Martin Castellote)", status = StatusType.EXPERIMENTAL)
@Authors(authors = {"Martin Castellote"}, emails = {"castellotemartin@yahoo.com.ar"})
// @DatabaseTarget(name = "PGSC", description = "PGSC files", version = "3.4", url = "http://solanaceae.plantbiology.msu.edu/pgsc_download.shtml")
@DataURL(name = "GFF3, FASTA and Mapping",
        description = "Gene annotation for v2.1.11 Pseudomolecules in GFF3 format \n Amino acid sequences corresponding to all gene coding sequences \n Linking file between gene ID, and peptide ID",
        urls = {"http://solanaceae.plantbiology.msu.edu/data/PGSC_DM_v3_2.1.11_pseudomolecule_annotation.gff.zip",
                "http://solanaceae.plantbiology.msu.edu/data/PGSC_DM_v3.4_pep.fasta.zip",
                "http://solanaceae.plantbiology.msu.edu/data/PGSC_DM_v3.4_g2t2c2p2func.txt.zip"})
@Custodians(custodians = {"Keywan Hassani-pak"}, emails = {"keywan at users.sourceforge.net"})

/**
 * Parser for GFF3 and FASTA
 * 
 * @author mcastellote
 *
 */
public class Parser extends ONDEXParser {

	@Override
	public String getId() {
		return "fastagff";
	}

	@Override
	public String getName() {
		return "FASTA and GFF3";
	}

	@Override
	public String getVersion() {
		return "15/08/2012";
	}

	@Override
	public ArgumentDefinition<?>[] getArgumentDefinitions() {
		return new ArgumentDefinition<?>[]{
				new FileArgumentDefinition(ArgumentNames.GFF_ARG, "Absolute path to a GFF3 input file with 9 columns. It uses 1)chromosome id, 4)start, 5)end and 9)gene id and gene description i.e. \"ID=PGSC0003DMG400030251;Name=\"\"Conserved gene of unknown function\"\"\" ", true, true, false, false),
				new FileArgumentDefinition(ArgumentNames.FASTA_ARG, "Absolute path to a FASTA input file with protein secuences", true, true, false, false),
				new FileArgumentDefinition(ArgumentNames.MAPPING_ARG, "Absolute path to a mapping input file which provides mapping relationsship between the GFF and the FASTA file. It should contain two columns: 2) gene id and 4) protein id", false, true, false, false),
				new StringArgumentDefinition(ArgumentNames.TAXID_ARG, ArgumentNames.TAXID_ARG_DESC, true, null, false),
				new StringArgumentDefinition(ArgumentNames.XREF_ARG, ArgumentNames.XREF_ARG_DESC, true, null, false),
				new StringArgumentDefinition(ArgumentNames.DATASOURCE_ARG, ArgumentNames.DATASOURCE_ARG_DESC, true, null, false),
				new IntegerRangeArgumentDefinition(ArgumentNames.MAPPING_GENE, ArgumentNames.MAPPING_GENE_DESC, true, 1, 0, 10),
				new IntegerRangeArgumentDefinition(ArgumentNames.MAPPING_PROTEIN, ArgumentNames.MAPPING_PROTEIN_DESC, true, 3, 0, 10)
		};

	}

	@Override
	public void start() throws Exception {

		//get the metadata
		ONDEXGraphMetaData md = graph.getMetaData();

		//stores the metadata related with concept classes, relation types, attribute names, evidence type and data source
		ConceptClass ccGene = md.getConceptClass(MetaData.CC_GENE);
		ConceptClass ccProtein = md.getConceptClass(MetaData.CC_PROTEIN);
		RelationType rtEncodes = md.getRelationType(MetaData.RT_ENCODES);
		AttributeName anChromosome = md.getAttributeName(MetaData.CHROMOSOME);
		AttributeName anLocation = md.getFactory().createAttributeName("Location", String.class);
		AttributeName anBegin = md.getAttributeName(MetaData.AN_BEGIN);
		AttributeName anEnd = md.getAttributeName(MetaData.AN_END);
		AttributeName anTaxid = md.getAttributeName(MetaData.AN_TAXID);
		AttributeName anSecuenceAA = md.getAttributeName(MetaData.AN_AA);
		EvidenceType etIMPD = md.getEvidenceType(MetaData.ET_IMPD);
		DataSource dsConcept = null;		
		DataSource dsAccession = null;		

		//saves taxid and data source name into variables
		String taxid = (String) args.getUniqueValue(ArgumentNames.TAXID_ARG);
		String xref = (String) args.getUniqueValue(ArgumentNames.XREF_ARG);
		String dsName = (String) args.getUniqueValue(ArgumentNames.DATASOURCE_ARG);


		if(md.getDataSource(dsName) != null){
			dsConcept = md.getDataSource(dsName);
		}else{
			dsConcept = md.createDataSource(dsName, dsName, dsName);
			System.out.println("New data source object was created: "+ dsName);
		}
		
		if(md.getDataSource(xref) != null){
			dsAccession = md.getDataSource(xref);
		}else{
			dsAccession = md.createDataSource(xref, xref, xref);
			System.out.println("New data source object was created: "+ xref);
		}
		
		//creates hashmaps between ondex and concept classes
		HashMap<String,Integer> ondex2gene = new HashMap<String,Integer>();
		HashMap<String,Integer> ondex2protein = new HashMap<String,Integer>();

		
		//parse GFF lines and create Gene concepts
		//----------------------------------------
		String GFFFilePath = (String) args.getUniqueValue(ArgumentNames.GFF_ARG);
		File gffFile = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {

			gffFile = new File (GFFFilePath);
			fr = new FileReader (gffFile);
			br = new BufferedReader(fr);

			//Analyze the GFF file
			String row;
			int missingChr = 0;
			while((row = br.readLine())!=null){
				String[] splited = row.split("\t");
				
				if(splited.length < 2 || !(splited[2].toLowerCase().contains("gene"))){
					continue;
				}
				
				
				String geneId = "";
				String geneDescription = "";
				if(splited[8].contains(";")){
					String[] col =   splited[8].split(";");
					geneId = col[0].split("=")[1].toUpperCase();
					geneDescription = col[1].split("=")[1].toUpperCase();
				}else{
					geneId = splited[8].split("=")[1].toUpperCase();
					geneDescription = splited[8].split("=")[1].toUpperCase();
				}
				
				//Standarize the name of the chromosome
				String geneLocation = splited[0];
				
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(splited[0]);

                List<String> values = new ArrayList<String>();
                
                while(m.find()){
                       values.add(m.group());
                }
                
                String geneChrName = "0";
                
                if (values.size() == 0){
                       missingChr++;
                }
                else {
                	//everything which is higher tan 99 or equals NA is not a chromosome
                	 if ((values.get(0).length() > 2) || values.equals("NA")){
                         geneChrName = "0";
                		 missingChr++;
                     }
                	 else {
                		 geneChrName = values.get(0);
                	 }
                }

				Integer geneChr = Integer.parseInt(geneChrName);  
				Integer geneBegin = Integer.parseInt(splited[3]);
				Integer geneEnd = Integer.parseInt(splited[4]);

				ONDEXConcept c1 = graph.getFactory().createConcept(geneId, "", geneDescription, dsConcept, ccGene, etIMPD);
				c1.createConceptName(geneId, true);
				c1.createConceptAccession(geneId, dsAccession, false);
				c1.createAttribute(anTaxid, taxid, false);
				c1.createAttribute(anChromosome, geneChr, false);
				c1.createAttribute(anLocation, geneLocation, false);
				c1.createAttribute(anBegin, geneBegin, false);
				c1.createAttribute(anEnd, geneEnd, false);
				ondex2gene.put(geneId, c1.getId());
			}
			System.out.println("Amount of missing chromosomes: "+missingChr);
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{

			try{                    
				if( null != fr ){   
					fr.close();     
				}                  
			}catch (Exception e2){ 
				e2.printStackTrace();
			}
		}

		
		//parse FASTA and create protein concepts
		//---------------------------------------
		String FASTAFilePath = (String) args.getUniqueValue(ArgumentNames.FASTA_ARG);
		File FASTAFile = null;
		FileReader FASTAfr = null;
		BufferedReader FASTAbr = null;

		try {

			FASTAFile = new File (FASTAFilePath);
			FASTAfr = new FileReader (FASTAFile);
			FASTAbr = new BufferedReader(FASTAfr);

			//Analyze the GFF file
			String FASTArow;
			String secuenceName = "";
			String secuence = "";
			while((FASTArow = FASTAbr.readLine())!=null){
				if(FASTArow.isEmpty())
					continue;
				if(FASTArow.substring(0, 1).equals(">")){
					if(!secuenceName.isEmpty()){
						//creates protein concept when find the next > symbol
						ONDEXConcept c2 = graph.getFactory().createConcept(secuenceName, "", "", dsConcept, ccProtein, etIMPD);
						c2.createConceptName(secuenceName, true);
						c2.createConceptAccession(secuenceName, dsAccession, false);	    	     		 
						c2.createAttribute(anSecuenceAA, secuence, false);
						c2.createAttribute(anTaxid, taxid, false);
						ondex2protein.put(secuenceName, c2.getId());
						//saves the new secuence name and clears de secuence	    	        			 
						secuenceName = FASTArow.split("\\s|\\|")[0].substring(1).toUpperCase();
						secuence = "";
					} else {
						//saves the first secuence name
						secuenceName = FASTArow.split("\\s|\\|")[0].substring(1).toUpperCase();	        			 	        			 
					}
				} else {
					//concate the secuence to the current secuence name
					secuence = secuence.concat(FASTArow);	        		 
				}
			}
			//creates the last protein concept			 
			ONDEXConcept c2 = graph.getFactory().createConcept(secuenceName, "", "", dsConcept, ccProtein, etIMPD);
			c2.createConceptName(secuenceName, true);
			c2.createConceptAccession(secuenceName, dsConcept, false);	    	     		 
			c2.createAttribute(anSecuenceAA, secuence, false);
			ondex2protein.put(secuenceName, c2.getId());
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{

			try{                    
				if( null != fr ){   
					FASTAfr.close();     
				}                  
			}catch (Exception e2){ 
				e2.printStackTrace();
			}
		}

		//parse mapping file and create relations
		//---------------------------------------
		String MappingFilePath = null;
		MappingFilePath = (String) args.getUniqueValue(ArgumentNames.MAPPING_ARG);
		if(!(MappingFilePath == null)){			
			int geneColumn = Integer.parseInt(args.getUniqueValue(ArgumentNames.MAPPING_GENE).toString());
			int proteinColumn = Integer.parseInt(args.getUniqueValue(ArgumentNames.MAPPING_PROTEIN).toString());
			File MappingFile = null;
			FileReader Mappingfr = null;
			BufferedReader Mappingbr = null;
	
			try {
				MappingFile = new File (MappingFilePath);
				Mappingfr = new FileReader (MappingFile);
				Mappingbr = new BufferedReader(Mappingfr);
	
				//Analyze the GFF file 
				Integer missingGenes = 0;     //TODO Check if we have a newer version of gff file!!!!!
				Integer missingProteins = 0;
				String Mappingrow;
				while((Mappingrow = Mappingbr.readLine())!=null){
					String[] splited = Mappingrow.split("\t");
	
					String geneId = splited[geneColumn];
					String proteinId = splited[proteinColumn];
					
					if (ondex2protein.get(proteinId) == null){
						missingProteins++;
						continue;
					}
					if (ondex2gene.get(geneId) == null){
						missingGenes++;	        		
						continue;
					}
					
					int ondexGeneId = ondex2gene.get(geneId);
					int ondexProteinId = ondex2protein.get(proteinId);
	
					ONDEXConcept geneCocnept = graph.getConcept(ondexGeneId);
					ONDEXConcept proteinCocnept = graph.getConcept(ondexProteinId);
	
					graph.getFactory().createRelation(geneCocnept, proteinCocnept, rtEncodes, etIMPD);			

				}
				System.out.println("Amount of missing genes: "+missingGenes);
				System.out.println("Amount of missing proteins: "+missingProteins);			
			}
			catch(Exception e){
				e.printStackTrace();
			}finally{
	
				try{                    
					if( null != fr ){   
						Mappingfr.close();     
					}                  
				}catch (Exception e2){ 
					e2.printStackTrace();
				}
			}
		}else{ 
			//If mapping file is not provided
			Integer missingGenes = 0;   
			Integer missingProteins = 0;
			for(String pAcc : ondex2protein.keySet()){
				
				int ondexGeneId;
				int ondexProteinId;
				
				if((ondex2gene.get(pAcc) != null) || (ondex2gene.get(pAcc.split("\\.")[0]) != null)){
					ondexProteinId = ondex2protein.get(pAcc);
					if (ondex2gene.containsKey(pAcc)) {
						ondexGeneId = ondex2gene.get(pAcc);
					}
					else {
						ondexGeneId = ondex2gene.get(pAcc.split("\\.")[0]);
					}
				}
				else {
					missingGenes++;	        		
					continue;
				}

				ONDEXConcept geneCocnept = graph.getConcept(ondexGeneId);
				ONDEXConcept proteinCocnept = graph.getConcept(ondexProteinId);

				graph.getFactory().createRelation(geneCocnept, proteinCocnept, rtEncodes, etIMPD);								
			}
			System.out.println("Mapping File Not Provided");
			System.out.println("Amount of missing genes: "+missingGenes);
			System.out.println("Amount of missing proteins: "+missingProteins);	
		}
	}

	@Override
	public String[] requiresValidators() {
		return new String[0];
	}
}
