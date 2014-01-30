package net.sourceforge.ondex.server.result;

/**
 * Provides the tag and description of a job.
 *
 * Currently only used by Export and Statistics.
 *
 * @author Christian Brenninkmeijer
 */
public class WSJob {

    private String jobTag;

    private String description;

    public WSJob(){
    }

    public WSJob(String jobTag, String description){
        this.jobTag = jobTag;
        this.description = description;
    }

    public String getDscription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getJobTag(){
        return jobTag;
    }

    public void setJobTag(String jobTag){
        this.jobTag = jobTag;
    }

 }
