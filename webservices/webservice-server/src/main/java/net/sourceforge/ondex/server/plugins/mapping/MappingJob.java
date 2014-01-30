package net.sourceforge.ondex.server.plugins.mapping;

import net.sourceforge.ondex.ONDEXPluginArguments;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.mapping.ONDEXMapping;
import net.sourceforge.ondex.server.exceptions.JobException;
import net.sourceforge.ondex.server.executor.WebserviceJob;
import net.sourceforge.ondex.workflow.engine.Engine;
import net.sourceforge.ondex.wsapi.plugins.PluginWS;

import org.apache.log4j.Logger;

/**
 * @author christian
 */
public class MappingJob extends WebserviceJob
{

    private static final Logger logger = Logger.getLogger(MappingJob.class);

    ONDEXMapping mapping;

    private ONDEXGraph graph;

    private ONDEXPluginArguments arguments;

    public MappingJob(ONDEXGraph graph, ONDEXMapping mapping, ONDEXPluginArguments mappingArguments)
            throws JobException {
        super();
        try {
            this.description = "Mapping of " + graph.getName() + " using " + mapping.getName();
            this.mapping = mapping;
            this.graph = graph;
            this.arguments = arguments;
            PluginWS.addDefaultArgements(mapping, mappingArguments);
            logger.info("added default arguements");
            submitJob();
        } catch (Exception e) {
            throw new JobException("Exception creating MappingJob", e, logger);
        }
    }

    @Override
    public String innerCall() throws Exception {
        mapping.addONDEXListener(getBufferedOndexListener(logger));

        logger.info("added listener");
        mapping.setONDEXGraph(graph);
        logger.info("set graph");
        logger.info("running Mapping");
        PluginWS.initMetaData(graph);
        Engine engine = Engine.getEngine();
        engine.runMapping(mapping, arguments, graph);
        logger.info("ran mapping");
        logger.info("done");
        return getBufferedOndexListener(logger).getCompleteEventHistory();
    }

    public boolean deleteJob() throws JobException {
        super.deleteJob();
        graph = null;
        mapping = null;
        arguments = null;
        return true;
    }

}
