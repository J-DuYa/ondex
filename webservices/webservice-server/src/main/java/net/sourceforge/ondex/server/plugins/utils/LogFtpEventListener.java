/*
 * Based on example code from Enterprise Distributed Technologies Ltd
 * www.enterprisedt.com
 */
package net.sourceforge.ondex.server.plugins.utils;

import com.enterprisedt.net.ftp.EventListener;
import org.apache.log4j.Logger;

/**
 *
 * @author christian
 */
public class LogFtpEventListener implements EventListener {

    private Logger log = Logger.getLogger(LogFtpEventListener.class);

    @Override
    public void bytesTransferred(String connId, String remoteFilename, long bytes) {
        log.info("Bytes transferred=" + bytes);
    }

    /**
     * Log an FTP command being sent to the server. Not used for SFTP.
     *
     * @param cmd   command string
     */
    @Override
    public void commandSent(String connId, String cmd) {
        log.info("Command sent: " + cmd);
    }

    /**
     * Log an FTP reply being sent back to the client. Not used for
     * SFTP.
     *
     * @param reply   reply string
     */
    @Override
    public void replyReceived(String connId, String reply) {
        log.info("Reply received: " + reply);
    }

    /**
     * Notifies that a download has started
     *
     * @param remoteFilename   remote file name
     */
    @Override
    public void downloadStarted(String connId, String remoteFilename) {
        log.info("Started download: " + remoteFilename);
    }

    /**
     * Notifies that a download has completed
     *
     * @param remoteFilename   remote file name
     */
    @Override
    public void downloadCompleted(String connId, String remoteFilename) {
        log.info("Completed download: " + remoteFilename);
    }

    /**
     * Notifies that an upload has started
     *
     * @param remoteFilename   remote file name
     */
    @Override
    public void uploadStarted(String connId, String remoteFilename) {
        log.info("Started upload: " + remoteFilename);
    }

    /**
     * Notifies that an upload has completed
     *
     * @param remoteFilename   remote file name
     */
    @Override
    public void uploadCompleted(String connId, String remoteFilename) {
        log.info("Completed upload: " + remoteFilename);
    }
}

