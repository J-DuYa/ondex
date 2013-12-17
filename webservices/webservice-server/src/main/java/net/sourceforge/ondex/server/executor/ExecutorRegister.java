/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.ondex.server.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.sourceforge.ondex.server.exceptions.JobException;
import net.sourceforge.ondex.server.result.WSJob;
import org.apache.log4j.Logger;

/**
 *
 * @author christian
 */
public class ExecutorRegister {
    private HashMap<UUID,WebserviceJob> jobs;

    private ExecutorService executor;

    private static ExecutorRegister instance;

    private static final Logger logger = Logger.getLogger(ExecutorRegister.class);

    public static ExecutorRegister getRegister(){
        if (instance == null){
            instance = new ExecutorRegister();
        }
        return instance;
    }

    private ExecutorRegister(){
        jobs = new HashMap<UUID,WebserviceJob>();
        executor = Executors.newSingleThreadExecutor();
    }

    public Future submitJob(WebserviceJob job){
       // Long jobId = new Date().getTime();
        jobs.put(job.getJobId(), job);
        return executor.submit(job);
    }

    public void clearJob(UUID jobId){
        jobs.remove(jobId);
    }

    public boolean isDone(UUID jobId) throws JobException{
        WebserviceJob job = jobs.get(jobId);
        if (job == null){
            throw new JobException("Unable to find job "+jobId,logger);
        }
        return job.isDone();
    }

    public String get(UUID jobId)throws JobException{
        WebserviceJob job = jobs.get(jobId);
        if (job == null){
            throw new JobException("Unable to find job "+jobId,logger);
        }
        return job.get();
    }

    public boolean isSuccessful(UUID jobId)throws JobException{
        WebserviceJob job = jobs.get(jobId);
        if (job == null){
            throw new JobException("Unable to find job "+jobId,logger);
        }
        return job.isSuccessful();
    }

    public String checkStatus(UUID jobId){
        WebserviceJob job = jobs.get(jobId);
        if (job == null){
            return Status.NOT_FOUND.toString();
        }
        return job.checkStatus();
    }

    public String getErrorHistory(UUID jobId)throws JobException{
        WebserviceJob job = getJob(jobId);
        return job.getErrorHistory();
    }

    public String getHistory(UUID jobId)throws JobException{
        WebserviceJob job = getJob(jobId);
        return job.getHistory();
    }

    public String getUrl(UUID jobId)throws JobException{
        WebserviceJob job = getJob(jobId);
        return job.getUrl();
    }

    public Long getSize(UUID jobId)throws JobException{
        WebserviceJob job = getJob(jobId);
        return job.getSize();
    }

    public WebserviceJob getJob(UUID jobId)throws JobException{
        WebserviceJob job = jobs.get(jobId);
        if (job == null){
            throw new JobException("Unable to find job "+jobId,logger);
        }
        return job;
    }

    public boolean deleteJob(UUID jobId) throws JobException{
        WebserviceJob job = jobs.get(jobId);
        if (job == null){
            throw new JobException("Unable to find job "+jobId,logger);
        }
        return job.deleteJob();
        //Job must clear itself from the register.
    }

    public List<WSJob> getJobs(){
        List<WSJob> list = new ArrayList<WSJob>();
        Set<UUID> uuids = jobs.keySet();
        Iterator<UUID> ids = uuids.iterator();
        while (ids.hasNext()){
            UUID jobId = ids.next();
            WebserviceJob job = jobs.get(jobId);
            list.add(new WSJob(jobId.toString(),job.getDescription()));
        }
        return list;
    }

    public boolean deleteAllJobs() throws JobException{
        boolean ok = true;
        Set<UUID> uuids = jobs.keySet();
        Iterator<UUID> ids = uuids.iterator();
        while (ids.hasNext()){
            UUID jobId = ids.next();
            ok = ok && deleteJob(jobId);
        }
        return ok;
    }
}
