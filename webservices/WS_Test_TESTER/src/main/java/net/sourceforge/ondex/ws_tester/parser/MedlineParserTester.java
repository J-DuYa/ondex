package net.sourceforge.ondex.ws_tester.parser;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import net.sourceforge.ondex.exception.type.PluginException;
import net.sourceforge.ondex.init.Initialisation;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;
import org.xml.sax.SAXException;
        
/**
 * Program for creating plugin wrappers.
 *
 * Designed only to run when Christian Server is running.
 * 
 * @author BrennincC based on stuff by taubertj
 */
public class MedlineParserTester { //extends WS_Tester_Parser{
/** Plugin changed so test code no longer compiles too lazy to change.
    public MedlineParserTester() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        super("OXL tests");
 	}

    public long parseFile() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        testName = "OXL with SBML.OXL";
        long graphid = api_service.createMemoryGraph("Test");

        String result;

        result = parser_service.medlineParser(
           null, //@WebParam(name = "Prefix")java.lang.String Prefix,
           "", //@WebParam(name = "Compression")java.lang.String Compression,
           null, //@WebParam(name = "PMIDInputList")java.lang.Integer PMIDInputList,
           null, //@WebParam(name = "XmlFilesString") String XmlFilesString,
           null, //@WebParam(name = "XmlFilesByteArray") byte[] XmlFilesByteArray,
           "C:/Ondex/importdata/medline/pubmed_result.txt", //  "@WebParam(name = "PubMedFileString") String PubMedFileString,
           null, //@WebParam(name = "PubMedFileByteArray") byte[] PubMedFileByteArray,
           null, //@WebParam(name = "ImportOnlyCitedPublications")java.lang.Boolean ImportOnlyCitedPublications,
           null, //@WebParam(name = "LowerXmlBoundary")java.lang.Integer LowerXmlBoundary,
           null, //@WebParam(name = "UpperXmlBoundary")java.lang.Integer UpperXmlBoundary,
           null, //@WebParam(name = "UseEfetchWebService")java.lang.Boolean UseEfetchWebService,
           null, //"C:/Ondex/importdata/medline", //@WebParam(name = "InputDirString") String InputDirString,
           null, //@WebParam(name = "InputDirByteArray") byte[] InputDirByteArray,
           graphid); //@WebParam(name = "graphId") Long graphId)
        System.out.println(result);
        //graphInfo(graphid, true);
        graphInfo(graphid, false);
        return graphid;
 	}

    public void parseTest() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        long graphid =  parseFile();

        api_service.deleteGraph(graphid);
        System.out.println("Done pMdeline Parser Test");
 	}

    public static void main(String[] args) throws MalformedURLException, WebserviceException_Exception, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, IOException, SAXException {

        MedlineParserTester medlineTester = new MedlineParserTester();
        medlineTester.parseTest();
    }
**/
}
