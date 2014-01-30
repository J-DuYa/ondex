/**
 * Adds more methods that are not dependent on plugins.
 */
package net.sourceforge.ondex.server.plugins;

import net.sourceforge.ondex.InvalidPluginArgumentException;
import net.sourceforge.ondex.ONDEXPlugin;
import net.sourceforge.ondex.ONDEXPluginArguments;
import net.sourceforge.ondex.args.FileArgumentDefinition;
import net.sourceforge.ondex.args.ArgumentDefinition;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.export.ONDEXExport;
import net.sourceforge.ondex.filter.ONDEXFilter;
import net.sourceforge.ondex.mapping.ONDEXMapping;
import net.sourceforge.ondex.parser.ONDEXParser;
import net.sourceforge.ondex.server.exceptions.WSImportException;
import net.sourceforge.ondex.server.plugins.utils.Unpacker;
import net.sourceforge.ondex.server.result.WSFilterResult;
import net.sourceforge.ondex.transformer.ONDEXTransformer;
import net.sourceforge.ondex.wsapi.exceptions.CaughtException;
import net.sourceforge.ondex.wsapi.exceptions.IllegalArguementsException;
import net.sourceforge.ondex.wsapi.exceptions.PluginNotFoundException;
import net.sourceforge.ondex.wsapi.exceptions.WebserviceException;
import net.sourceforge.ondex.wsapi.plugins.BufferedOndexListener;
import net.sourceforge.ondex.wsapi.plugins.PluginWS;
import net.sourceforge.ondex.wsapi.plugins.ZipFormat;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 *
 * @author christian
 */
public class Base extends PluginWS{
  
    private static final Logger logger = Logger.getLogger(Base.class);

    protected Base() throws CaughtException{
        super();
    }

    protected ONDEXExport getExport(String name)
            throws PluginNotFoundException, CaughtException {
        PluginFinder pluginFinder = PluginFinder.getInstance();
        return pluginFinder.getExport(name);
    }

    protected ONDEXFilter getFilter(String name)
            throws PluginNotFoundException, CaughtException {
        PluginFinder pluginFinder = PluginFinder.getInstance();
        return pluginFinder.getFilter(name);
    }

    protected ONDEXMapping getMapping(String name)
            throws PluginNotFoundException, CaughtException {
        PluginFinder pluginFinder = PluginFinder.getInstance();
        return pluginFinder.getMapping(name);
    }

    protected ONDEXParser getParser(String name)
            throws PluginNotFoundException, CaughtException {
        PluginFinder pluginFinder = PluginFinder.getInstance();
        return pluginFinder.getParser(name);
    }

    protected ONDEXTransformer getTransformer(String name)
            throws PluginNotFoundException, CaughtException {
        PluginFinder pluginFinder = PluginFinder.getInstance();
        return pluginFinder.getTransformer(name);
    }

    protected String runExport (ONDEXGraph graph, File dir,
           ONDEXExport export, ONDEXPluginArguments args, ZipFormat zipFormat)
           throws WebserviceException {
      BufferedOndexListener bufferedOndexListener
                 = new BufferedOndexListener(logger);
      try {
         export.addONDEXListener(bufferedOndexListener);
         logger.info("added listener");
         export.setONDEXGraph(graph);
         logger.info("set graph");
         addDefaultArgements(export, args);
         logger.info("added default arguements");
         logger.info("running export");
         engine.runExport(export, args, graph);
         logger.info("ran export");
         logger.info("done");
         if (zipFormat.equals(ZipFormat.RAW) && dir.isFile()){
            return fileToString(dir);
         } else {
            File zipFile = zipFile(dir, zipFormat);
            return fileToUrl(zipFile);
         }
      } catch (WebserviceException e)   {
            throw e;
      } catch (Exception e)    {
          throw new CaughtException (e, bufferedOndexListener, logger);
      }
    }

    protected WSFilterResult runFilter (ONDEXGraph graph,
           ONDEXFilter filter, ONDEXPluginArguments args)
           throws WebserviceException {
      BufferedOndexListener bufferedOndexListener
              = new BufferedOndexListener(logger);
        try {
            filter.addONDEXListener(bufferedOndexListener);
            logger.info("added listener");
            filter.setONDEXGraph(graph);
            logger.info("set graph");
            addDefaultArgements(filter, args);
            logger.info("added default arguements");
            ONDEXGraph newGraph = webServiceEngine.makeGraph(graph.getName(), "filter");
            logger.info("running filter");
            engine.runFilter(filter, args, graph, newGraph);
            logger.info("ran filter");
            logger.info("done");
            return new WSFilterResult(newGraph, bufferedOndexListener);

        } catch (WebserviceException e)   {
            throw e;
        } catch (Exception e)    {
            throw new CaughtException (e, bufferedOndexListener, logger);
        }
    }

