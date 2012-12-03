package net.sourceforge.ondex.ws_auto_create;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import net.sourceforge.ondex.webservice.client.auto.AutoCreator;
import net.sourceforge.ondex.webservice.client.auto.AutoCreatorService;
import net.sourceforge.ondex.webservice.client.auto.CaughtException_Exception;
import net.sourceforge.ondex.webservice.client.auto.PluginNotFoundException_Exception;
import net.sourceforge.ondex.webservice.client.auto.WebserviceException_Exception;

/**
 * Program for creating plugin wrappers.
 *
 * Designed only to run on Christian's Machine
 * 
 * @author BrennincC based on stuff by taubertj
 */
public class AutoMaker {

    /**
     * Directory where the local copy of the Webservice modue is saved.
     * This will need editing if run by someone other the Christian
     */
    static final String WEBSERVICE_ROOT = "c:/Users/Christian/Ondex/webservices/";
    /**
     * Directories where WS plugin wrappers are stored.
     * Will only need editing if package names have been changed
     */
    static final String PLUGIN_PACKAGE = WEBSERVICE_ROOT + "webservice-server/src/main/java/net/sourceforge/ondex/server/plugins/";
    /**
     * Address of the server.
     * This will need editing if run by someone other the Christian
     */
    static final String ONDEX_SERVER = "http://rpc466.cs.man.ac.uk:8080";
    /**
     * Name given to the ondex service.
     * Will only need editing if suggested name not used.
     */
    static final String ONDEX_SERVICE_NAME = "ondex";
    /**
     * Full path to the automaker service.
     * Will only need editing if the services have been renamed.
     */
    static final String AUTO_SERVICE = ONDEX_SERVER + "/" + ONDEX_SERVICE_NAME + "/services/ondex-auto";

    static AutoCreator wsAuto;

 	public AutoMaker(URL url) 
    {
		//connect to webservice from URL for graph name
		//ONDEXapiWSService ondexService = new ONDEXapiWSService(url);
        AutoCreatorService service = new AutoCreatorService(url);
        wsAuto = service.getAutoCreatorPort();
    }

 	public void makeExport() throws WebserviceException_Exception, PluginNotFoundException_Exception, CaughtException_Exception, IOException
    {
        String code = wsAuto.autoCreateExporter();
        File file = new File(PLUGIN_PACKAGE + "export/ExportAuto.java");
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
 	}

 	public void makeExportJob() throws WebserviceException_Exception, PluginNotFoundException_Exception, CaughtException_Exception, IOException
    {
        String code = wsAuto.autoCreateJobExporter();
        File file = new File(PLUGIN_PACKAGE + "export/ExportUsingJobAuto.java");
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
 	}

 	public void makeFilter() throws WebserviceException_Exception, PluginNotFoundException_Exception, CaughtException_Exception, IOException
    {
        String code = wsAuto.autoCreateFilter();
        File file = new File(PLUGIN_PACKAGE + "filter/FilterAuto.java");
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
 	}

 	public void makeFilterJob() throws WebserviceException_Exception, PluginNotFoundException_Exception, CaughtException_Exception, IOException
    {
        String code = wsAuto.autoCreateJobFilter();
        File file = new File(PLUGIN_PACKAGE + "filter/FilterUsingJobAuto.java");
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
 	}

    public void makeParser() throws WebserviceException_Exception, PluginNotFoundException_Exception, CaughtException_Exception, IOException
    {
        String code = wsAuto.autoCreateParser();
        File file = new File(PLUGIN_PACKAGE + "parser/ParserAuto.java");
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
 	}

    public void makeParserJob() throws WebserviceException_Exception, PluginNotFoundException_Exception, CaughtException_Exception, IOException
    {
        String code = wsAuto.autoCreateJobParser();
        File file = new File(PLUGIN_PACKAGE + "parser/ParserUsingJobAuto.java");
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
 	}

    public void makeMapping() throws WebserviceException_Exception, PluginNotFoundException_Exception, CaughtException_Exception, IOException
    {
        String code = wsAuto.autoCreateMapping();
        File file = new File(PLUGIN_PACKAGE + "mapping/MappingAuto.java");
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
 	}

    public void makeMappingJob() throws WebserviceException_Exception, PluginNotFoundException_Exception, CaughtException_Exception, IOException
    {
        String code = wsAuto.autoCreateJobMapping();
        File file = new File(PLUGIN_PACKAGE + "mapping/MappingUsingJobAuto.java");
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
 	}

 	public void makeTransformer() throws WebserviceException_Exception, PluginNotFoundException_Exception, CaughtException_Exception, IOException
    {
        String code = wsAuto.autoCreateTransformer();
        File file = new File(PLUGIN_PACKAGE + "transformer/TransformerAuto.java");
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
 	}

 	public void makeTransformerJob() throws WebserviceException_Exception, PluginNotFoundException_Exception, CaughtException_Exception, IOException
    {
        String code = wsAuto.autoCreateJobTransformer();
        File file = new File(PLUGIN_PACKAGE + "transformer/TransformerUsingJobAuto.java");
        FileWriter writer = new FileWriter(file);
        writer.write(code);
        writer.close();
 	}

    public void showProperties(){
        String props = wsAuto.getSystemProperties();
        System.out.println(props);
    }
    public static void main(String[] args) throws MalformedURLException, CaughtException_Exception, WebserviceException_Exception, PluginNotFoundException_Exception, IOException {
        AutoMaker autoMaker = new AutoMaker(new URL(AUTO_SERVICE));
        autoMaker.makeExport();
        autoMaker.makeExportJob();
        autoMaker.makeFilter();
        autoMaker.makeFilterJob();
        autoMaker.makeParser();
        autoMaker.makeParserJob();
        autoMaker.makeMapping();
        autoMaker.makeMappingJob();
        autoMaker.makeTransformer();
        autoMaker.makeTransformerJob();
        //autoMaker.showProperties();
    }

}
