package net.sourceforge.ondex.parser.uniprot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import static junit.framework.Assert.assertTrue;
import net.sourceforge.ondex.parser.uniprot.Parser;
import net.sourceforge.ondex.ONDEXPluginArguments;
import net.sourceforge.ondex.args.FileArgumentDefinition;
import net.sourceforge.ondex.core.*;
import net.sourceforge.ondex.core.memory.MemoryONDEXGraph;
import net.sourceforge.ondex.export.oxl.Export;
import org.junit.Test;

/**
 * To test the Uniprot XML parser code.
 * @author Ajit Singh
 * @version 11/02/16
 */
public class UniprotParserTest {

    @Test
    public void testUniportParser() throws Throwable {
        // Using XML test file located under src/test/resources/.
        ClassLoader classLoader= getClass().getClassLoader();
        // example XML file to Test.
	File testFile= new File(classLoader.getResource("disease_small.xml").getFile());

        // Import the test file using the Uniprot XML Parser from the Ondex API.
        System.out.println("Test using example Uniprot XML file: "+ testFile.getName() +"\n Path: "+ testFile.getPath());
	ONDEXGraph graph= new MemoryONDEXGraph("test");

        Parser uniprot_parser= new Parser(); // Uniprot Parser.

        ONDEXPluginArguments pa= new ONDEXPluginArguments(uniprot_parser.getArgumentDefinitions());
        pa.setOption(FileArgumentDefinition.INPUT_FILE, testFile.getAbsolutePath());

        uniprot_parser.setArguments(pa);
	uniprot_parser.setONDEXGraph(graph);
	System.out.println("Ondex test Graph Loaded Into Memory");

        // Now, parse the given input (.xml) file from Uniprot to a new 'graph' object.
        System.out.println("Running Uniprot XML Parser...");
        uniprot_parser.start();

        System.out.println("Evaluating retrieved ONDEXGraph object...");

        // Check retrieved 'graph' contents.
        ONDEXGraph out_graph= uniprot_parser.getGraph();

        int conceptsCount= out_graph.getConcepts().size();
        int relationsCount= out_graph.getRelations().size();

        // tests
//        assertNotNull(graph);
        assertTrue(conceptsCount > 0);
        assertTrue(relationsCount > 0);
        System.out.println("conceptsCount= "+ conceptsCount +", relationsCount= "+ relationsCount);
        System.out.println("Disease concepts: ");
        for(ONDEXConcept con : out_graph.getConcepts()) {
            if(con.getOfType().getFullname().equals("disease")) {
               int conId= con.getId(); // concept ID.
               System.out.print("Concept ID: "+ conId +", name: "+ con.getConceptName().getName());
               // test
               assertTrue(conId > 0);
              }
           }
/*
        // Now, Export the graph as JSON using JSON Exporter plugin.
        Export oxlExport= new Export(); // Export.

        // output file (with timestamped filename) to get exported network graph data in JSON format.
        String outFileName= "uniprotParserTestGraph_"+ new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) +".oxl";
        File outFile= new File(System.getProperty("java.io.tmpdir") + File.separator + outFileName);

        ONDEXPluginArguments ea= new ONDEXPluginArguments(oxlExport.getArgumentDefinitions());
        ea.setOption(FileArgumentDefinition.EXPORT_FILE, outFile.getAbsolutePath());

        System.out.println("OXL Export file: "+ ea.getOptions().get(FileArgumentDefinition.EXPORT_FILE));
        // test
        assertTrue(ea.getOptions().get(FileArgumentDefinition.EXPORT_FILE).contains(outFile.getAbsolutePath()));

        oxlExport.setArguments(ea);
        oxlExport.setONDEXGraph(graph);

        System.out.println("Running OXL Exporter plugin... \n");

        // Export the contents of the 'graph' object as multiple JSON objects to an output file ('jsonOutputFile').
        oxlExport.start();

        // delete on exit
//        outFile.deleteOnExit();

*/
    }

}
