package net.sourceforge.ondex.server.plugins.export;

import java.io.File;
import java.net.UnknownHostException;

import net.sourceforge.ondex.ONDEXPluginArguments;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.event.ONDEXEvent;
import net.sourceforge.ondex.event.ONDEXListener;
import net.sourceforge.ondex.event.type.EventType;
import net.sourceforge.ondex.export.ONDEXExport;
import net.sourceforge.ondex.server.exceptions.JobException;
import net.sourceforge.ondex.server.executor.WebserviceJob;
import net.sourceforge.ondex.server.plugins.Base;
import net.sourceforge.ondex.server.plugins.JobBase;
import net.sourceforge.ondex.workflow.engine.Engine;
import net.sourceforge.ondex.wsapi.plugins.PluginWS;

import org.apache.log4j.Logger;

/**
 * @author christian
 */
public class ExportJob extends WebserviceJob implements ONDEXListener
{

    private static final Logger logger = Logger.getLogger(ExportJob.class);

    ONDEXGraph graph;

    File file;

    ONDEXExport export;

    ONDEXPluginArguments arguments;

    private final static int SIZE_CUTOFF = 100000;

    public ExportJob(ONDEXGraph graph, File file, ONDEXExport export, ONDEXPluginArguments exportArguments)
            throws JobException {
        super();
        try {
            this.description = "Export of graph " + graph.getName() + " using " + export.getName();
            this.graph = graph;
            this.file = file;
            this.export = export;
            this.arguments = exportArguments;
            PluginWS.addDefaultArgements(export, exportArguments);
            logger.info("added default arguements");
            submitJob();
        } catch (Exception e) {
            throw new JobException("Exception creating ExportJob", e, logger);
        }
    }

    @Override
    public String innerCall() throws Exception {
        export.addONDEXListener(this);
        logger.info("added listener");
        export.setONDEXGraph(graph);
        logger.info("set graph");
        logger.info("running export");
        Engine engine = Engine.getEngine();
        engine.runExport(export, arguments, graph);
        logger.info("ran export");
        logger.info("done");
        return "Done";
    }

    @Override
    public void eventOccurred(ONDEXEvent e) {
        EventType eventType = e.getEventType();
        log(eventType.getCompleteMessage());
    }

     @Override
     public Long getSize()throws JobException {
        logger.info("getSize called");
        super.get();
        logger.info("super get done");
        if (!file.exists()) {
            throw new JobException("Output file for job " + this.getJobId() + " no longer exists.", logger);
        }
        return file.length();
    }

     @Override
     public String get() throws JobException {
        logger.info("GetString called");
        super.get();
        logger.info("super get done");
        try {
            //Wait for job to be finished or aborted
            return Base.fileToString(file);
        } catch (Exception ex) {
            throw new JobException(ex, logger);
        }
    }

    @Override
    public String getUrl() throws JobException {
        try {
            return Base.fileToUrl(file);
        } catch (UnknownHostException ex) {
            throw new JobException(ex, logger);
        }
    }

    public boolean deleteJob() throws JobException {
        boolean ok = JobBase.deleteFile(file);
        if (ok) {
            super.deleteJob();
            graph = null;
            file = null;
            export = null;
            arguments = null;
        }
        return ok;
    }

}
