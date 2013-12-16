/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.ondex.server.plugins;

import java.util.Properties;
import java.util.Set;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import net.sourceforge.ondex.export.ONDEXExport;
import net.sourceforge.ondex.filter.ONDEXFilter;
import net.sourceforge.ondex.mapping.ONDEXMapping;
import net.sourceforge.ondex.parser.ONDEXParser;
import net.sourceforge.ondex.transformer.ONDEXTransformer;
import net.sourceforge.ondex.wsapi.exceptions.CaughtException;
import net.sourceforge.ondex.wsapi.exceptions.PluginNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.WebserviceException;
import org.apache.log4j.Logger;

/**
 *
 * @author christian
 */
public class AutoCreator extends Base{

    public AutoCreator() throws CaughtException{
        super();
    }

    private static final Logger logger = Logger.getLogger(AutoCreator.class);

    /**
     * Automatically creates webservice code for know filter plugins
     * @return String representation of this class
     * @throws CaughtException
     */
    @WebResult(name = "class")
    @WebMethod(exclude = false)
    public String autoCreateFilter() 
            throws CaughtException, PluginNotFoundException, WebserviceException{
        AutoCreate autoCreate = new AutoCreate();
        return autoCreate.wholeClass(TypeOfPlugin.FILTER);
    }

    /**
     * Automatically creates webservice job code for know filter plugins
     * @return String representation of this class
     * @throws CaughtException
     */
    @WebResult(name = "class")
    @WebMethod(exclude = false)
    public String autoCreateJobFilter()
            throws CaughtException, PluginNotFoundException, WebserviceException{
        AutoCreate autoCreate = new AutoCreate();
        return autoCreate.wholeClass(TypeOfPlugin.FILTERJOB);
    }

    /**
     * Automatically creates webservice code for know parser plugins
     * @return String representation of this class
     * @throws CaughtException
     */
    @WebResult(name = "class")
    @WebMethod(exclude = false)
    public String autoCreateParser() 
            throws CaughtException, PluginNotFoundException, WebserviceException{
        AutoCreate autoCreate = new AutoCreate();
        return autoCreate.wholeClass(TypeOfPlugin.PARSER);
    }

    /**
     * Automatically creates webservice job code for know parser plugins
     * @return String representation of this class
     * @throws CaughtException
     */
    @WebResult(name = "class")
    @WebMethod(exclude = false)
    public String autoCreateJobParser()
            throws CaughtException, PluginNotFoundException, WebserviceException{
        AutoCreate autoCreate = new AutoCreate();
        return autoCreate.wholeClass(TypeOfPlugin.PARSERJOB);
    }

    /**
     * Automatically creates webservice code for know mapping
     * @return String representation of this class
     * @throws CaughtException
     */
    @WebResult(name = "class")
    @WebMethod(exclude = false)
    public String autoCreateMapping() 
            throws CaughtException, PluginNotFoundException, WebserviceException{
        AutoCreate autoCreate = new AutoCreate();
        return autoCreate.wholeClass(TypeOfPlugin.MAPPING);
    }

    /**
     * Automatically creates webservice job code for know mapping plugins
     * @return String representation of this class
     * @throws CaughtException
     */
    @WebResult(name = "class")
    @WebMethod(exclude = false)
    public String autoCreateJobMapping()
            throws CaughtException, PluginNotFoundException, WebserviceException{
        AutoCreate autoCreate = new AutoCreate();
        return autoCreate.wholeClass(TypeOfPlugin.MAPPINGJOB);
    }

    /**
     * Automatically creates webservice code for know transformers
     * @return String representation of this class
     * @throws CaughtException
     */
    @WebResult(name = "class")
    @WebMethod(exclude = false)
    public String autoCreateTransformer() 
            throws CaughtException, PluginNotFoundException, WebserviceException{
        AutoCreate autoCreate = new AutoCreate();
        return autoCreate.wholeClass(TypeOfPlugin.TRANSFORMER);
    }

    /**
     * Automatically creates webservice job code for know transformers
     * @return String representation of this class
     * @throws CaughtException
     */
    @WebResult(name = "class")
    @WebMethod(exclude = false)
    public String autoCreateJobTransformer()
            throws CaughtException, PluginNotFoundException, WebserviceException{
        AutoCreate autoCreate = new AutoCreate();
        return autoCreate.wholeClass(TypeOfPlugin.TRANSFORMERJOB);
    }

    /**
     * Automatically creates webservice code for know exporters
     * @return String representation of this class
     * @throws CaughtException
     */
    @WebResult(name = "class")
    @WebMethod(exclude = false)
    public String autoCreateExporter() 
            throws CaughtException, PluginNotFoundException, WebserviceException{
        AutoCreate autoCreate = new AutoCreate();
        return  autoCreate.wholeClass(TypeOfPlugin.EXPORT);
    }

    /**
     * Automatically creates webservice code for know exporters
     * @return String representation of this class
     * @throws CaughtException
     */
    @WebResult(name = "class")
    @WebMethod(exclude = false)
    public String autoCreateJobExporter()
            throws CaughtException, PluginNotFoundException, WebserviceException{
        AutoCreate autoCreate = new AutoCreate();
        return  autoCreate.wholeClass(TypeOfPlugin.EXPORTJOB);
    }

