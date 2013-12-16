package net.sourceforge.ondex.ws_tester.parserUsingJob;

import java.net.MalformedURLException;
import java.net.URL;
import net.sourceforge.ondex.webservice.client.parserUsingJob.ParserUsingJobAuto;
import net.sourceforge.ondex.webservice.client.parserUsingJob.ParserUsingJobAutoService;
import net.sourceforge.ondex.ws_tester.WS_Tester_Base;

/**
 * Program for creating plugin wrappers.
 *
 * Designed only to run on Christian Machine
 * 
 * @author BrennincC based on stuff by taubertj
 */
public class WS_Tester_ParserUsingJob extends WS_Tester_Base{

    protected static ParserUsingJobAuto parser_service;

    public WS_Tester_ParserUsingJob(String name) throws MalformedURLException{
        super(name);
        if (parser_service == null){
            geParserUsingJobServer();
        }
    }

    private void geParserUsingJobServer() throws MalformedURLException
    {
        URL url = new URL("http://rpc466.cs.man.ac.uk:8080/ondex/services/ondex-parserUsingJob");
        ParserUsingJobAutoService server = new ParserUsingJobAutoService(url);
        parser_service = server.getParserUsingJobAutoPort();
 	}


}
