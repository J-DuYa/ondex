package net.sourceforge.ondex.ws_tester.transformer;

import java.net.MalformedURLException;
import java.net.URL;
import net.sourceforge.ondex.webservice.client.transformer.TransformerAuto;
import net.sourceforge.ondex.webservice.client.transformer.TransformerAutoService;
import net.sourceforge.ondex.ws_tester.WS_Tester_Base;

/**
 * Program for creating plugin wrappers.
 *
 * Designed only to run on Christian Machine
 * 
 * @author BrennincC based on stuff by taubertj
 */
public class WS_Tester_Transformer extends WS_Tester_Base{

    protected static TransformerAuto transformer_service;

    public WS_Tester_Transformer(String name) throws MalformedURLException{
        super(name);
        if (transformer_service == null){
            getTransformerServer();
        }
    }

    private void getTransformerServer() throws MalformedURLException
    {
        URL url = new URL("http://rpc466.cs.man.ac.uk:8080/ondex/services/ondex-transformer");
        TransformerAutoService server = new TransformerAutoService(url);
        transformer_service = server.getTransformerAutoPort();
 	}


}
