/**
 * Adds more methods that are not dependent on plugins.
 */
package net.sourceforge.ondex.server.plugins;

import net.sourceforge.ondex.server.exceptions.JobException;
import net.sourceforge.ondex.server.executor.ExecutorRegister;
import net.sourceforge.ondex.server.result.WSExportJobResult;
import net.sourceforge.ondex.server.result.WSJob;
import net.sourceforge.ondex.wsapi.exceptions.CaughtException;

import org.apache.log4j.Logger;

import java.io.File;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author christian
 */
public class JobBase extends Base{
  
    private static final Logger logger = Logger.getLogger(JobBase.class);

    protected JobBase() throws CaughtException{
        super();
    }

    @WebMethod(exclude = true)
    private UUID fromString(@WebParam(name = "jobTag") String jobTag) throws JobException{
        try{
            return UUID.fromString(jobTag);
        } catch (IllegalArgumentException e){
            throw new JobException("Unable to convert "+jobTag+" to a UUID.",e,logger);
        }
    }

    /**
     * Gets the status of any given job.
     * If the job fails this just returns ERROR.
     *
     * For looping the isSuccessfull method is advised.
     *
     * Note: The output will be wrapped in a SOAP object so if checking use match rather than equals.
     * For example match ".*DONE.*"
     *
     * @param jobTag UUID identifier of the job
     * @return "DONE", "ERROR", "NOT_FOUND", "PENDING", "RUNNING"
     * @throws JobException Wrapper for any exception which happen accessing jobs.
     */
    @WebResult(name = "status")
    @WebMethod(exclude = false)
    public String checkStatus(@WebParam(name = "jobTag") String jobTag) throws JobException{
        UUID jobId = fromString(jobTag);
        return ExecutorRegister.getRegister().checkStatus(jobId);
    }

   /**
     * Checks if a job is finished.
     *
     * If the job is pending or running this method returns false.
     * If the job is either not found or ended with an exception a exception is thrown.
     *
     * Note: The output will be wrapped in a SOAP object so if checking use match rather than equals.
     * For example match(".*false.*") or match(.*true.*)

     * True is only returned if the job finsihed succesfully and still available on the server.
     * @param jobTag UUID identifier of the job
     * @return Boolean True if job is finsihed successfully, false if running or pending.
     * @throws JobException
     */
    @WebResult(name = "finished")
    @WebMethod(exclude = false)
    public boolean isSuccessful(@WebParam(name = "jobTag") String jobTag)throws JobException{
        UUID jobId = fromString(jobTag);
        boolean result =  ExecutorRegister.getRegister().isSuccessful(jobId);
        logger.info("Is success for job "+ jobTag + " = " + result);
        return result;
    }

    /**
     * Returns an appropriate String representation of the output of this job.
     *
     * For small files this will be the actual value,
     * for large files and non text files this will be the url.
     *
     * @param jobTag UUID identifier of the job
     * @return String representation of the output of this job
     * @throws JobException
     */
    @WebResult(name = "result")
    @WebMethod(exclude = false)
    public String get(@WebParam(name = "jobTag") String jobTag)throws JobException{
        UUID jobId = fromString(jobTag);
        return ExecutorRegister.getRegister().get(jobId).toString();
    }


    /**
     * Returns the URL of the output file or directory.
     *
     * @param jobTag UUID identifier of the job
     * @return String representation of the output of this job
     * @throws JobException
     */
    @WebResult(name = "url")
    @WebMethod(exclude = false)
    public String getUrl(@WebParam(name = "jobTag") String jobTag)throws JobException{
        UUID jobId = fromString(jobTag);
        return ExecutorRegister.getRegister().getUrl(jobId);
    }

    /**
     * Returns the error history of this job
     * @param jobTag UUID identifier of the job
     * @return error history of this job
     * @throws JobException
     */
    @WebResult(name = "errorHistory")
    @WebMethod(exclude = false)
    public String getErrorHistory(@WebParam(name = "jobTag") String jobTag)throws JobException{
        UUID jobId = fromString(jobTag);
        return ExecutorRegister.getRegister().getErrorHistory(jobId);
    }

    /**
     * Returns the run History of this job.
     *
     * This includes any error history if available.
     *
     * @param jobTag UUID identifier of the job
     * @return History or Events fire during execution of the plugin.
     * @throws JobException
     */
    @WebResult(name = "history")
    @WebMethod(exclude = false)
    public String getHistory(@WebParam(name = "jobTag") String jobTag)throws JobException{
        UUID jobId = fromString(jobTag);
        return ExecutorRegister.getRegister().getHistory(jobId);
    }

    /**
     * Returns the get and History of this job.
     *
     * This includes any error history if available.
     *
     * @param jobTag UUID identifier of the job
     * @return History or Events fire during execution of the plugin.
     * @throws JobException
     */
    @WebResult(name = "result")
    @WebMethod(exclude = false)
    public WSExportJobResult getResult(@WebParam(name = "jobTag") String jobTag)throws JobException{
        UUID jobId = fromString(jobTag);
        String history = ExecutorRegister.getRegister().getHistory(jobId);
        String result = ExecutorRegister.getRegister().get(jobId).toString();
        return new WSExportJobResult(result, history);
    }

    /**
     * Returns the size of the result.
     *
     * This can help the client dettermine if it should use getString() or getURL()
     *
     * @param jobTag UUID identifier of the job
     * @return Size of the Temproary file that can be returned as a String or URL
     * @throws JobException
     */
    @WebResult(name = "resultSize")
    @WebMethod(exclude = false)
    public Long getResultSize(@WebParam(name = "jobTag") String jobTag)throws JobException{
        UUID jobId = fromString(jobTag);
        return ExecutorRegister.getRegister().getSize(jobId);
    }

    /**
     * Deletes a job.
     *
     * @param jobTag UUID identifier of the job
     * @return True if successful.
     * @throws JobException
     */
    @WebResult(name = "success")
    @WebMethod(exclude = false)
    public boolean deleteJob(@WebParam(name = "jobTag") String jobTag)throws JobException{
        UUID jobId = fromString(jobTag);
        return ExecutorRegister.getRegister().deleteJob(jobId);
    }

    /**
     * Gets a list of all {@link WSJob}s currently open.
     *
     * This method returns all jobs and not just the Export jobs.
     * Returns all jobs making no destinction between which users stated the job.
     *
     * @return list of all {@link WSJob}s currently open
     */
    @WebResult(name = "jobs")
    @WebMethod(exclude = false)
    public List<WSJob> getJobs(){
        return ExecutorRegister.getRegister().getJobs();
    }

    @WebMethod(exclude = true)
    public static boolean deleteFile(File file) {
        boolean ok = true;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File inner : files) {
                ok = ok && deleteFile(inner);
            }
        }
        ok = ok && file.delete();
        return ok;
    }

    private static boolean deleteAllTemporaryFiles() {
        boolean success = deleteFile(tempDir);
        //Now put a new tempDir back for later use.
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        return success;
    }
    /**
     * Deletes all jobs and all temporary files.
     *
     * Removes the job from memory and deletes any created files or other records.
     * Deletes all jobs and files not just Export jobs and files.
     * Deletes all jobs and files making no destinction between which users stated the job/ created the file.
     *
     * @return TRUE if every job and file was deleted sucessfully.
     *
     * @throws JobException
     */
    @WebResult(name = "jobs")
    @WebMethod(exclude = false)
    public boolean deleteAllJobs() throws JobException{
        Boolean success = ExecutorRegister.getRegister().deleteAllJobs();
        success = success && deleteAllTemporaryFiles();
        return success;
    }

}
