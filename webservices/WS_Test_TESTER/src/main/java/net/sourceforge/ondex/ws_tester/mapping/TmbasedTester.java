package net.sourceforge.ondex.ws_tester.mapping;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import net.sourceforge.ondex.exception.type.PluginException;
import net.sourceforge.ondex.init.Initialisation;
import net.sourceforge.ondex.webservice.client.WebserviceException_Exception;
import net.sourceforge.ondex.webservice.client.mapping.ArrayOfString;
import net.sourceforge.ondex.ws_tester.inputs.MappingArrayOfString;
import net.sourceforge.ondex.ws_tester.parser.MedlineParserTester;
import org.xml.sax.SAXException;
        
/**
 * Program for creating plugin wrappers.
 *
 * Designed only to run when Christian Server is running.
 * 
 * @author BrennincC based on stuff by taubertj
 */
public class TmbasedTester extends WS_Tester_Mapping{

    public TmbasedTester() throws MalformedURLException, net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception, WebserviceException_Exception, IOException
    {
        super("OXL tests");
 	}

    public long mapFile(long graphid) throws net.sourceforge.ondex.webservice.client.mapping.WebserviceException_Exception, WebserviceException_Exception
    {

        String[] cc = {"Comp", "Reaction"};
        ArrayOfString ConceptClass = new MappingArrayOfString(cc);
        String result;

        result = mapping_service.tmbasedMapping(
                ConceptClass  , //@WebParam(name = "ConceptClass") java.lang.String[] ConceptClass,
                null, //@WebParam(name = "OnlyPreferredNames")java.lang.Boolean OnlyPreferredNames,
                null, //@WebParam(name = "UseFullText")java.lang.Boolean UseFullText,
                "exact", //@WebParam(name = "Search")java.lang.String Search,
                null, //@WebParam(name = "Filter")java.lang.String Filter,
                graphid); //@WebParam(name = "graphId") Long graphId)

        System.out.println(result);
        //graphInfo(graphid, true);
        graphInfo(graphid, false);
        return graphid;
 	}

/*   Plugin changed so code does not compile too lazy to change
     public void testMapper() throws MalformedURLException,
            net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception,
            WebserviceException_Exception, IOException,
            net.sourceforge.ondex.webservice.client.mapping.WebserviceException_Exception
    {
        MedlineParserTester medlineParserTester = new MedlineParserTester();
        long graphid =  medlineParserTester.parseFile();

        mapFile(graphid);
        api_service.deleteGraph(graphid);
        System.out.println("done Medline TBBased mapping tested");
 	}

    public static void main(String[] args) throws MalformedURLException,
            net.sourceforge.ondex.webservice.client.parser.WebserviceException_Exception,
            WebserviceException_Exception, IOException,
            net.sourceforge.ondex.webservice.client.mapping.WebserviceException_Exception {
        TmbasedTester medlineTester = new TmbasedTester();
        medlineTester.testMapper();
    }
*/
}
