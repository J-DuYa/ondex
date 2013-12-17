package net.sourceforge.ondex.ws_tester.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;
import net.sourceforge.ondex.webservice.client.parser.ArrayOfString;
import net.sourceforge.ondex.ws_tester.inputs.ParserArrayOfString;
        
/**
 * Program for creating plugin wrappers.
 *
 * Designed only to run when Christian Server is running.
 * 
 * @author BrennincC based on stuff by taubertj
 */
public class FastaTester extends WS_Tester_Parser{

    public FastaTester() throws MalformedURLException
    {
        super("Fasta");
 	}

    public void testShort() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        testName = "Festa with short.fasta";
        long graphId = api_service.createMemoryGraph("Test");
        String InputFileString = INPUT_DATA + "short.fasta";
        byte[] InputFileByteArray = null;
        java.lang.String FastaFileType = "simple";
        java.lang.String TaxId = "40559";
        java.lang.String CC = "Protein";
        java.lang.String CV = null; //"unknown";
        ArrayOfString POS_TO_ACCESSION = null; //new ParserArrayOfString();
        java.lang.String SeqType = "AA";
        java.lang.String Separator = null;
        java.lang.String AccessionRegEx = null;
        String parseResult = parser_service.fastaParser(InputFileString, InputFileByteArray, FastaFileType, TaxId, CC, CV, POS_TO_ACCESSION, SeqType, Separator, AccessionRegEx, graphId);
        System.out.println(parseResult);
        //graphInfo(graphid, true);
        graphInfo(graphId, false);
        writeOXL(graphId, "fasta.oxl");
        api_service.deleteGraph(graphId);
        System.out.println("Done Fasta Test");
 	}

    public static void main(String[] args) throws MalformedURLException, WebserviceException_Exception, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, IOException {
        FastaTester testMain = new FastaTester();
        testMain.testShort();
    }

}