    @WebResult(name = "properties")
    @WebMethod(exclude = false)
    public String getSystemProperties(){
        Properties properties = System.getProperties();
        Set keys = properties.keySet();
        StringBuffer buffer = new StringBuffer("Properties: ");
        for (Object key: keys){
            Object value = properties.get(key);
            buffer.append(key);
            buffer.append(": ");
            buffer.append(value.toString());
            buffer.append("\n");
        }
        logger.info(buffer.toString());
        return buffer.toString();
    }

    /**
     * Retrieves inforamtion about the available export.
     *
     * @return Set of the ids of the export
     */
    @WebResult(name = "names")
    @WebMethod(exclude = false)
    public Set<String> getExportNames()  throws CaughtException {
        logger.info("get export called");
        PluginFinder pluginFinder = PluginFinder.getInstance();
        return pluginFinder.getExportNames();
    }

    /**
     * Returns information about the requested export.
     *
     * @param name Name of the export to get info for.
     * @return info on the requested mapping
     * @throws PluginNotFoundException
     */
    @WebResult(name = "info")
    @WebMethod(exclude = false)
    public String getExportInfo(
            @WebParam(name = "name") String name)
            throws PluginNotFoundException, CaughtException {
        logger.info("get export info called with " + name);
        ONDEXExport export = getExport(name);

        return getInfo(export);
    }

    /**
     * Retrieves inforamtion about the available filter.
     *
     * @return Set of the ids of the filter
     */
    @WebResult(name = "names")
    @WebMethod(exclude = false)
    public Set<String> getFilterNames()  throws CaughtException {
        logger.info("get filter called");
        PluginFinder pluginFinder = PluginFinder.getInstance();
        return pluginFinder.getFilterNames();
    }

    /**
     * Returns information about the requested filter.
     *
     * @param name Name of the filter to get info for.
     * @return info on the requested mapping
     * @throws PluginNotFoundException
     */
    @WebResult(name = "info")
    @WebMethod(exclude = false)
    public String getFilterInfo(
            @WebParam(name = "name") String name)
            throws PluginNotFoundException, CaughtException {
        logger.info("get filter info called with " + name);
        ONDEXFilter filter = getFilter(name);

        return getInfo(filter);
    }

    /**
     * Retrieves inforamtion about the available mapping.
     *
     * @return Set of the ids of the mapping
     */
    @WebResult(name = "names")
    @WebMethod(exclude = false)
    public Set<String> getMappingNames()  throws CaughtException {
        logger.info("get mapping called");
        PluginFinder pluginFinder = PluginFinder.getInstance();
        return pluginFinder.getMappingNames();
    }

    /**
     * Returns information about the requested mapping.
     *
     * @param name Name of the mapping to get info for.
     * @return info on the requested mapping
     * @throws PluginNotFoundException
     */
    @WebResult(name = "info")
    @WebMethod(exclude = false)
    public String getMappingInfo(
            @WebParam(name = "name") String name)
            throws PluginNotFoundException, CaughtException {
        logger.info("get mapping info called with " + name);
        ONDEXMapping mapping = getMapping(name);

        return getInfo(mapping);
    }

    /**
     * Retrieves inforamtion about the available parser.
     *
     * @return Set of the ids of the parser
     */
    @WebResult(name = "names")
    @WebMethod(exclude = false)
    public Set<String> getParserNames()  throws CaughtException {
        logger.info("get parser called");
        PluginFinder pluginFinder = PluginFinder.getInstance();
        return pluginFinder.getParserNames();
    }

    /**
     * 
     * Returns information about the requested parser.
     *
     * @param name Name of the parser to get info for.
     * @return info on the requested mapping
     * @throws PluginNotFoundException
     */
    @WebResult(name = "info")
    @WebMethod(exclude = false)
    public String getParserInfo(
            @WebParam(name = "name") String name)
            throws PluginNotFoundException, CaughtException {
        logger.info("get parser info called with " + name);
        ONDEXParser parser = getParser(name);

        return getInfo(parser);
    }

    /**
     * Retrieves inforamtion about the available transformer.
     *
     * @return Set of the ids of the transformer
     */
    @WebResult(name = "info")
    @WebMethod(exclude = false)
    public Set<String> getTransformerNames()  throws CaughtException {
        logger.info("get transformer called");
        PluginFinder pluginFinder = PluginFinder.getInstance();
        return pluginFinder.getTransformerNames();
    }

    /**
     * Returns information about the requested transformer.
     *
     * @param name Name of the transformer to get info for.
     * @return info on the requested mapping
     * @throws PluginNotFoundException
     */
    @WebResult(name = "info")
    @WebMethod(exclude = false)
    public String getTransformerInfo(
            @WebParam(name = "name") String name)
            throws PluginNotFoundException, CaughtException {
        logger.info("get transformer info called with " + name);
        ONDEXTransformer transformer = getTransformer(name);

        return getInfo(transformer);
    }

    /**
     * Automatically report the annotaions found in each plugin
     * @return String representation of the report
     * @throws CaughtException
     */
    @WebResult(name = "reprot")
    @WebMethod(exclude = false)
    public String reportAnnotations()
            throws WebserviceException{
        return AnnotationsReport.report();
    }
}