    protected String runMapping (ONDEXGraph graph,
           ONDEXMapping mapping, ONDEXPluginArguments args)
           throws WebserviceException {
      BufferedOndexListener bufferedOndexListener
              = new BufferedOndexListener(logger);
      try {
         mapping.addONDEXListener(bufferedOndexListener);
         logger.info("added listener");
         mapping.setONDEXGraph(graph);
         logger.info("set graph");
         addDefaultArgements(mapping, args);
         logger.info("added default arguements");
         logger.info("running mapping");
         engine.runMapping(mapping, args, graph);
         logger.info("ran mapping");
         logger.info("done");
         return bufferedOndexListener.getCompleteEventHistory();

        } catch (WebserviceException e)   {
            throw e;
      } catch (Exception e)    {
          throw new CaughtException (e, bufferedOndexListener, logger);
      }
    }

    protected String runTransformer (ONDEXGraph graph,
           ONDEXTransformer transformer, ONDEXPluginArguments args)
           throws WebserviceException {
      BufferedOndexListener bufferedOndexListener
              = new BufferedOndexListener(logger);
      try {
         transformer.addONDEXListener(bufferedOndexListener);
         logger.info("added listener");
         transformer.setONDEXGraph(graph);
         logger.info("set graph");
         addDefaultArgements(transformer, args);
         logger.info("added default arguements");
         logger.info("running transformer");
         engine.runTransformer(transformer, args, graph);
         logger.info("ran transformer");
         logger.info("done");
         return bufferedOndexListener.getCompleteEventHistory();

        } catch (WebserviceException e)   {
            throw e;
      } catch (Exception e)    {
          throw new CaughtException (e, bufferedOndexListener, logger);
      }
    }

    protected String runTransformer (ONDEXGraph graph, File file, ONDEXTransformer transformer,
            ONDEXPluginArguments args, ZipFormat zipFormat) throws WebserviceException {
        BufferedOndexListener bufferedOndexListener = new BufferedOndexListener(logger);
        try {
            transformer.addONDEXListener(bufferedOndexListener);
            logger.info("added listener");
            transformer.setONDEXGraph(graph);
            logger.info("set graph");
            addDefaultArgements(transformer, args);
            logger.info("added default arguements");
            logger.info("running transformer");
            engine.runTransformer(transformer, args, graph);
            logger.info("ran transformer");
            logger.info("done");
            if (zipFormat.equals(zipFormat.RAW)){
                return fileToString(file);
            } else {
                File compressed = zipFile(file, zipFormat);
                return fileToUrl(compressed);
            }
        } catch (WebserviceException e)   {
            throw e;
        } catch (Exception e)    {
            throw new CaughtException (e, bufferedOndexListener, logger);
        }
    }

    protected String runTransformer (ONDEXGraph graph, StringWriter stringWriter,
           ONDEXTransformer transformer, ONDEXPluginArguments args)
           throws WebserviceException {
       BufferedOndexListener bufferedOndexListener
              = new BufferedOndexListener(logger);
     try {
         transformer.addONDEXListener(bufferedOndexListener);
         logger.info("added listener");
         transformer.setONDEXGraph(graph);
         logger.info("set graph");
         addDefaultArgements(transformer, args);
         logger.info("added default arguements");
         logger.info("running transformer");
         engine.runTransformer(transformer, args, graph);
         logger.info("ran transformer");
         logger.info("done");
         return bufferedOndexListener.getCompleteEventHistory();

        } catch (WebserviceException e)   {
            throw e;
      } catch (Exception e)    {
          throw new CaughtException (e, bufferedOndexListener, logger);
      }
    }

