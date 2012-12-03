package net.sourceforge.ondex.ws_tester.transformer;

import java.io.IOException;
import java.net.MalformedURLException;
import net.sourceforge.ondex.webservice.client.transformer.WebserviceException_Exception;
import net.sourceforge.ondex.ws_tester.mapping.TmbasedTester;
import net.sourceforge.ondex.ws_tester.parser.MedlineParserTester;

/**
 * Program for creating plugin wrappers.
 *
 * Designed only to run when Christian Server is running.
 * 
 * @author BrennincC based on stuff by taubertj
 */
public class CooccurrenceTester {//extends WS_Tester_Transformer{
/** //plugin changed so test code no longer compiles too lazy to change
    public CooccurrenceTester() throws MalformedURLException
    {
        super("OXL tests");
 	}

    public long transformFile(long graphid) throws WebserviceException_Exception,
            net.sourceforge.ondex.webservice.client.WebserviceException_Exception
    {

        String result;

        result = transformer_service.coocurrenceTransformer(
                "Pub_In", //@WebParam(name = "RelationType")java.lang.String RelationType,
                "Comp,Reactyion", //@WebParam(name = "ConceptClass")java.lang.String ConceptClass,
                "Publication", //@WebParam(name = "QualifierClass")java.lang.String QualifierClass,
                graphid); //@WebParam(name = "graphId") Long graphId)

        System.out.println(result);
        //graphInfo(graphid, true);
        graphInfo(graphid, false);
        return graphid;
 	}

    public void testTransformer() throws MalformedURLException,
            net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, 
            net.sourceforge.ondex.webservice.client.WebserviceException_Exception, IOException,
            net.sourceforge.ondex.webservice.client.mapping.WebserviceException_Exception, WebserviceException_Exception
    {
        MedlineParserTester medlineParserTester = new MedlineParserTester();
        long graphid =  medlineParserTester.parseFile();

        TmbasedTester tmbasedTester = new TmbasedTester();
        tmbasedTester.mapFile(graphid);

        transformFile(graphid);
        api_service.deleteGraph(graphid);
        System.out.println("done Medline TBBased mapping tested");
 	}

    public static void main(String[] args) throws MalformedURLException,
            net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception,
            WebserviceException_Exception, IOException,
            net.sourceforge.ondex.webservice.client.mapping.WebserviceException_Exception,
            net.sourceforge.ondex.webservice.client.WebserviceException_Exception {
        CooccurrenceTester medlineTester = new CooccurrenceTester();
        medlineTester.testTransformer();
    }
**/
}
