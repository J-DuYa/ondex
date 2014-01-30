package net.sourceforge.ondex.ws_tester.parser;

import java.net.MalformedURLException;
import java.net.URL;
import net.sourceforge.ondex.webservice.client.parser.ParserAuto;
import net.sourceforge.ondex.webservice.client.parser.ParserAutoService;
import net.sourceforge.ondex.ws_tester.WS_Tester_Base;

/**
 * Program for creating plugin wrappers.
 *
 * Designed only to run on Christian Machine
 * 
 * @author BrennincC based on stuff by taubertj
 */
public class WS_Tester_Parser extends WS_Tester_Base{

    protected static ParserAuto parser_service;

    public WS_Tester_Parser(String name) throws MalformedURLException{
        super(name);
        if (parser_service == null){
            geParserServer();
        }
    }

    private void geParserServer() throws MalformedURLException
    {
        URL url = new URL("http://rpc466.cs.man.ac.uk:8080/ondex/services/ondex-parser");
        ParserAutoService server = new ParserAutoService(url);
        parser_service = server.getParserAutoPort();
 	}


}
