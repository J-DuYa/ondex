package net.sourceforge.ondex.server.plugins.parser;

import net.sourceforge.ondex.ONDEXPluginArguments;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.parser.ONDEXParser;
import net.sourceforge.ondex.server.exceptions.JobException;
import net.sourceforge.ondex.server.executor.WebserviceJob;
import net.sourceforge.ondex.workflow.engine.Engine;
import net.sourceforge.ondex.wsapi.plugins.BufferedOndexListener;
import net.sourceforge.ondex.wsapi.plugins.PluginWS;

import org.apache.log4j.Logger;

/**
 * @author christian
 */
public class ParserJob extends WebserviceJob 
{

    private static final Logger logger = Logger.getLogger(ParserJob.class);

    ONDEXGraph graph;

    //File file;

    ONDEXParser parser;

    ONDEXPluginArguments arguments;

    public ParserJob(ONDEXGraph graph, ONDEXParser parser, ONDEXPluginArguments parserArguments)
            throws JobException {
        super();
        try {
            this.description = "Parsing of " + graph.getName() + " using " + parser.getName();
            this.graph = graph;
            this.parser = parser;
            this.arguments = parserArguments;
            PluginWS.addDefaultArgements(parser, parserArguments);
            logger.info("added default arguements");
            submitJob();
        } catch (Exception e) {
            throw new JobException("Exception creating ParserJob", e, logger);
        }
    }

    @Override
    public String innerCall() throws Exception {
        parser.addONDEXListener(getBufferedOndexListener(logger));

        logger.info("added listener");
        parser.setONDEXGraph(graph);
        logger.info("set graph");
        logger.info("running export");
        PluginWS.initMetaData(graph);
        Engine engine = Engine.getEngine();
        engine.runParser(parser, arguments, graph);
        logger.info("ran parser");
        logger.info("done");
        return getBufferedOndexListener(logger).getCompleteEventHistory();
    }

    public boolean deleteJob() throws JobException {
        super.deleteJob();
        graph = null;
        parser = null;
        arguments = null;
        return true;
    }

}
