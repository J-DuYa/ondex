package net.sourceforge.ondex.ws_tester.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;
        
/**
 * Program for creating plugin wrappers.
 *
 * Designed only to run when Christian Server is running.
 * 
 * @author BrennincC based on stuff by taubertj
 */
public class OXL_Tester extends WS_Tester_Parser{

    public OXL_Tester() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        super("OXL tests");
 	}

    public void testAracyc_subset() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        testName = "OXL with aracyc_subset.xml.gz";
        long graphid = api_service.createMemoryGraph("Test");
        String oxlFile = INPUT_DATA + "aracyc_subset.xml.gz";
        parser_service.oxlParser(null, oxlFile, null, graphid);
        //graphInfo(graphid, true);
        concpetClassess = 141;
        cvs = 136;
        relationTypes = 139;
        evidenceTypes = 46;
        relations = 132;
        concepts = 100;
        graphInfo(graphid, false);
        writeOXL(graphid, "aracyc_subset.oxl");
        api_service.deleteGraph(graphid);
        System.out.println("Done aracyc_subset.xml.gz Test");
 	}

    public void testPhi_base() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        testName = "OXL with phi_base_3_1";
        long graphid = api_service.createMemoryGraph("Test");
        String oxlFile = INPUT_DATA + "phi_base_3_1.xml";
        parser_service.oxlParser(null, oxlFile, null, graphid);
        //graphInfo(graphid, true);
        concpetClassess = 7;
        cvs = 6;
        relationTypes = 5;
        evidenceTypes = 12;
        relations = 10281;
        concepts = 3469;
        graphInfo(graphid, false);
        writeOXL(graphid, "phi_base.oxl");
        api_service.deleteGraph(graphid);
        System.out.println("Done phi_base_3_1 Test");
 	}

    public void testPoplar_subset() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        testName = "OXL with poplar_subset.xml.gz";
        long graphid = api_service.createMemoryGraph("Test");
        String oxlFile = INPUT_DATA + "poplar_subset.xml.gz";
        parser_service.oxlParser(null, oxlFile, null, graphid);
        //graphInfo(graphid, true);
        concpetClassess = 141;
        cvs = 144;
        relationTypes = 139;
        evidenceTypes = 46;
        relations = 367;
        concepts = 5972;
        graphInfo(graphid, false);
        writeOXL(graphid, "poplar_subset.oxl");
        api_service.deleteGraph(graphid);
        System.out.println("Done poplar_subset.xml.gz");
 	}

    public void testpoplar_exercise() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        testName = "OXL with poplar_exercise.xml.gz";
        long graphid = api_service.createMemoryGraph("Test");
        String oxlFile = INPUT_DATA + "poplar_exercise.xml.gz";
        parser_service.oxlParser(null, oxlFile, null, graphid);
        //graphInfo(graphid, true);
        concpetClassess = 142;
        cvs = 149;
        relationTypes = 139;
        evidenceTypes = 46;
        relations = 339;
        concepts = 319;
        graphInfo(graphid, false);
        writeOXL(graphid, "poplar_exercise.oxl");
        api_service.deleteGraph(graphid);
        System.out.println("Done poplar_exercise.xml.gz");
 	}

    public void testJochenFile() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        testName = "OXL with SBML.OXL";
        long graphid = api_service.createMemoryGraph("Test");
        String oxlFile = INPUT_DATA + "dip_eco.xml.gz";
        parser_service.oxlParser(null, oxlFile, null, graphid);

        //graphInfo(graphid, true);
        graphInfo(graphid, false);
        writeOXL(graphid, "dip_eco.xml");
        api_service.deleteGraph(graphid);
        System.out.println("Done dip_eco.xml.gz");
 	}

   public void testUrl_return() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        testName = "oxl from url";
        long graphid = api_service.createMemoryGraph("Test");
        String oxlFile = "http://rpc466.cs.man.ac.uk:8080/ondex/output/output1444365534294303986.xml";
        parser_service.oxlParser(null, oxlFile, null, graphid);

        //graphInfo(graphid, true);
        graphInfo(graphid, false);
        writeOXL(graphid, "test.oxl");
        api_service.deleteGraph(graphid);
        System.out.println("Done url return");
 	}

   public void testSBML_return() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        testName = "OXL with SBML.OXL";
        long graphid = api_service.createMemoryGraph("Test");
        String oxlFile = RESULT_DATA + "SBML.oxl";
        parser_service.oxlParser(null, oxlFile, null, graphid);

        //graphInfo(graphid, true);
        graphInfo(graphid, false);
        writeOXL(graphid, "SBML2.oxl");
        api_service.deleteGraph(graphid);
        System.out.println("Done poplar_exercise.xml.gz");
 	}



   public static void main(String[] args) throws MalformedURLException, WebserviceException_Exception, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, IOException {
       //String test = "rpc466.cs.man.ac.uk:8080/ondex/output\\output913919364722468026.xml";
      // test = test.replaceAll("\\\\", "/");
      // System.out.println (test);
        OXL_Tester oxlTester = new OXL_Tester();
          //oxlTester.testAracyc_subset();
          //oxlTester.testPoplar_subset();
          //oxlTester.testpoplar_exercise();
          //oxlTester.testPhi_base();
          //oxlTester.testSBML_return();
        oxlTester.testJochenFile();
        //oxlTester.testUrl_return();
    }

}
