package net.sourceforge.ondex.server.plugins.transformer;

import net.sourceforge.ondex.ONDEXPluginArguments;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.server.exceptions.JobException;
import net.sourceforge.ondex.server.executor.WebserviceJob;
import net.sourceforge.ondex.transformer.ONDEXTransformer;
import net.sourceforge.ondex.workflow.engine.Engine;
import net.sourceforge.ondex.wsapi.plugins.PluginWS;

import org.apache.log4j.Logger;

/**
 * @author christian
 */
public class TransformerJob extends WebserviceJob
{

    private static final Logger logger = Logger.getLogger(TransformerJob.class);

    ONDEXTransformer transformer;

    private ONDEXGraph graph;

    private ONDEXPluginArguments arguments;

    public TransformerJob(ONDEXGraph graph, ONDEXTransformer transformer, ONDEXPluginArguments mappingArguments)
            throws JobException {
        super();
        try {
            this.description = "Mapping of " + graph.getName() + " using " + transformer.getName();
            this.transformer = transformer;
            this.graph = graph;
            this.arguments = arguments;
            PluginWS.addDefaultArgements(transformer, mappingArguments);
            logger.info("added default arguements");
            submitJob();
        } catch (Exception e) {
            throw new JobException("Exception creating TransformerJob", e, logger);
        }
    }

    @Override
    public String innerCall() throws Exception {
        transformer.addONDEXListener(getBufferedOndexListener(logger));

        logger.info("added listener");
        transformer.setONDEXGraph(graph);
        logger.info("set graph");
        logger.info("running Transformer");
        PluginWS.initMetaData(graph);
        Engine engine = Engine.getEngine();
        engine.runTransformer(transformer, arguments, graph);
        logger.info("ran Transformer");
        logger.info("done");
        return getBufferedOndexListener(logger).getCompleteEventHistory();
    }

    public boolean deleteJob() throws JobException {
        super.deleteJob();
        graph = null;
        transformer = null;
        arguments = null;
        return true;
    }

}
