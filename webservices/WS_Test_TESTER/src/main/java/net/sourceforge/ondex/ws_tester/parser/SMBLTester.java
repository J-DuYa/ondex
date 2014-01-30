package net.sourceforge.ondex.ws_tester.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;
//import net.sourceforge.ondex.webservice.client.parser.ArrayOfInputStream;
import net.sourceforge.ondex.webservice.client.parser.ArrayOfString;
        
/**
 * Program for creating plugin wrappers.
 *
 * Designed only to run when Christian Server is running.
 * 
 * @author BrennincC based on stuff by taubertj
 */
public class SMBLTester extends WS_Tester_Parser{

    public SMBLTester() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        super("SBML2a with yeast_4.02.xml");
        long graphid = api_service.createMemoryGraph("Test");
        String MataDataString = INPUT_DATA  + "metadata.tsv";
        byte[] MetaDataByteArray = null;
        String InputFileString = "http://www.comp-sys-bio.org/yeastnet/v4/yeast_4.02.xml";
        byte[] InputFileByteArray = null;
  //      ArrayOfInputStream Data = null; //Error to be removed
        java.lang.String IMPDFullName = null;
        java.lang.String IMPDDescription = null;
        java.lang.String DataSource = null;

//        parser_service.sbml2AParser(MataDataString, MetaDataByteArray, InputFileString, InputFileByteArray, Data,
//                IMPDFullName, IMPDDescription, DataSource, graphid);
         //graphInfo(graphid, true);
        concpetClassess = 8;
        cvs = 12;
        relationTypes =  10;
        evidenceTypes = 1;
        relations = 11721;
        concepts = 4538;
        graphInfo(graphid, false);
        writeOXL(graphid, "SBML.oxl");
        api_service.deleteGraph(graphid);
        System.out.println("Done SBML Test");
 	}

	public static void main(String[] args) throws MalformedURLException, WebserviceException_Exception, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, IOException {
        SMBLTester testMain = new SMBLTester();
    }

}
