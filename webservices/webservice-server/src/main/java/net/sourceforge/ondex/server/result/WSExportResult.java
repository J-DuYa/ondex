package net.sourceforge.ondex.server.result;

import net.sourceforge.ondex.server.exceptions.JobException;
import net.sourceforge.ondex.server.plugins.export.ExportJob;

/**
 * Provides the required result from an Export.
 *
 * This includes the id of the new graph and the String output of the events
 *
 * @author Christian Brenninkmeijer
 */
public class WSExportResult {

        /**
	 * The url to the data
	 */
	private String url;

    private String jobTag;

    public WSExportResult(ExportJob job) throws JobException{
        url = job.getUrl();
        jobTag = job.getJobId().toString();
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getJobTag(){
        return jobTag;
    }

    public void setJobTag(String jobTag){
        this.jobTag = jobTag;
    }

 }