    protected void createInputDirectoryArguement(ONDEXPlugin plugin, ONDEXPluginArguments arguments,
            String arguementName, String asString, byte[] asArray) throws WebserviceException{
        try{
            if (asString == null || asString.isEmpty()){
                throw new WSImportException("Converting array of Bytes representing a Directory "
                        + arguementName + "requires a zip format in the matching String parameter",logger);
            }
            if (asArray != null && asArray.length > 0){
                ZipFormat zipFormat = ZipFormat.parseString(asString);
                if (zipFormat.isDirectory()){
                    Unpacker unpacker = new Unpacker();
                    InputStream zipped = new ByteArrayInputStream(asArray);
                    File temp = unpacker.createTempDirectory(tempDir);
                    unpacker.toDirectory(zipped, zipFormat, temp);
               } else{
                    throw new WSImportException("Unexpected compression type when importing directory for "
                            + arguementName, logger);
               }
            } else if (asString != null && asString.length() > 0){
                Unpacker unpacker = new Unpacker();
                File inputDirectory = unpacker.toDirectory(asString, tempDir);
                createArguement(plugin, arguments, arguementName, inputDirectory.getAbsolutePath());
            } else {
                throw new WSImportException("Directory for " + arguementName +
                        " must be supplied as either a String or in bytes", logger);
            }
        } catch (WSImportException e) {
            throw e;
        } catch (Exception e) {
            throw new CaughtException("Trying to create a InputDirectory for " + arguementName, e, logger);
        }
    }

    protected void createOptionalInputDirectoryArguement(ONDEXPlugin plugin, ONDEXPluginArguments arguments,
            String arguementName, String asString, byte[] asArray) throws WebserviceException{
         if (asString == null || asString.isEmpty()){
             logger.info("No Value found for " + arguementName);
             return;
        }
        createInputDirectoryArguement(plugin, arguments, arguementName, asString, null);
    }

    /*
    protected File createFileWriterArguement(ONDEXPlugin plugin, ONDEXPluginArguments arguments,
            String arguementName, String suffix, boolean gzip) throws CaughtException{
        try {
            Writer writer;
            File file;
            if (gzip){
                file = createOutputFile("output",suffix+".gz");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(fileOutputStream);
                writer = new OutputStreamWriter(gZIPOutputStream);
            } else {
                file = createOutputFile("output",suffix);
                writer = new FileWriter(file);
            }
            createArguement(plugin, arguments, arguementName, writer);
            return file;
        } catch (Exception e) {
            throw new CaughtException("Trying to create a FileWriter", e, logger);
        }
    }*/

    protected String getInfo(ONDEXPlugin plugin)
            throws PluginNotFoundException, CaughtException {
        logger.info("get infomapping name called " + plugin.getId());
        StringBuffer info = new StringBuffer();
        info.append(plugin.getId());
        info.append(": ");
        info.append(plugin.getName());
        info.append(NEW_LINE);
        ArgumentDefinition<ONDEXPluginArguments>[] argumentDefinitions =
                (ArgumentDefinition<ONDEXPluginArguments>[]) plugin.getArgumentDefinitions();
        for (ArgumentDefinition argumentDefinition : argumentDefinitions) {
            info.append(argumentDefinition.getName());
            info.append(", ");
            //info.append(argumentDefinition.getDescription());
            //info.append(", ");
            info.append(argumentDefinition.getClassType());
            info.append(", ");
            info.append(argumentDefinition.getDefaultValue());
            if (argumentDefinition.isRequiredArgument()) {
                info.append(", Required");
            } else {
                info.append(", Optional");
            }
            if (argumentDefinition.isAllowedMultipleInstances()) {
                info.append(", Multiple");
            } else {
                info.append(", Single");
            }
            info.append(", ");
            info.append(argumentDefinition.getDescription());
            info.append(NEW_LINE);
        }
        return info.toString();
    }

    protected void addExportDir(ONDEXPlugin plugin, ONDEXPluginArguments arguments, File file)
            throws IllegalArguementsException {
        checkArguementName (plugin, FileArgumentDefinition.EXPORT_DIR);
        try {
            arguments.addOption(FileArgumentDefinition.EXPORT_DIR, file.getAbsolutePath());
        } catch (InvalidPluginArgumentException e) {
            new IllegalArguementsException("Error adding the export dir " , e, plugin, logger);
        }
    }

    public File createTempDirectory() throws IOException
    {
        final File temp;
        temp = File.createTempFile("TempDir", "", tempDir);
        if(!(temp.delete())){
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }
        if(!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }
        //temp.deleteOnExit();
        return (temp);
    }

  }
