package net.sourceforge.ondex.ws_tester.parserUsingJob;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.ondex.webservice.client.parserUsingJob.JobException_Exception;
import net.sourceforge.ondex.ws_tester.parserUsingJob.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;

/**
 * Program for creating plugin wrappers.
 *
 * Designed only to run when Christian Server is running.
 * 
 * @author BrennincC based on stuff by taubertj
 */
public class OXL_Tester extends WS_Tester_ParserUsingJob{

    public OXL_Tester() throws MalformedURLException
    {
        super("OXL tests");
 	}

    public void testAracyc_subset() throws WebserviceException_Exception, net.sourceforge.ondex.webservice.client.parserUsingJob.WebserviceException_Exception, IOException
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

    public void testPhi_base()  throws WebserviceException_Exception, net.sourceforge.ondex.webservice.client.parserUsingJob.WebserviceException_Exception, IOException
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

    public void testPoplar_subset() throws WebserviceException_Exception, net.sourceforge.ondex.webservice.client.parserUsingJob.WebserviceException_Exception, IOException
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

    public void testpoplar_exercise()  throws WebserviceException_Exception, net.sourceforge.ondex.webservice.client.parserUsingJob.WebserviceException_Exception, IOException
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

    public synchronized void testSBML_return() throws WebserviceException_Exception, net.sourceforge.ondex.webservice.client.parserUsingJob.WebserviceException_Exception, IOException, JobException_Exception
    {
        testName = "OXL with SBML.OXL";
        long graphid = api_service.createMemoryGraph("Test");
        String oxlFile = RESULT_DATA + "SBML.oxl";
        String jobId = parser_service.oxlParser(null, oxlFile, null, graphid);
        System.out.println(jobId + new Date());
        while (parser_service.checkStatus(jobId).equals("PENDING " + new Date().getTime())){
            try {
                this.wait(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("RUNNING "  + new Date().getTime());
        while (parser_service.checkStatus(jobId).equals("RUNNING")){
            try {
                this.wait(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println(parser_service.checkStatus(jobId) + " " + new Date().getTime());
        //graphInfo(graphid, true);
        graphInfo(graphid, false);
        writeOXL(graphid, "SBML2.oxl");
        api_service.deleteGraph(graphid);
        System.out.println("Done poplar_exercise.xml.gz");
 	}

    public static void main(String[] args)  throws WebserviceException_Exception, net.sourceforge.ondex.webservice.client.parserUsingJob.WebserviceException_Exception, IOException, JobException_Exception
    {
        OXL_Tester oxlTester = new OXL_Tester();
        //oxlTester.testAracyc_subset();
        //oxlTester.testPoplar_subset();
        //oxlTester.testpoplar_exercise();
        //oxlTester.testPhi_base();
        oxlTester.testSBML_return();
    }

}
