package net.sourceforge.ondex.server.plugins.filter;

import net.sourceforge.ondex.ONDEXPluginArguments;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.filter.ONDEXFilter;
import net.sourceforge.ondex.server.exceptions.JobException;
import net.sourceforge.ondex.server.executor.WebserviceJob;
import net.sourceforge.ondex.workflow.engine.Engine;
import net.sourceforge.ondex.wsapi.plugins.PluginWS;

import org.apache.log4j.Logger;

/**
 * @author christian
 */
public class FilterJob extends WebserviceJob
{

    private static final Logger logger = Logger.getLogger(FilterJob.class);

    ONDEXFilter filter;

    private ONDEXGraph graph;

    private ONDEXPluginArguments arguments;

    public FilterJob(ONDEXGraph graph, ONDEXFilter filter, ONDEXPluginArguments mappingArguments)
            throws JobException {
        super();
        try {
            this.description = "Mapping of " + graph.getName() + " using " + filter.getName();
            this.filter = filter;
            this.graph = graph;
            this.arguments = arguments;
            PluginWS.addDefaultArgements(filter, mappingArguments);
            logger.info("added default arguements");
            submitJob();
        } catch (Exception e) {
            throw new JobException("Exception creating FilterJob", e, logger);
        }
    }

    @Override
    public String innerCall() throws Exception {
        filter.addONDEXListener(getBufferedOndexListener(logger));

        logger.info("added listener");
        filter.setONDEXGraph(graph);
        logger.info("set graph");
        logger.info("running Filter");
        PluginWS.initMetaData(graph);
        Engine engine = Engine.getEngine();
        engine.runFilter(filter, arguments, graph, graph);
        logger.info("ran filter");
        logger.info("done");
        return getBufferedOndexListener(logger).getCompleteEventHistory();
    }

    public boolean deleteJob() throws JobException {
        super.deleteJob();
        graph = null;
        filter = null;
        arguments = null;
        return true;
    }

}
