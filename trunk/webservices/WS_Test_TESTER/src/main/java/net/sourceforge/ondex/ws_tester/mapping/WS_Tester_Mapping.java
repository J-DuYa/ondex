package net.sourceforge.ondex.ws_tester.mapping;

import java.net.MalformedURLException;
import java.net.URL;
import net.sourceforge.ondex.webservice.client.mapping.MappingAuto;
import net.sourceforge.ondex.webservice.client.mapping.MappingAutoService;
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
public class WS_Tester_Mapping extends WS_Tester_Base{

    protected static MappingAuto mapping_service;

    public WS_Tester_Mapping(String name) throws MalformedURLException{
        super(name);
        if (mapping_service == null){
            getMappingServer();
        }
    }

    private void getMappingServer() throws MalformedURLException
    {
        URL url = new URL("http://rpc466.cs.man.ac.uk:8080/ondex/services/ondex-mapping");
        MappingAutoService server = new MappingAutoService(url);
        mapping_service = server.getMappingAutoPort();
 	}


}
