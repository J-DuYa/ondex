package net.sourceforge.ondex.parser.fastagff;

import net.sourceforge.ondex.parser.fastagff.Parser;
import net.sourceforge.ondex.parser.fastagff.ArgumentNames;
import net.sourceforge.ondex.parser.fastagff.MetaData;
import java.io.File;
import java.util.Arrays;
import static junit.framework.Assert.assertTrue;
import net.sourceforge.ondex.ONDEXPluginArguments;
import net.sourceforge.ondex.core.*;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.memory.MemoryONDEXGraph;
import org.junit.Test;

/**
 * To test the fastagff parser for gff3
 * @author singha
 * @version 02/08/2016
 */
public class fastagffTest {
    
    @Test
    public void testParser() throws Throwable {

        ClassLoader classLoader= getClass().getClassLoader();
        // example files to test with
	File gffTestFile= new File(classLoader.getResource("genes.gff3").getFile());
	File fastaTestFile= new File(classLoader.getResource("pep.all.fa").getFile());

        // Import the OXL test file using the OXL Parser from the Ondex API.
        System.out.println("Test using example gff3 file: "+ gffTestFile.getName() +", fasta file: "+ fastaTestFile.getName());

        Parser fg_parser= new Parser(); // fasta-gff Parser.
        ONDEXPluginArguments p_args= new ONDEXPluginArguments(fg_parser.getArgumentDefinitions());
        p_args.setOption(ArgumentNames.GFF_ARG, gffTestFile.getAbsolutePath());
        p_args.setOption(ArgumentNames.FASTA_ARG, fastaTestFile.getAbsolutePath());
        p_args.setOption(ArgumentNames.TAXID_ARG, "3702");
        p_args.setOption(ArgumentNames.XREF_ARG, "TAIR");
        p_args.setOption(ArgumentNames.DATASOURCE_ARG, "ENSEMBL");
        p_args.setOption(ArgumentNames.MAPPING_GENE, 0);
        p_args.setOption(ArgumentNames.MAPPING_PROTEIN, 1);

        // Creating a MemoryONDEXGraph object.
        ONDEXGraph graph= new MemoryONDEXGraph("test");
        // Create EvidenceType and ConceptClass gene for test case.
        graph.getMetaData().getFactory().createEvidenceType(MetaData.ET_IMPD, MetaData.ET_IMPD); // evidenceType
        graph.getMetaData().getFactory().createConceptClass("Gene");

        fg_parser.setONDEXGraph(graph);
        fg_parser.setArguments(p_args);
        System.out.println("Running fasta-gff3 Parser...");
        fg_parser.start(); // run the parser

        // Test the graph object created by the parser.
        System.out.println("Evaluating retrieved ONDEXGraph object...");
        //
        
        // Check retrieved 'graph' contents.
        int conceptsCount= graph.getConcepts().size();
        int relationsCount= graph.getRelations().size();
        // tests
//        assertNotNull(graph);
        assertTrue(conceptsCount > 0);
        assertTrue(relationsCount > 0);
        System.out.println("conceptsCount= "+ conceptsCount +" , relationsCount= "+ relationsCount);
        System.out.println("Gene concepts: ");
        for(ONDEXConcept con : graph.getConcepts()) {
            if(con.getOfType().getFullname().equals(MetaData.CC_GENE)) {
               System.out.println("ID: "+ con.getId() +", description: "+ con.getDescription() +", conceptNames: "+ Arrays.toString(con.getConceptNames().toArray()));
              }
           }
    }

}
